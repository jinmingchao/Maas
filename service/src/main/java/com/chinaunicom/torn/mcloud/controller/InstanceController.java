package com.chinaunicom.torn.mcloud.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chinaunicom.torn.mcloud.dao.InstanceInstallOpLogDao;
import com.chinaunicom.torn.mcloud.entity.CloudbootTokenEntity;
import com.chinaunicom.torn.mcloud.entity.HistorySetupInstanceEntity;
import com.chinaunicom.torn.mcloud.entity.InstallInstanceEntity;
import com.chinaunicom.torn.mcloud.entity.InstanceEntity;
import com.chinaunicom.torn.mcloud.entity.InstanceInstallOpLogEntity;
import com.chinaunicom.torn.mcloud.entity.LogEntityFactory;
import com.chinaunicom.torn.mcloud.enums.InstanceInstallOpType;
import com.chinaunicom.torn.mcloud.enums.ManageInstanceField;
import com.chinaunicom.torn.mcloud.message.InstanceStatRequestMessage;
import com.chinaunicom.torn.mcloud.message.ModifyInstanceNetInfoMessage;
import com.chinaunicom.torn.mcloud.message.ModifyOobMessage;
import com.chinaunicom.torn.mcloud.rpc.cloudboot.CloudbootOperationPayload;
import com.chinaunicom.torn.mcloud.rpc.cloudboot.CloudbootResultStatusInfo;
import com.chinaunicom.torn.mcloud.service.AreaZoneService;
import com.chinaunicom.torn.mcloud.service.AuthenticationService;
import com.chinaunicom.torn.mcloud.service.BaremetalService;
import com.chinaunicom.torn.mcloud.service.CloudbootInfoService;
import com.chinaunicom.torn.mcloud.service.HistoryService;
import com.chinaunicom.torn.mcloud.service.InstallService;
import com.chinaunicom.torn.mcloud.service.InstallingService;
import com.chinaunicom.torn.mcloud.service.InstanceService;
import com.chinaunicom.torn.mcloud.service.LoggerService;
import com.chinaunicom.torn.mcloud.service.ProjectService;
import com.chinaunicom.torn.mcloud.service.UserService;

import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 */
@RestController
@RequestMapping(path = "/api/instance")
public class InstanceController {
    private static LogEntityFactory logFactory = new LogEntityFactory(InstanceController.class);

    @Autowired
    private LoggerService loggerService;

    public class InstanceMessage {

        public class InstanceTagMessage {
            private String color;
            private String content;

            public InstanceTagMessage(String color, String content) {
                this.color = color;
                this.content = content;
            }

            public String getColor() {
                return color;
            }

            public String getContent() {
                return content;
            }
        }

        private String sn;
        private Date lastModifyAt;
        private List<InstanceTagMessage> tags;
        private Integer setupId;
        private String company;
        private String modelName;
        private Integer cpuCount;
        private Integer diskCapacitySum;
        private Integer memoryCapacitySum;
        private String dhcpIp;
        private String innerIp;
        private String netmask;
        private String oobIp;
        private Integer projectId;
        private Integer hardwareId;
        private Integer pxeId;
        private Integer operationSystemId;
        private Integer netAreaId;
        private Boolean distributeable;
        private Boolean installable;
        private Boolean managed;
        private Boolean distributed;

        private String pxeName;
        private String operationSystemName;
        private String hardwareName;
        private String projectName;

        private String hostname;
        private Integer ippool;
        private String boundMac1;
        private String boundMac2;
        private String boundType;

        private String oobUsername;
        private String nic;

        public InstanceMessage(InstanceEntity entity, InstallingService installingService) {
            this.tags = new LinkedList<>();
            this.sn = entity.getSn();
            this.lastModifyAt = entity.getLastModifyAt();
            this.setupId = entity.getSetupId();
            this.company = entity.getCompany();
            this.modelName = entity.getModelName();
            this.cpuCount = entity.getCpuCoreCount();
            this.diskCapacitySum = entity.getDiskCapacitySum();
            this.memoryCapacitySum = entity.getMemoryCapacitySum();
            this.dhcpIp = entity.getDhcpIp();
            this.innerIp = entity.getInnerIp();
            this.netmask = entity.getNetmask();
            this.oobIp = entity.getOobIp();
            this.projectId = entity.getProjectId();
            this.hardwareId = entity.getHardwareId();
            this.pxeId = entity.getPxeId();
            this.operationSystemId = entity.getSystemId();
            this.netAreaId = entity.getNetAreaId();
            this.distributeable = entity.getManaged() && entity.getDiscovery() && !entity.getDistributed();
            this.installable = entity.getInstallable();

            this.hostname = entity.getHostname();
            this.ippool = entity.getIppool();
            this.boundMac1 = entity.getBoundMac1();
            this.boundMac2 = entity.getBoundMac2();
            this.boundType = entity.getBoundType();

            this.oobUsername = entity.getOobUsername();
            this.nic = entity.getNic();

            this.managed = entity.getManaged();
            this.distributed = entity.getDistributed();

            if (entity.getInstallable()) {
                if (entity.getStatus() != null && (entity.getStatus().equals("可编辑") || entity.getStatus().equals("装机成功") || entity.getStatus().equals("装机失败"))) {
                    this.tags.add(new InstanceTagMessage("geekblue", entity.getStatus()));
                }
                else {
                    this.tags.add(new InstanceTagMessage("volcano", "装机核验通过"));
                }
            }
            else if (entity.getStatus() != null) {
                this.tags.add(new InstanceTagMessage("geekblue", entity.getStatus()));
            }


            if (entity.getDistributed()) {
                if (installingService == null || installingService.exist(entity)) {
                    this.tags.add(new InstanceTagMessage("geekblue", "安装中"));
                }
                else if (!entity.getInstalled()) {
                    this.tags.add(new InstanceTagMessage("geekblue", "待装机"));
                }
                else {
                    this.tags.add(new InstanceTagMessage("geekblue", "已装机"));
                }
            }
            else if (entity.getManaged() && entity.getDiscovery()) {
                this.tags.add(new InstanceTagMessage("green", "可分配"));
            }
            else {
                if (!entity.getDiscovery()) {
                    this.tags.add(new InstanceTagMessage("cyan", "未发现"));
                }

                if (!entity.getManaged()) {
                    this.tags.add(new InstanceTagMessage("cyan", "未纳管"));
                }
            }
        }

