package com.chinaunicom.torn.mcloud.dao;

import com.chinaunicom.torn.mcloud.entity.IPPoolAreaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface IPPoolAreaDao extends JpaRepository<IPPoolAreaEntity, Integer> {

    List<IPPoolAreaEntity> findAllByNetAsst(Integer netAsst);
    List<IPPoolAreaEntity> findAllByNetAsstIn(List<Integer> netAsstList);
    List<IPPoolAreaEntity> findAllByNetAsstAndEnabledIsTrue(Integer netAsst);
    List<IPPoolAreaEntity> findAllByNetAsstAndEnabledIsFalse(Integer netAsst);

    @Transactional
    @Modifying
    @Query("update IPPoolAreaEntity e set e.name = :name, e.cidr = :cidr, e.netAsst = :netAsst, e.netmask = :netmask, e.gatewayIp = :gatewayIp," +
            " e.vlanId = :vlanId, e.enabled = :enabled where e.id = :id")
    void updateById(String name, String cidr, Integer netAsst, String netmask, String gatewayIp, Integer vlanId, boolean enabled, Integer id);

    @Transactional
    void deleteById(Integer id);

}
