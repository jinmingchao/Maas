package com.chinaunicom.torn.mcloud.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "tb_project_cloudboot_hardware")
public class ProjectCloudbootHardwareEntity {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "project_id")
    private Integer projectId;
    @Column(name = "hardware_id")
    private Integer hardwareId;

    public ProjectCloudbootHardwareEntity() { }

    public ProjectCloudbootHardwareEntity(Integer projId, Integer hardwareId) {
        this.projectId = projId;
        this.hardwareId = hardwareId;
    }

    public Integer getId() {
        return id;
    }

    public Integer getProjectId() {
        return projectId;
    }

    public Integer getHardwareId() {
        return hardwareId;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }

    public void setHardwareId(Integer hardwareId) {
        this.hardwareId = hardwareId;
    }
}
