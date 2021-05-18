package com.chinaunicom.torn.mcloud.rpc.cloudboot;

import com.alibaba.fastjson.annotation.JSONField;

public class CloudbootPxeInfo {

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
    @JSONField(name = "Pxe")
    private String pxe;

    public Integer getId() {
        return id;
    }

    public String getPxe() {
        return pxe;
    }

    public String getName() {
        return name;
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

    public void setPxe(String pxe) {
        this.pxe = pxe;
    }

    public void setName(String name) {
        this.name = name;
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
