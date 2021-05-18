package com.chinaunicom.torn.mcloud.entity;

import javax.persistence.*;

@Entity
@Table(name = "tb_ip_table")
public class IPTableEntity {
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Id
    @Column(name = "id")
    private Integer id;
    @Column(name = "host")
    private String host;
    @Column(name = "pool_asst")
    private Integer poolAsst;
    @Column(name = "enabled")
    private boolean enabled;
    @Column(name = "sn")
    private String sn;

    public Integer getId() {
        return id;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public String getIp() {
        return host;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPoolAsst() {
        return poolAsst;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setPoolAsst(Integer poolAsst) {
        this.poolAsst = poolAsst;
    }


    public void setId(Integer id) {
        this.id = id;
    }


    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setIp(String host) {
        this.host = host;
    }
}
