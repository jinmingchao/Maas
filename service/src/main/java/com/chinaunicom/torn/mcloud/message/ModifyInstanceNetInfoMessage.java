package com.chinaunicom.torn.mcloud.message;

import com.chinaunicom.torn.mcloud.entity.InstallInstanceEntity;
import com.chinaunicom.torn.mcloud.entity.InstanceEntity;

public class ModifyInstanceNetInfoMessage {

    private Integer areaId;
    private String hostname;
    private Integer ippool;
    private String innerIp;
    private String boundMac1;
    private String boundMac2;
    private String boundType;

    public InstallInstanceEntity transferInstallInstanceEntity(InstanceEntity instance) {
        InstallInstanceEntity result = new InstallInstanceEntity();

        result.setAreaId(instance.getAreaId());
        result.setSn(instance.getSn());
        result.setHostname(this.hostname);
        result.setInnerIp(this.innerIp);
        result.setIppool(this.ippool);
        result.setPxeId(instance.getPxeId());
        result.setOperationSystemId(instance.getSystemId());
        result.setBoundMac1(this.boundMac1);
        result.setBoundMac2(this.boundMac2);
        result.setBoundType(this.boundType);
        result.setModified(true);

        return result;
    }

    public Integer getAreaId() {
        return areaId;
    }

    public String getInnerIp() {
        return innerIp;
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

    public void setBoundType(String boundType) {
        this.boundType = boundType;
    }

    public void setAreaId(Integer areaId) {
        this.areaId = areaId;
    }

    public void setInnerIp(String innerIp) {
        this.innerIp = innerIp;
    }

    public void setIppool(Integer ippool) {
        this.ippool = ippool;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public void setBoundMac1(String boundMac1) {
        this.boundMac1 = boundMac1;
    }

    public void setBoundMac2(String boundMac2) {
        this.boundMac2 = boundMac2;
    }
}
