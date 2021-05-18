package com.chinaunicom.torn.mcloud;

import com.chinaunicom.torn.mcloud.cronjob.CronJobMetadata;
import com.chinaunicom.torn.mcloud.dao.CloudbootAreaDao;
import com.chinaunicom.torn.mcloud.entity.CloudbootAreaEntity;
import com.chinaunicom.torn.mcloud.entity.LogEntityFactory;
import com.chinaunicom.torn.mcloud.enums.LogHow;
import com.chinaunicom.torn.mcloud.enums.ServiceRole;
import com.chinaunicom.torn.mcloud.service.AuthenticationService;
import com.chinaunicom.torn.mcloud.service.LoggerService;
import com.chinaunicom.torn.mcloud.service.SchedulerService;
import com.chinaunicom.torn.mcloud.utils.ClassScanner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Component;
import org.springframework.scheduling.quartz.QuartzJobBean;

@Component
public class AppStartupCloudboots implements ApplicationRunner {

    private static LogEntityFactory logFactory = new LogEntityFactory(AppStartupCloudboots.class);

    @Autowired
    private LoggerService logService;

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private CloudbootAreaDao cloudbootAreaDao;

    @Autowired
    private SchedulerService schedulerService;

    @Override
    @SuppressWarnings("unchecked")
    public void run(ApplicationArguments args) throws Exception {
        CloudbootAreaEntity filter = new CloudbootAreaEntity();
        filter.setEnabled(true);

        this.cloudbootAreaDao.findAll(Example.of(filter)).forEach(area -> {
            this.logService.info(AppStartupCloudboots.logFactory.product()
                    .who(ServiceRole.PROMOTER).how(LogHow.STARTUP).what(String.format("login Datacenter %s Cloudboot", area.getName())).build());
            try {
                this.authenticationService.cloudbootLogin(area);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        ClassScanner.find(CronJobMetadata.class).forEach(clazz -> {
            CronJobMetadata metadata = clazz.getDeclaredAnnotation(CronJobMetadata.class);
            if (metadata == null) {
                return;
            }

            this.logService.info(AppStartupCloudboots.logFactory.product()
                    .who(ServiceRole.PROMOTER).how(LogHow.STARTUP).what(String.format("register cloudboot area scheduler [%s (type: %s)]", clazz.getName(), metadata.type().toString())).build());
//            try {
                this.schedulerService.registerCloudbootArea((Class<? extends QuartzJobBean>) clazz);
//            } catch (Exception e) {
//                this.logService.error(AppStartupCloudboots.logFactory.product()
//                    .who(ServiceRole.PROMOTER).how(LogHow.STARTUP).what(String.format("register cloudboot area scheduler [%s (type: %s)]", clazz.getName(), metadata.type().toString())).why(e.getMessage()).build());
//            }
        });
    }

}
