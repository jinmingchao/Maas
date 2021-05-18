package com.chinaunicom.torn.mcloud.service;

import com.chinaunicom.torn.mcloud.dao.ProjectCloudbootHardwareDao;
import com.chinaunicom.torn.mcloud.dao.ProjectDao;

public interface ProjectService {

    public ProjectDao getProjectDao();

    public ProjectCloudbootHardwareDao getProjectCloudbootHardwareDao();
}
