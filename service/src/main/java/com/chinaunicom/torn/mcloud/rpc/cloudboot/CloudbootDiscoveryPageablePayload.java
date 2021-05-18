package com.chinaunicom.torn.mcloud.rpc.cloudboot;

import com.alibaba.fastjson.annotation.JSONField;

public class CloudbootDiscoveryPageablePayload extends CloudbootPageablePayload {

    @JSONField(name = "IsShowActiveDevice")
    private String isShowActiveDevice;
    @JSONField(name = "HardwareID")
    private Integer hardwareId;
    @JSONField(name = "Keyword")
    private String keyword;
    @JSONField(name = "ModelName")
    private String modelName;
    @JSONField(name = "OsID")
    private String osId;
    @JSONField(name = "Product")
    private String product;
    @JSONField(name = "Status")
    private String status;
    @JSONField(name = "SystemID")
    private Integer systemId;

    public static CloudbootDiscoveryPageablePayload build() {
        return CloudbootDiscoveryPageablePayload.build(CloudbootPageablePayload.MAX_LIMIT);
    }

    public static CloudbootDiscoveryPageablePayload build(Integer limit) {
        return CloudbootDiscoveryPageablePayload.build(0, limit);
    }

    public static CloudbootDiscoveryPageablePayload build(Integer offset, Integer limit) {
        CloudbootDiscoveryPageablePayload payload = new CloudbootDiscoveryPageablePayload();

        payload.setLimit(limit);
        payload.setOffset(offset);

        return payload;
    }

    public String getIsShowActiveDevice() {
        return isShowActiveDevice;
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

    public String getProduct() {
        return product;
    }

    public String getModelName() {
        return modelName;
    }

    public Integer getSystemId() {
        return systemId;
    }

    public Integer getHardwareId() {
        return hardwareId;
    }

    public void setIsShowActiveDevice(String isShowActiveDevice) {
        this.isShowActiveDevice = isShowActiveDevice;
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

    public void setProduct(String product) {
        this.product = product;
    }

    public void setSystemId(Integer systemId) {
        this.systemId = systemId;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public void setHardwareId(Integer hardwareId) {
        this.hardwareId = hardwareId;
    }
}
