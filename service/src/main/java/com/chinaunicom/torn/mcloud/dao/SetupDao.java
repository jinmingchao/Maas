package com.chinaunicom.torn.mcloud.dao;

import com.chinaunicom.torn.mcloud.entity.SetupEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SetupDao extends JpaRepository<SetupEntity, Integer> {

    
}
