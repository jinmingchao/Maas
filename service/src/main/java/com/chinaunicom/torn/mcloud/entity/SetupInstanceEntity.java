package com.chinaunicom.torn.mcloud.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "tb_setup_instance")
public class SetupInstanceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @Column(name = "setup_id")
    private Integer setupId;
    @Column(name = "sn")
    private String sn;

    public SetupInstanceEntity(Integer setupId, String sn) {
        this.setupId = setupId;
        this.sn = sn;
    }

    public SetupInstanceEntity() {

    }

    public String getSn() {
        return sn;
    }

    public Integer getId() {
        return id;
    }

    public Integer getSetupId() {
        return setupId;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setSetupId(Integer setupId) {
        this.setupId = setupId;
    }
}
