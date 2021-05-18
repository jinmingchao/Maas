package com.chinaunicom.torn.mcloud.service;

import com.chinaunicom.torn.mcloud.entity.CloudbootAreaEntity;
import com.chinaunicom.torn.mcloud.entity.IPNetAreaEntity;
import com.chinaunicom.torn.mcloud.entity.IPPoolAreaEntity;
import com.chinaunicom.torn.mcloud.entity.IPTableEntity;
import com.chinaunicom.torn.mcloud.entity.InstallInstanceEntity;
import com.chinaunicom.torn.mcloud.message.BatchIPMessage;

import java.util.List;

public interface AreaZoneService {

    /**
     * 获取所有cloudBoot
     * @return List<CloudbootAreaEntity>
     */
    List<CloudbootAreaEntity> getAllArea();
    List<CloudbootAreaEntity> fetchAllCloud();
    /**
     * 创建 cloudBoot
     * @return void
     */
    void createCloudArea(CloudbootAreaEntity cloud);
    /**
     * 更新 cloudBoot
     * @return void
     */
    void updateCloudArea(CloudbootAreaEntity cloud);

    /**
     * 删除 cloudBoot
     * @return void
     */
    void deleteCloudArea(CloudbootAreaEntity cloud);

    /**
     * 获取所有 网络区域
     * @return List<IPNetAreaEntity>
     */
    List<IPNetAreaEntity> fetchAllNetArea();

    /**
     * 根据cloudBoot id 获取关联的 网络区域
     * @param  cloud CloudbootAreaEntity
     * @return List<IPNetAreaEntity
     */
    List<IPNetAreaEntity> fetchNetArea(CloudbootAreaEntity cloud);

    /**
     * 创建 网络区域
     * @return void
     */
    void createNetArea(IPNetAreaEntity net);

    /**
     * 更新 网络区域
     * @return void
     */
    void updateNetArea(IPNetAreaEntity net);

    /**
     * 删除 网络区域
     * @return void
     */
    void deleteNetArea(IPNetAreaEntity net);

    /**
     * 获取所有 ip 池
     * @return List<CloudbootAreaEntity>
     */
    List<IPPoolAreaEntity> fetchAllPool();

    /**
     * 获取 cloud area 区域下的 ip 池
     * @param area CloudbootAreaEntity
     * @return List<CloudbootAreaEntity>
     */
    List<IPPoolAreaEntity> fetchPoolByArea(CloudbootAreaEntity area);

    /**
     * 创建 ip 池
     * @return void
     */
    void createPool(IPPoolAreaEntity pool);

    /**
     * 更新 ip 池
     * @return void
     */
    void updatePool(IPPoolAreaEntity pool);

    /**
     * 删除 ip 池
     * @return void
     */
    void deletePool(IPPoolAreaEntity pool);

    /**
     * 根据网络区域 id 获取关联的 ip 池
     * @param  net IPNetAreaEntity
     * @return List<IPPoolAreaEntity
     */
    List<IPPoolAreaEntity> fetchPool(IPNetAreaEntity net);

    /**
     * 根据 ip池的id 获取 IP 列表
     * @param  pool pool
     * @return List<IPTableEntity
     */
    List<IPTableEntity> fetchIP(IPPoolAreaEntity pool);

    /**
     * 根据 ip池的id 获取 已使用 IP 列表
     * @param  pool pool
     * @return List<IPTableEntity
     */
    List<IPTableEntity> fetchUsedIP(IPPoolAreaEntity pool);

    /**
     * 根据 ip池的id 获取 未使用 IP 列表
     * @param  pool pool
     * @return List<IPTableEntity
     */
    List<IPTableEntity> fetchUnusedIP(IPPoolAreaEntity pool);

    /**
     * 根据entities中的ippool字段填充网关、子网掩码、vlanid等信息
     * @param entities
     */
    void ipPoolFillInstallInstance(List<InstallInstanceEntity> entities);

    /**
     * 使用一个IP
     * @return void
     */
    void setUsedIP(IPTableEntity ip, String sn);

    /**
     * 创建 ip
     * @return void
     */
    void createIP(IPTableEntity ip) ;

    /**
     * 更新 ip
     * @return void
     */
    void updateIP(IPTableEntity ip);

    void batchUpdateIP(BatchIPMessage ip);
    /**
     * 删除 ip
     * @return void
     */
    void deleteIP(IPTableEntity ip);

    
    /**
     * 获取IP是否可用
     * @param ippoolId
     * @param ip
     * @return
     */
    public Boolean getIpUsable(Integer ippoolId, String ip, String sn);

    /**
     * 设定IP已经使用
     * @param ippoolId
     * @param ip
     */
    public void setUsedIp(Integer ippoolId, String ip, String sn);

    /**
     * 设定IP未使用
     * @param ippoolId
     * @param ip
     */
    public void setUnusedIp(Integer ippoolId, String ip, String sn);
}
