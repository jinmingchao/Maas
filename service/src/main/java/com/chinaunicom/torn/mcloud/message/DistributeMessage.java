package com.chinaunicom.torn.mcloud.message;

import java.util.List;

public class DistributeMessage {
    private String name;
    private String areaId;
    private List<DistributeInstanceMessage> instances;

    public String getName() {
        return name;
    }

    public List<DistributeInstanceMessage> getInstances() {
        return instances;
    }

    public String getAreaId() {
        return areaId;
    }

    public void setAreaId(String areaId) {
        this.areaId = areaId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setInstances(List<DistributeInstanceMessage> instances) {
        this.instances = instances;
    }
}