        public String getSn() {
            return sn;
        }

        public String getOobIp() {
            return oobIp;
        }

        public String getDhcpIp() {
            return dhcpIp;
        }

        public List<InstanceTagMessage> getTags() {
            return tags;
        }

        public String getCompany() {
            return company;
        }

        public String getInnerIp() {
            return innerIp;
        }

        public Integer getPxeId() {
            return pxeId;
        }

        public Integer getOperationSystemId() {
            return operationSystemId;
        }

        public String getNetmask() {
            return netmask;
        }

        public Integer getSetupId() {
            return setupId;
        }

        public Integer getCpuCount() {
            return cpuCount;
        }

        public String getModelName() {
            return modelName;
        }

        public Integer getHardwareId() {
            return hardwareId;
        }

        public Integer getDiskCapacitySum() {
            return diskCapacitySum;
        }

        public Integer getMemoryCapacitySum() {
            return memoryCapacitySum;
        }

        public Integer getNetAreaId() {
            return netAreaId;
        }

        public Date getLastModifyAt() {
            return lastModifyAt;
        }

        public Boolean getDistributeable() {
            return distributeable;
        }

        public Boolean getInstallable() {
            return installable;
        }

        public Boolean getManaged() {
            return managed;
        }

        public Boolean getDistributed() {
            return distributed;
        }

        public String getPxeName() {
            return pxeName;
        }

        public String getHardwareName() {
            return hardwareName;
        }

        public String getOperationSystemName() {
            return operationSystemName;
        }

        public Integer getIppool() {
            return ippool;
        }

        public String getHostname() {
            return hostname;
        }

        public String getBoundMac1() {
            return boundMac1;
        }

        public String getBoundMac2() {
            return boundMac2;
        }

        public String getBoundType() {
            return boundType;
        }

        public Integer getProjectId() {
            return projectId;
        }

        public String getNic() {
            return nic;
        }

        public String getOobUsername() {
            return oobUsername;
        }

        public String getProjectName() {
            return projectName;
        }

        public void setProjectName(String projectName) {
            this.projectName = projectName;
        }

        public void setPxeName(String pxeName) {
            this.pxeName = pxeName;
        }

        public void setHardwareName(String hardwareName) {
            this.hardwareName = hardwareName;
        }

        public void setOperationSystemName(String operationSystemName) {
            this.operationSystemName = operationSystemName;
        }
    }

    public class ManagedResultMessage {
        private String sn;
        private Boolean status;

        public ManagedResultMessage(String sn, Boolean status) {
            this.sn = sn;
            this.status = status;
        }

        public String getSn() {
            return sn;
        }

        public Boolean getStatus() {
            return status;
        }
    }

    public class InstanceStatMessage {
        private String sn;
        private String area;
        private String project;
        private String hardware;
        private String pxe;
        private String operationSystem;
        private Boolean managed;
        private Boolean distributed;
        private Boolean installed;
        private Integer successCount;
        private Integer failedCount;

        public InstanceStatMessage(InstanceEntity entity, String area, String project, String hardware, String pxe, String operationSystem) {
            this.sn = entity.getSn();
            this.area = area;
            this.project = project;
            this.hardware = hardware;
            this.pxe = pxe;
            this.operationSystem = operationSystem;
            this.managed = entity.getManaged();
            this.distributed = entity.getDistributed();
            this.installed = entity.getInstalled();
            this.successCount = 0;
            this.failedCount = 0;
        }

        public String getSn() {
            return sn;
        }

        public String getArea() {
            return area;
        }

        public String getPxe() {
            return pxe;
        }

        public String getProject() {
            return project;
        }

        public String getHardware() {
            return hardware;
        }

        public Boolean getManaged() {
            return managed;
        }

        public Boolean getInstalled() {
            return installed;
        }

        public Boolean getDistributed() {
            return distributed;
        }

        public Integer getFailedCount() {
            return failedCount;
        }

        public Integer getSuccessCount() {
            return successCount;
        }

        public String getOperationSystem() {
            return operationSystem;
        }

        public void setSuccessCount(Integer successCount) {
            this.successCount = successCount;
        }

        public void setFailedCount(Integer failedCount) {
            this.failedCount = failedCount;
        }
    }

    @Autowired
    private InstanceService instanceService;

