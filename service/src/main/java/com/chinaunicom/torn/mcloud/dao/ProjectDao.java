package com.chinaunicom.torn.mcloud.dao;

import com.chinaunicom.torn.mcloud.entity.ProjectEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectDao extends JpaRepository<ProjectEntity, Integer> {

    
}
