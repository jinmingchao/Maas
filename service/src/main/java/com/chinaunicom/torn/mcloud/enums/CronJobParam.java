package com.chinaunicom.torn.mcloud.enums;

public enum CronJobParam {
    AREA_ID("AREA_ID");

    private final String key;

    private CronJobParam(String key) {
        this.key = key;
    }

    @Override
    public String toString() {
        return this.key;
    }
}