    @Autowired
    private CloudbootInfoService infoService;

    @Autowired
    private InstallService installService;

    @Autowired
    private InstallingService installingService;

    @Autowired
    private BaremetalService baremetalService;

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private AreaZoneService poolService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private HistoryService historyService;

    @Autowired
    private UserService userService;

    @Autowired
    private InstanceInstallOpLogDao instanceInstallOpLogDao;


    @GetMapping(path = "/{AREA_ID}")
    @ApiOperation(value = "获取某区域所有主机列表/获取某区域某装机批次的主机列表/查看某区域装机中列表 ")
    public List<InstanceMessage> getInstances(
            @PathVariable(name = "AREA_ID") String areaKey,
            @RequestParam(name = "batchId", required = false) Integer batchId,
            @RequestParam(name = "installing", required = false) Boolean installing) {
        InstanceEntity filter = InstanceEntity.factoryBuilder(areaKey);

        if (installing != null) { // 查看某区域装机中列表: http://localhost:8000/api/instance/888?installing=true
            List<InstanceEntity> installingInstance = this.installingService.getInstallingInstance(areaKey);
            filter.setStatus("安装失败");//过滤出安装失败的机器
            installingInstance.addAll(this.instanceService.getInstances(Example.of(filter)));//安装失败的放在正在安装的后面

            return this.transferInstanceEntityToMessage(areaKey, installingInstance.stream(), true);
        }

        Set<String> batchSn = null;
        if (batchId != null) {// 获取某装机批次的主机列表: http://localhost:8000/api/instance/888?batchId=8
            batchSn = this.installService.getBatchSNSet(batchId);
        }

        final Set<String> refBatchSn = batchSn;
        return this.transferInstanceEntityToMessage(areaKey, this.instanceService.getInstances(Example.of(filter))
                .stream()
                .filter(instance -> {//如果入参有batchId,则返回某批次下所有主机列表,否则返回该区域所有主机列表
                    if (refBatchSn == null) {
                        return true;
                    }
                    return refBatchSn.contains(instance.getSn());
                }), false);
    }

    @PostMapping(path = "/{AREA_ID}/sync")
    @ApiOperation(value = "手动触发同步某区域下所有主机")
    public String syncInstances(@PathVariable(name = "AREA_ID") String areaId) {
        this.loggerService.operationLog(InstanceController.logFactory.product().how("webapi").what("/api/instance/" + areaId + "/sync").build());

        this.instanceService.syncDiscoveryInstance(areaId);
        return "{}";
    }

    @GetMapping(path = "/{SN}/history")
    public List<HistorySetupInstanceEntity> getHistory(@PathVariable(name = "SN") String sn) {
        return this.instanceService.singleInstanceHistory(sn);
    }

    @GetMapping(path = "/{SN}/install_history")
    @ApiOperation(value = "查看某主机装机历史")
    public List<InstanceInstallOpLogEntity> getInstallHistory(@PathVariable(name = "SN") String sn) {
        InstanceInstallOpLogEntity filter = new InstanceInstallOpLogEntity();
        filter.setSn(sn);

        return this.instanceInstallOpLogDao.findAll(Example.of(filter));
    }

    @PutMapping(path = "/{AREA_ID}/recovery_reset")
    public String recoveryReset(@PathVariable(name = "AREA_ID") String areaKey, @RequestBody List<String> snList) {
        snList.forEach(sn -> {
            InstanceInstallOpLogEntity filter = new InstanceInstallOpLogEntity();
            filter.setSn(sn);

            Optional<InstanceEntity> instance = this.instanceService.getInstanceDao().findById(sn);
            if (!instance.isPresent()) {
                return;
            }

            this.instanceInstallOpLogDao.findAll(Example.of(filter)).stream()
                .sorted((a, b) -> a.getId() - b.getId())
                .forEach(history -> {
                    boolean updated = false;
                    if (history.getOpType().equals(InstanceInstallOpType.INSTALL_SUCCESS.toString())) {
                        updated = true;
                        instance.get().setStatus("装机成功");
                    }
                    if (history.getOpType().equals(InstanceInstallOpType.INSTALL_FAILURE.toString())) {
                        updated = true;
                        instance.get().setStatus("装机失败");
                    }
                    if (updated) {
                        instance.get().setPxeId(history.getPxeId());
                        instance.get().setHardwareId(history.getHardwareId());
                        instance.get().setSystemId(history.getSystemId());
                    }
                });

            this.instanceService.getInstanceDao().saveAndFlush(instance.get());
        });

        return "{}";
    }

    @GetMapping(path = "/{SN}/detail")
    @ApiOperation(value = "获取某主机的详细信息")
    public InstanceEntity detail(@PathVariable(name = "SN") String sn) {
        Optional<InstanceEntity> instance = this.instanceService.getInstanceDao().findById(sn);
        if (instance.isPresent()) {
            instance.get().setOobPassword("");
            return instance.get();
        }
        return new InstanceEntity();
    }
    
