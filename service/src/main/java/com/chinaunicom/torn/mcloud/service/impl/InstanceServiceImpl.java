package com.chinaunicom.torn.mcloud.service.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.alibaba.fastjson.JSONObject;
import com.chinaunicom.torn.mcloud.dao.HistorySetupInstanceDao;
import com.chinaunicom.torn.mcloud.dao.InstanceDao;
import com.chinaunicom.torn.mcloud.dao.InstanceInstallOpLogDao;
import com.chinaunicom.torn.mcloud.entity.*;
import com.chinaunicom.torn.mcloud.enums.InstanceInstallOpType;
import com.chinaunicom.torn.mcloud.enums.LogHow;
import com.chinaunicom.torn.mcloud.enums.ManageInstanceField;
import com.chinaunicom.torn.mcloud.enums.ServiceRole;
import com.chinaunicom.torn.mcloud.rpc.cloudboot.CloudbootCancelInstallPayload;
import com.chinaunicom.torn.mcloud.rpc.cloudboot.CloudbootDeletePayload;
import com.chinaunicom.torn.mcloud.rpc.cloudboot.CloudbootResultStatusInfo;
import com.chinaunicom.torn.mcloud.service.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

@Service
public class InstanceServiceImpl implements InstanceService {

    private static final LogEntityFactory logFactory = new LogEntityFactory(InstanceServiceImpl.class, ServiceRole.CALLER.name());

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private BaremetalService baremetalService;

    @Autowired
    private InstanceDao instanceDao;

    @Autowired
    private LoggerService loggerService;

    @Autowired
    private HistorySetupInstanceDao historySetupInstanceDao;

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private UserService userService;

    @Autowired
    private InstanceInstallOpLogDao instanceInstallOpLogDao;

    @Override
    public void syncDiscoveryInstance(String areaKey) {

        Optional<CloudbootTokenEntity> token = this.authenticationService.getCloudbootToken(areaKey);//获得指定区域的token
        if (!token.isPresent()) {
            this.loggerService.error(logFactory.product()
                    .how(LogHow.CALL).what(String.format("schedule sync [area: %s] discovery instances failed", areaKey)).why("Token not exist").build());
            return;
        }

        Set<String> updateSN = new HashSet<>();//set 用来存新更新设备的SN
        this.baremetalService.getDiscoveryInfos(token.get()).forEach(info -> {//http请求CB获得JSONList: CloudbootDiscoveryInfo
            updateSN.add(info.getSn());

            Optional<InstanceEntity> instance = this.instanceDao.findById(info.getSn());//根据sn在本地db中找到设备对象
            //无论如何都会把每个搜索到的对象添加到本地数据库中,已经有的对象会进行更新/覆盖操作
            if (instance.isPresent()) {
                instance.get().transferCloudbootDiscoveryInfo(info, areaKey);//将areakey和info中信息装配到instance中
                this.instanceDao.saveAndFlush(instance.get());//刷新装配好的instance到本地库

            } else {
                InstanceEntity newlyInstance = new InstanceEntity();
                newlyInstance.transferCloudbootDiscoveryInfo(info, areaKey);
                this.instanceDao.saveAndFlush(newlyInstance);
            }
        });

        this.loggerService.info(logFactory.product()
                .how(LogHow.CALL).what(String.format("update instance [sn: %s]", String.join(",", updateSN))).build());
    }

    @Override
    public List<InstanceEntity> getInstances(Example<InstanceEntity> entity) {
        return this.instanceDao.findAll(entity);
//                .stream()
//                .filter(instance ->
//                        this.permissionService.checkPermission(
//                                new PolicyEntity(
//                                        this.userService.getCurrentUser(),
//                                        instance.getSn(),
//                                        "read")))
//                .collect(Collectors.toList());
    }

