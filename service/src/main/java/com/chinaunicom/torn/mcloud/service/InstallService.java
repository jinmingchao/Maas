package com.chinaunicom.torn.mcloud.service;

import java.util.List;
import java.util.Set;

import com.chinaunicom.torn.mcloud.dao.SetupDao;
import com.chinaunicom.torn.mcloud.entity.InstallInstanceEntity;
import com.chinaunicom.torn.mcloud.message.InstallResultMessage;

public interface InstallService {

    public void createBatch(String name, String areaId, List<InstallInstanceEntity> instances) throws InterruptedException;

    public List<InstallResultMessage> install(List<String> sn);

    public void asyncCheck(Integer batchId, List<InstallInstanceEntity> instances) throws InterruptedException;

    public Set<String> getBatchSNSet(Integer batchId);

    public SetupDao getSetupDao();
}