    private List<InstanceMessage> transferInstanceEntityToMessage(String areaKey, Stream<InstanceEntity> stream, Boolean installing) {
        Map<Integer, String> pxeMapper = this.infoService.getAllPXE(areaKey)
            .stream()
            .reduce(new HashMap<Integer, String>(), (prev, curr) -> {
                prev.put(curr.getId(), curr.getName());
                return prev;
            }, (a, b) -> null);
        Map<Integer, String> osMapper = this.infoService.getAllOperationSystem(areaKey)
            .stream()
            .reduce(new HashMap<Integer, String>(), (prev, curr) -> {
                prev.put(curr.getId(), curr.getName());
                return prev;
            }, (a, b) -> null);
        Map<Integer, String> hardwareMapper = this.infoService.getAllHardware(areaKey)
            .stream()
            .reduce(new HashMap<Integer, String>(), (prev, curr) -> {
                prev.put(curr.getId(), curr.getShowName());
                return prev;
            }, (a, b) -> null);

        Map<Integer, String> projectMapper = this.projectService.getProjectDao().findAll()
            .stream()
            .reduce(new HashMap<Integer, String>(), (prev, curr) -> {
                prev.put(curr.getId(), curr.getName());
                return prev;
            }, (a, b) -> null);

        // 权限
        String username = this.userService.getCurrentUser();
        Set<Integer> projectId = this.userService.getUserProject(username).stream().mapToInt(e -> e.getId()).boxed().collect(Collectors.toSet());

        return stream
            .filter(instance -> username.equals("admin") || instance.getProjectId() == null || projectId.contains(instance.getProjectId()))
            .map(instance -> {
                InstanceMessage msg = new InstanceMessage(instance, installing ? null : this.installingService);

                msg.setHardwareName(hardwareMapper.get(msg.getHardwareId()));
                msg.setPxeName(pxeMapper.get(msg.getPxeId()));
                msg.setOperationSystemName(osMapper.get(msg.getOperationSystemId()));
                msg.setProjectName(projectMapper.get(msg.getProjectId()));

                return msg;
            })
            .sorted((o1, o2) -> {
                Long ts1 = 0L;
                Long ts2 = 0L;

                if (o1.getLastModifyAt() != null) {
                    ts1 = o1.getLastModifyAt().getTime();
                }
                if (o2.getLastModifyAt() != null) {
                    ts2 = o2.getLastModifyAt().getTime();
                }

                return ts2.compareTo(ts1);
            })
            .collect(Collectors.toList());
    }

    @PostMapping(path = "/{AREA_ID}")
    public List<ManagedResultMessage> manageInstances(@PathVariable(name = "AREA_ID") String areaKey, @RequestBody JSONArray array) {
        this.loggerService.operationLog(InstanceController.logFactory.product().how("webapi").what("managed instances: /api/instance/" + areaKey).build());

        List<ManagedResultMessage> result = new LinkedList<>();

        for (int i = 0; i < array.size(); i++) {
            JSONObject obj = array.getJSONObject(i);

            if (!obj.containsKey(ManageInstanceField.SN.getField())) {
                continue;
            }

            String sn = obj.getString(ManageInstanceField.SN.getField());

            result.add(new ManagedResultMessage(sn, this.instanceService.manageInstance(sn, areaKey, obj)));
        }

        return result;
    }

    @PutMapping(path = "/{SN}/hardware/{HARDWARE_ID}")
    public String modifyHardwareId(@PathVariable(name = "SN") String sn, @PathVariable(name = "HARDWARE_ID") Integer hardwareId) {
        this.loggerService.operationLog(InstanceController.logFactory.product().how("webapi").what("modify hardware: /api/instance/" + sn + "/hardware/" + hardwareId).build());

        Optional<InstanceEntity> instance = this.instanceService.getInstanceDao().findById(sn);
        if (!instance.isPresent()) {
            return "{}";
        }
        if (instance.get().getHardwareId() == null) {
            return "{}";
        }

        instance.get().setHardwareId(hardwareId);
        this.instanceService.getInstanceDao().saveAndFlush(instance.get());

        instanceInstallOpLogDao.saveAndFlush(instance.get().generateOpLog(this.userService.getCurrentUser(), InstanceInstallOpType.UPDATE));

        return "{}";
    }

    @PutMapping(path = "/{SN}/pxe/{PXE_ID}")
    public String modifyPXEId(@PathVariable(name = "SN") String sn, @PathVariable(name = "PXE_ID") Integer pxeId) {
        this.loggerService.operationLog(InstanceController.logFactory.product().how("webapi").what("modify pxe: /api/instance/" + sn + "/hardware/" + pxeId).build());

        Optional<InstanceEntity> instance = this.instanceService.getInstanceDao().findById(sn);
        if (!instance.isPresent()) {
            return "{}";
        }
        if (instance.get().getPxeId() == null) {
            return "{}";
        }

        instance.get().setPxeId(pxeId);
        this.instanceService.getInstanceDao().saveAndFlush(instance.get());

        instanceInstallOpLogDao.saveAndFlush(instance.get().generateOpLog(this.userService.getCurrentUser(), InstanceInstallOpType.UPDATE));

        return "{}";
    }

    @PutMapping(path = "/{SN}/operation_system/{OS_ID}")
    public String modifyOperationSystemId(@PathVariable(name = "SN") String sn, @PathVariable(name = "OS_ID") Integer operationSystemId) {
        this.loggerService.operationLog(InstanceController.logFactory.product().how("webapi").what("modify operation system: /api/instance/" + sn + "/hardware/" + operationSystemId).build());

        Optional<InstanceEntity> instance = this.instanceService.getInstanceDao().findById(sn);
        if (!instance.isPresent()) {
            return "{}";
        }
        if (instance.get().getSystemId() == null) {
            return "{}";
        }

        instance.get().setSystemId(operationSystemId);
        this.instanceService.getInstanceDao().saveAndFlush(instance.get());

        instanceInstallOpLogDao.saveAndFlush(instance.get().generateOpLog(this.userService.getCurrentUser(), InstanceInstallOpType.UPDATE));

        return "{}";
    }

