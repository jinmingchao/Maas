package com.chinaunicom.torn.mcloud.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "tb_instance_install_oplog")
public class InstanceInstallOpLogEntity {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "sn")
    private String sn;
    @Column(name = "op_type")
    private String opType;
    @Column(name = "created_at")
    private Date createdAt;
    @Column(name = "op_user")
    private String user;
    @Column(name = "batch_id")
    private Integer batchId;
    @Column(name = "inner_ip")
    private String innerIp;
    @Column(name = "netmask")
    private String netmask;
    @Column(name = "vlanid")
    private Integer vlanId;
    @Column(name = "net_area_id")
    private Integer netAreaId;
    @Column(name = "gateway_ip")
    private String gatewayIp;
    @Column(name = "hostname")
    private String hostname;
    @Column(name = "pxe_id")
    private Integer pxeId;
    @Column(name = "system_id")
    private Integer systemId;
    @Column(name = "hardware_id")
    private Integer hardwareId;


    public String getSn() {
        return sn;
    }

    public Integer getId() {
        return id;
    }

    public String getUser() {
        return user;
    }

    public String getOpType() {
        return opType;
    }

    public Integer getPxeId() {
        return pxeId;
    }

    public Date getCreatedAt() {
        return createdAt;
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

    public Integer getBatchId() {
        return batchId;
    }

    public String getHostname() {
        return hostname;
    }

    public String getGatewayIp() {
        return gatewayIp;
    }

    public Integer getSystemId() {
        return systemId;
    }

    public Integer getNetAreaId() {
        return netAreaId;
    }

    public Integer getHardwareId() {
        return hardwareId;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setInnerIp(String innerIp) {
        this.innerIp = innerIp;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public void setOpType(String opType) {
        this.opType = opType;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public void setBatchId(Integer batchId) {
        this.batchId = batchId;
    }

    public void setNetmask(String netmask) {
        this.netmask = netmask;
    }

    public void setNetAreaId(Integer netAreaId) {
        this.netAreaId = netAreaId;
    }

    public void setGatewayIp(String gatewayIp) {
        this.gatewayIp = gatewayIp;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public void setHardwareId(Integer hardwareId) {
        this.hardwareId = hardwareId;
    }

    public void setSystemId(Integer systemId) {
        this.systemId = systemId;
    }

    public void setVlanId(Integer vlanId) {
        this.vlanId = vlanId;
    }

    public void setPxeId(Integer pxeId) {
        this.pxeId = pxeId;
    }

    public void setUser(String user) {
        this.user = user;
    }
}
