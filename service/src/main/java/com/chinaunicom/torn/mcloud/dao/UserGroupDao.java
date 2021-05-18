package com.chinaunicom.torn.mcloud.dao;

import com.chinaunicom.torn.mcloud.entity.UserGroupEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserGroupDao extends JpaRepository<UserGroupEntity, Integer> {

    
}
