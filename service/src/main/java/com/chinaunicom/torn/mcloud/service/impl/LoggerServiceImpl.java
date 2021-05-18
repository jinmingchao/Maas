package com.chinaunicom.torn.mcloud.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.chinaunicom.torn.mcloud.dao.OperationLogDao;
import com.chinaunicom.torn.mcloud.entity.LogEntity;
import com.chinaunicom.torn.mcloud.service.LoggerService;
import com.chinaunicom.torn.mcloud.service.UserService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LoggerServiceImpl implements LoggerService {

    private static Logger logger = LoggerFactory.getLogger("torn_mcloud");

    @Autowired
    private OperationLogDao operationLogDao;

    @Autowired
    private UserService userService;
    
    @Override
    public void error(LogEntity logEntity) {
        LoggerServiceImpl.logger.error(JSONObject.toJSONString(logEntity));
    }

    @Override
    public void info(LogEntity logEntity) {
        LoggerServiceImpl.logger.info(JSONObject.toJSONString(logEntity));
    }

    @Override
    public void warn(LogEntity logEntity) {
        LoggerServiceImpl.logger.warn(JSONObject.toJSONString(logEntity));
    }

    @Override
    public void operationLog(LogEntity logEntity) {
        try {
            logEntity.setWho(this.userService.getCurrentUser());
            LoggerServiceImpl.logger.info(JSONObject.toJSONString(logEntity));

            this.operationLogDao.saveAndFlush(logEntity);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
