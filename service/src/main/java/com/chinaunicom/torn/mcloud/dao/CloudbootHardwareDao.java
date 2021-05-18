package com.chinaunicom.torn.mcloud.dao;

import com.chinaunicom.torn.mcloud.entity.CloudbootHardwareEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CloudbootHardwareDao extends JpaRepository<CloudbootHardwareEntity, Integer> {

    
}
