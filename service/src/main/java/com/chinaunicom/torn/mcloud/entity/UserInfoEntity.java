package com.chinaunicom.torn.mcloud.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;

public class UserInfoEntity {

    @JsonProperty("qq")
    private String qq;
    @JsonProperty("phone")
    private String phone;
    @JSONField(name = "wx_userid")
    private String wechat;
    @JsonProperty("language")
    private String language;
    @JSONField(name = "time_zone")
    private String timeZone;
    @JSONField(name = "bk_username")
    private String username;
    @JsonProperty("email")
    private String email;
    @JSONField(name = "chname")
    private String name;
    @JSONField(name = "bk_role")
    private Integer role;


    public String getQq() {
        return qq;
    }

    public void setQq(String qq) {
        this.qq = qq;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getWechat() {
        return wechat;
    }

    public void setWechat(String wechat) {
        this.wechat = wechat;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getRole() {
        return role;
    }

    public void setRole(Integer role) {
        this.role = role;
    }
}
