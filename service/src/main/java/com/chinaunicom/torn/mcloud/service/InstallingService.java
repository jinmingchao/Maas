package com.chinaunicom.torn.mcloud.service;

import java.util.List;

import com.chinaunicom.torn.mcloud.entity.InstanceEntity;

public interface InstallingService {

    public void addInstance(InstanceEntity instance);

    public List<InstanceEntity> getInstallingInstance(String areaKey);

    public boolean exist(InstanceEntity instance);
}
