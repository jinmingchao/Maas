package com.chinaunicom.torn.mcloud.dao;

import com.chinaunicom.torn.mcloud.entity.IPTableEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Repository
public interface IPTableDao extends JpaRepository<IPTableEntity, Integer> {

    List<IPTableEntity> findAllByPoolAsst(Integer poolAsst);
    List<IPTableEntity> findAllByPoolAsstAndEnabledIsTrue(Integer poolAsst);
    List<IPTableEntity> findAllByPoolAsstAndEnabledIsFalse(Integer poolAsst);


    @Transactional
    @Modifying
    @Query("update IPTableEntity e set e.enabled = false, e.sn = :sn where e.id = :id")
    void useIP(Integer id, String sn);

    @Transactional
    @Modifying
    @Query("update IPTableEntity e set e.enabled = true, e.sn = '' where e.id = :id")
    void unuseIp(Integer id);

    @Transactional
    @Modifying
    @Query("update IPTableEntity e set e.host = :host, e.poolAsst = :poolAsst," +
            "e.enabled = :enabled where e.id = :id")
    void updateById(String host, Integer poolAsst, boolean enabled, Integer id);

    @Query("select e from IPTableEntity e where e.host = :host and e.poolAsst = :poolAsst")
    public Optional<IPTableEntity> findOneByHostAndIppool(@Param("host") String host, @Param("poolAsst") Integer poolAsst);

    @Transactional
    void deleteAllByPoolAsst(Integer poolAsst);


    @Transactional
    @Modifying
    @Query("update IPTableEntity e set e.enabled = :enabled where e.id in :id")
    void batchUpdateById(boolean enabled, List<Integer> id);
}
