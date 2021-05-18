package com.chinaunicom.torn.mcloud.service;

import com.chinaunicom.torn.mcloud.entity.LogEntity;

public interface LoggerService {

    public void error(LogEntity logEntity);

    public void info(LogEntity logEntity);

    public void warn(LogEntity logEntity);

    public void operationLog(LogEntity logEntity);
}
