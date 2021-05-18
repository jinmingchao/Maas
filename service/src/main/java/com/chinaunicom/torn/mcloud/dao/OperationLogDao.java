package com.chinaunicom.torn.mcloud.dao;

import com.chinaunicom.torn.mcloud.entity.LogEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OperationLogDao extends JpaRepository<LogEntity, Integer> {

    
}
