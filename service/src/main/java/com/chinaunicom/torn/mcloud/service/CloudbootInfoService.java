package com.chinaunicom.torn.mcloud.service;

import java.util.List;

import com.chinaunicom.torn.mcloud.dao.CloudbootHardwareDao;
import com.chinaunicom.torn.mcloud.dao.CloudbootHardwareTemplateDao;
import com.chinaunicom.torn.mcloud.dao.CloudbootOperationSystemDao;
import com.chinaunicom.torn.mcloud.dao.CloudbootPXEDao;
import com.chinaunicom.torn.mcloud.entity.CloudbootHardwareEntity;
import com.chinaunicom.torn.mcloud.entity.CloudbootOperationSystemEntity;
import com.chinaunicom.torn.mcloud.entity.CloudbootPXEEntity;

public interface CloudbootInfoService {

    public void syncHardware(String areaKey);

    public void syncOperationSystem(String areaKey);

    public void syncPXE(String areaKey);

    public List<CloudbootHardwareEntity> getAllHardware(String areaKey);

    public List<CloudbootOperationSystemEntity> getAllOperationSystem(String areaKey);

    public List<CloudbootPXEEntity> getAllPXE(String areaKey);

    public CloudbootHardwareDao getHardwareDao();

    public CloudbootOperationSystemDao getOperationSystemDao();

    public CloudbootPXEDao getPXEDao(); 


    public CloudbootHardwareTemplateDao getHardwareTemplateDao();
}
