package com.chinaunicom.torn.mcloud.service.impl;

import com.chinaunicom.torn.mcloud.dao.ProjectCloudbootHardwareDao;
import com.chinaunicom.torn.mcloud.dao.ProjectDao;
import com.chinaunicom.torn.mcloud.service.ProjectService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProjectServiceImpl implements ProjectService {

    @Autowired
    private ProjectDao projectDao;

    @Autowired
    private ProjectCloudbootHardwareDao projectCloudbootHardwareDao;

    @Override
    public ProjectDao getProjectDao() {
        
        return this.projectDao;
    }

    @Override
    public ProjectCloudbootHardwareDao getProjectCloudbootHardwareDao() {
        
        return this.projectCloudbootHardwareDao;
    }
}
