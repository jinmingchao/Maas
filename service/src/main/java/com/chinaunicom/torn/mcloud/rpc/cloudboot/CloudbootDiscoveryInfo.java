package com.chinaunicom.torn.mcloud.rpc.cloudboot;

import com.alibaba.fastjson.annotation.JSONField;

public class CloudbootDiscoveryInfo {

    @JSONField(name = "ID")
    private Integer id;
    @JSONField(name = "BootosLastActiveTime")
    private String bootosLastActiveTime;
    @JSONField(name = "Ip")
    private String ip;
    @JSONField(name = "Company")
    private String company;
    @JSONField(name = "Cpu")
    private String cpu;
    @JSONField(name = "CpuSum")
    private Integer cpuSum;
    @JSONField(name = "DeviceID")
    private Integer deviceId;
    @JSONField(name = "Disk")
    private String disk;
    @JSONField(name = "DiskSum")
    private Integer diskSum;
    @JSONField(name = "IsShowInScanList")
    private String isShowInScanList;
    @JSONField(name = "IsVm")
    private String isVm;
    @JSONField(name = "Mac")
    private String mac;
    @JSONField(name = "Memory")
    private String memory;
    @JSONField(name = "MemorySum")
    private Integer memorySum;
    @JSONField(name = "ModelName")
    private String modelName;
    @JSONField(name = "Motherboard")
    private String motherboard;
    @JSONField(name = "Nic")
    private String nic;
    @JSONField(name = "NicDevice")
    private String nicDevice;
    @JSONField(name = "Oob")
    private String oob;
    @JSONField(name = "OwnerName")
    private String ownerName;
    @JSONField(name = "Product")
    private String product;
    @JSONField(name = "Raid")
    private String raid;
    @JSONField(name = "Sn")
    private String sn;
    @JSONField(name = "UserId")
    private Integer userId;

    public String getCpu() {
        return cpu;
    }

    public Integer getId() {
        return id;
    }

    public String getSn() {
        return sn;
    }

    public String getMac() {
        return mac;
    }

    public String getNic() {
        return nic;
    }

    public String getOob() {
        return oob;
    }

    public String getDisk() {
        return disk;
    }

    public String getIsVm() {
        return isVm;
    }

    public String getRaid() {
        return raid;
    }

    public String getMemory() {
        return memory;
    }

    public String getCompany() {
        return company;
    }

    public Integer getCpuSum() {
        return cpuSum;
    }

    public String getProduct() {
        return product;
    }

    public Integer getUserId() {
        return userId;
    }

    public Integer getDiskSum() {
        return diskSum;
    }

    public Integer getDeviceId() {
        return deviceId;
    }

    public String getModelName() {
        return modelName;
    }

    public String getNicDevice() {
        return nicDevice;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public Integer getMemorySum() {
        return memorySum;
    }

    public String getMotherboard() {
        return motherboard;
    }

    public String getIsShowInScanList() {
        return isShowInScanList;
    }

    public String getBootosLastActiveTime() {
        return bootosLastActiveTime;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setCpu(String cpu) {
        this.cpu = cpu;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public void setDisk(String disk) {
        this.disk = disk;
    }

    public void setIsVm(String isVm) {
        this.isVm = isVm;
    }

    public void setNic(String nic) {
        this.nic = nic;
    }

    public void setOob(String oob) {
        this.oob = oob;
    }

    public void setMemory(String memory) {
        this.memory = memory;
    }

    public void setCpuSum(Integer cpuSum) {
        this.cpuSum = cpuSum;
    }

    public void setRaid(String raid) {
        this.raid = raid;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public void setDiskSum(Integer diskSum) {
        this.diskSum = diskSum;
    }

    public void setDeviceId(Integer deviceId) {
        this.deviceId = deviceId;
    }
    
    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public void setNicDevice(String nicDevice) {
        this.nicDevice = nicDevice;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public void setMemorySum(Integer memorySum) {
        this.memorySum = memorySum;
    }

    public void setMotherboard(String motherboard) {
        this.motherboard = motherboard;
    }

    public void setIsShowInScanList(String isShowInScanList) {
        this.isShowInScanList = isShowInScanList;
    }

    public void setBootosLastActiveTime(String bootosLastActiveTime) {
        this.bootosLastActiveTime = bootosLastActiveTime;
    }
}
