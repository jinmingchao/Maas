package com.chinaunicom.torn.mcloud.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.chinaunicom.torn.mcloud.rpc.cloudboot.CloudbootPxeInfo;

@Entity
@Table(name = "tb_cloudboot_pxe")
public class CloudbootPXEEntity {

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
    @Column(name = "name")
    private String name;
    @Column(name = "pxe")
    private String pxe;
    @Column(name = "enabled")
    private Boolean enabled;

    public void transferCloudbootSyncInfo(CloudbootPxeInfo info, String areaKey) {
        this.areaId = areaKey;
        this.discoveryLastDate = new Date();
        this.cloudbootId = info.getId();
        this.name = info.getName();
        this.pxe = info.getPxe();
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAreaId() {
        return areaId;
    }

    public Integer getCloudbootId() {
        return cloudbootId;
    }

    public Date getDiscoveryLastDate() {
        return discoveryLastDate;
    }

    public String getPxe() {
        return pxe;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public void setPxe(String pxe) {
        this.pxe = pxe;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAreaId(String areaId) {
        this.areaId = areaId;
    }

    public void setCloudbootId(Integer cloudbootId) {
        this.cloudbootId = cloudbootId;
    }

    public void setDiscoveryLastDate(Date discoveryLastDate) {
        this.discoveryLastDate = discoveryLastDate;
    }
    
}
