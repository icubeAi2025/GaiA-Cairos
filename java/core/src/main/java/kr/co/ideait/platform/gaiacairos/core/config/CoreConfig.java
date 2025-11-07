package kr.co.ideait.platform.gaiacairos.core.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import kr.co.ideait.platform.gaiacairos.core.config.property.*;
import lombok.extern.slf4j.Slf4j;
import org.egovframe.rte.fdl.cmmn.trace.LeaveaTrace;
import org.egovframe.rte.fdl.cmmn.trace.handler.DefaultTraceHandler;
import org.egovframe.rte.fdl.cmmn.trace.handler.TraceHandler;
import org.egovframe.rte.fdl.cmmn.trace.manager.DefaultTraceHandleManager;
import org.egovframe.rte.fdl.cmmn.trace.manager.TraceHandlerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.*;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.util.AntPathMatcher;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;

@ServletComponentScan
@Configuration
@EnableAsync
@EnableEncryptableProperties
@PropertySources({
        @PropertySource("classpath:core.properties"),
        @PropertySource(value = "classpath:core-${spring.profiles.active}.properties", ignoreResourceNotFound = true),
        @PropertySource(value = "classpath:properties/biz-api-services.properties", ignoreResourceNotFound = true),
//        @PropertySource(value = "classpath:properties/eureca-#{'${spring.profiles.active}'.split(',')[0]}.properties", ignoreResourceNotFound = false)
        @PropertySource(value = "classpath:properties/eureca-${spring.profiles.active}.properties", ignoreResourceNotFound = true),
        @PropertySource(value = "classpath:properties/icube-${spring.profiles.active}.properties", ignoreResourceNotFound = true),
        @PropertySource(value = "classpath:properties/document-service-${spring.profiles.active}.properties", ignoreResourceNotFound = true),
        @PropertySource(value = "classpath:properties/eapproval-service-${spring.profiles.active}.properties", ignoreResourceNotFound = true)
})
@EnableConfigurationProperties({ ApplicationProp.class, GaiaProp.class, EurecaProp.class, ICubeProp.class, DocumentServiceProp.class, EapprovalServiceProp.class })
@Slf4j
public class CoreConfig {
    @Autowired
    GenericApplicationContext applicationContext;

    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        return new Jackson2ObjectMapperBuilder()
                .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .modules(new JavaTimeModule())
                .timeZone("Asia/Seoul")
                .build();
    }

    @Bean
    public Properties properties() {
//        ConfigurableListableBeanFactory beanFactory = applicationContext.getBeanFactory();
//        log.info("CoreConfig.properties(), [beanFactory] : {}", beanFactory);
        return new Properties(applicationContext);
    }

    /**
     * EGOV(leaveaTrace) 관련 빈으로 확인이 필요함. (필요할 경우 사용)
     */

    /**
     * for egov
     */
    // @Bean
    // DefaultTraceHandleManager type 의 bean 이 한개라서 @Qualifier 지정 안함.
    public LeaveaTrace leaveaTrace(DefaultTraceHandleManager traceHandlerService) {
        LeaveaTrace bean = new LeaveaTrace();
        bean.setTraceHandlerServices(new TraceHandlerService[] { traceHandlerService });
        return bean;
    }

    /**
     * for egov
     */
    // @Bean
    public DefaultTraceHandleManager traceHandlerService() {
        // AntPathMatcher antPathMatcher, DefaultTraceHandler defaultTraceHandler
        DefaultTraceHandleManager bean = new DefaultTraceHandleManager();
        bean.setReqExpMatcher(new AntPathMatcher());
        bean.setPatterns(new String[] { "*" });
        bean.setHandlers(new TraceHandler[] { new DefaultTraceHandler() });
        return bean;
    }

    @Bean
    ThreadPoolTaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(5);
        taskExecutor.setMaxPoolSize(10);
        taskExecutor.setQueueCapacity(25);
        return taskExecutor;
    }
}
