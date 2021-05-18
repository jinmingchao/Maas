package com.chinaunicom.torn.mcloud.rpc.cloudboot;

import com.alibaba.fastjson.annotation.JSONField;

public class CloudbootUpdateOperationSystemPayload {

    @JSONField(name = "ID")
    private Integer id;
    @JSONField(name = "Name")
    private String name;
    @JSONField(name = "Content")
    private String content;
    @JSONField(name = "AccessToken")
    private String accessToken;

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getContent() {
        return content;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setId(Integer id) {
        this.id = id;
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