    @PutMapping(path = "/{SN}/project/{PROJ_ID}")
    public String modifyProjectId(@PathVariable(name = "SN") String sn, @PathVariable(name = "PROJ_ID") Integer projectId) {
        this.loggerService.operationLog(InstanceController.logFactory.product().how("webapi").what("modify project: /api/instance/" + sn + "/project/" + projectId).build());

        Optional<InstanceEntity> instance = this.instanceService.getInstanceDao().findById(sn);
        if (!instance.isPresent()) {
            return "{}";
        }

        instance.get().setProjectId(projectId);
        this.instanceService.getInstanceDao().saveAndFlush(instance.get());

        return "{}";
    }

    @PutMapping(path = "/{SN}/net_info")
    public String modifyNetInfo(@PathVariable(name = "SN") String sn, @RequestBody ModifyInstanceNetInfoMessage info) throws InterruptedException {
        this.loggerService.operationLog(InstanceController.logFactory.product().how("webapi").what("modify net info: /api/instance/" + sn + "/net_info").build());

        Optional<InstanceEntity> instance = this.instanceService.getInstanceDao().findById(sn);
        if (!instance.isPresent()) {
            this.loggerService.error(InstanceController.logFactory.product().how("webapi").what("modify net info sn: " + sn + " not exist").build());
            return "{}";
        }

        if (instance.get().getSetupId() == null) {
            this.loggerService.warn(InstanceController.logFactory.product().how("webapi").what("sn: " + instance.get().getSn() + " not belong to any batch").build());
        }

        List<InstallInstanceEntity> installInstances = new ArrayList<>(1);
        installInstances.add(info.transferInstallInstanceEntity(instance.get()));

        if (instance.get().getInstallable()) {
            if (!info.getInnerIp().equals(instance.get().getInnerIp())) {
                this.poolService.setUnusedIp(instance.get().getIppool(), instance.get().getInnerIp(), instance.get().getSn());
            }
        }

        instance.get().setInstallable(false);
        instance.get().setStatus("核验中");
        this.instanceService.getInstanceDao().saveAndFlush(instance.get());

        instanceInstallOpLogDao.saveAndFlush(instance.get().generateOpLog(this.userService.getCurrentUser(), InstanceInstallOpType.UPDATE));

        this.installService.asyncCheck(instance.get().getSetupId(), installInstances);

        return "{}";
    }

    @DeleteMapping(path = "/{SN}")
    public String deleteInstance(@PathVariable(name = "SN") String sn) {
        this.instanceService.getInstanceDao().deleteById(sn);

        return "{}";
    }

    @PutMapping(path = "/{AREA_ID}/op/power_on")
    public List<CloudbootResultStatusInfo> operationPowerOn(@PathVariable(name = "AREA_ID") String areaKey, @RequestBody List<String> sn) {
        sn.forEach(item -> this.loggerService.operationLog(InstanceController.logFactory.product().how("webapi").what("power on[" + item + "]: /api/instance/" + areaKey + "/op/power_on").build()));

        List<CloudbootResultStatusInfo> result = new ArrayList<>(sn.size());

        Optional<CloudbootTokenEntity> token = this.authenticationService.getCloudbootToken(areaKey);
        if (!token.isPresent()) {
            CloudbootResultStatusInfo failed = new CloudbootResultStatusInfo();
            failed.setStatus("失败");
            failed.setMessage("未找到指定区域的Token");

            result.add(failed);
            return result;
        }

        this.generateCloudbootOperationPayloads(sn).forEach(payload -> {
            List<CloudbootOperationPayload> operation = new ArrayList<>(1);
            operation.add(payload);

            result.add(this.baremetalService.powerOn(operation, token.get()));
        });
        return result;
    }

    @PutMapping(path = "/{AREA_ID}/op/power_off")
    public List<CloudbootResultStatusInfo> operationPowerOff(@PathVariable(name = "AREA_ID") String areaKey, @RequestBody List<String> sn) {
        sn.forEach(item -> this.loggerService.operationLog(InstanceController.logFactory.product().how("webapi").what("power off[" + item + "]: /api/instance/" + areaKey + "/op/power_off").build()));

        List<CloudbootResultStatusInfo> result = new ArrayList<>(sn.size());

        Optional<CloudbootTokenEntity> token = this.authenticationService.getCloudbootToken(areaKey);
        if (!token.isPresent()) {
            CloudbootResultStatusInfo failed = new CloudbootResultStatusInfo();
            failed.setStatus("失败");
            failed.setMessage("未找到指定区域的Token");

            result.add(failed);
            return result;
        }

        this.generateCloudbootOperationPayloads(sn).forEach(payload -> {
            List<CloudbootOperationPayload> operation = new ArrayList<>(1);
            operation.add(payload);

            result.add(this.baremetalService.powerOff(operation, token.get()));
        });
        return result;
    }

