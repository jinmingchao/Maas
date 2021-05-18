package com.chinaunicom.torn.mcloud.rpc.cloudboot;

import com.alibaba.fastjson.annotation.JSONField;

public class CloudbootDeletePayload {

    @JSONField(name = "ID")
    private Integer id;
    @JSONField(name = "AccessToken")
    private String accessToken;
    @JSONField(name = "UserID")
    private Integer userId;

    public Integer getId() {
        return id;
    }

    public Integer getUserId() {
        return userId;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}
