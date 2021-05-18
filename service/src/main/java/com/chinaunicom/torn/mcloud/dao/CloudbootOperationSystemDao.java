package com.chinaunicom.torn.mcloud.dao;

import com.chinaunicom.torn.mcloud.entity.CloudbootOperationSystemEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CloudbootOperationSystemDao extends JpaRepository<CloudbootOperationSystemEntity, Integer> {

    
}
