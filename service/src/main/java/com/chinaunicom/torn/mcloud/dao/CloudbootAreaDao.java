package com.chinaunicom.torn.mcloud.dao;

import com.chinaunicom.torn.mcloud.entity.CloudbootAreaEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
public interface CloudbootAreaDao extends JpaRepository<CloudbootAreaEntity, String> {
    @Transactional
    @Modifying
    @Query("update CloudbootAreaEntity e set e.name = :name, e.host = :host , e.username = :username," +
            "e.password = :password, e.syncInstanceInterval = :syncInstanceInterval,  e.enabled = :enabled where e.id = :id")
    void updateById(String name, String host, String username, String password, Long syncInstanceInterval, boolean enabled, String id);

    @Transactional
    void deleteById(String id);
    
}
