package com.chinaunicom.torn.mcloud.service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.chinaunicom.torn.mcloud.entity.CloudbootTokenEntity;
import com.chinaunicom.torn.mcloud.rpc.cloudboot.CloudbootAddDeviceInstancePayload;
import com.chinaunicom.torn.mcloud.rpc.cloudboot.CloudbootAddHardwarePayload;
import com.chinaunicom.torn.mcloud.rpc.cloudboot.CloudbootAddOperationSystemPayload;
import com.chinaunicom.torn.mcloud.rpc.cloudboot.CloudbootAddPXEPayload;
import com.chinaunicom.torn.mcloud.rpc.cloudboot.CloudbootCancelInstallPayload;
import com.chinaunicom.torn.mcloud.rpc.cloudboot.CloudbootDeletePayload;
import com.chinaunicom.torn.mcloud.rpc.cloudboot.CloudbootDeviceInstanceInfo;
import com.chinaunicom.torn.mcloud.rpc.cloudboot.CloudbootDiscoveryInfo;
import com.chinaunicom.torn.mcloud.rpc.cloudboot.CloudbootHardwareInfo;
import com.chinaunicom.torn.mcloud.rpc.cloudboot.CloudbootNetworkInfo;
import com.chinaunicom.torn.mcloud.rpc.cloudboot.CloudbootOperationPayload;
import com.chinaunicom.torn.mcloud.rpc.cloudboot.CloudbootPxeInfo;
import com.chinaunicom.torn.mcloud.rpc.cloudboot.CloudbootResultStatusInfo;
import com.chinaunicom.torn.mcloud.rpc.cloudboot.CloudbootSystemInfo;
import com.chinaunicom.torn.mcloud.rpc.cloudboot.CloudbootUpdateHardwarePayload;
import com.chinaunicom.torn.mcloud.rpc.cloudboot.CloudbootUpdateOperationSystemPayload;
import com.chinaunicom.torn.mcloud.rpc.cloudboot.CloudbootUpdatePXEPayload;

public interface BaremetalService {

    /**
     * 登录Cloudboot获取登录token
     * @param username 账户
     * @param password 密码
     * @return Token
     */
    Optional<CloudbootTokenEntity> loginCloudboot(String host, String username, String password);

    /**
     * 从Cloudboot中获取PXE信息
     * @param token token
     * @return List<CloudbootPxeInfo>
     */
    List<CloudbootPxeInfo> getPxeInfos(CloudbootTokenEntity token);

    /**
     * 从Cloudboot中获取操作系统信息
     * @param token token
     * @return List<CloudbootSystemInfo>
     */
    List<CloudbootSystemInfo> getSystemInfos(CloudbootTokenEntity token);

    /**
     * 从Cloudboot中获取典配信息
     * @param token
     * @return
     */
    List<CloudbootHardwareInfo> getHardwareInfos(CloudbootTokenEntity token);

    /**
     * 从Cloudboot中发现新主机
     * @param token
     * @return
     */
    List<CloudbootDiscoveryInfo> getDiscoveryInfos(CloudbootTokenEntity token);


    /**
     * 从Cloudboot中获取设备列表
     * @param token
     * @return
     */
    List<CloudbootDeviceInstanceInfo> getDeviceInstanceInfos(CloudbootTokenEntity token);
    List<CloudbootDeviceInstanceInfo> getDeviceInstanceInfos(CloudbootTokenEntity token, Set<String> snList);

    /**
     * 从Cloudboot中获取网络列表
     * @param token
     * @return
     */
    List<CloudbootNetworkInfo> getNetworkInfos(CloudbootTokenEntity token);

    /**
     * 批量装机
     * @param payloads
     * @return 批量装机是否成功
     */
    CloudbootResultStatusInfo setupMetalInstance(List<CloudbootAddDeviceInstancePayload> payloads, CloudbootTokenEntity token);

    /**
     * 批量开机
     * @param operations
     * @param token
     * @return
     */
    CloudbootResultStatusInfo powerOn(List<CloudbootOperationPayload> operations, CloudbootTokenEntity token);

    /**
     * 批量关机
     * @param operations
     * @param token
     * @return
     */
    CloudbootResultStatusInfo powerOff(List<CloudbootOperationPayload> operations, CloudbootTokenEntity token);

    /**
     * 从硬盘重启
     * @param operations
     * @param token
     * @return
     */
    CloudbootResultStatusInfo restart(List<CloudbootOperationPayload> operations, CloudbootTokenEntity token);

    /**
     * 从PXE重启
     * @param operations
     * @param token
     * @return
     */
    CloudbootResultStatusInfo restartFromPXE(List<CloudbootOperationPayload> operations, CloudbootTokenEntity token);

    /**
     * 从Cloudboot删除
     * @param operations
     * @param token
     * @return
     */
    CloudbootResultStatusInfo deleteInstance(List<CloudbootDeletePayload> operations, CloudbootTokenEntity token);

    /**
     * 取消Cloudboot安装
     * @param payloads
     * @param token
     * @return
     */
    CloudbootResultStatusInfo cancelInstall(List<CloudbootCancelInstallPayload> payloads, CloudbootTokenEntity token);


    /**
     * 添加一个PXE信息
     * @param payload
     * @return
     */
    CloudbootResultStatusInfo addPxe(CloudbootAddPXEPayload payload, CloudbootTokenEntity token);

    /**
     * 更新一个PXE信息
     * @param payload
     * @param token
     * @return
     */
    CloudbootResultStatusInfo updatePxe(CloudbootUpdatePXEPayload payload, CloudbootTokenEntity token);

    /**
     * 添加一个操作系统信息
     * @param payload
     * @param token
     * @return
     */
    CloudbootResultStatusInfo addOperationSystem(CloudbootAddOperationSystemPayload payload, CloudbootTokenEntity token);

    /**
     * 更新一个操作系统信息
     * @param payload
     * @param token
     * @return
     */
    CloudbootResultStatusInfo updateOperationSystem(CloudbootUpdateOperationSystemPayload payload, CloudbootTokenEntity token);

    /**
     * 添加一个硬件典配
     * @return
     */
    CloudbootResultStatusInfo addHardware(CloudbootAddHardwarePayload payload, CloudbootTokenEntity token);

    /**
     * 更新一个硬件典配
     * @param payload
     * @param token
     * @return
     */
    CloudbootResultStatusInfo updateHardware(CloudbootUpdateHardwarePayload payload, CloudbootTokenEntity token);
}
