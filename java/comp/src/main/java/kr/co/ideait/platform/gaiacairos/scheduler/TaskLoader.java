package kr.co.ideait.platform.gaiacairos.scheduler;

import com.tware.components.scheduler.TaskInitializer;
import com.tware.components.scheduler.dto.JobRequest;
import com.tware.components.scheduler.service.ScheduleService;
import kr.co.ideait.platform.gaiacairos.core.base.AbstractComponent;
import kr.co.ideait.platform.gaiacairos.scheduler.loader.task.RetryTask;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobDataMap;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TaskLoader extends AbstractComponent implements TaskInitializer {

    @Override
    public void initialize(ScheduleService scheduleService) {
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("jobId", "123456789");

        JobRequest jobRequest = new JobRequest();
        jobRequest.setJobName("cronJob");
        jobRequest.setCronExpression("0 * * ? * *"); //every min
//        jobRequest.setCronExpression("0 0 */1 ? * *");
        jobRequest.setJobDataMap(jobDataMap);

//        scheduleService.addJob(jobRequest, BackupTask.class);

        jobRequest = new JobRequest();
        jobRequest.setJobName("retryJob1");
//        jobRequest.setCronExpression("*/20 * * ? * *"); //
        jobRequest.setCronExpression("0 * * ? * *"); //every min
        jobRequest.setJobDataMap(jobDataMap);

        scheduleService.addJob(jobRequest, RetryTask.class);
    }
}
