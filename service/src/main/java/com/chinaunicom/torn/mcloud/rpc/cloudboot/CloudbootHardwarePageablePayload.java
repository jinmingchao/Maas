package com.chinaunicom.torn.mcloud.rpc.cloudboot;

import com.alibaba.fastjson.annotation.JSONField;

public class CloudbootHardwarePageablePayload extends CloudbootPageablePayload {

    @JSONField(name = "Company")
    private String company;
    @JSONField(name = "ModelName")
    private String modelName;
    @JSONField(name = "Product")
    private String product;

    public static CloudbootHardwarePageablePayload build() {
        return CloudbootHardwarePageablePayload.build(CloudbootPageablePayload.MAX_LIMIT);
    }

    public static CloudbootHardwarePageablePayload build(Integer limit) {
        return CloudbootHardwarePageablePayload.build(0, limit);
    }

    public static CloudbootHardwarePageablePayload build(Integer offset, Integer limit) {
        CloudbootHardwarePageablePayload payload = new CloudbootHardwarePageablePayload();

        payload.setLimit(limit);
        payload.setOffset(offset);

        return payload;
    }

    public String getCompany() {
        return company;
    }

    public String getProduct() {
        return product;
    }

    public String getModelName() {
        return modelName;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }
}
