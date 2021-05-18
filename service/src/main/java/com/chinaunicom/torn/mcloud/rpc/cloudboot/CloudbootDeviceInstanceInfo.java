package com.chinaunicom.torn.mcloud.rpc.cloudboot;

import com.alibaba.fastjson.annotation.JSONField;

public class CloudbootDeviceInstanceInfo {

    @JSONField(name = "ID")
    private Integer id;
    @JSONField(name = "AssetNumber")
    private String assetNumber;
    @JSONField(name = "BatchNumber")
    private String batchNumber;
    @JSONField(name = "BootosIP")
    private String bootosIp;
    @JSONField(name = "CreatedAt")
    private String createdAt;
    @JSONField(name = "HardwareID")
    private Integer hardwareId;
    @JSONField(name = "HardwareName")
    private String hardwareName;
    @JSONField(name = "Hostname")
    private String hostname;
    @JSONField(name = "InstallLog")
    private String installLog;
    @JSONField(name = "InstallProgress")
    private Integer installProgress;
    @JSONField(name = "Ip")
    private String ip;
    @JSONField(name = "IsSupportVm")
    private String isSupportVm;
    @JSONField(name = "Location")
    private String location;
    @JSONField(name = "LocationID")
    private Integer locationId;
    @JSONField(name = "LocationName")
    private String locationName;
    @JSONField(name = "NetworkID")
    private Integer networkId;
    @JSONField(name = "NetworkName")
    private String networkName;
    @JSONField(name = "OobIp")
    private String oobIp;
    @JSONField(name = "OsID")
    private Integer osId;
    @JSONField(name = "OsName")
    private String osName;
    @JSONField(name = "OwnerName")
    private String ownerName;
    @JSONField(name = "Sn")
    private String sn;
    @JSONField(name = "Status")
    private String status;
    @JSONField(name = "SystemID")
    private Integer systemId;
    @JSONField(name = "SystemName")
    private String systemName;
    @JSONField(name = "UpdatedAt")
    private String updatedAt;
    @JSONField(name = "UserID")
    private Integer userId;

    public CloudbootDeletePayload generateDeletePayload() {
        CloudbootDeletePayload payload = new CloudbootDeletePayload();
        payload.setId(this.id);
        return payload;
    }

    public CloudbootCancelInstallPayload generateCancelPayload() {
        CloudbootCancelInstallPayload payload = new CloudbootCancelInstallPayload();
        payload.setId(this.id);
        return payload;
    }

    public String getIp() {
        return ip;
    }

    public Integer getId() {
        return id;
    }

    public String getSn() {
        return sn;
    }

    public String getOobIp() {
        return oobIp;
    }

    public Integer getOsId() {
        return osId;
    }
    
    public String getOsName() {
        return osName;
    }

    public String getStatus() {
        return status;
    }

    public Integer getUserId() {
        return userId;
    }

    public String getBootosIp() {
        return bootosIp;
    }

    public String getHostname() {
        return hostname;
    }

    public String getLocation() {
        return location;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public Integer getSystemId() {
        return systemId;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public String getInstallLog() {
        return installLog;
    }

    public Integer getNetworkId() {
        return networkId;
    }

    public String getSystemName() {
        return systemName;
    }

    public String getAssetNumber() {
        return assetNumber;
    }

    public String getBatchNumber() {
        return batchNumber;
    }

    public Integer getHardwareId() {
        return hardwareId;
    }

    public String getIsSupportVm() {
        return isSupportVm;
    }

    public Integer getLocationId() {
        return locationId;
    }

    public String getNetworkName() {
        return networkName;
    }

    public String getHardwareName() {
        return hardwareName;
    }

    public String getLocationName() {
        return locationName;
    }

    public Integer getInstallProgress() {
        return installProgress;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setBootosIp(String bootosIp) {
        this.bootosIp = bootosIp;
    }
    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public void setOobIp(String oobIp) {
        this.oobIp = oobIp;
    }

    public void setOsId(Integer osId) {
        this.osId = osId;
    }

    public void setOsName(String osName) {
        this.osName = osName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setSystemId(Integer systemId) {
        this.systemId = systemId;
    }

    public void setNetworkId(Integer networkId) {
        this.networkId = networkId;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public void setInstallLog(String installLog) {
        this.installLog = installLog;
    }

    public void setSystemName(String systemName) {
        this.systemName = systemName;
    }

    public void setHardwareId(Integer hardwareId) {
        this.hardwareId = hardwareId;
    }

    public void setLocationId(Integer locationId) {
        this.locationId = locationId;
    }

    public void setAssetNumber(String assetNumber) {
        this.assetNumber = assetNumber;
    }

    public void setBatchNumber(String batchNumber) {
        this.batchNumber = batchNumber;
    }

    public void setIsSupportVm(String isSupportVm) {
        this.isSupportVm = isSupportVm;
    }

    public void setNetworkName(String networkName) {
        this.networkName = networkName;
    }

    public void setHardwareName(String hardwareName) {
        this.hardwareName = hardwareName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public void setInstallProgress(Integer installProgress) {
        this.installProgress = installProgress;
    }
}
