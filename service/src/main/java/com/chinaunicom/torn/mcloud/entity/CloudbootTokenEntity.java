package com.chinaunicom.torn.mcloud.entity;

/**
 * CloudbootTokenEntity
 */
public class CloudbootTokenEntity {

    private String id;
    private String host;
    private String token;
    private String cookie;
    private Integer defaultCloudbootNetworkId;

    public String getId() {
        return id;
    }

    public String getHost() {
        return host;
    }

    public String getToken() {
        return token;
    }

    public String getCookie() {
        return cookie;
    }

    public Integer getDefaultCloudbootNetworkId() {
        return defaultCloudbootNetworkId;
    }

    public void setDefaultCloudbootNetworkId(Integer defaultCloudbootNetworkId) {
        this.defaultCloudbootNetworkId = defaultCloudbootNetworkId;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setCookie(String cookie) {
        this.cookie = cookie;
    }
}
