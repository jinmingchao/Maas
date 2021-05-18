package com.chinaunicom.torn.mcloud.entity;

public class CasbinHasPermissionEntity {
    private String obj;
    private Boolean hasPermission;

    public CasbinHasPermissionEntity(String obj, Boolean hasPermission){
        this.obj = obj;
        this.hasPermission = hasPermission;
    }

    public void setObj(String obj) {
        this.obj = obj;
    }

    public void setHasPermission(Boolean hasPermission) {
        this.hasPermission = hasPermission;
    }

    public String getObj() {
        return obj;
    }

    public Boolean getHasPermission() {
        return hasPermission;
    }
}
