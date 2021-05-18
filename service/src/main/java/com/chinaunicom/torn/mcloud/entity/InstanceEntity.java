package com.chinaunicom.torn.mcloud.entity;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.alibaba.fastjson.JSONObject;
import com.chinaunicom.torn.mcloud.enums.InstanceInstallOpType;
import com.chinaunicom.torn.mcloud.enums.ManageInstanceField;
import com.chinaunicom.torn.mcloud.rpc.cloudboot.CloudbootDiscoveryCPUInfo;
import com.chinaunicom.torn.mcloud.rpc.cloudboot.CloudbootDiscoveryDiskInfo;
import com.chinaunicom.torn.mcloud.rpc.cloudboot.CloudbootDiscoveryInfo;
import com.chinaunicom.torn.mcloud.rpc.cloudboot.CloudbootDiscoveryMemoryInfo;
import com.chinaunicom.torn.mcloud.rpc.cloudboot.CloudbootOperationPayload;

@Entity
@Table(name = "tb_instance")
public class InstanceEntity {

    @Id
    @Column(name = "sn")
    private String sn;
    @Column(name = "last_modify_at")
    private Date lastModifyAt;
    @Column(name = "hostname")
    private String hostname;
    @Column(name = "discovery")
    private Boolean discovery;
    @Column(name = "discovery_last_time")
    private Date discoveryLastTime;
    @Column(name = "managed")
    private Boolean managed;
    @Column(name = "distributed")
    private Boolean distributed;
    @Column(name = "setup_id")
    private Integer setupId;
    @Column(name = "project_id")
    private Integer projectId;
    @Column(name = "installable")
    private Boolean installable;
    @Column(name = "installed")
    private Boolean installed;
    @Column(name = "company")
    private String company;
    @Column(name = "model_name")
    private String modelName;
    @Column(name = "motherborad")
    private String motherborad;
    @Column(name = "cpu")
    private String cpu;
    @Column(name = "cpu_core_count")
    private Integer cpuCoreCount;
    @Column(name = "cloudboot_id")
    private Integer cloudbootId;
    @Column(name = "disk")
    private String disk;
    @Column(name = "disk_count")
    private Integer diskCount;
    @Column(name = "disk_capacity_sum")
    private Integer diskCapacitySum;
    @Column(name = "dhcp_ip")
    private String dhcpIp;
    @Column(name = "inner_ip")
    private String innerIp;
    @Column(name = "ippool_id")
    private Integer ippool;
    @Column(name = "gateway_ip")
    private String gatewayIp;
    @Column(name = "netmask")
    private String netmask;
    @Column(name = "vlanid")
    private Integer vlanId;
    @Column(name = "net_area_id")
    private Integer netAreaId;
    @Column(name = "bound_mac1")
    private String boundMac1;
    @Column(name = "bound_mac2")
    private String boundMac2;
    @Column(name = "bound_type")
    private String boundType;
    @Column(name = "memory")
    private String memory;
    @Column(name = "memory_count")
    private Integer memoryCount;
    @Column(name = "memory_capacity_sum")
    private Integer memoryCapacitySum;
    @Column(name = "nic")
    private String nic;
    @Column(name = "nic_device")
    private String nicDevice;
    @Column(name = "oob_ip")
    private String oobIp;
    @Column(name = "oob_username")
    private String oobUsername;
    @Column(name = "oob_password")
    private String oobPassword;
    @Column(name = "raid")
    private String raid;
    @Column(name = "pxe_id")
    private Integer pxeId;
    @Column(name = "system_id")
    private Integer systemId;
    @Column(name = "hardware_id")
    private Integer hardwareId;
    @Column(name = "area_id")
    private String areaId;
    @Column(name = "manage_info")
    private String manageInfo;
    @Column(name = "status")
    private String status;

    public InstanceEntity() {
        this.discovery = false;
        this.managed = false;
        this.distributed = false;
        this.installable = false;
        this.installed = false;
    }

    private InstanceEntity(String areaId) {
        this.areaId = areaId;
    }

    public static InstanceEntity factoryBuilder(String areaId) {
        return new InstanceEntity(areaId);
    }

