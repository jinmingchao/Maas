package com.chinaunicom.torn.mcloud.entity;

public class AddResourceResEntity {
    private String groupName;
    private String resource;
    private Boolean result;

    public AddResourceResEntity(String groupName, String resource, Boolean result) {
        this.groupName = groupName;
        this.resource = resource;
        this.result = result;
    }

    public String getGroupName() {
        return groupName;
    }

    public String getResource() {
        return resource;
    }

    public Boolean getResult() {
        return result;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public void setResult(Boolean result) {
        this.result = result;
    }
}
