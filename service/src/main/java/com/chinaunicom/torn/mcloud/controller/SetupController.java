package com.chinaunicom.torn.mcloud.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.chinaunicom.torn.mcloud.entity.CloudbootTokenEntity;
import com.chinaunicom.torn.mcloud.entity.InstanceEntity;
import com.chinaunicom.torn.mcloud.entity.LogEntityFactory;
import com.chinaunicom.torn.mcloud.entity.SetupEntity;
import com.chinaunicom.torn.mcloud.message.DistributeMessage;
import com.chinaunicom.torn.mcloud.message.InstallResultMessage;
import com.chinaunicom.torn.mcloud.rpc.cloudboot.CloudbootResultStatusInfo;
import com.chinaunicom.torn.mcloud.service.AuthenticationService;
import com.chinaunicom.torn.mcloud.service.BaremetalService;
import com.chinaunicom.torn.mcloud.service.InstallService;
import com.chinaunicom.torn.mcloud.service.InstanceService;
import com.chinaunicom.torn.mcloud.service.LoggerService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/setup")
public class SetupController {
    private static LogEntityFactory logFactory = new LogEntityFactory(SetupController.class);

    @Autowired
    private LoggerService loggerService;

    @Autowired
    private InstallService installService;

    @Autowired
    private BaremetalService baremetalService;

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private InstanceService instanceService;
    
    @PostMapping(path = "/distribute")
    public String distribute(@RequestBody DistributeMessage message) throws InterruptedException {
        message.getInstances().forEach(instance -> this.loggerService.operationLog(SetupController.logFactory.product().how("webapi").what("distribute[" + instance.getSn() + "]: /distribute").build()));

        this.installService.createBatch(message.getName(), message.getAreaId(), message.getInstances()
                .stream()
                .filter(instance -> {//java stream ??????list
                    Optional<InstanceEntity> result = this.instanceService.getInstanceDao().findById(instance.getSn());
                    if (!result.isPresent()) {//??????????????????????????????
                        this.loggerService.warn(logFactory.product()
                                .what("distribute ignore sn: " + instance.getSn()).build());
                        return false;
                    }
                    if (result.get().getDistributed()) {//???????????????????????????
                        this.loggerService.warn(logFactory.product()
                                .what("distribute ignore sn: " + instance.getSn() + " because distributed").build());

                    }
                    return !result.get().getDistributed();//??????????????????????????????????????????
                })
                .map(instance -> instance.transferInstallInstanceEntity())//?????????install instance entity
                .collect(Collectors.toList()));//??????list????????????

        return "{}";
    }

    @PostMapping(path = "/install")
    public List<InstallResultMessage> install(@RequestBody List<String> sn) {
        sn.forEach(item -> this.loggerService.operationLog(SetupController.logFactory.product().how("webapi").what("install[" + item + "]: /install").build()));
        //?????????list?????????????sn????????????
        List<InstanceEntity> instances = this.instanceService.getInstanceDao().findAllById(sn);
        if (instances.isEmpty()) {
            return new ArrayList<>();
        }
        String areaId = instances.get(0).getAreaId();
        if (instances.stream().anyMatch(instance -> !instance.getAreaId().equals(areaId))) {//?????????instance???areaId????????????, ??????????????????????????????areaId??????sn?????????,???????????????2??????????????????,????????????
            InstallResultMessage errorMessage = new InstallResultMessage();
            errorMessage.setStatus("failure");
            errorMessage.setMessage("?????????????????????????????????????????????????????????");

            List<InstallResultMessage> result = new ArrayList<>(1);
            result.add(errorMessage);
            return result;
        }

        Optional<CloudbootTokenEntity> token = this.authenticationService.getCloudbootToken(areaId);
        if (!token.isPresent()) {
            InstallResultMessage errorMessage = new InstallResultMessage();
            errorMessage.setStatus("failure");
            errorMessage.setMessage("??????Token?????????");

            List<InstallResultMessage> result = new ArrayList<>(1);
            result.add(errorMessage);
            return result;
        }

        //CloudbootResultStatusInfo status = this.baremetalService.restartFromPXE(instances
                //.stream()
                //.map(instance -> instance.generateCloudbootOperationPayload())
                //.collect(Collectors.toList()), token.get());
        //if (status.getStatus().equals("failure")) {
            //this.loggerService.warn(logFactory.product().what("restart failure").build());

            //List<InstallResultMessage> result = new ArrayList<>(1);
            //result.add(new InstallResultMessage(areaId, status));
            //return result;
        //}

        return this.installService.install(sn);
    }

    @GetMapping(path = "/batch/{AREA_ID}")
    public List<SetupEntity> getAllSetupBatch(@PathVariable(name = "AREA_ID") String areaId) {
        SetupEntity filter = new SetupEntity();
        filter.setAreaId(areaId);

        return this.installService.getSetupDao().findAll(Example.of(filter))
            .stream()
            .sorted((o1, o2) -> o2.getId().compareTo(o1.getId()))
            .collect(Collectors.toList());
    }
}