    public void transferManageMessage(String areaKey, JSONObject obj) {
        this.lastModifyAt = new Date();

        this.sn = obj.getString(ManageInstanceField.SN.getField());
        this.managed = true;
        this.hardwareId = obj.getInteger(ManageInstanceField.HARDWARE.getField());
        this.netAreaId = obj.getInteger(ManageInstanceField.NET_AREA.getField());
        if (obj.containsKey(ManageInstanceField.OOB_USERNAME.getField())) {
            this.oobUsername = obj.getString(ManageInstanceField.OOB_USERNAME.getField());
        }
        if (obj.containsKey(ManageInstanceField.OOB_PASSWORD.getField())) {
            this.oobPassword = obj.getString(ManageInstanceField.OOB_PASSWORD.getField());
        }
        if (obj.containsKey(ManageInstanceField.OOB_IP.getField())) {
            String ip = obj.getString(ManageInstanceField.OOB_IP.getField());
            if (ip != null && !ip.isEmpty()) {
                this.oobIp = ip;
            }
        }
        this.areaId = areaKey;
        this.projectId = obj.getInteger(ManageInstanceField.PROJECT_ID.getField());
        this.manageInfo = obj.toJSONString();
    }

    public void transferSetup(Integer setupId, InstallInstanceEntity entity) {
        this.lastModifyAt = new Date();

        this.distributed = true;
        this.setupId = setupId;
        this.pxeId = entity.getPxeId();
        this.systemId = entity.getOperationSystemId();
        this.ippool = entity.getIppool();
        this.vlanId = entity.getVlanId();
        this.gatewayIp = entity.getGateway();
        this.netmask = entity.getNetmask();
        this.innerIp = entity.getInnerIp();
        this.hostname = entity.getHostname();
        this.boundMac1 = entity.getBoundMac1();
        this.boundMac2 = entity.getBoundMac2();
        this.boundType = entity.getBoundType();
    }

    public void transferBackSetup() {
        this.lastModifyAt = new Date();

        this.transferReset();

        this.installed = false;
        this.installable = false;
        this.distributed = false;
        this.setupId = null;
        this.pxeId = null;
        this.systemId = null;
        this.ippool = null;
        this.vlanId = null;
        this.gatewayIp = null;
        this.netmask = null;
        this.innerIp = null;
        this.hostname = null;
        this.boundMac1 = null;
        this.boundMac2 = null;
        this.boundType = null;
    }

    public void transferReset() {
        this.lastModifyAt = new Date();

        //this.installed = false;
        //this.installable = false;
        this.status = null;
    }

    public void transferCloudbootDiscoveryInfo(CloudbootDiscoveryInfo info, String areaId) {
        this.lastModifyAt = new Date();

        this.sn = info.getSn();
        this.discovery = true;
        this.discoveryLastTime = new Date();
        this.cloudbootId = info.getId();
        this.company = info.getCompany();
        this.modelName = info.getModelName();
        this.motherborad = info.getMotherboard();
        this.cpu = info.getCpu();
        this.disk = info.getDisk();
        this.dhcpIp = info.getIp();
        this.memory = info.getMemory();
        this.nic = info.getNic();
        this.nicDevice = info.getNicDevice();
        if (info.getOob() != null && !info.getOob().equals("0.0.0.0")) {
            this.oobIp = info.getOob(); 
        }
        this.raid = info.getRaid();
        this.areaId = areaId;

        CloudbootDiscoveryCPUInfo cpuInfo = JSONObject.parseObject(this.cpu, CloudbootDiscoveryCPUInfo.class);
        this.cpuCoreCount = cpuInfo.getCore();

        List<CloudbootDiscoveryDiskInfo> diskInfos = JSONObject.parseArray(this.disk, CloudbootDiscoveryDiskInfo.class);
        this.diskCount = diskInfos.size();
        this.diskCapacitySum = diskInfos.stream()
            .mapToInt(disk -> transferCapacity(disk.getSize()))
            .sum();

        List<CloudbootDiscoveryMemoryInfo> memoryInfos = JSONObject.parseArray(this.memory, CloudbootDiscoveryMemoryInfo.class);
        this.memoryCount = memoryInfos.size();
        this.memoryCapacitySum = memoryInfos.stream()
            .mapToInt(memory -> transferCapacity(memory.getSize()))
            .sum();
    }

