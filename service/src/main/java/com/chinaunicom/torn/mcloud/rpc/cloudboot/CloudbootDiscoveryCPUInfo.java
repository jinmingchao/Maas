package com.chinaunicom.torn.mcloud.rpc.cloudboot;

import com.alibaba.fastjson.annotation.JSONField;

public class CloudbootDiscoveryCPUInfo {

    @JSONField(name = "Model")
    private String model;
    @JSONField(name = "Core")
    private Integer core;

    public Integer getCore() {
        return core;
    }

    public String getModel() {
        return model;
    }

    public void setCore(Integer core) {
        this.core = core;
    }

    public void setModel(String model) {
        this.model = model;
    }
}