    @PutMapping(path = "/{AREA_ID}/op/restart_from_disk")
    public List<CloudbootResultStatusInfo> operationRestartFromDisk(@PathVariable(name = "AREA_ID") String areaKey, @RequestBody List<String> sn) {
        sn.forEach(item -> this.loggerService.operationLog(InstanceController.logFactory.product().how("webapi").what("restart from disk[" + item + "]: /api/instance/" + areaKey + "/op/restart_from_disk").build()));

        List<CloudbootResultStatusInfo> result = new ArrayList<>(sn.size());

        Optional<CloudbootTokenEntity> token = this.authenticationService.getCloudbootToken(areaKey);
        if (!token.isPresent()) {
            CloudbootResultStatusInfo failed = new CloudbootResultStatusInfo();
            failed.setStatus("失败");
            failed.setMessage("未找到指定区域的Token");

            result.add(failed);
            return result;
        }

        this.generateCloudbootOperationPayloads(sn).forEach(payload -> {
            List<CloudbootOperationPayload> operation = new ArrayList<>(1);
            operation.add(payload);

            result.add(this.baremetalService.restart(operation, token.get()));
        });
        return result;
    }

    @PutMapping(path = "/{AREA_ID}/op/restart_from_pxe")
    public List<CloudbootResultStatusInfo> operationRestartFromPXE(@PathVariable(name = "AREA_ID") String areaKey, @RequestBody List<String> sn) {
        sn.forEach(item -> this.loggerService.operationLog(InstanceController.logFactory.product().how("webapi").what("restart from pxe[" + item + "]: /api/instance/" + areaKey + "/op/restart_from_pxe").build()));

        List<CloudbootResultStatusInfo> result = new ArrayList<>(sn.size());

        Optional<CloudbootTokenEntity> token = this.authenticationService.getCloudbootToken(areaKey);
        if (!token.isPresent()) {
            CloudbootResultStatusInfo failed = new CloudbootResultStatusInfo();
            failed.setStatus("失败");
            failed.setMessage("未找到指定区域的Token");

            result.add(failed);
            return result;
        }

        this.generateCloudbootOperationPayloads(sn).forEach(payload -> {
            List<CloudbootOperationPayload> operation = new ArrayList<>(1);
            operation.add(payload);

            result.add(this.baremetalService.restartFromPXE(operation, token.get()));
        });
        return result;
    }


    @PutMapping(path = "/{AREA_ID}/nondistribute")
    public CloudbootResultStatusInfo nondistribute(@PathVariable(name = "AREA_ID") String areaKey, @RequestBody List<String> sn) {
        sn.forEach(item -> this.loggerService.operationLog(InstanceController.logFactory.product().how("webapi").what("nondistribute[" + item + "]: /api/instance/" + areaKey + "/nondistribute").build()));

        Optional<CloudbootTokenEntity> token = this.authenticationService.getCloudbootToken(areaKey);
        if (!token.isPresent()) {
            CloudbootResultStatusInfo failed = new CloudbootResultStatusInfo();
            failed.setStatus("失败");
            failed.setMessage("未找到指定区域的Token");

            return failed;
        }

        List<InstanceEntity> resetList = new LinkedList<>();
        List<InstanceEntity> backList = new LinkedList<>();
        this.instanceService.getInstanceDao().findAllById(sn).forEach(instance -> {
            this.poolService.setUnusedIp(instance.getIppool(), instance.getInnerIp(), instance.getSn());

            if (instance.getStatus() != null && (instance.getStatus().equals("装机成功") || instance.getStatus().equals("装机失败"))) {
                resetList.add(instance);
            }
            else {
                backList.add(instance);
            }

            instance.transferBackSetup();

            instanceInstallOpLogDao.saveAndFlush(instance.generateOpLog(this.userService.getCurrentUser(), InstanceInstallOpType.BACK));
        });

        CloudbootResultStatusInfo info = null;
        if (!resetList.isEmpty()) {
            info = this.innerReset(resetList, token.get());
            if (info.getStatus().equals("success")) {
                info = null;
            }
        }

        backList.addAll(resetList);
        this.instanceService.getInstanceDao().saveAll(backList);
        this.instanceService.getInstanceDao().flush();

        if (info == null) {
            info = new CloudbootResultStatusInfo();
            info.setStatus("success");
            info.setStatus("这些主机已回退");
        }
        return info;
    }

    @PutMapping(path = "/{SN}/oob")
    public String modifyOob(@PathVariable(name = "SN") String sn, @RequestBody ModifyOobMessage oob) {
        this.loggerService.operationLog(InstanceController.logFactory.product().how("webapi").what("modify oob: /api/instance/" + sn + "/oob").build());

        Optional<InstanceEntity> instance = this.instanceService.getInstanceDao().findById(sn);
        if (!instance.isPresent()) {
            return "{}";
        }

        instance.get().setOobUsername(oob.getUsername());
        instance.get().setOobPassword(oob.getPassword());
        this.instanceService.getInstanceDao().saveAndFlush(instance.get());

        return "{}";
    }