    public CloudbootOperationPayload generateCloudbootOperationPayload() {
        CloudbootOperationPayload payload = new CloudbootOperationPayload();

        payload.setSn(this.sn);
        payload.setOobIp(this.oobIp);
        payload.setPassword(this.oobPassword);
        payload.setUsername(this.oobUsername);

        return payload;
    }

    public InstanceInstallOpLogEntity generateOpLog(String user, InstanceInstallOpType type) {
        InstanceInstallOpLogEntity entity = new InstanceInstallOpLogEntity();

        entity.setSn(this.sn);
        entity.setOpType(type.toString());
        entity.setCreatedAt(new Date());
        entity.setUser(user);
        entity.setBatchId(this.setupId);
        entity.setInnerIp(this.innerIp);
        entity.setNetmask(this.netmask);
        entity.setVlanId(this.vlanId);
        entity.setNetAreaId(this.netAreaId);
        entity.setGatewayIp(this.gatewayIp);
        entity.setHostname(this.hostname);
        entity.setPxeId(this.pxeId);
        entity.setSystemId(this.systemId);
        entity.setHardwareId(this.hardwareId);

        return entity;
    }

    private static Integer transferCapacity(String size) {
        if (size.endsWith("GB")) {
            return (int) (Float.parseFloat(size.substring(0, size.lastIndexOf(' '))) * 1024);
        }
        else if (size.endsWith("MB")) {
            return (int) (Float.parseFloat(size.substring(0, size.lastIndexOf(' '))));
        }
        return 0;
    }

    public String getSn() {
        return sn;
    }

    public String getHostname() {
        return hostname;
    }

    public String getCpu() {
        return cpu;
    }

    public String getNic() {
        return nic;
    }

    public String getDisk() {
        return disk;
    }

    public String getRaid() {
        return raid;
    }

    public String getOobIp() {
        return oobIp;
    }

    public String getDhcpIp() {
        return dhcpIp;
    }

    public String getMemory() {
        return memory;
    }

    public String getNicDevice() {
        return nicDevice;
    }

    public Boolean getDiscovery() {
        return discovery;
    }

    public Integer getDiskCount() {
        return diskCount;
    }

    public Integer getCloudbootId() {
        return cloudbootId;
    }

    public Integer getMemoryCount() {
        return memoryCount;
    }

    public Integer getCpuCoreCount() {
        return cpuCoreCount;
    }

    public Integer getDiskCapacitySum() {
        return diskCapacitySum;
    }

    public Integer getMemoryCapacitySum() {
        return memoryCapacitySum;
    }

    public String getOobPassword() {
        return oobPassword;
    }

    public String getOobUsername() {
        return oobUsername;
    }

    public String getAreaId() {
        return areaId;
    }

    public Integer getPxeId() {
        return pxeId;
    }

    public String getCompany() {
        return company;
    }

    public String getInnerIp() {
        return innerIp;
    }

    public String getNetmask() {
        return netmask;
    }

    public Integer getVlanId() {
        return vlanId;
    }

    public Boolean getManaged() {
        return managed;
    }

    public String getGatewayIp() {
        return gatewayIp;
    }

    public String getModelName() {
        return modelName;
    }

    public Integer getSystemId() {
        return systemId;
    }

    public String getMotherborad() {
        return motherborad;
    }

    public Date getDiscoveryLastTime() {
        return discoveryLastTime;
    }

    public Integer getSetupId() {
        return setupId;
    }

    public String getManageInfo() {
        return manageInfo;
    }

    public Integer getHardwareId() {
        return hardwareId;
    }

    public Boolean getDistributed() {
        return distributed;
    }

    public Integer getNetAreaId() {
        return netAreaId;
    }

    public String getStatus() {
        return status;
    }

