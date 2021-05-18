package com.chinaunicom.torn.mcloud.service.impl;

import java.io.IOException;
import java.util.Date;
import java.util.Optional;

import com.chinaunicom.torn.mcloud.cronjob.CronJobMetadata;
import com.chinaunicom.torn.mcloud.dao.CloudbootAreaDao;
import com.chinaunicom.torn.mcloud.entity.CloudbootAreaEntity;
import com.chinaunicom.torn.mcloud.entity.LogEntityFactory;
import com.chinaunicom.torn.mcloud.enums.CronJobParam;
import com.chinaunicom.torn.mcloud.enums.CronJobType;
import com.chinaunicom.torn.mcloud.enums.LogHow;
import com.chinaunicom.torn.mcloud.enums.ServiceRole;
import com.chinaunicom.torn.mcloud.service.AuthenticationService;
import com.chinaunicom.torn.mcloud.service.LoggerService;
import com.chinaunicom.torn.mcloud.service.SchedulerService;
import com.chinaunicom.torn.mcloud.utils.ClassScanner;

import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Example;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.scheduling.quartz.SimpleTriggerFactoryBean;
import org.springframework.stereotype.Service;

@Service
public class SchedulerServiceImpl implements SchedulerService {
    private static final String SCHEDULER_GROUP = "SCHEDULER_GROUP";

    private static final LogEntityFactory logFactory = new LogEntityFactory(SchedulerServiceImpl.class);

    @Autowired
    private LoggerService logService;

    @Autowired
    @Lazy
    private SchedulerFactoryBean schedulerFactoryBean;

    @Autowired
    private ApplicationContext context;

    @Autowired
    private CloudbootAreaDao cloudbootAreaDao;

    @Autowired
    private AuthenticationService authenticationService;

    @Override
    @SuppressWarnings("unchecked")
    public void registerSpecCloudbootAreaScheduler(CloudbootAreaEntity area) throws IOException, ClassNotFoundException {
        ClassScanner.find(CronJobMetadata.class).forEach(clazz -> {
            CronJobMetadata metadata = clazz.getDeclaredAnnotation(CronJobMetadata.class);
            if (metadata == null) {
                return;
            }

            this.logService.info(SchedulerServiceImpl.logFactory.product()
                    .who(ServiceRole.PROMOTER).how(LogHow.STARTUP).what(String.format("register cloudboot area scheduler [%s (type: %s)]", clazz.getName(), metadata.type().toString())).build());

            if (metadata.type().equals(CronJobType.SYNC_CLOUDBOOT)) {
                long syncInstanceInterval = 24L * 60 * 60 * 1000;
                if (area.getSyncInstanceInterval() != null) {
                    syncInstanceInterval = area.getSyncInstanceInterval();
                }

                this.registerCloudbootAreaInterval((Class<? extends QuartzJobBean>) clazz, area.getId(), metadata.delay(), syncInstanceInterval);
            }
        });
        
    }

    public void stop() throws SchedulerException {
        Scheduler scheduler = schedulerFactoryBean.getScheduler();

        scheduler.clear();
    }

    @SuppressWarnings("unchecked")
    public void start() {
        CloudbootAreaEntity filter = new CloudbootAreaEntity();
        filter.setEnabled(true);

        this.cloudbootAreaDao.findAll(Example.of(filter)).forEach(area -> {
            this.authenticationService.cloudbootLogin(area);
        });
        try {
            ClassScanner.find(CronJobMetadata.class).forEach(clazz -> {
                CronJobMetadata metadata = clazz.getDeclaredAnnotation(CronJobMetadata.class);
                if (metadata == null) {
                    return;
                }

                this.logService.info(SchedulerServiceImpl.logFactory.product()
                        .who(ServiceRole.PROMOTER).how(LogHow.STARTUP).what(String.format("register cloudboot area scheduler [%s (type: %s)]", clazz.getName(), metadata.type().toString())).build());
                this.registerCloudbootArea((Class<? extends QuartzJobBean>) clazz);
            });
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

    }


    
    @Override
    public void registerCloudbootArea(Class<? extends QuartzJobBean> clazz) {
        CronJobMetadata metadata = clazz.getDeclaredAnnotation(CronJobMetadata.class);

        if (metadata.type().equals(CronJobType.SYNC_CLOUDBOOT)) {
            this.authenticationService.getAllCloudbootTokens()
                .forEach(token -> {
                    Optional<CloudbootAreaEntity> area = this.cloudbootAreaDao.findById(token.getId());
                    long syncInstanceInterval = 24L * 60 * 60 * 1000;
                    try {
                        if (area.isPresent() && area.get().getSyncInstanceInterval() != null) {
                            syncInstanceInterval = area.get().getSyncInstanceInterval();
                        }

                        this.registerCloudbootAreaInterval(clazz, token.getId(), metadata.delay(), syncInstanceInterval);
                    } catch (Exception e) {
                        e.printStackTrace();
                        this.logService.error(SchedulerServiceImpl.logFactory.product()
                                .who(ServiceRole.PROMOTER).how(LogHow.STARTUP).what(String.format("register cloudboot area scheduler [%s (type: %s)]", clazz.getName(), metadata.type().toString())).why(e.getMessage()).build());
                    }
                });
        }
    }

    private void registerCloudbootAreaInterval(Class<? extends QuartzJobBean> clazz, String areaKey, long delay, long interval) {

        SimpleTriggerFactoryBean simpleFactoryBean = new SimpleTriggerFactoryBean();
        simpleFactoryBean.setName(String.format("%s_%s_trigger", clazz.getName(), areaKey));
        simpleFactoryBean.setStartTime(new Date(new Date().getTime() + delay));
        simpleFactoryBean.setMisfireInstruction(SimpleTrigger.MISFIRE_INSTRUCTION_FIRE_NOW);
        simpleFactoryBean.setRepeatInterval(interval);
        simpleFactoryBean.setRepeatCount(-1);

        JobDetailFactoryBean jobFactoryBean = new JobDetailFactoryBean();
        jobFactoryBean.setJobClass(clazz);
        jobFactoryBean.setDurability(false);
        jobFactoryBean.setApplicationContext(this.context);
        jobFactoryBean.setName(String.format("%s_%s_job", clazz.getName(), areaKey));
        jobFactoryBean.setGroup(String.format("%s_%s_job", SchedulerServiceImpl.SCHEDULER_GROUP, areaKey));

        JobDataMap param = new JobDataMap();
        param.put(CronJobParam.AREA_ID.toString(), areaKey);
        jobFactoryBean.setJobDataMap(param);

        this.logService.info(SchedulerServiceImpl.logFactory.product()
                .who(ServiceRole.CALLER).how(LogHow.CALL).what(String.format("register quartz job: %s", clazz.getName())).build());

        try {
            simpleFactoryBean.afterPropertiesSet();
            jobFactoryBean.afterPropertiesSet();

            Trigger trigger = simpleFactoryBean.getObject();
            JobDetail jobDetail = jobFactoryBean.getObject();

            Scheduler scheduler = this.schedulerFactoryBean.getScheduler();
            scheduler.scheduleJob(jobDetail, trigger);
        }
        catch (Exception ex) {
            ex.printStackTrace();
            this.logService.error(SchedulerServiceImpl.logFactory.product()
                    .who(ServiceRole.CALLER).how(LogHow.CALL).what(String.format("register quartz job: %s failed", clazz.getName())).why(ex.getMessage()).build());
        }
    }
}
