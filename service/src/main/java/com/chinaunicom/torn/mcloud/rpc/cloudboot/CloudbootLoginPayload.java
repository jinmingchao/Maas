package com.chinaunicom.torn.mcloud.rpc.cloudboot;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CloudbootLoginPayload {

    @JsonProperty("Username")
    private String username;
    @JsonProperty("Password")
    private String password;

    public CloudbootLoginPayload(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public String getUsername() {
        return username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
