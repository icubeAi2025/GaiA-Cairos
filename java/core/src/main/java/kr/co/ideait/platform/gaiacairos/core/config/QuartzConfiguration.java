package kr.co.ideait.platform.gaiacairos.core.config;

import com.tware.components.scheduler.service.JobsListener;
import com.tware.components.scheduler.service.TriggersListener;
import com.tware.components.scheduler.spring.config.AutowiringSpringBeanJobFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.quartz.QuartzProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import java.util.Properties;

@Slf4j
@Configuration
public class QuartzConfiguration {
	@Autowired
	private TriggersListener triggersListener;

	@Autowired
	private JobsListener jobsListener;

	@Autowired
	private QuartzProperties quartzProperties;

	/**
	 * Quartz 관련 설정
	 *
	 * @param applicationContext the applicationContext
	 * @return SchedulerFactoryBean
	 */
	@Bean
	public SchedulerFactoryBean schedulerFactoryBean(ApplicationContext applicationContext) {
		AutowiringSpringBeanJobFactory jobFactory = new AutowiringSpringBeanJobFactory();
		jobFactory.setApplicationContext(applicationContext);

		Properties properties = new Properties();
		properties.putAll(quartzProperties.getProperties());

		SchedulerFactoryBean schedulerFactoryBean = new SchedulerFactoryBean();
		schedulerFactoryBean.setJobFactory(jobFactory);
		schedulerFactoryBean.setApplicationContext(applicationContext);
		schedulerFactoryBean.setGlobalTriggerListeners(triggersListener);
		schedulerFactoryBean.setGlobalJobListeners(jobsListener);
		schedulerFactoryBean.setOverwriteExistingJobs(true);
		schedulerFactoryBean.setQuartzProperties(properties);
		schedulerFactoryBean.setWaitForJobsToCompleteOnShutdown(true);

		return schedulerFactoryBean;
	}
}
