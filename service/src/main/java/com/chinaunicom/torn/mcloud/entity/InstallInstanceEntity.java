package com.chinaunicom.torn.mcloud.entity;

public class InstallInstanceEntity {
    
    private String areaId;
    private String sn;
    private String hostname;
    private String innerIp;
    private Integer ippool;
    private String gateway;
    private String netmask;
    private Integer vlanId;
    private Integer pxeId;
    private Integer operationSystemId;
    private String boundMac1;
    private String boundMac2;
    private String boundType;
    private Boolean modified;

    public String getAreaId() {
        return areaId;
    }

    public String getSn() {
        return sn;
    }

    public Integer getPxeId() {
        return pxeId;
    }

    public String getGateway() {
        return gateway;
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

    public String getHostname() {
        return hostname;
    }

    public Integer getOperationSystemId() {
        return operationSystemId;
    }

    public Integer getIppool() {
        return ippool;
    }

    public String getBoundMac1() {
        return boundMac1;
    }

    public String getBoundMac2() {
        return boundMac2;
    }

    public Boolean getModified() {
        return modified;
    }

    public String getBoundType() {
        return boundType;
    }

    public void setBoundType(String boundType) {
        this.boundType = boundType;
    }

    public void setModified(Boolean modified) {
        this.modified = modified;
    }

    public void setBoundMac1(String boundMac1) {
        this.boundMac1 = boundMac1;
    }

    public void setBoundMac2(String boundMac2) {
        this.boundMac2 = boundMac2;
    }

    public void setIppool(Integer ippool) {
        this.ippool = ippool;
    }

    public void setAreaId(String areaId) {
        this.areaId = areaId;
    }

    public void setPxeId(Integer pxeId) {
        this.pxeId = pxeId;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public void setVlanId(Integer vlanId) {
        this.vlanId = vlanId;
    }

    public void setGateway(String gateway) {
        this.gateway = gateway;
    }

    public void setInnerIp(String innerIp) {
        this.innerIp = innerIp;
    }

    public void setNetmask(String netmask) {
        this.netmask = netmask;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public void setOperationSystemId(Integer operationSystemId) {
        this.operationSystemId = operationSystemId;
    }
}
