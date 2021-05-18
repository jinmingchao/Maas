package com.chinaunicom.torn.mcloud.rpc.cloudboot;

import com.alibaba.fastjson.annotation.JSONField;

public class CloudbootHardwareInfo {
    
    @JSONField(name = "ID")
    private Integer id;
    @JSONField(name = "Bios")
    private String bios;
    @JSONField(name = "Company")
    private String company;
    @JSONField(name = "Data")
    private String data;
    @JSONField(name = "IsSystemAdd")
    private String isSystemAdd;
    @JSONField(name = "Oob")
    private String oob;
    @JSONField(name = "Product")
    private String product;
    @JSONField(name = "Raid")
    private String raid;
    @JSONField(name = "ModelName")
    private String modelName;
    @JSONField(name = "ShowName")
    private String showName;
    @JSONField(name = "Source")
    private String source;
    @JSONField(name = "Status")
    private String status;
    @JSONField(name = "Tpl")
    private String tpl;
    @JSONField(name = "Version")
    private String version;

    public Integer getId() {
        return id;
    }

    public String getOob() {
        return oob;
    }

    public String getTpl() {
        return tpl;
    }

    public String getBios() {
        return bios;
    }

    public String getData() {
        return data;
    }

    public String getRaid() {
        return raid;
    }

    public String getSource() {
        return source;
    }

    public String getStatus() {
        return status;
    }

    public String getCompany() {
        return company;
    }
    
    public String getProduct() {
        return product;
    }

    public String getVersion() {
        return version;
    }

    public String getShowName() {
        return showName;
    }

    public String getIsSystemAdd() {
        return isSystemAdd;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setOob(String oob) {
        this.oob = oob;
    }

    public void setTpl(String tpl) {
        this.tpl = tpl;
    }

    public void setBios(String bios) {
        this.bios = bios;
    }

    public void setData(String data) {
        this.data = data;
    }

    public void setRaid(String raid) {
        this.raid = raid;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void setShowName(String showName) {
        this.showName = showName;
    }

    public void setIsSystemAdd(String isSystemAdd) {
        this.isSystemAdd = isSystemAdd;
    }
}
