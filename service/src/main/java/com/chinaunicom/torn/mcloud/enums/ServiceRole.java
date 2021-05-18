package com.chinaunicom.torn.mcloud.enums;

public enum ServiceRole {

    PROMOTER("Promoter"),
    CALLER("Caller"),
    SCHEDULER("Scheduler");

    private final String name;

    private ServiceRole(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
