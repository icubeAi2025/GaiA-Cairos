package kr.co.ideait.platform.gaiacairos.web.config;

import java.util.concurrent.Executor;

import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

public class AsyncConfig implements AsyncConfigurer {
	
	@Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(50);
        executor.setThreadNamePrefix("async-thread");
        executor.initialize();
        return executor;
    }
}
