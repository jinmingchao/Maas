package com.chinaunicom.torn.mcloud.dao;

import com.chinaunicom.torn.mcloud.entity.HistorySetupInstanceEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HistorySetupInstanceDao extends JpaRepository<HistorySetupInstanceEntity, Integer> {

    
}
