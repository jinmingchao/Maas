package com.chinaunicom.torn.mcloud.entity;

import java.util.Date;

import javax.persistence.*;

@Entity
@Table(name = "tb_history_setup_instance")
public class HistorySetupInstanceEntity {
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Id
    @Column(name = "id")
    private String id;
    @Column(name = "setup_id")
    private Integer setupId;
    @Column(name = "sn")
    private String sn;
    @Column(name = "create_at")
    private Date createAt;
    @Column(name = "detail")
    private String detail;

    public HistorySetupInstanceEntity() { }

    public HistorySetupInstanceEntity(Integer setupId, String sn, String detail) {
        this.setupId = setupId;
        this.sn = sn;
        this.createAt = new Date();
        this.detail = detail;
    }

    public String getId() {
        return id;
    }

    public String getSn() {
        return sn;
    }

    public Date getCreateAt() {
        return createAt;
    }

    public String getDetail() {
        return detail;
    }

    public Integer getSetupId() {
        return setupId;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public void setCreateAt(Date createAt) {
        this.createAt = createAt;
    }

    public void setSetupId(Integer setupId) {
        this.setupId = setupId;
    }
}
