package com.chinaunicom.torn.mcloud.service;

import com.chinaunicom.torn.mcloud.dao.HistorySetupInstanceDao;
import com.chinaunicom.torn.mcloud.entity.InstanceEntity;

public interface HistoryService {

    public void appendHistory(InstanceEntity instance, String detail);

    public HistorySetupInstanceDao getHistorySetupInstanceDao();
}
