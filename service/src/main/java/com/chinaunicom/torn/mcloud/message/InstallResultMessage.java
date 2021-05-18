package com.chinaunicom.torn.mcloud.message;

import com.chinaunicom.torn.mcloud.rpc.cloudboot.CloudbootResultStatusInfo;

public class InstallResultMessage {
    
    private String areaKey;
    private String status;
    private String message;

    public InstallResultMessage() { }

    public InstallResultMessage(String areaKey, CloudbootResultStatusInfo statusInfo) {
        this.areaKey = areaKey;
        this.status = statusInfo.getStatus();
        this.message = statusInfo.getMessage();
    }

    public String getStatus() {
        return status;
    }

    public String getAreaKey() {
        return areaKey;
    }

    public String getMessage() {
        return message;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }

    public void setAreaKey(String areaKey) {
        this.areaKey = areaKey;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
