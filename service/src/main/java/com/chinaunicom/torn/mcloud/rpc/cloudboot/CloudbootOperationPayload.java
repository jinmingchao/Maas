package com.chinaunicom.torn.mcloud.rpc.cloudboot;

import com.alibaba.fastjson.annotation.JSONField;

public class CloudbootOperationPayload {

    @JSONField(name = "Sn")
    private String sn;
    @JSONField(name = "AccessToken")
    private String accessToken;
    @JSONField(name = "UserID")
    private Integer userId;
    @JSONField(name = "OobIp")
    private String oobIp;
    @JSONField(name = "Username")
    private String username;
    @JSONField(name = "Password")
    private String password;

    public String getSn() {
        return sn;
    }

    public String getOobIp() {
        return oobIp;
    }

    public Integer getUserId() {
        return userId;
    }

    public String getPassword() {
        return password;
    }

    public String getUsername() {
        return username;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public void setOobIp(String oobIp) {
        this.oobIp = oobIp;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}
