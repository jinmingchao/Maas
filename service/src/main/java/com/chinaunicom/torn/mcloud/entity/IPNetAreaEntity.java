package com.chinaunicom.torn.mcloud.entity;

import javax.persistence.*;

@Entity
@Table(name = "tb_ip_net_area")
public class IPNetAreaEntity {
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Id
    @Column(name = "id")
    private Integer id;
    @Column(name = "name")
    private String name;
    @Column(name = "comment")
    private String comment;
    @Column(name = "area_id")
    private String areaId;
    @Column(name = "enabled")
    private boolean enabled;

    public String getAreaId() {
        return areaId;
    }

    public String getComment() {
        return comment;
    }

    public void setAreaId(String areaId) {
        this.areaId = areaId;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }


    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }


    public void setId(Integer id) {
        this.id = id;
    }


    public void setName(String name) {
        this.name = name;
    }

    public String toString() {
        return "pool{id: "+id+",name："+name+",comment："+comment+",areaId："+areaId+",enabled："+enabled+"}";
    }

}
