package com.chinaunicom.torn.mcloud.service;

import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.chinaunicom.torn.mcloud.dao.InstanceDao;
import com.chinaunicom.torn.mcloud.entity.CloudbootTokenEntity;
import com.chinaunicom.torn.mcloud.entity.HistorySetupInstanceEntity;
import com.chinaunicom.torn.mcloud.entity.InstanceEntity;
import com.chinaunicom.torn.mcloud.rpc.cloudboot.CloudbootResultStatusInfo;

import org.springframework.data.domain.Example;

public interface InstanceService {

    public void syncDiscoveryInstance(String areaKey);

    public CloudbootResultStatusInfo deleteCloudbootInstance(List<InstanceEntity> instances, CloudbootTokenEntity token);

    public CloudbootResultStatusInfo cancelCloudbootInstall(List<InstanceEntity> instances, CloudbootTokenEntity token);

    public List<InstanceEntity> getInstances(Example<InstanceEntity> entity);

    public boolean manageInstance(String sn, String areaKey, JSONObject payload);

    public String initShell(String sn);

    public InstanceDao getInstanceDao();

    public List<HistorySetupInstanceEntity> singleInstanceHistory(String sn);
}
