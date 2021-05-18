package com.chinaunicom.torn.mcloud.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.chinaunicom.torn.mcloud.entity.*;
import com.chinaunicom.torn.mcloud.service.*;
import com.chinaunicom.torn.mcloud.dao.CloudbootHardwareDao;
import com.chinaunicom.torn.mcloud.dao.CloudbootHardwareTemplateDao;
import com.chinaunicom.torn.mcloud.dao.CloudbootOperationSystemDao;
import com.chinaunicom.torn.mcloud.dao.CloudbootPXEDao;
import com.chinaunicom.torn.mcloud.enums.LogHow;
import com.chinaunicom.torn.mcloud.enums.ServiceRole;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;

@Service
public class CloudbootInfoServiceImpl implements CloudbootInfoService {

    private static final LogEntityFactory logFactory = new LogEntityFactory(CloudbootInfoServiceImpl.class, ServiceRole.SCHEDULER.name());

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private BaremetalService baremetalService;

    @Autowired
    private CloudbootHardwareDao hardwareDao;

    @Autowired
    private LoggerService loggerService;

    @Autowired
    private CloudbootOperationSystemDao operationSystemDao;

    @Autowired
    private CloudbootPXEDao pxeDao;

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private UserService userService;

    @Autowired
    private CloudbootHardwareTemplateDao hardwareTemplateDao;

    @Override
    public void syncHardware(String areaKey) {

        Optional<CloudbootTokenEntity> token = this.authenticationService.getCloudbootToken(areaKey);
        if (!token.isPresent()) {
            this.loggerService.error(logFactory.product()
                    .how(LogHow.CALL).what(String.format("sync [area: %s] hardware failed", areaKey)).why("Token not exist").build());
            return;
        }

        this.baremetalService.getHardwareInfos(token.get()).forEach(hardware -> {
            CloudbootHardwareEntity filter = new CloudbootHardwareEntity();
            filter.setAreaId(areaKey);
            filter.setCloudbootId(hardware.getId());

            Optional<CloudbootHardwareEntity> entity = this.hardwareDao.findOne(Example.of(filter));
            if (entity.isPresent()) {
                entity.get().transferCloudbootSyncInfo(hardware, areaKey);
                this.hardwareDao.saveAndFlush(entity.get());
            } else {
                CloudbootHardwareEntity newlyEntity = new CloudbootHardwareEntity();
                newlyEntity.transferCloudbootSyncInfo(hardware, areaKey);
                newlyEntity.setEnabled(true);
                this.hardwareDao.saveAndFlush(newlyEntity);
            }
        });
    }

    @Override
    public void syncOperationSystem(String areaKey) {
        Optional<CloudbootTokenEntity> token = this.authenticationService.getCloudbootToken(areaKey);
        if (!token.isPresent()) {
            this.loggerService.error(logFactory.product()
                    .how(LogHow.CALL).what(String.format("sync [area: %s] operation system failed", areaKey)).why("Token not exist").build());
            return;
        }

        this.baremetalService.getSystemInfos(token.get()).forEach(info -> {
            CloudbootOperationSystemEntity filter = new CloudbootOperationSystemEntity();
            filter.setAreaId(areaKey);
            filter.setCloudbootId(info.getId());

            Optional<CloudbootOperationSystemEntity> entity = this.operationSystemDao.findOne(Example.of(filter));
            if (entity.isPresent()) {
                entity.get().transferCloudbootSyncInfo(info, areaKey);
                this.operationSystemDao.saveAndFlush(entity.get());
            } else {
                CloudbootOperationSystemEntity newlyEntity = new CloudbootOperationSystemEntity();
                newlyEntity.transferCloudbootSyncInfo(info, areaKey);
                newlyEntity.setEnabled(true);
                this.operationSystemDao.saveAndFlush(newlyEntity);
            }
        });
    }

    @Override
    public void syncPXE(String areaKey) {
        Optional<CloudbootTokenEntity> token = this.authenticationService.getCloudbootToken(areaKey);
        if (!token.isPresent()) {
            this.loggerService.error(logFactory.product()
                    .how(LogHow.CALL).what(String.format("sync [area: %s] pxe failed", areaKey)).why("Token not exist").build());
            return;
        }

        this.baremetalService.getPxeInfos(token.get()).forEach(info -> {
            CloudbootPXEEntity filter = new CloudbootPXEEntity();
            filter.setAreaId(areaKey);
            filter.setCloudbootId(info.getId());

            Optional<CloudbootPXEEntity> entity = this.pxeDao.findOne(Example.of(filter));
            if (entity.isPresent()) {
                entity.get().transferCloudbootSyncInfo(info, areaKey);
                this.pxeDao.saveAndFlush(entity.get());
            } else {
                CloudbootPXEEntity newlyEntity = new CloudbootPXEEntity();
                newlyEntity.transferCloudbootSyncInfo(info, areaKey);
                newlyEntity.setEnabled(true);
                this.pxeDao.saveAndFlush(newlyEntity);
            }
        });
    }

    @Override
    public List<CloudbootPXEEntity> getAllPXE(String areaKey) {
        CloudbootPXEEntity filter = new CloudbootPXEEntity();
        filter.setAreaId(areaKey);

        return this.pxeDao.findAll(Example.of(filter));
//                .stream()
//                .filter(pxe ->
//                        this.permissionService.checkPermission(
//                                new PolicyEntity(
//                                        this.userService.getCurrentUser(),
//                                        "PXE-" + pxe.getId(),
//                                        "read")))
//                .collect(Collectors.toList());
    }

    @Override
    public List<CloudbootHardwareEntity> getAllHardware(String areaKey) {
        CloudbootHardwareEntity filter = new CloudbootHardwareEntity();
        filter.setAreaId(areaKey);

        return this.hardwareDao.findAll(Example.of(filter));
//                .stream()
//                .filter(hardware ->
//                        this.permissionService.checkPermission(
//                                new PolicyEntity(
//                                        this.userService.getCurrentUser(),
//                                        "HARDWARE-" + hardware.getId(),
//                                        "read")))
//                .collect(Collectors.toList());
    }

    @Override
    public List<CloudbootOperationSystemEntity> getAllOperationSystem(String areaKey) {
        CloudbootOperationSystemEntity filter = new CloudbootOperationSystemEntity();
        filter.setAreaId(areaKey);

        return this.operationSystemDao.findAll(Example.of(filter));
    }

    public CloudbootHardwareDao getHardwareDao() {
        return this.hardwareDao;
    }

    public CloudbootOperationSystemDao getOperationSystemDao() {
        return this.operationSystemDao;
    }

    public CloudbootPXEDao getPXEDao() {
        return this.pxeDao;
    }

    @Override
    public CloudbootHardwareTemplateDao getHardwareTemplateDao() {
        return this.hardwareTemplateDao;
    }
}
