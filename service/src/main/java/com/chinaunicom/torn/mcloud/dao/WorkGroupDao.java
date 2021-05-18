package com.chinaunicom.torn.mcloud.dao;

import com.chinaunicom.torn.mcloud.entity.WorkGroupEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkGroupDao extends JpaRepository<WorkGroupEntity, Integer> {

    
}
