package com.chinaunicom.torn.mcloud.service.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.chinaunicom.torn.mcloud.entity.CloudbootAreaEntity;
import com.chinaunicom.torn.mcloud.entity.CloudbootTokenEntity;
import com.chinaunicom.torn.mcloud.entity.LogEntityFactory;
import com.chinaunicom.torn.mcloud.enums.LogHow;
import com.chinaunicom.torn.mcloud.enums.ServiceRole;
import com.chinaunicom.torn.mcloud.service.AuthenticationService;
import com.chinaunicom.torn.mcloud.service.BaremetalService;
import com.chinaunicom.torn.mcloud.service.LoggerService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    private static LogEntityFactory logFactory = new LogEntityFactory(AuthenticationServiceImpl.class);

    private Map<String, CloudbootTokenEntity> cloudbootTokens;

    @Autowired
    private LoggerService logService;

    @Autowired
    private BaremetalService baremetalService;

    public AuthenticationServiceImpl() {
        this.cloudbootTokens = new HashMap<>();
    }

    @Override
    public boolean cloudbootLogin(CloudbootAreaEntity areaEntity) {
        Optional<CloudbootTokenEntity> token = this.baremetalService.loginCloudboot(
                areaEntity.getHost(), areaEntity.getUsername(), areaEntity.getPassword());
        if (!token.isPresent()) {
            this.logService.error(AuthenticationServiceImpl.logFactory.product()
                    .who(ServiceRole.PROMOTER).how(LogHow.CALL).what("Cloudboot Login Failed").why("Cannot got Cloudboot token.").build());
            return false;
        }

        this.logService.info(AuthenticationServiceImpl.logFactory.product()
                .who(ServiceRole.PROMOTER).how(LogHow.STARTUP).what("Cloudboot Login Success").build());

        token.get().setId(areaEntity.getId());
        token.get().setDefaultCloudbootNetworkId(areaEntity.getDefaultCloudbootNetworkId());
        this.cloudbootTokens.put(areaEntity.getId(), token.get());//Key: 每个cloudboot area 区域ID
                                                                  //Value: 每个区域对应的cloudboot area对象, 存map
//        this.cloudbootTokens.forEach( (key, tk) -> {
//
//                System.out.println(key);
//                System.out.println(tk.getToken());
//            System.out.println();
//                }
//        );
        return true;
    }

    public void  updateToken(CloudbootAreaEntity area) {
        CloudbootTokenEntity curToken = this.cloudbootTokens.get(area.getId());
        Optional<CloudbootTokenEntity> curArea = this.baremetalService.loginCloudboot(area.getHost(), area.getUsername(), area.getPassword());
        curArea.ifPresent(cloudbootTokenEntity -> curToken.setToken(cloudbootTokenEntity.getToken()));
    }

    public void deleteToken() {
        this.cloudbootTokens.clear();
    }

    @Override
    public Optional<CloudbootTokenEntity> getCloudbootToken(String key) {
        if (!this.cloudbootTokens.containsKey(key)) {
            return Optional.empty();
        }

        return Optional.of(this.cloudbootTokens.get(key));
    }

    @Override
    public Collection<CloudbootTokenEntity> getAllCloudbootTokens() {
        return this.cloudbootTokens.values();
    }
}
