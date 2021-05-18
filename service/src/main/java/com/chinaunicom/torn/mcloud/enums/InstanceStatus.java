package com.chinaunicom.torn.mcloud.enums;

public enum InstanceStatus {
    PRE_INSTALL("pre_install");

    private final String value;

    private InstanceStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
