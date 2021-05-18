package com.chinaunicom.torn.mcloud.message;

import com.chinaunicom.torn.mcloud.entity.InstallInstanceEntity;

public class DistributeInstanceMessage {
    private String sn;
    private String areaId;
    private String hostname;
    private Integer ippool;
    private String innerIp;
    private Integer pxeId;
    private Integer operationSystemId;
    private String boundMac1;
    private String boundMac2;

    public InstallInstanceEntity transferInstallInstanceEntity() {
        InstallInstanceEntity entity = new InstallInstanceEntity();

        entity.setSn(this.sn);
        entity.setAreaId(this.areaId);
        entity.setHostname(this.hostname);
        entity.setIppool(this.ippool);
        entity.setInnerIp(this.innerIp);
        entity.setPxeId(this.pxeId);
        entity.setOperationSystemId(this.operationSystemId);
        entity.setBoundMac1(this.boundMac1);
        entity.setBoundMac2(this.boundMac2);
        entity.setModified(false);

        return entity;
    }

    public String getSn() {
        return sn;
    }

    public String getAreaId() {
        return areaId;
    }

    public Integer getPxeId() {
        return pxeId;
    }

    public String getInnerIp() {
        return innerIp;
    }

    public Integer getIppool() {
        return ippool;
    }

    public Integer getOperationSystemId() {
        return operationSystemId;
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

    public void setBoundMac1(String boundMac1) {
        this.boundMac1 = boundMac1;
    }

    public void setBoundMac2(String boundMac2) {
        this.boundMac2 = boundMac2;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public void setPxeId(Integer pxeId) {
        this.pxeId = pxeId;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public void setAreaId(String areaId) {
        this.areaId = areaId;
    }

    public void setIppool(Integer ippool) {
        this.ippool = ippool;
    }

    public void setInnerIp(String innerIp) {
        this.innerIp = innerIp;
    }

    public void setOperationSystemId(Integer operationSystemId) {
        this.operationSystemId = operationSystemId;
    }

}
