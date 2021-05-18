package com.chinaunicom.torn.mcloud.cronjob;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.chinaunicom.torn.mcloud.enums.CronJobType;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface CronJobMetadata {
    CronJobType type();

    long delay();
}