    @Override
    public boolean manageInstance(String sn, String areaKey, JSONObject payload) {
        Optional<InstanceEntity> instance = this.instanceDao.findById(sn);

        if (instance.isPresent()) {
            if (!payload.containsKey(ManageInstanceField.HARDWARE.getField())) {
                return false;
            }
            if (!payload.containsKey(ManageInstanceField.NET_AREA.getField())) {
                return false;
            }

            instance.get().transferManageMessage(areaKey, payload);
            this.instanceDao.saveAndFlush(instance.get());

            instanceInstallOpLogDao.saveAndFlush(instance.get().generateOpLog(this.userService.getCurrentUser(), InstanceInstallOpType.MANAGED));

        } else {
            InstanceEntity newlyInstance = new InstanceEntity();
            newlyInstance.transferManageMessage(areaKey, payload);
            this.instanceDao.saveAndFlush(newlyInstance);

            instanceInstallOpLogDao.saveAndFlush(newlyInstance.generateOpLog(this.userService.getCurrentUser(), InstanceInstallOpType.MANAGED));
        }


        return true;
    }

    @Override
    public String initShell(String sn) {
        Optional<InstanceEntity> instance = this.instanceDao.findById(sn);
        if (!instance.isPresent()) {
            return "";
        }

        StringBuilder sb = new StringBuilder();

        sb.append("HOSTNAME=\"");
        sb.append(instance.get().getHostname());
        sb.append("\"\n");
        sb.append("IPADDR=\"");
        sb.append(instance.get().getInnerIp());
        sb.append("\"\n");
        sb.append("NETMASK=\"");
        sb.append(instance.get().getNetmask());
        sb.append("\"\n");
        sb.append("GATEWAY=\"");
        sb.append(instance.get().getGatewayIp());
        sb.append("\"\n");
        sb.append("VLANID=\"");
        sb.append(instance.get().getVlanId());
        sb.append("\"\n");
        sb.append("BOUND_TYPE=\"");
        sb.append(instance.get().getBoundType());
        sb.append("\"\n");
        sb.append("MAC1=\"");
        sb.append(instance.get().getBoundMac1());
        sb.append("\"\n");
        sb.append("MAC2=\"");
        sb.append(instance.get().getBoundMac2());
        sb.append("\"\n");

        return sb.toString();
    }

    @Override
    public InstanceDao getInstanceDao() {
        return this.instanceDao;
    }

    @Override
    public List<HistorySetupInstanceEntity> singleInstanceHistory(String sn) {
        HistorySetupInstanceEntity filter = new HistorySetupInstanceEntity();
        filter.setSn(sn);

        return this.historySetupInstanceDao.findAll(Example.of(filter));
    }

    @Override
    public CloudbootResultStatusInfo deleteCloudbootInstance(List<InstanceEntity> instances, CloudbootTokenEntity token) {
        List<CloudbootDeletePayload> deletePayloads = this.baremetalService.getDeviceInstanceInfos(token,
                instances.stream().map(instance -> instance.getSn()).collect(Collectors.toSet()))
                .stream()
                .map(instance -> instance.generateDeletePayload())
                .collect(Collectors.toList());
        if (!deletePayloads.isEmpty()) {
            return this.baremetalService.deleteInstance(deletePayloads, token);
        }

        CloudbootResultStatusInfo result = new CloudbootResultStatusInfo();
        result.setStatus("success");
        result.setStatus("已被删除");

        return result;
    }

    @Override
    public CloudbootResultStatusInfo cancelCloudbootInstall(List<InstanceEntity> instances, CloudbootTokenEntity token) {
        List<CloudbootCancelInstallPayload> cancelPayloads = this.baremetalService.getDeviceInstanceInfos(token,
                instances.stream().map(instance -> {
                    instanceInstallOpLogDao.saveAndFlush(instance.generateOpLog(this.userService.getCurrentUser(), InstanceInstallOpType.CANCEL));

                    return instance.getSn();
                }).collect(Collectors.toSet()))
                .stream()
                .map(instance -> instance.generateCancelPayload())
                .collect(Collectors.toList());
        if (!cancelPayloads.isEmpty()) {

            return this.baremetalService.cancelInstall(cancelPayloads, token);
        }

        CloudbootResultStatusInfo result = new CloudbootResultStatusInfo();
        result.setStatus("success");
        result.setStatus("已被删除");

        return result;
    }
}
