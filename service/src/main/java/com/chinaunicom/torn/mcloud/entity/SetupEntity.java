package com.chinaunicom.torn.mcloud.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "tb_setup")
public class SetupEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @Column(name = "area_id")
    private String areaId;
    @Column(name = "name")
    private String name;
    @Column(name = "created_at")
    private Date date;
    @Column(name = "who")
    private String who;
    @Column(name = "callback_address")
    private String callback;

    public SetupEntity() { }

    public SetupEntity(String name, String areaId) {
        this.name = name;
        this.areaId = areaId;
        this.date = new Date();
    }

    public Date getDate() {
        return date;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getWho() {
        return who;
    }

    public String getCallback() {
        return callback;
    }

    public String getAreaId() {
        return areaId;
    }

    public void setAreaId(String areaId) {
        this.areaId = areaId;
    }

    public void setWho(String who) {
        this.who = who;
    }

    public void setCallback(String callback) {
        this.callback = callback;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setName(String name) {
        this.name = name;
    }
}