    @PutMapping(path = "/{AREA_ID}/reset")
    public CloudbootResultStatusInfo reset(@PathVariable(name = "AREA_ID") String areaKey, @RequestBody List<String> sn) {
        sn.forEach(item -> this.loggerService.operationLog(InstanceController.logFactory.product().how("webapi").what("reset[" + item + "]: /api/instance/" + areaKey + "/reset").build()));

        Optional<CloudbootTokenEntity> token = this.authenticationService.getCloudbootToken(areaKey);
        if (!token.isPresent()) {
            CloudbootResultStatusInfo failed = new CloudbootResultStatusInfo();
            failed.setStatus("失败");
            failed.setMessage("未找到指定区域的Token");

            return failed;
        }

        //List<InstanceEntity> resetList = new LinkedList<>();
        List<InstanceEntity> instances = this.instanceService.getInstanceDao().findAllById(sn);
        instances.forEach(instance -> {
            // 重置不释放业务IP
            //this.poolService.setUnusedIp(instance.getIppool(), instance.getInnerIp(), instance.getSn());

            //if (instance.getStatus() != null && (instance.getStatus().equals("装机成功") || instance.getStatus().equals("装机失败"))) {
                //resetList.add(instance);
            //}

            instance.transferReset();
            //instance.setInstalled(true);
            instance.setInstallable(true);
            instance.setStatus("可编辑");

            this.loggerService.info(InstanceController.logFactory.product().what("reset instance").build());

            instanceInstallOpLogDao.saveAndFlush(instance.generateOpLog(this.userService.getCurrentUser(), InstanceInstallOpType.RESET));
        });

        this.instanceService.getInstanceDao().saveAll(instances);
        this.instanceService.getInstanceDao().flush();

        CloudbootResultStatusInfo info = new CloudbootResultStatusInfo();
        info.setStatus("success");
        info.setStatus("这些主机已重置");
        return info;
    }

    @PutMapping(path = "/{AREA_ID}/set_install_success")
    public CloudbootResultStatusInfo setInstallSuccess(@PathVariable(name = "AREA_ID") String areaKey, @RequestBody List<String> sn) {
        sn.forEach(item -> this.loggerService.operationLog(InstanceController.logFactory.product().how("webapi").what("reset[" + item + "]: /api/instance/" + areaKey + "/set_install_success").build()));

        Optional<CloudbootTokenEntity> token = this.authenticationService.getCloudbootToken(areaKey);
        if (!token.isPresent()) {
            CloudbootResultStatusInfo failed = new CloudbootResultStatusInfo();
            failed.setStatus("失败");
            failed.setMessage("未找到指定区域的Token");

            return failed;
        }

        List<InstanceEntity> instances = this.instanceService.getInstanceDao().findAllById(sn);
        instances.forEach(instance -> {
            instance.transferReset();
            instance.setInstalled(true);
            instance.setInstallable(true);
            instance.setStatus("装机成功");

            this.loggerService.info(InstanceController.logFactory.product().what("set instance install success").build());

            instanceInstallOpLogDao.saveAndFlush(instance.generateOpLog(this.userService.getCurrentUser(), InstanceInstallOpType.INSTALL_SUCCESS));
        });

        this.instanceService.getInstanceDao().saveAll(instances);
        this.instanceService.getInstanceDao().flush();

        CloudbootResultStatusInfo info = new CloudbootResultStatusInfo();
        info.setStatus("success");
        info.setStatus("这些主机已重置");
        return info;
    }

    @PutMapping(path = "/{AREA_ID}/set_install_failure")
    public CloudbootResultStatusInfo setInstallFailure(@PathVariable(name = "AREA_ID") String areaKey, @RequestBody List<String> sn) {
        sn.forEach(item -> this.loggerService.operationLog(InstanceController.logFactory.product().how("webapi").what("reset[" + item + "]: /api/instance/" + areaKey + "/set_install_failure").build()));

        Optional<CloudbootTokenEntity> token = this.authenticationService.getCloudbootToken(areaKey);
        if (!token.isPresent()) {
            CloudbootResultStatusInfo failed = new CloudbootResultStatusInfo();
            failed.setStatus("失败");
            failed.setMessage("未找到指定区域的Token");

            return failed;
        }

        List<InstanceEntity> instances = this.instanceService.getInstanceDao().findAllById(sn);
        instances.forEach(instance -> {
            instance.transferReset();
            instance.setInstalled(true);
            instance.setInstallable(true);
            instance.setStatus("装机失败");

            this.loggerService.info(InstanceController.logFactory.product().what("set instance install failure").build());

            instanceInstallOpLogDao.saveAndFlush(instance.generateOpLog(this.userService.getCurrentUser(), InstanceInstallOpType.INSTALL_FAILURE));
        });

        this.instanceService.getInstanceDao().saveAll(instances);
        this.instanceService.getInstanceDao().flush();

        CloudbootResultStatusInfo info = new CloudbootResultStatusInfo();
        info.setStatus("success");
        info.setStatus("这些主机已重置");
        return info;
    }

    @PostMapping(path = "/{AREA_ID}/cancel")
    public CloudbootResultStatusInfo cancel(@PathVariable(name = "AREA_ID") String areaKey, @RequestBody List<String> sn) {
        sn.forEach(item -> this.loggerService.operationLog(InstanceController.logFactory.product().how("webapi").what("cancel[" + item + "]: /api/instance/" + areaKey + "/cancel").build()));

        Optional<CloudbootTokenEntity> token = this.authenticationService.getCloudbootToken(areaKey);
        if (!token.isPresent()) {
            CloudbootResultStatusInfo failed = new CloudbootResultStatusInfo();
            failed.setStatus("失败");
            failed.setMessage("未找到指定区域的Token");

            return failed;
        }

        List<InstanceEntity> instances = this.instanceService.getInstanceDao().findAllById(sn);

        return this.instanceService.cancelCloudbootInstall(instances, token.get());
    }

