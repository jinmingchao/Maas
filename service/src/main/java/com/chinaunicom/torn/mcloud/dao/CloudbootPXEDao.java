package com.chinaunicom.torn.mcloud.dao;

import com.chinaunicom.torn.mcloud.entity.CloudbootPXEEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CloudbootPXEDao extends JpaRepository<CloudbootPXEEntity, Integer> {

    
}
