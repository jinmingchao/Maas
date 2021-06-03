package com.chinaunicom.torn.mcloud;

import com.chinaunicom.torn.mcloud.enums.InstanceInstallOpType;
import com.chinaunicom.torn.mcloud.service.InstanceService;
import com.chinaunicom.torn.mcloud.service.LoggerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Author MingChao Jin
 * @Date 2021/5/26 14:10
 * @Description: TODO
 */

@EnableScheduling
//@Component
public class ScheduledTasks {

    @Autowired
    private LoggerService loggerService;

    @Autowired
    private InstanceService instanceService;

    //每30秒执行一次
    @Scheduled(fixedRate = 1000 * 30, fixedDelay = 1000 * 2, initialDelay = 1000 * 2 )
    public void reportCurrentTime(){


    }

    //在固定时间执行
    @Scheduled(cron = "0 */1 *  * * * ")
    public void reportCurrentByCron(){
        System.out.println ("Scheduling Tasks Examples By Cron: The time is now " + dateFormat ().format (new Date()));
    }

    private SimpleDateFormat dateFormat(){
        return new SimpleDateFormat ("HH:mm:ss");
    }

    public static void main(String[] args) {
        InstanceInstallOpType firstDay = InstanceInstallOpType.INSTALL;
        System.out.println(firstDay.name());
        System.out.println(InstanceInstallOpType.INSTALL);
    }
}
