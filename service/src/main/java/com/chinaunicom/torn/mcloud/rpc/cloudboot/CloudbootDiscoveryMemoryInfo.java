package com.chinaunicom.torn.mcloud.rpc.cloudboot;

import com.alibaba.fastjson.annotation.JSONField;

public class CloudbootDiscoveryMemoryInfo {

    @JSONField(name = "Name")
    private String name;
    @JSONField(name = "Size")
    private String size;

    public String getName() {
        return name;
    }

    public String getSize() {
        return size;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSize(String size) {
        this.size = size;
    }
}
