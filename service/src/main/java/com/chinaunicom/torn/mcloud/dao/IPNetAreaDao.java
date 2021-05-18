package com.chinaunicom.torn.mcloud.dao;

import com.chinaunicom.torn.mcloud.entity.IPNetAreaEntity;
import com.chinaunicom.torn.mcloud.entity.IPPoolAreaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface IPNetAreaDao extends JpaRepository<IPNetAreaEntity, Integer> {

    List<IPNetAreaEntity> findAllByAreaId(String areaId);
    @Query("select e.id from IPNetAreaEntity  e where e.areaId = :areaId")
    List<Integer> findIdByAreaId(String areaId);
    List<IPNetAreaEntity> findAllByAreaIdAndEnabledIsTrue(String areaId);
    List<IPNetAreaEntity> findAllByAreaIdAndEnabledIsFalse(String areaId);

    @Transactional
    @Modifying
    @Query("update IPNetAreaEntity e set e.name = :name, e.comment = :comment, e.areaId = :areaId," +
            "e.enabled = :enabled where e.id = :id")
    void updateById(String name, String comment, String areaId, boolean enabled, Integer id);

    @Transactional
    void deleteById(Integer id);


}
