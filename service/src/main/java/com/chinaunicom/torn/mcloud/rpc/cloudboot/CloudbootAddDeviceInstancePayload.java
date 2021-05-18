package com.chinaunicom.torn.mcloud.rpc.cloudboot;

import com.alibaba.fastjson.annotation.JSONField;
import com.chinaunicom.torn.mcloud.entity.InstanceEntity;

public class CloudbootAddDeviceInstancePayload {

    @JSONField(name = "BatchNumber")
    private String batchNumber;
    @JSONField(name = "Sn")
    private String sn;
    @JSONField(name = "Hostname")
    private String hostname;
    @JSONField(name = "Ip")
    private String ip;
    @JSONField(name = "ManageIp")
    private String manageIp;
    @JSONField(name = "NetworkID")
    private Integer networkId;
    @JSONField(name = "ManageNetworkID")
    private Integer manageNetworkId;
    @JSONField(name = "OsID")
    private Integer osId;
    @JSONField(name = "HardwareID")
    private Integer hardwareId;
    @JSONField(name = "SystemID")
    private Integer systemId;
    @JSONField(name = "LocationID")
    private Integer locationId;
    @JSONField(name = "AssetNumber")
    private String assetNumber;
    @JSONField(name = "IsSupportVm")
    private String isSupportVm;
    @JSONField(name = "Status")
    private String status;
    @JSONField(name = "UserID")
    private Integer userId;
    @JSONField(name = "AccessToken")
    private String accessToken;
    @JSONField(name = "Callback")
    private String callback;
    @JSONField(name = "Oob")
    private String oob;
    @JSONField(name = "Network")
    private String network;

    public CloudbootAddDeviceInstancePayload(InstanceEntity entity) {
        this.sn = entity.getSn();
        this.hostname = entity.getHostname();
        this.ip = entity.getDhcpIp();
        this.hardwareId = entity.getHardwareId();
        this.systemId = entity.getSystemId();
        this.osId = entity.getPxeId();
        this.oob = entity.getOobIp();

        // Need Access Token and networkId
    }

    public String getIp() {
        return ip;
    }

    public String getSn() {
        return sn;
    }

    public Integer getOsId() {
        return osId;
    }

    public String getStatus() {
        return status;
    }

    public Integer getUserId() {
        return userId;
    }

    public String getCallback() {
        return callback;
    }

    public String getHostname() {
        return hostname;
    }

    public String getManageIp() {
        return manageIp;
    }
    
    public Integer getSystemId() {
        return systemId;
    }

    public Integer getNetworkId() {
        return networkId;
    }

    public String getAccessToken() {
        return accessToken;
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

    public Integer getManageNetworkId() {
        return manageNetworkId;
    }

    public String getOob() {
        return oob;
    }

    public String getNetwork() {
        return network;
    }
    
    public void setNetwork(String network) {
        this.network = network;
    }

    public void setOob(String oob) {
        this.oob = oob;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public void setOsId(Integer osId) {
        this.osId = osId;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setCallback(String callback) {
        this.callback = callback;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public void setManageIp(String manageIp) {
        this.manageIp = manageIp;
    }

    public void setSystemId(Integer systemId) {
        this.systemId = systemId;
    }

    public void setNetworkId(Integer networkId) {
        this.networkId = networkId;
    }

    public void setHardwareId(Integer hardwareId) {
        this.hardwareId = hardwareId;
    }

    public void setLocationId(Integer locationId) {
        this.locationId = locationId;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
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

    public void setManageNetworkId(Integer manageNetworkId) {
        this.manageNetworkId = manageNetworkId;
    }
}
