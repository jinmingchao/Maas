package com.chinaunicom.torn.mcloud.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

import com.chinaunicom.torn.mcloud.dao.InstanceDao;
import com.chinaunicom.torn.mcloud.dao.InstanceInstallOpLogDao;
import com.chinaunicom.torn.mcloud.entity.CloudbootTokenEntity;
import com.chinaunicom.torn.mcloud.entity.InstanceEntity;
import com.chinaunicom.torn.mcloud.entity.LogEntityFactory;
import com.chinaunicom.torn.mcloud.enums.InstanceInstallOpType;
import com.chinaunicom.torn.mcloud.enums.LogHow;
import com.chinaunicom.torn.mcloud.service.AuthenticationService;
import com.chinaunicom.torn.mcloud.service.BaremetalService;
import com.chinaunicom.torn.mcloud.service.HistoryService;
import com.chinaunicom.torn.mcloud.service.InstallingService;
import com.chinaunicom.torn.mcloud.service.InstanceService;
import com.chinaunicom.torn.mcloud.service.LoggerService;
import com.chinaunicom.torn.mcloud.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class InstallingSerivceImpl implements InstallingService {

    private static LogEntityFactory logFactory = new LogEntityFactory(BaremetalServiceImpl.class);

    private Lock lock;
    private Map<String, Map<String, InstanceEntity>> installingMap;


    @Autowired
    private LoggerService logService;

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private BaremetalService baremetalService;

    @Autowired
    private InstanceDao instanceDao;

    @Autowired
    private InstanceService instanceService;

    @Autowired
    private HistoryService historyService; 

    @Autowired
    private UserService userService;

    @Autowired
    private InstanceInstallOpLogDao instanceInstallOpLogDao;

    public InstallingSerivceImpl() {
        this.installingMap = new HashMap<>();
        this.lock = new ReentrantLock();
    }

    @Override
    public void addInstance(InstanceEntity instance) {

        instanceInstallOpLogDao.saveAndFlush(instance.generateOpLog(this.userService.getCurrentUser(), InstanceInstallOpType.INSTALL));

        this.lock.lock();

        if (!this.installingMap.containsKey(instance.getAreaId())) {
            this.installingMap.put(instance.getAreaId(), new HashMap<>());
        }
        if (!this.installingMap.get(instance.getAreaId()).containsKey(instance.getSn())) {
            this.installingMap.get(instance.getAreaId()).put(instance.getSn(), instance);
        }

        this.logService.info(InstallingSerivceImpl.logFactory.product()
                .how(LogHow.CALL).what(String.format("add installing instance [sn: %s]", instance.getSn())).build());

        this.lock.unlock();
    }

    @Override
    public List<InstanceEntity> getInstallingInstance(String areaKey) {
        this.lock.lock();
        final List<InstanceEntity> result = new LinkedList<>();

        if (this.installingMap.containsKey(areaKey)) {
            Set<String> sn = this.installingMap.get(areaKey).values().stream().map(instance -> instance.getSn()).collect(Collectors.toSet());
            result.addAll(this.instanceDao.findAllById(sn));
        }

        this.lock.unlock();

        return result;
    }

    @Override
    public boolean exist(InstanceEntity instance) {
        this.lock.lock();
        if (!this.installingMap.containsKey(instance.getAreaId())) {
            this.lock.unlock();
            return false;
        }
        if (!this.installingMap.get(instance.getAreaId()).containsKey(instance.getSn())) {
            this.lock.unlock();
            return false;
        }
        this.lock.unlock();
        return true;
    }

    @Scheduled(cron = "*/10 * * * * ?")
    public void sync() {
        this.lock.lock();

        this.installingMap.forEach((areaKey, instances) -> {
            Optional<CloudbootTokenEntity> token = this.authenticationService.getCloudbootToken(areaKey);
            if (!token.isPresent()) {
                this.logService.error(InstallingSerivceImpl.logFactory.product()
                        .how(LogHow.SCHEDULE).what(String.format("sync installing failed")).why("Token not exist").build());
                return;
            }

            this.logService.info(InstallingSerivceImpl.logFactory.product()
                    .how(LogHow.SCHEDULE).what(String.format("sync installing area :%s", areaKey)).build());


            // 从指定的Cloudboot中，获取指定Cloudboot装机列表中设备的状态
            final Set<String> infoStatus = new HashSet<>();
            this.baremetalService.getDeviceInstanceInfos(token.get(), instances.keySet()).forEach(status -> {
                if (!instances.containsKey(status.getSn())) {
                    return;
                }
                Optional<InstanceEntity> instance = this.instanceDao.findById(status.getSn());
                if (!instance.isPresent()) {
                    return;
                }
                infoStatus.add(status.getSn());

                this.logService.info(InstallingSerivceImpl.logFactory.product()
                        .how(LogHow.SCHEDULE).what(String.format("sync installing instance [sn: %s]", instance.get().getSn())).build());
                
                if (status.getStatus().equals("success")) {
                    // 标记装机结束
                    instance.get().setInstalled(true);
                    instance.get().setStatus("装机成功");
                    this.instanceDao.saveAndFlush(instance.get());

                    // 从装机队列中删除
                    instances.remove(status.getSn());

                    // 添加装机日志
                    this.historyService.appendHistory(instance.get(), "装机成功");

                    // 从Cloudboot装机成功的设备列表中删除记录
                    List<InstanceEntity> deleteCloudbootInstances = new ArrayList<>(1);
                    deleteCloudbootInstances.add(instance.get());
                    this.instanceService.deleteCloudbootInstance(deleteCloudbootInstances, token.get());

                    instanceInstallOpLogDao.saveAndFlush(instance.get().generateOpLog("system", InstanceInstallOpType.INSTALL_SUCCESS));
                }
                else if (status.getStatus().equals("failure")) {
                    // 标记装机结束
                    instance.get().setInstalled(true);
                    instance.get().setStatus("装机失败");
                    this.instanceDao.saveAndFlush(instance.get());

                    // 从装机队列中删除
                    instances.remove(status.getSn());

                    // 添加装机日志
                    this.historyService.appendHistory(instance.get(), "装机失败");

                    instanceInstallOpLogDao.saveAndFlush(instance.get().generateOpLog("system", InstanceInstallOpType.INSTALL_FAILURE));
                }
                else {
                    instance.get().setStatus(status.getInstallLog());
                    this.instanceDao.saveAndFlush(instance.get());

                    this.historyService.appendHistory(instance.get(), status.getInstallLog());
                }
            });

            // 当出现installing队列中存在SN但是Cloudboot没有该SN信息时，应将这些SN退出installing队列，并标记为调用API异常
            instances.keySet()
                .stream()
                .filter(sn -> !infoStatus.contains(sn))
                .collect(Collectors.toList())
                .forEach(sn -> {
                    Optional<InstanceEntity> instance = this.instanceDao.findById(sn);
                    if (!instance.isPresent()) {
                        return;
                    }

                    instance.get().setStatus("装机失败");
                    instance.get().setInstalled(true);
                    instance.get().setInstallable(true);
                    this.instanceDao.saveAndFlush(instance.get());

                    instances.remove(sn);
                });
        });

        this.lock.unlock();
    }
}
