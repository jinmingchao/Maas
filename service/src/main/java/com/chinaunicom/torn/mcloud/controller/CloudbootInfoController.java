package com.chinaunicom.torn.mcloud.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.chinaunicom.torn.mcloud.entity.*;
import com.chinaunicom.torn.mcloud.message.CloudbootInfoMessage;
import com.chinaunicom.torn.mcloud.rpc.cloudboot.CloudbootAddHardwarePayload;
import com.chinaunicom.torn.mcloud.rpc.cloudboot.CloudbootAddOperationSystemPayload;
import com.chinaunicom.torn.mcloud.rpc.cloudboot.CloudbootAddPXEPayload;
import com.chinaunicom.torn.mcloud.rpc.cloudboot.CloudbootResultStatusInfo;
import com.chinaunicom.torn.mcloud.rpc.cloudboot.CloudbootUpdateHardwarePayload;
import com.chinaunicom.torn.mcloud.rpc.cloudboot.CloudbootUpdateOperationSystemPayload;
import com.chinaunicom.torn.mcloud.rpc.cloudboot.CloudbootUpdatePXEPayload;
import com.chinaunicom.torn.mcloud.service.AreaZoneService;
import com.chinaunicom.torn.mcloud.service.AuthenticationService;
import com.chinaunicom.torn.mcloud.service.BaremetalService;
import com.chinaunicom.torn.mcloud.service.CloudbootInfoService;
import com.chinaunicom.torn.mcloud.service.LoggerService;
import com.chinaunicom.torn.mcloud.service.PermissionService;
import com.chinaunicom.torn.mcloud.service.ProjectService;

import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/info")
public class CloudbootInfoController {
    private static LogEntityFactory logFactory = new LogEntityFactory(CloudbootInfoController.class);

    @Autowired
    private LoggerService loggerService;

    public class TemplateMessage {
        private Integer id;
        private Integer maasId;
        private String name;
        private String optionalName;
        private String payload;
        private Boolean enabled;

        public TemplateMessage(CloudbootHardwareEntity entity) {
            this.id = entity.getCloudbootId();
            this.maasId = entity.getId();
            this.name = entity.getShowName();
            this.optionalName = entity.getCompany();
            this.payload = entity.getData();
            this.enabled = entity.getEnabled();
        }

        public TemplateMessage(CloudbootPXEEntity entity) {
            this.id = entity.getCloudbootId();
            this.maasId = entity.getId();
            this.name = entity.getName();
            this.payload = entity.getPxe();
            this.enabled = entity.getEnabled();
        }

        public TemplateMessage(CloudbootOperationSystemEntity entity) {
            this.id = entity.getCloudbootId();
            this.maasId = entity.getId();
            this.name = entity.getName();
            this.payload = entity.getContent();
            this.enabled = entity.getEnabled();
        }

        public Integer getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getPayload() {
            return payload;
        }

        public String getOptionalName() {
            return optionalName;
        }

        public Boolean getEnabled() {
            return enabled;
        }

        public Integer getMaasId() {
            return maasId;
        }
    }

    public class SimpleAreaMessage {

        private String id;
        private String name;

