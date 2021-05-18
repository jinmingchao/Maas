package com.chinaunicom.torn.mcloud.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "tb_user_group")
public class UserGroupEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @Column(name = "username")
    private String username;
    @Column(name = "group_id")
    private Integer groupId;

    public Integer getId() {
        return id;
    }

    public Integer getGroupId() {
        return groupId;
    }

    public String getUsername() {
        return username;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
