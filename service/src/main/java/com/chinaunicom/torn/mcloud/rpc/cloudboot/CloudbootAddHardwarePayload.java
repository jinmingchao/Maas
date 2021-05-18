package com.chinaunicom.torn.mcloud.rpc.cloudboot;

import com.alibaba.fastjson.annotation.JSONField;

public class CloudbootAddHardwarePayload {

    @JSONField(name = "Company")
    private String company;
    @JSONField(name = "Product")
    private String product;
    @JSONField(name = "ModelName")
    private String modelName;
    @JSONField(name = "Raid")
    private String raid;
    @JSONField(name = "Oob")
    private String oob;
    @JSONField(name = "Bios")
    private String bios;
    @JSONField(name = "IsSystemAdd")
    private String isSystemAdd;
    @JSONField(name = "Tpl")
    private String tpl;
    @JSONField(name = "Data")
    private String data;
    @JSONField(name = "Source")
    private String source;
    @JSONField(name = "Version")
    private String version;
    @JSONField(name = "Status")
    private String status;
    @JSONField(name = "AccessToken")
    private String accessToken;

    public String getOob() {
        return oob;
    }

    public void setOob(String oob) {
        this.oob = oob;
    }

    public String getTpl() {
        return tpl;
    }

    public void setTpl(String tpl) {
        this.tpl = tpl;
    }

    public String getBios() {
        return bios;
    }

    public void setBios(String bios) {
        this.bios = bios;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getRaid() {
        return raid;
    }

    public void setRaid(String raid) {
        this.raid = raid;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getIsSystemAdd() {
        return isSystemAdd;
    }

    public void setIsSystemAdd(String isSystemAdd) {
        this.isSystemAdd = isSystemAdd;
    }
}