        public SimpleAreaMessage(CloudbootAreaEntity entity) {
            this.id = entity.getId();
            this.name = entity.getName();
        }

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }
    }

    public class SimpleHardwareMessage {
        private Integer id;
        private Integer maasId;
        private String name;
        private String optionalName;
        private String payload;
        private Boolean enabled;

        public List<ProjectEntity> projects;

        public SimpleHardwareMessage(CloudbootHardwareEntity entity) {
            this.maasId = entity.getId();
            this.id = entity.getCloudbootId();
            this.name = entity.getShowName();
            this.optionalName = entity.getCompany();
            this.payload = entity.getData();
            this.enabled = entity.getEnabled();

            this.projects = new ArrayList<>(0);
        }

        public Integer getId() {
            return id;
        }

        public Integer getMaasId() {
            return maasId;
        }

        public String getName() {
            return name;
        }

        public String getPayload() {
            return payload;
        }

        public String getOptionalName() {
            return optionalName;
        }

        public List<ProjectEntity> getProjects() {
            return projects;
        }

        public void setProjects(List<ProjectEntity> projects) {
            this.projects = projects;
        }

        public Boolean getEnabled() {
            return enabled;
        }
    }

    @Autowired
    private CloudbootInfoService infoService;

    @Autowired
    private AreaZoneService areaZoneService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private BaremetalService baremetalService;

    @ApiOperation(value = "获取某区域硬件模板信息(RAID+OOB)* ")
    @GetMapping(path = "/{AREA_ID}/hardware")
    public List<SimpleHardwareMessage> getAreaHardware(@PathVariable(name = "AREA_ID") String areaKey) {
        return this.infoService.getAllHardware(areaKey)
                .stream()
                .map(hardware -> {
                    SimpleHardwareMessage message = new SimpleHardwareMessage(hardware);

                    ProjectCloudbootHardwareEntity filter = new ProjectCloudbootHardwareEntity();
                    filter.setHardwareId(hardware.getId());

                    Set<Integer> id = this.projectService.getProjectCloudbootHardwareDao().findAll(Example.of(filter))
                            .stream()
                            .mapToInt(ref -> ref.getProjectId())
                            .boxed()
                            .collect(Collectors.toSet());
                    message.setProjects(this.projectService.getProjectDao().findAllById(id));

                    return message;
                })
                .collect(Collectors.toList());
    }

    @ApiOperation(value = "改变某硬件模板可使用状态* ")
    @PutMapping(path = "/hardware/{HID}/switch/{ENABLED}")
    public String modifyHardware(@PathVariable(name = "HID") Integer hardwareId, @PathVariable(name = "ENABLED") Boolean enabled) {
        Optional<CloudbootHardwareEntity> entity = this.infoService.getHardwareDao().findById(hardwareId);
        if (!entity.isPresent()) {
            return "{}";
        }
        entity.get().setEnabled(enabled);
        this.infoService.getHardwareDao().saveAndFlush(entity.get());

        return "{}";
    }

    @ApiOperation(value = "添加PXE模板*CB交互 ")
    @PostMapping(path = "/{AREA_ID}/pxe")
    public String addPxe(@PathVariable(name = "AREA_ID") String areaKey, @RequestBody CloudbootInfoMessage msg) {
        Optional<CloudbootTokenEntity> token = this.authenticationService.getCloudbootToken(areaKey);
        if (!token.isPresent()) {
            return "{}";
        }

        CloudbootAddPXEPayload payload = new CloudbootAddPXEPayload();
        payload.setName(msg.getName());
        payload.setPxe(msg.getContent());

        this.baremetalService.addPxe(payload, token.get());

        return "{}";
    }

    @ApiOperation(value = "修改PXE模板*CB交互 ")
    @PutMapping(path = "/{AREA_ID}/pxe/{ID}")
    public String updatePxe(@PathVariable(name = "AREA_ID") String areaKey, @PathVariable(name = "ID") Integer id, @RequestBody CloudbootInfoMessage msg) {
        Optional<CloudbootTokenEntity> token = this.authenticationService.getCloudbootToken(areaKey);
        if (!token.isPresent()) {
            return "{}";
        }

        CloudbootUpdatePXEPayload payload = new CloudbootUpdatePXEPayload();
        payload.setId(id);
        payload.setName(msg.getName());
        payload.setPxe(msg.getContent());

        this.baremetalService.updatePxe(payload, token.get());

        return "{}";
    }

    @ApiOperation(value = "获得某区域所有pxe模板列表* ")
    @GetMapping(path = "/{AREA_ID}/pxe")
    public List<TemplateMessage> getAreaPXE(@PathVariable(name = "AREA_ID") String areaKey) {
        return this.infoService.getAllPXE(areaKey)
                .stream()
                .map(pxe -> new TemplateMessage(pxe))
                .collect(Collectors.toList());
    }

    @ApiOperation(value = "修改PXE文件可使用状态* ")
    @PutMapping(path = "/pxe/{PID}/switch/{ENABLED}")
    public String modifyPXE(@PathVariable(name = "PID") Integer pxeId, @PathVariable(name = "ENABLED") Boolean enabled) {
        Optional<CloudbootPXEEntity> entity = this.infoService.getPXEDao().findById(pxeId);
        if (!entity.isPresent()) {
            return "{}";
        }
        entity.get().setEnabled(enabled);
        this.infoService.getPXEDao().saveAndFlush(entity.get());

        return "{}";
    }

    @ApiOperation(value = "添加操作系统模板*CB交互 ")
    @PostMapping(path = "/{AREA_ID}/operation_system")
    public String addOperationSystem(@PathVariable(name = "AREA_ID") String areaKey, @RequestBody CloudbootInfoMessage msg) {
        Optional<CloudbootTokenEntity> token = this.authenticationService.getCloudbootToken(areaKey);
        if (!token.isPresent()) {
            return "{}";
        }

        CloudbootAddOperationSystemPayload payload = new CloudbootAddOperationSystemPayload();
        payload.setName(msg.getName());
        payload.setContent(msg.getContent());

        this.baremetalService.addOperationSystem(payload, token.get());

        return "{}";
    }

    @ApiOperation(value = "修改操作系统模板*CB交互 ")
    @PutMapping(path = "/{AREA_ID}/operation_system/{ID}")
    public String addOperationSystem(@PathVariable(name = "AREA_ID") String areaKey, @PathVariable(name = "ID") Integer id, @RequestBody CloudbootInfoMessage msg) {
        Optional<CloudbootTokenEntity> token = this.authenticationService.getCloudbootToken(areaKey);
        if (!token.isPresent()) {
            return "{}";
        }

        CloudbootUpdateOperationSystemPayload payload = new CloudbootUpdateOperationSystemPayload();
        payload.setId(id);
        payload.setName(msg.getName());
        payload.setContent(msg.getContent());

        this.baremetalService.updateOperationSystem(payload, token.get());

        return "{}";
    }

    @ApiOperation(value = "获取操作系统模板* ")
    @GetMapping(path = "/{AREA_ID}/operation_system")
    public List<TemplateMessage> getAreaOperationSystem(@PathVariable(name = "AREA_ID") String areaKey) {
        return this.infoService.getAllOperationSystem(areaKey)
                .stream()
                .map(operationSystem -> new TemplateMessage(operationSystem))
                .collect(Collectors.toList());
    }

    @ApiOperation(value = "修改操作系统模板可用状态* ")
    @PutMapping(path = "/operation_system/{OSID}/switch/{ENABLED}")
    public String modifyOpertionSystem(@PathVariable(name = "OSID") Integer osId, @PathVariable(name = "ENABLED") Boolean enabled) {
        Optional<CloudbootOperationSystemEntity> entity = this.infoService.getOperationSystemDao().findById(osId);
        if (!entity.isPresent()) {
            return "{}";
        }
        entity.get().setEnabled(enabled);
        this.infoService.getOperationSystemDao().saveAndFlush(entity.get());

        return "{}";
    }

    @ApiOperation(value = "修改操作系统模板可用状态* ")
    @GetMapping(path = "/area")
    public List<SimpleAreaMessage> getAllArea() {
        String curUser = SecurityContextHolder.getContext().getAuthentication().getName();
        return this.areaZoneService.getAllArea()
                .stream()
                .map(area -> new SimpleAreaMessage(area))
                .collect(Collectors.toList());
    }

    @ApiOperation(value = "同步某区域的pxe,硬件信息,操作系统到本地数据库*CB交互 ")
    @PutMapping(path = "/{AREA_ID}/sync")
    public String syncInfo(@PathVariable(name = "AREA_ID") String areaKey) {
        this.loggerService.operationLog(CloudbootInfoController.logFactory.product().how("webapi").what("/api/area/post-area").build());

        this.infoService.syncPXE(areaKey);
        this.infoService.syncHardware(areaKey);
        this.infoService.syncOperationSystem(areaKey);

        return "{}";
    }

    @ApiOperation(value = "获得所有硬件模板* ")
    @GetMapping(path = "/hardware_template")
    public List<CloudbootHardwareTemplateEntity> getHardwareTemplate() {
        return this.infoService.getHardwareTemplateDao().findAll();
    }

    @ApiOperation(value = "添加典配模板* ")
    @PostMapping(path = "/hardware_template")
    public String postHardwareTemplate(@RequestBody CloudbootHardwareTemplateEntity entity) {
        this.infoService.getHardwareTemplateDao().saveAndFlush(entity);
        return "{}";
    }

    @ApiOperation(value = "删除典配模板* ")
    @DeleteMapping(path = "/hardware_template/{ID}")
    public String deleteHardwareTemplate(@PathVariable(name = "ID") Integer id) {
        this.infoService.getHardwareTemplateDao().deleteById(id);
        return "{}";
    }

    @ApiOperation(value = "克隆硬件模板* ")
    @PostMapping(path = "/{AREA_ID}/hardware")
    public CloudbootResultStatusInfo addHardware(@PathVariable(name = "AREA_ID") String areaKey, @RequestBody CloudbootAddHardwarePayload payload) {
        Optional<CloudbootTokenEntity> token = this.authenticationService.getCloudbootToken(areaKey);
        if (!token.isPresent()) {
            CloudbootResultStatusInfo info = new CloudbootResultStatusInfo();
            info.setStatus("操作失败");
            info.setMessage("丢失Token");
            return info;
        }

        payload.setStatus("Success");
        return this.baremetalService.addHardware(payload, token.get());
    }

    @ApiOperation(value = "根据ID获取典配模板* ")
    @GetMapping(path = "/hardware/{ID}/template")
    public CloudbootHardwareTemplateEntity getHardwareTpl(@PathVariable(name = "ID") Integer id) {
        Optional<CloudbootHardwareEntity> hardware = this.infoService.getHardwareDao().findById(id);
        CloudbootHardwareTemplateEntity template = new CloudbootHardwareTemplateEntity();
        if (hardware.isPresent()) {
            template.setId(hardware.get().getId());
            template.setName(hardware.get().getModelName());
            template.setCompany(hardware.get().getCompany());
            template.setTpl(hardware.get().getTpl());
        }

        return template;
    }

    @ApiOperation(value = "根据硬件信息ID修改硬件信息* ")
    @PutMapping(path = "/hardware/{ID}")
    public CloudbootResultStatusInfo updateHardware(@PathVariable(name = "ID") Integer id, @RequestBody CloudbootAddHardwarePayload payload) {
        Optional<CloudbootHardwareEntity> hardware = this.infoService.getHardwareDao().findById(id);
        if (!hardware.isPresent()) {
            CloudbootResultStatusInfo info = new CloudbootResultStatusInfo();
            info.setStatus("操作失败");
            info.setMessage("丢失Hardware");
            return info;
        }
        Optional<CloudbootTokenEntity> token = this.authenticationService.getCloudbootToken(hardware.get().getAreaId());
        if (!token.isPresent()) {
            CloudbootResultStatusInfo info = new CloudbootResultStatusInfo();
            info.setStatus("操作失败");
            info.setMessage("丢失Token");
            return info;
        }

        CloudbootUpdateHardwarePayload updatePayload = new CloudbootUpdateHardwarePayload(); 
        updatePayload.setId(hardware.get().getCloudbootId());
        updatePayload.setTpl(hardware.get().getTpl());
        updatePayload.setData(payload.getData());
        updatePayload.setCompany(payload.getCompany());
        updatePayload.setModelName(payload.getModelName());
        updatePayload.setStatus("Success");
        return this.baremetalService.updateHardware(updatePayload, token.get());
    }
}