    private List<CloudbootOperationPayload> generateCloudbootOperationPayloads(List<String> sn) {
        return this.instanceService.getInstanceDao().findAllById(sn)
            .stream()
            .map(instance -> instance.generateCloudbootOperationPayload())
            .collect(Collectors.toList());
    }

    private CloudbootResultStatusInfo innerReset(List<InstanceEntity> resetList, CloudbootTokenEntity token) {
        // 从PXE重启
        CloudbootResultStatusInfo info = this.baremetalService.restartFromPXE(resetList
                .stream()
                .map(instance -> instance.generateCloudbootOperationPayload())
                .collect(Collectors.toList()), token);
        if (!info.getStatus().equals("success")) {
            System.out.println("reset from PXE failed");
            return info;
        }

        // 删除之前的Cloudboot装机信息
        this.instanceService.deleteCloudbootInstance(resetList, token);

        info.setStatus("success");
        info.setStatus("这些主机已重置");
        return info;
    }

    public class StatTemplate {
        private Map<Integer, String> pxeMapper;
        private Map<Integer, String> osMapper;
        private Map<Integer, String> hardwareMapper;

        public StatTemplate(String areaKey, InstanceController ctrl) {
            this.pxeMapper = ctrl.infoService.getAllPXE(areaKey)
                .stream()
                .reduce(new HashMap<Integer, String>(), (prev, curr) -> {
                    prev.put(curr.getId(), curr.getName());
                    return prev;
                }, (a, b) -> null);
            this.osMapper = ctrl.infoService.getAllOperationSystem(areaKey)
                .stream()
                .reduce(new HashMap<Integer, String>(), (prev, curr) -> {
                    prev.put(curr.getId(), curr.getName());
                    return prev;
                }, (a, b) -> null);
            this.hardwareMapper = ctrl.infoService.getAllHardware(areaKey)
                .stream()
                .reduce(new HashMap<Integer, String>(), (prev, curr) -> {
                    prev.put(curr.getId(), curr.getShowName());
                    return prev;
                }, (a, b) -> null);
        }

        public Map<Integer, String> getOsMapper() {
            return osMapper;
        }

        public Map<Integer, String> getPxeMapper() {
            return pxeMapper;
        }

        public Map<Integer, String> getHardwareMapper() {
            return hardwareMapper;
        }
    }

    @PostMapping(path = "/stat")
    public List<InstanceStatMessage> stat(@RequestBody InstanceStatRequestMessage message) {
        Map<Integer, String> projectMapper = this.projectService.getProjectDao().findAll()
            .stream()
            .reduce(new HashMap<Integer, String>(), (prev, curr) -> {
                prev.put(curr.getId(), curr.getName());
                return prev;
            }, (a, b) -> null);

        Map<String, String> areaMapper = this.poolService.getAllArea()
            .stream()
            .reduce(new HashMap<String, String>(), (prev, curr) -> {
                prev.put(curr.getId(), curr.getName());
                return prev;
            }, (a, b) -> null);

        Map<String, StatTemplate> templates = new HashMap<>();

        List<InstanceEntity> instances = this.instanceService.getInstanceDao().findAll()
            .stream()
            .filter(instance -> {
                if (!message.getProjectId().isEmpty() && !message.getAreaId().isEmpty()) {
                    return message.getProjectId().contains(instance.getProjectId()) && message.getAreaId().contains(instance.getAreaId());
                }
                else if (!message.getProjectId().isEmpty()) {
                    return message.getProjectId().contains(instance.getProjectId());
                }
                else if (!message.getAreaId().isEmpty()) {
                    return message.getAreaId().contains(instance.getAreaId());
                }
                return true;
            })
            .collect(Collectors.toList());

        instances.forEach(instance -> {
            if (!templates.containsKey(instance.getAreaId())) {
                templates.put(instance.getAreaId(), new StatTemplate(instance.getAreaId(), this));
            }
        });

        List<InstanceStatMessage> stats = instances
            .stream()
            .map(instance -> new InstanceStatMessage(
                        instance,
                        areaMapper.get(instance.getAreaId()),
                        projectMapper.get(instance.getProjectId()),
                        templates.get(instance.getAreaId()).getHardwareMapper().get(instance.getHardwareId()),
                        templates.get(instance.getAreaId()).getPxeMapper().get(instance.getPxeId()),
                        templates.get(instance.getAreaId()).getOsMapper().get(instance.getSystemId())))
            .collect(Collectors.toList());

        stats.forEach(stat -> {
            HistorySetupInstanceEntity successFilter = new HistorySetupInstanceEntity();
            successFilter.setSn(stat.getSn());
            successFilter.setDetail("装机成功");

            stat.setSuccessCount((int) this.historyService.getHistorySetupInstanceDao().count(Example.of(successFilter)));

            HistorySetupInstanceEntity failedFilter = new HistorySetupInstanceEntity();
            failedFilter.setSn(stat.getSn());
            failedFilter.setDetail("装机失败");

            stat.setFailedCount((int) this.historyService.getHistorySetupInstanceDao().count(Example.of(failedFilter)));
        });

        return stats;
    }
}
