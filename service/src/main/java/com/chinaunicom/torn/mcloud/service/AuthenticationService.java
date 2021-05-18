package com.chinaunicom.torn.mcloud.service;

import java.util.Collection;
import java.util.Optional;

import com.chinaunicom.torn.mcloud.entity.CloudbootAreaEntity;
import com.chinaunicom.torn.mcloud.entity.CloudbootTokenEntity;

public interface AuthenticationService {

    public boolean cloudbootLogin(CloudbootAreaEntity areaEntity);

    public Optional<CloudbootTokenEntity> getCloudbootToken(String key);

    public Collection<CloudbootTokenEntity> getAllCloudbootTokens();

    public void  updateToken(CloudbootAreaEntity area);

    void deleteToken();
}
