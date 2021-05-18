package com.chinaunicom.torn.mcloud.message;

import java.util.Date;

import com.chinaunicom.torn.mcloud.entity.ProjectEntity;

public class CreateProjectMessage {

    private String name;
    private String description;

    public ProjectEntity generateProjectEntity() {
        ProjectEntity entity = new ProjectEntity();
        entity.setName(this.name);
        entity.setDescription(this.description);
        entity.setCreatedAt(new Date());

        return entity;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
