package com.chinaunicom.torn.mcloud.rpc.cloudboot;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * CloudbootAddPXEPayload
 */
public class CloudbootAddPXEPayload {

    @JSONField(name = "Name")
    private String name;
    @JSONField(name = "Pxe")
    private String pxe;
    @JSONField(name = "AccessToken")
    private String accessToken;

    public String getPxe() {
        return pxe;
    }

    public String getName() {
        return name;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPxe(String pxe) {
        this.pxe = pxe;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}
