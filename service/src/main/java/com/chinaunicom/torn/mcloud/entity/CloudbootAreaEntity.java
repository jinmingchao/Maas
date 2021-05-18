package com.chinaunicom.torn.mcloud.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "tb_cloudboot_area")
public class CloudbootAreaEntity {

    @Id
    @Column(name = "id")
    private String id;
    @Column(name = "name")
    private String name;
    @Column(name = "host")
    private String host;
    @Column(name = "username")
    private String username;
    @Column(name = "password")
    private String password;
    @Column(name = "default_cloudboot_network_id")
    private Integer defaultCloudbootNetworkId;
    @Column(name = "sync_instance_interval")
    private Long syncInstanceInterval;
    @Column(name = "enabled")
    private Boolean enabled;

    public String getId() {
        return id;
    }

    public String getHost() {
        return host;
    }

    public String getName() {
        return name;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public String getPassword() {
        return password;
    }

    public String getUsername() {
        return username;
    }

    public Long getSyncInstanceInterval() {
        return syncInstanceInterval;
    }

    public Integer getDefaultCloudbootNetworkId() {
        return defaultCloudbootNetworkId;
    }

    public void setDefaultCloudbootNetworkId(Integer defaultCloudbootNetworkId) {
        this.defaultCloudbootNetworkId = defaultCloudbootNetworkId;
    }

    public void setSyncInstanceInterval(Long syncInstanceInterval) {
        this.syncInstanceInterval = syncInstanceInterval;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }


    public void setId(String id) {
        this.id = id;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String toString() {
        return "area{id: "+id+",name："+name+",host："+host+",username："+username+",enabled："+enabled+",syncInstanceInterval："+syncInstanceInterval+ "}";
    }
}
