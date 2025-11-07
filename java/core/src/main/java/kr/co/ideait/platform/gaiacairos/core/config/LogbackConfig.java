package kr.co.ideait.platform.gaiacairos.core.config;

import ch.qos.logback.classic.LoggerContext;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Slf4j
//@Profile({"local","local2","dev"})
@Configuration
public class LogbackConfig {
    private final static LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();

//    @Bean
//    public LogbackConsole logbackConsole() {
//        LogbackConsole logbackConsole = new LogbackConsole();
//        logbackConsole.logConfig();
//        return logbackConsole;
//    }
//
//    // @Bean
//    public LogbackFile logbackFile() {
//        LogbackFile logbackFile = new LogbackFile();
//        logbackFile.logConfig();
//        return logbackFile;
//    }

    @PostConstruct
    public void init() {
        log.info("LogbackConfig() loggerContext: " + loggerContext.getProperty("platform"));
    }

}
