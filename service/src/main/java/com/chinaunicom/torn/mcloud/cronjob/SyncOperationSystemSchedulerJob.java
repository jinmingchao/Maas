package com.chinaunicom.torn.mcloud.cronjob;

import com.chinaunicom.torn.mcloud.entity.LogEntityFactory;
import com.chinaunicom.torn.mcloud.enums.CronJobParam;
import com.chinaunicom.torn.mcloud.enums.CronJobType;
import com.chinaunicom.torn.mcloud.enums.LogHow;
import com.chinaunicom.torn.mcloud.enums.ServiceRole;
import com.chinaunicom.torn.mcloud.service.CloudbootInfoService;
import com.chinaunicom.torn.mcloud.service.LoggerService;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;

@CronJobMetadata(type = CronJobType.SYNC_CLOUDBOOT, delay = 3 * 1000)
public class SyncOperationSystemSchedulerJob extends QuartzJobBean {

    private static final LogEntityFactory logFactory = new LogEntityFactory(SyncOperationSystemSchedulerJob.class, ServiceRole.SCHEDULER.name());

    @Autowired
    private LoggerService loggerService;

    @Autowired
    private CloudbootInfoService infoService;

    @Override
    protected void executeInternal(JobExecutionContext ctx) throws JobExecutionException {
        String areaKey = ctx.getJobDetail().getJobDataMap().getString(CronJobParam.AREA_ID.toString());

        this.loggerService.info(SyncOperationSystemSchedulerJob.logFactory.product()
                .how(LogHow.SCHEDULE).what(String.format("schedule sync [area: %s] operation system", areaKey)).build());

        this.infoService.syncOperationSystem(areaKey);
    }
}
