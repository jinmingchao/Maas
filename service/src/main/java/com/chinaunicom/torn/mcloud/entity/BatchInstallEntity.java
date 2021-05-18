package com.chinaunicom.torn.mcloud.entity;

import java.util.List;

public class BatchInstallEntity {

    private Integer batchId;
    private List<InstallInstanceEntity> instances;

    public Integer getBatchId() {
        return batchId;
    }

    public List<InstallInstanceEntity> getInstances() {
        return instances;
    }

    public void setBatchId(Integer batchId) {
        this.batchId = batchId;
    }

    public void setInstances(List<InstallInstanceEntity> instances) {
        this.instances = instances;
    }
}
