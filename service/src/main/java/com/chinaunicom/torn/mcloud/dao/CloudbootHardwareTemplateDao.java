package com.chinaunicom.torn.mcloud.dao;

import com.chinaunicom.torn.mcloud.entity.CloudbootHardwareTemplateEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CloudbootHardwareTemplateDao extends JpaRepository<CloudbootHardwareTemplateEntity, Integer> {

    
}
