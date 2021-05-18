package com.chinaunicom.torn.mcloud.dao;

import com.chinaunicom.torn.mcloud.entity.UserGroupProjectEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserGroupProjectDao extends JpaRepository<UserGroupProjectEntity, Integer> {

    
}
