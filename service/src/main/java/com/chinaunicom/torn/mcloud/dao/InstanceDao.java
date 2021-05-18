package com.chinaunicom.torn.mcloud.dao;

import com.chinaunicom.torn.mcloud.entity.InstanceEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InstanceDao extends JpaRepository<InstanceEntity, String> {


}
