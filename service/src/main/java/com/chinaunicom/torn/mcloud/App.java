package com.chinaunicom.torn.mcloud;

import java.text.SimpleDateFormat;
import java.util.TimeZone;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Springboot Starter
 */
@SpringBootApplication
@EnableScheduling
public class App {

    public static void main(String args[]) {
        SpringApplication.run(App.class, args);
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper()
            .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
            .setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"))
            .setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
    }
}
