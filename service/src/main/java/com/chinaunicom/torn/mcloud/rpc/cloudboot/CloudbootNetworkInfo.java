package com.chinaunicom.torn.mcloud.rpc.cloudboot;

import com.alibaba.fastjson.annotation.JSONField;

public class CloudbootNetworkInfo {

    @JSONField(name = "ID")
    private Integer id;
    @JSONField(name = "Bonding")
    private String bonding;
    @JSONField(name = "CreatedAt")
    private String createdAt;
    @JSONField(name = "DeletedAt")
    private String deletedAt;
    @JSONField(name = "UpdatedAt")
    private String updatedAt;
    @JSONField(name = "Gateway")
    private String gateway;
    @JSONField(name = "Netmask")
    private String netmask;
    @JSONField(name = "Network")
    private String network;
    @JSONField(name = "Trunk")
    private String trunk;
    @JSONField(name = "Vlan")
    private String vlan;


    public Integer getId() {
        return id;
    }

    public String getVlan() {
        return vlan;
    }

    public String getTrunk() {
        return trunk;
    }

    public String getBonding() {
        return bonding;
    }

    public String getGateway() {
        return gateway;
    }

    public String getNetmask() {
        return netmask;
    }

    public String getNetwork() {
        return network;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getDeletedAt() {
        return deletedAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setTrunk(String trunk) {
        this.trunk = trunk;
    }

    public void setBonding(String bonding) {
        this.bonding = bonding;
    }

    public void setVlan(String vlan) {
        this.vlan = vlan;
    }

    public void setGateway(String gateway) {
        this.gateway = gateway;
    }

    public void setNetmask(String netmask) {
        this.netmask = netmask;
    }

    public void setNetwork(String network) {
        this.network = network;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public void setDeletedAt(String deletedAt) {
        this.deletedAt = deletedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}
