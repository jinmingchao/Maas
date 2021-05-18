package com.chinaunicom.torn.mcloud.enums;

public enum LogHow {
    STARTUP("Startup"),
    CALL("Call"),
    SCHEDULE("Schedule"),
    TASK_QUEUE("TaskQueue"),
    UPDATE("update"),
    DELETE("delete"),
    CREATE("create"),
    LOGIN("login");


    private final String name;

    private LogHow(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
