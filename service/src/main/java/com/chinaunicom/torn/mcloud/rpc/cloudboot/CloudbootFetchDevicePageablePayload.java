package com.chinaunicom.torn.mcloud.rpc.cloudboot;

import com.alibaba.fastjson.annotation.JSONField;

public class CloudbootFetchDevicePageablePayload extends CloudbootPageablePayload {

    @JSONField(name = "HardwareID")
    private Integer hardwareId;
    @JSONField(name = "Keyword")
    private String keyword;
    @JSONField(name = "OsID")
    private String osId;
    @JSONField(name = "Status")
    private String status;
    @JSONField(name = "SystemID")
    private Integer systemId;

    public static CloudbootFetchDevicePageablePayload build() {
        return CloudbootFetchDevicePageablePayload.build(CloudbootPageablePayload.MAX_LIMIT);
    }

    public static CloudbootFetchDevicePageablePayload build(Integer limit) {
        return CloudbootFetchDevicePageablePayload.build(0, limit);
    }

    public static CloudbootFetchDevicePageablePayload build(Integer offset, Integer limit) {
        CloudbootFetchDevicePageablePayload payload = new CloudbootFetchDevicePageablePayload();

        payload.setLimit(limit);
        payload.setOffset(offset);

        return payload;
    }

    public String getOsId() {
        return osId;
    }

    public String getStatus() {
        return status;
    }

    public String getKeyword() {
        return keyword;
    }

    public Integer getSystemId() {
        return systemId;
    }

    public Integer getHardwareId() {
        return hardwareId;
    }

    public void setOsId(String osId) {
        this.osId = osId;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public void setSystemId(Integer systemId) {
        this.systemId = systemId;
    }

    public void setHardwareId(Integer hardwareId) {
        this.hardwareId = hardwareId;
    }
    
}
