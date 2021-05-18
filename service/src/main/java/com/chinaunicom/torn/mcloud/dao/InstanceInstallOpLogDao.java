package com.chinaunicom.torn.mcloud.dao;

import com.chinaunicom.torn.mcloud.entity.InstanceInstallOpLogEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InstanceInstallOpLogDao extends JpaRepository<InstanceInstallOpLogEntity, Integer> {

    
}
