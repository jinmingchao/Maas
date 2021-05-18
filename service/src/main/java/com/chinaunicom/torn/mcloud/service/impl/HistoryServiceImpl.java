package com.chinaunicom.torn.mcloud.service.impl;

import com.chinaunicom.torn.mcloud.dao.HistorySetupInstanceDao;
import com.chinaunicom.torn.mcloud.entity.HistorySetupInstanceEntity;
import com.chinaunicom.torn.mcloud.entity.InstanceEntity;
import com.chinaunicom.torn.mcloud.service.HistoryService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HistoryServiceImpl implements HistoryService {

    @Autowired
    private HistorySetupInstanceDao historySetupInstanceDao;

    @Override
    public void appendHistory(InstanceEntity instance, String detail) {
        this.historySetupInstanceDao.saveAndFlush(new HistorySetupInstanceEntity(instance.getSetupId(), instance.getSn(), detail));
    }

    @Override
    public HistorySetupInstanceDao getHistorySetupInstanceDao() {
        return this.historySetupInstanceDao;
    }
}
