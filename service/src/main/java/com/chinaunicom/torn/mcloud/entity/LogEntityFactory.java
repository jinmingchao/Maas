package com.chinaunicom.torn.mcloud.entity;

/**
 * LogEntityFactory
 */
public class LogEntityFactory {
    private Class<?> clazz;
    private String who;

    public LogEntityFactory(Class<?> clazz) {
        this.clazz = clazz;
    }

    public LogEntityFactory(Class<?> clazz, String who) {
        this.clazz = clazz;
        this.who = who;
    }

    public LogEntityBuilder product() {
        return LogEntityBuilder.generate().where(this.clazz).who(this.who);
    }
}
