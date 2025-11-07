package kr.co.ideait.platform.gaiacairos.scheduler.loader.task;

import com.tware.components.scheduler.job.CronJob;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.JobKey;

import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

@Slf4j
public class BackupTask extends CronJob {

    @Override
    protected void doExecute(JobExecutionContext context, JobKey jobKey) {
        IntStream.range(0, 5).forEach(i -> {
            log.info("{} TestTask Counting - {}", jobKey, i);
            try {
                TimeUnit.SECONDS.sleep(MAX_SLEEP_IN_SECONDS);
            } catch (InterruptedException e) {
                log.error(e.getMessage(), e);
            }
        });
    }
}
