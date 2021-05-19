package com.chinaunicom.torn.mcloud.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
//@ConfigurationProperties
public class LoginRedirectConfig {
    @Value("${spring.bk-login.api}")
    private String api;

    @Value("${spring.bk-login.redirect}")
    private String bkLoginRedirect;

    @Value("${spring.bk-login.cookie}")
    private String cookieKey;

    @Value("${spring.bk-login.dev}")
    private Boolean dev;

    @Value("${spring.bk-login.dev-username}")
    private String devUsername;

    @Value("${spring.profiles.active}")
    private String active;

    public String getBkLoginRedirect() {
        return this.bkLoginRedirect;
    }

    public String getCookieKey() {
        return cookieKey;
    }

    public String getApi() {
        return api;
    }

    public Boolean getDev() {
        return dev;
    }

    public String getDevUsername() {
        return devUsername;
    }

    public String getActive() {
        return active;
    }
}

