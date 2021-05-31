package com.chinaunicom.torn.mcloud.service.impl;

import com.chinaunicom.torn.mcloud.component.CIDRUtils;
import com.chinaunicom.torn.mcloud.dao.CloudbootAreaDao;
import com.chinaunicom.torn.mcloud.dao.IPNetAreaDao;
import com.chinaunicom.torn.mcloud.dao.IPPoolAreaDao;
import com.chinaunicom.torn.mcloud.dao.IPTableDao;
import com.chinaunicom.torn.mcloud.entity.*;
import com.chinaunicom.torn.mcloud.enums.LogHow;
import com.chinaunicom.torn.mcloud.enums.ServiceRole;
import com.chinaunicom.torn.mcloud.message.BatchIPMessage;

import com.chinaunicom.torn.mcloud.service.*;

import org.hibernate.criterion.Example;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class AreaZoneServiceImpl implements AreaZoneService {
    private static LogEntityFactory logFactory = new LogEntityFactory(AreaZoneServiceImpl.class);

    @Autowired
    private LoggerService logService;

    @Autowired
    private IPPoolAreaDao poolAreaDao;

    @Autowired
    private IPTableDao ipTableDao;

    @Autowired
    private CloudbootAreaDao cloudDao;

    @Autowired
    private IPNetAreaDao netAreaDao;

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private SchedulerService schedulerService;

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private UserService userService;
//    @Autowired
//    private CIDRUtils cidrUtils;

    // area
    public List<CloudbootAreaEntity> getAllArea() {
        return this.cloudDao.findAll();
//                .stream()
//                .filter(cloudbootAreaEntity ->
//                        this.permissionService.checkPermission(
//                                new PolicyEntity(
//                                        this.userService.getCurrentUser(),
//                                        "AREA-" + cloudbootAreaEntity.getId(),
//                                        "read")))
//                .collect(Collectors.toList());
    }

    public List<CloudbootAreaEntity> fetchAllCloud() {
        return this.cloudDao.findAll();

//                .stream()
//                .filter(cloudbootAreaEntity ->
//                        this.permissionService.checkPermission(
//                                new PolicyEntity(
//                                        this.userService.getCurrentUser(),
//                                        "AREA-" + cloudbootAreaEntity.getId(),
//                                        "read")))
//                .collect(Collectors.toList());
    }

    public void createCloudArea(CloudbootAreaEntity cloud) {
        this.logService.info(AreaZoneServiceImpl.logFactory.product()
                    .who(ServiceRole.PROMOTER).how(LogHow.CREATE).what("create cloud area :" + cloud.toString()).build());
        try {
            this.cloudDao.saveAndFlush(cloud);
            this.logService.info(AreaZoneServiceImpl.logFactory.product()
                    .who(ServiceRole.PROMOTER).how(LogHow.LOGIN).what(String.format("login Datacenter %s Cloudboot", cloud.getName())).build());

            ExecutorService executor = Executors.newFixedThreadPool(2);
            CompletableFuture<Integer> future = CompletableFuture.supplyAsync(new Supplier<Integer>() {
                @Override
                public Integer get() {
                    if (cloud.getEnabled() && authenticationService.cloudbootLogin(cloud)) {
                        try {
                            schedulerService.registerSpecCloudbootAreaScheduler(cloud);
                        } catch (IOException | ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                    return 3;
                }
            }, executor);
        } catch (Exception e) {
            this.logService.error(AreaZoneServiceImpl.logFactory.product()
                    .who(ServiceRole.PROMOTER).how(LogHow.CREATE).what("create failed").why(e.getMessage()).build());
        }
    }

    public void updateCloudArea(CloudbootAreaEntity cloud) {
        this.logService.info(AreaZoneServiceImpl.logFactory.product()
                    .who(ServiceRole.PROMOTER).how(LogHow.UPDATE).what("update cloud area :" + cloud.toString()).build());
        try {
            this.cloudDao.updateById(
                    cloud.getName(), cloud.getHost(), cloud.getUsername(), cloud.getPassword(),
                    cloud.getSyncInstanceInterval(), cloud.getEnabled(), cloud.getId()
            );
            if (cloud.getEnabled()) {
                this.schedulerService.stop();
                if (this.authenticationService.cloudbootLogin(cloud)) {
                    this.authenticationService.updateToken(cloud);
                    this.schedulerService.registerSpecCloudbootAreaScheduler(cloud);
                }
            }
        } catch (Exception e) {
            this.logService.error(AreaZoneServiceImpl.logFactory.product()
                    .who(ServiceRole.PROMOTER).how(LogHow.CREATE).what("update failed").why(e.getMessage()).build());
        }
    }

    public void deleteCloudArea(CloudbootAreaEntity cloud) {
        this.logService.warn(AreaZoneServiceImpl.logFactory.product()
                    .who(ServiceRole.PROMOTER).how(LogHow.DELETE).what("delete  cloud  :" + cloud.toString()).build());
        try {
            this.cloudDao.deleteById(cloud.getId());
            this.schedulerService.stop();
            this.authenticationService.deleteToken();
            this.schedulerService.start();
        } catch (Exception e) {
            this.logService.error(AreaZoneServiceImpl.logFactory.product()
                    .who(ServiceRole.PROMOTER).how(LogHow.CREATE).what("delete failed").why(e.getMessage()).build());
        }
    }


    // 网络区域
    public List<IPNetAreaEntity> fetchAllNetArea() {
        return this.netAreaDao.findAll();
//                .stream()
//                .filter(ipNetArea ->
//                        this.permissionService.checkPermission(
//                                new PolicyEntity(
//                                        this.userService.getCurrentUser(),
//                                        "NET-" + ipNetArea.getId(),
//                                        "read")))
//                .collect(Collectors.toList());
    }

    public List<IPNetAreaEntity> fetchNetArea(CloudbootAreaEntity cloud) {
        return this.netAreaDao.findAllByAreaId(cloud.getId());
//                .stream()
//                .filter(ipNetArea ->
//                        this.permissionService.checkPermission(
//                                new PolicyEntity(
//                                        this.userService.getCurrentUser(),
//                                        "NET-" + ipNetArea.getId(),
//                                        "read")))
//                .collect(Collectors.toList());
    }

    public void createNetArea(IPNetAreaEntity net) {
        this.logService.info(AreaZoneServiceImpl.logFactory.product()
                    .who(ServiceRole.PROMOTER).how(LogHow.CREATE).what("create net area :" + net.toString()).build());
        try {
            this.netAreaDao.saveAndFlush(net);
        } catch (Exception e) {
            this.logService.error(AreaZoneServiceImpl.logFactory.product()
                    .who(ServiceRole.PROMOTER).how(LogHow.CREATE).what("create failed").why(e.getMessage()).build());
        }
    }

    public void updateNetArea(IPNetAreaEntity net) {
        this.logService.info(AreaZoneServiceImpl.logFactory.product()
                    .who(ServiceRole.PROMOTER).how(LogHow.UPDATE).what("update ip net  :" + net.toString()).build());
        try {
            this.netAreaDao.updateById(net.getName(), net.getComment(), net.getAreaId(), net.isEnabled(), net.getId());
        } catch (Exception e) {
            this.logService.error(AreaZoneServiceImpl.logFactory.product()
                    .who(ServiceRole.PROMOTER).how(LogHow.CREATE).what("update failed").why(e.getMessage()).build());
        }
    }

    public void deleteNetArea(IPNetAreaEntity net) {
        this.logService.warn(AreaZoneServiceImpl.logFactory.product()
                    .who(ServiceRole.PROMOTER).how(LogHow.DELETE).what("delete ip net  :" + net.toString()).build());
        try {
            this.netAreaDao.deleteById(net.getId());
        } catch (Exception e) {
            this.logService.error(AreaZoneServiceImpl.logFactory.product()
                    .who(ServiceRole.PROMOTER).how(LogHow.CREATE).what("delete failed").why(e.getMessage()).build());
        }
    }

    // IP 池
    public List<IPPoolAreaEntity> fetchAllPool() {
        return this.poolAreaDao.findAll();
    }

    public List<IPPoolAreaEntity> fetchPool(IPNetAreaEntity netArea) {
        return this.poolAreaDao.findAllByNetAsst(netArea.getId());
    }

    public List<IPPoolAreaEntity> fetchPoolByArea(CloudbootAreaEntity area) {
        return this.poolAreaDao.findAllByNetAsstIn(this.netAreaDao.findIdByAreaId(area.getId()));
    }

    public void createPool(IPPoolAreaEntity pool) {
        this.logService.info(AreaZoneServiceImpl.logFactory.product()
                    .who(ServiceRole.PROMOTER).how(LogHow.CREATE).what("create ip pool  :" + pool.toString()).build());
        try {
            this.poolAreaDao.saveAndFlush(pool);

            ExecutorService executor = Executors.newFixedThreadPool(2);
            CompletableFuture<Integer> future = CompletableFuture.supplyAsync(new Supplier<Integer>() {
                @Override
                public Integer get() {
                    createPoolIp(pool);
                    return 3;
                }
            }, executor);
//            future.thenAccept(e -> System.out.println(e));

        } catch (Exception e) {
            e.printStackTrace();
            this.logService.error(AreaZoneServiceImpl.logFactory.product()
                    .who(ServiceRole.PROMOTER).how(LogHow.CREATE).what("create failed").why(e.getMessage()).build());
        }
    }

    private void createPoolIp(IPPoolAreaEntity pool) {
        if (pool.getCidr().contains("-")) {
            Pattern pattern = Pattern.compile("(?<base>(\\d+\\.){3})(?<start>\\d+)-(?<end>\\d+)");
            Matcher matcher = pattern.matcher(pool.getCidr());

            if (!matcher.matches()) {
                return;
            }

            int start = Integer.parseInt(matcher.group("start"));
            int end = Integer.parseInt(matcher.group("end"));
            String base = matcher.group("base");

            if (start > end) {
                int mid = start;
                start = end;
                end = mid;
            }
            for (int num = start; num <= end; num++) {
                String ip = String.format("%s%d", base, num);
                if (Objects.equals(ip, pool.getGatewayIp())) {
                    continue;
                }
                IPTableEntity ipEntity = new IPTableEntity();
                ipEntity.setHost(ip);
                ipEntity.setPoolAsst(pool.getId());
                ipEntity.setEnabled(true);

                this.ipTableDao.saveAndFlush(ipEntity);
            }
        } else if (pool.getCidr().contains("/")) {
//            CIDRUtils cidrUtils = null;
            try {
                CIDRUtils cidrUtils = new CIDRUtils(pool.getCidr());
                String networkAddress = cidrUtils.getNetworkAddress();
                String broadcastAddress = cidrUtils.getBroadcastAddress();
                String[] address = cidrUtils.getAllAddresses();
                for(String ip : address){
                    IPTableEntity ipEntity = new IPTableEntity();
                    if (Objects.equals(ip, pool.getGatewayIp())) {
                        continue;
                    }
                    ipEntity.setHost(ip);
                    ipEntity.setPoolAsst(pool.getId());
                    ipEntity.setEnabled(true);
                    this.ipTableDao.saveAndFlush(ipEntity);
                }
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        }
    }

    public void updatePool(IPPoolAreaEntity pool) {
        this.logService.info(AreaZoneServiceImpl.logFactory.product()
                    .who(ServiceRole.PROMOTER).how(LogHow.CREATE).what("update ip pool  :" + pool.toString()).build());
        try {
            this.poolAreaDao.updateById(pool.getName(), pool.getCidr(), pool.getNetAsst(), pool.getNetmask(), pool.getGatewayIp(), pool.getVlanId(), pool.isEnabled(), pool.getId());
        } catch (Exception e) {
            this.logService.error(AreaZoneServiceImpl.logFactory.product()
                    .who(ServiceRole.PROMOTER).how(LogHow.CREATE).what("update failed").why(e.getMessage()).build());
        }
    }

    public void deletePool(IPPoolAreaEntity pool) {
        this.logService.warn(AreaZoneServiceImpl.logFactory.product()
                    .who(ServiceRole.PROMOTER).how(LogHow.CREATE).what("delete ip pool  :" + pool.toString()).build());
        try {
            this.poolAreaDao.deleteById(pool.getId());
            this.ipTableDao.deleteAllByPoolAsst(pool.getId());
        } catch (Exception e) {
            this.logService.error(AreaZoneServiceImpl.logFactory.product()
                    .who(ServiceRole.PROMOTER).how(LogHow.CREATE).what("delete failed").why(e.getMessage()).build());
        }
    }

    // IP
    public List<IPTableEntity> fetchIP(IPPoolAreaEntity pool) {
        return this.ipTableDao.findAllByPoolAsst(pool.getId());
    }

    public List<IPTableEntity> fetchUsedIP(IPPoolAreaEntity pool) {
        return this.ipTableDao.findAllByPoolAsstAndEnabledIsFalse(pool.getId());
    }

    public List<IPTableEntity> fetchUnusedIP(IPPoolAreaEntity pool) {
        return this.ipTableDao.findAllByPoolAsstAndEnabledIsTrue(pool.getId());
    }

    public void setUsedIP(IPTableEntity ip, String sn) {
        ip.setEnabled(false);
        ip.setSn(sn);
        this.ipTableDao.saveAndFlush(ip);
        this.logService.info(AreaZoneServiceImpl.logFactory.product()
                    .who(ServiceRole.PROMOTER).how(LogHow.CALL).what("cloud boot use a ip :" + ip.getIp()).build());
    }

    public void createIP(IPTableEntity ip) {
        this.logService.info(AreaZoneServiceImpl.logFactory.product()
                    .who(ServiceRole.PROMOTER).how(LogHow.CREATE).what("create ip ip  :" + ip.toString()).build());
        try {
            this.ipTableDao.saveAndFlush(ip);
        } catch (Exception e) {
            this.logService.error(AreaZoneServiceImpl.logFactory.product()
                    .who(ServiceRole.PROMOTER).how(LogHow.CREATE).what("create failed").why(e.getMessage()).build());
        }
    }

    public void updateIP(IPTableEntity ip) {
        this.logService.info(AreaZoneServiceImpl.logFactory.product()
                    .who(ServiceRole.PROMOTER).how(LogHow.CREATE).what("update ip ip  :" + ip.toString()).build());
        try {
            this.ipTableDao.updateById(ip.getHost(), ip.getPoolAsst(), ip.isEnabled(), ip.getId());
        } catch (Exception e) {
            this.logService.error(AreaZoneServiceImpl.logFactory.product()
                    .who(ServiceRole.PROMOTER).how(LogHow.CREATE).what("update failed").why(e.getMessage()).build());
        }
    }

    public void batchUpdateIP(BatchIPMessage ip) {
        this.logService.info(AreaZoneServiceImpl.logFactory.product()
                    .who(ServiceRole.PROMOTER).how(LogHow.CREATE).what("batch update  ip  :" + ip.toString()).build());
        try {
            this.ipTableDao.batchUpdateById(ip.isEnabled(), ip.getIpList());
        } catch (Exception e) {
            this.logService.error(AreaZoneServiceImpl.logFactory.product()
                    .who(ServiceRole.PROMOTER).how(LogHow.CREATE).what("batch update failed").why(e.getMessage()).build());
        }
    }

    public void deleteIP(IPTableEntity ip) {
        this.logService.warn(AreaZoneServiceImpl.logFactory.product()
                .who(ServiceRole.PROMOTER).how(LogHow.CREATE).what("delete ip ip  :" + ip.toString()).build());
        try {
            this.ipTableDao.deleteById(ip.getId());
        } catch (Exception e) {
            this.logService.error(AreaZoneServiceImpl.logFactory.product()
                    .who(ServiceRole.PROMOTER).how(LogHow.CREATE).what("delete failed").why(e.getMessage()).build());
        }
    }

    @Override
    public void ipPoolFillInstallInstance(List<InstallInstanceEntity> entities) {
        Map<Integer, IPPoolAreaEntity> mapper = new HashMap<>();
        entities.forEach(entity -> System.out.println(entity.getIppool()));
        this.poolAreaDao
            .findAllById(entities.stream().mapToInt(entity -> entity.getIppool()).boxed().collect(Collectors.toList()))
            .forEach(pool -> mapper.put(pool.getId(), pool));

        entities.forEach(entity -> {
            if (mapper.containsKey(entity.getIppool())) {
                IPPoolAreaEntity pool = mapper.get(entity.getIppool());

                entity.setVlanId(pool.getVlanId());
                entity.setGateway(pool.getGatewayIp());
                entity.setNetmask(pool.getNetmask());
            }
        });
    }


    @Override
    public Boolean getIpUsable(Integer ippoolId, String ip, String sn) {
        Optional<IPTableEntity> entity = this.ipTableDao.findOneByHostAndIppool(ip, ippoolId);
        if (!entity.isPresent()) {
            System.out.println("Not Exist");
            return false;
        }

        return entity.get().isEnabled() || entity.get().getSn().equals(sn);
    }

    @Override
    public void setUsedIp(Integer ippoolId, String ip, String sn) {
        Optional<IPTableEntity> entity = this.ipTableDao.findOneByHostAndIppool(ip, ippoolId);
        if (!entity.isPresent()) {
            return;
        }

        this.setUsedIP(entity.get(), sn);
    }

    @Override
    public void setUnusedIp(Integer ippoolId, String ip, String sn) {
        try {
            Optional<IPTableEntity> entity = this.ipTableDao.findOneByHostAndIppool(ip, ippoolId);
            if (!entity.isPresent()) {
                return;
            }
            if (entity.get().getSn() == null || entity.get().getSn().isEmpty() || entity.get().getSn().equals(sn)) {
                this.ipTableDao.unuseIp(entity.get().getId());
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
