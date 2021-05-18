package com.chinaunicom.torn.mcloud.service;

import java.io.IOException;

import com.chinaunicom.torn.mcloud.entity.CloudbootAreaEntity;

import org.quartz.SchedulerException;
import org.springframework.scheduling.quartz.QuartzJobBean;

public interface SchedulerService {

    public void registerSpecCloudbootAreaScheduler(CloudbootAreaEntity area) throws IOException, ClassNotFoundException;

    public void registerCloudbootArea(Class<? extends QuartzJobBean> clazz);

    void stop() throws SchedulerException;

    void start();
}
