package com.chinaunicom.torn.mcloud.entity;

import java.util.Date;

import com.chinaunicom.torn.mcloud.enums.LogHow;
import com.chinaunicom.torn.mcloud.enums.ServiceRole;

public class LogEntityBuilder {

    private LogEntity entity;

    private LogEntityBuilder() {
        this.entity = new LogEntity();
        this.when(new Date());
    }

    public static LogEntityBuilder generate() {
        return new LogEntityBuilder();
    }

    public LogEntityBuilder where(Class<?> clazz) {
        this.entity.setWhere(clazz.toString());
        return this;
    }

    public LogEntityBuilder when(Date date) {
        this.entity.setWhen(date.toString());
        return this;
    }

    public LogEntityBuilder who(String who) {
        this.entity.setWho(who);
        return this;
    }

    public LogEntityBuilder who(ServiceRole role) {
        this.entity.setHow(role.toString());
        return this;
    }

    public LogEntityBuilder why(String why) {
        this.entity.setWhy(why);
        return this;
    }

    public LogEntityBuilder what(String what) {
        this.entity.setWhat(what);
        return this;
    }

    public LogEntityBuilder how(String how) {
        this.entity.setHow(how);
        return this;
    }

    public LogEntityBuilder how(LogHow how) {
        this.entity.setHow(how.toString());
        return this;
    }

    public LogEntity build() {
        return this.entity;
    }
}
