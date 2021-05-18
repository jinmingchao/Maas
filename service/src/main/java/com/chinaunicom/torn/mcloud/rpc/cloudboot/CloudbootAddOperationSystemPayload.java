package com.chinaunicom.torn.mcloud.rpc.cloudboot;

import com.alibaba.fastjson.annotation.JSONField;

public class CloudbootAddOperationSystemPayload {

    @JSONField(name = "Name")
    private String name;
    @JSONField(name = "Content")
    private String content;
    @JSONField(name = "AccessToken")
    private String accessToken;

    public String getName() {
        return name;
    }

    public String getContent() {
        return content;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
