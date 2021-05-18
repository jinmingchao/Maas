package com.chinaunicom.torn.mcloud.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.chinaunicom.torn.mcloud.rpc.cloudboot.CloudbootHardwareInfo;

@Entity
@Table(name = "tb_cloudboot_hardware")
public class CloudbootHardwareEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @Column(name = "area_id")
    private String areaId;
    @Column(name = "discovery_last_time")
    private Date discoveryLastDate;
    @Column(name = "cloudboot_id")
    private Integer cloudbootId;
    @Column(name = "company")
    private String company;
    @Column(name = "model_name")
    private String modelName;
    @Column(name = "show_name")
    private String showName;
    @Column(name = "tpl")
    private String tpl;
    @Column(name = "data")
    private String data;
    @Column(name = "enabled")
    private Boolean enabled;

    public void transferCloudbootSyncInfo(CloudbootHardwareInfo info, String areaId) {
        this.areaId = areaId;
        this.discoveryLastDate = new Date();
        this.cloudbootId = info.getId();
        this.company = info.getCompany();
        this.modelName = info.getModelName(); 
        this.showName = info.getShowName();
        this.tpl = info.getTpl();
        this.data = info.getData();
    }

    public Integer getId() {
        return id;
    }

    public String getTpl() {
        return tpl;
    }

    public String getData() {
        return data;
    }

    public String getAreaId() {
        return areaId;
    }

    public String getCompany() {
        return company;
    }

    public String getShowName() {
        return showName;
    }

    public String getModelName() {
        return modelName;
    }

    public Integer getCloudbootId() {
        return cloudbootId;
    }

    public Date getDiscoveryLastDate() {
        return discoveryLastDate;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setData(String data) {
        this.data = data;
    }

    public void setTpl(String tpl) {
        this.tpl = tpl;
    }

    public void setAreaId(String areaId) {
        this.areaId = areaId;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public void setShowName(String showName) {
        this.showName = showName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public void setCloudbootId(Integer cloudbootId) {
        this.cloudbootId = cloudbootId;
    }

    public void setDiscoveryLastDate(Date discoveryLastDate) {
        this.discoveryLastDate = discoveryLastDate;
    }
}
