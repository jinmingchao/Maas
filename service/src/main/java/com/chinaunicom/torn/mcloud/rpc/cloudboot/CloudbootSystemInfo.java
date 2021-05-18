package com.chinaunicom.torn.mcloud.rpc.cloudboot;

import com.alibaba.fastjson.annotation.JSONField;

public class CloudbootSystemInfo {

    @JSONField(name = "ID")
    private Integer id;
    @JSONField(name = "CreateAt")
    private String createAt;
    @JSONField(name = "DeletedAt")
    private String deletedAt;
    @JSONField(name = "UpdatedAt")
    private String updatedAt;
    @JSONField(name = "Name")
    private String name;
    @JSONField(name = "Content")
    private String content;

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getContent() {
        return content;
    }

    public String getCreateAt() {
        return createAt;
    }

    public String getDeletedAt() {
        return deletedAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setCreateAt(String createAt) {
        this.createAt = createAt;
    }

    public void setDeletedAt(String deletedAt) {
        this.deletedAt = deletedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}
