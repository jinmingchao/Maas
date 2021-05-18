package com.chinaunicom.torn.mcloud.message;

import java.util.List;

public class InstanceStatRequestMessage {
    
    private List<Integer> projectId;
    private List<String> areaId;


    public List<String> getAreaId() {
        return areaId;
    }

    public List<Integer> getProjectId() {
        return projectId;
    }

    public void setAreaId(List<String> areaId) {
        this.areaId = areaId;
    }

    public void setProjectId(List<Integer> projectId) {
        this.projectId = projectId;
    }
}
