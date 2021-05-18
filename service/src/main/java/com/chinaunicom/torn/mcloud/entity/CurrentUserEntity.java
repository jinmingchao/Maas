package com.chinaunicom.torn.mcloud.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class CurrentUserEntity {
    @JsonProperty("user")
    private String user;
    @JsonProperty("roles")
    private List<String> roles;

    public CurrentUserEntity(String user, List<String> roles) {
        this.user = user;
        this.roles = roles;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public String getUser() {
        return user;
    }

    public List<String> getRoles() {
        return roles;
    }
}