    public Boolean getInstallable() {
        return installable;
    }

    public Boolean getInstalled() {
        return installed;
    }

    public String getBoundMac1() {
        return boundMac1;
    }

    public String getBoundMac2() {
        return boundMac2;
    }

    public Integer getIppool() {
        return ippool;
    }

    public String getBoundType() {
        return boundType;
    }

    public Integer getProjectId() {
        return projectId;
    }

    public Date getLastModifyAt() {
        return lastModifyAt;
    }

    public void setLastModifyAt(Date lastModifyAt) {
        this.lastModifyAt = lastModifyAt;
    }

    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }

    public void setBoundType(String boundType) {
        this.boundType = boundType;
    }

    public void setIppool(Integer ippool) {
        this.ippool = ippool;
    }

    public void setBoundMac1(String boundMac1) {
        this.boundMac1 = boundMac1;
    }

    public void setBoundMac2(String boundMac2) {
        this.boundMac2 = boundMac2;
    }

    public void setInstalled(Boolean installed) {
        this.installed = installed;
    }

    public void setInstallable(Boolean installable) {
        this.installable = installable;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public void setNetAreaId(Integer netAreaId) {
        this.netAreaId = netAreaId;
    }

    public void setSetupId(Integer setupId) {
        this.setupId = setupId;
    }

    public void setManageInfo(String manageInfo) {
        this.manageInfo = manageInfo;
    }

    public void setHardwareId(Integer hardwareId) {
        this.hardwareId = hardwareId;
    }

    public void setDistributed(Boolean distributed) {
        this.distributed = distributed;
    }

    public void setAreaId(String areaId) {
        this.areaId = areaId;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public void setInnerIp(String innerIp) {
        this.innerIp = innerIp;
    }

    public void setPxeId(Integer pxeId) {
        this.pxeId = pxeId;
    }

    public void setNetmask(String netmask) {
        this.netmask = netmask;
    }

    public void setManaged(Boolean managed) {
        this.managed = managed;
    }

    public void setVlanId(Integer vlanId) {
        this.vlanId = vlanId;
    }

    public void setSystemId(Integer systemId) {
        this.systemId = systemId;
    }

    public void setGatewayIp(String gatewayIp) {
        this.gatewayIp = gatewayIp;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public void setMotherborad(String motherborad) {
        this.motherborad = motherborad;
    }

    public void setDiscoveryLastTime(Date discoveryLastTime) {
        this.discoveryLastTime = discoveryLastTime;
    }

    public void setOobPassword(String oobPassword) {
        this.oobPassword = oobPassword;
    }

    public void setOobUsername(String oobUsername) {
        this.oobUsername = oobUsername;
    }

    public void setCpu(String cpu) {
        this.cpu = cpu;
    }

    public void setNic(String nic) {
        this.nic = nic;
    }

    public void setDisk(String disk) {
        this.disk = disk;
    }

    public void setRaid(String raid) {
        this.raid = raid;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public void setOobIp(String oobIp) {
        this.oobIp = oobIp;
    }

    public void setDhcpIp(String dhcpIp) {
        this.dhcpIp = dhcpIp;
    }

    public void setMemory(String memory) {
        this.memory = memory;
    }

    public void setNicDevice(String nicDevice) {
        this.nicDevice = nicDevice;
    }

    public void setDiscovery(Boolean discovery) {
        this.discovery = discovery;
    }

    public void setDiskCount(Integer diskCount) {
        this.diskCount = diskCount;
    }

    public void setCloudbootId(Integer cloudbootId) {
        this.cloudbootId = cloudbootId;
    }

    public void setMemoryCount(Integer memoryCount) {
        this.memoryCount = memoryCount;
    }

    public void setCpuCoreCount(Integer cpuCoreCount) {
        this.cpuCoreCount = cpuCoreCount;
    }

    public void setDiskCapacitySum(Integer diskCapacitySum) {
        this.diskCapacitySum = diskCapacitySum;
    }

    public void setMemoryCapacitySum(Integer memoryCapacitySum) {
        this.memoryCapacitySum = memoryCapacitySum;
    }
}
