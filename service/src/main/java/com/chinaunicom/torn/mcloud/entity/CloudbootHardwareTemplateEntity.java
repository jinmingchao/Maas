package com.chinaunicom.torn.mcloud.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "tb_cloudboot_hardware_template")
public class CloudbootHardwareTemplateEntity {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "company")
    private String company;
    @Column(name = "name")
    private String name;
    @Column(name = "tpl")
    private String tpl;

    public Integer getId() {
        return id;
    }

    public String getTpl() {
        return tpl;
    }

    public String getName() {
        return name;
    }

    public String getCompany() {
        return company;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setTpl(String tpl) {
        this.tpl = tpl;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCompany(String company) {
        this.company = company;
    }
}
