package kr.co.ideait.platform.gaiacairos.core.config.logback;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.RollingPolicy;
import ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy;
import ch.qos.logback.core.util.FileSize;

public class LogbackFile {

    private final LoggerContext logCtx = (LoggerContext) LoggerFactory.getILoggerFactory();

    private final static String ROLLING_PATTERN = "%d{yyyy-MM-dd HH:mm:ss.SSS}  %logger{5} - %msg %n";
    private final static String FILE_NAME = "gaia.log";
    private final static String LOG_NAME_PATTERN = "./logs/gaia-%d{yyyy-MM-dd-HH-mm}.%i.log";
    private final static String MAX_FILE_SIZE = "10MB";
    private final static int MAX_HISTORY = 2;

    private RollingFileAppender<ILoggingEvent> rollingAppender;

    public void logConfig() {
        rollingAppender = getLogAppender();
        createLoggers();
    }

    private void createLoggers() {
        createLogger("root", Level.ERROR, true);
    }

    private void createLogger(String loggerName, Level logLevel, Boolean additive) {
        Logger logger = logCtx.getLogger(loggerName);
        logger.setAdditive(additive);
        logger.setLevel(logLevel);
        logger.addAppender(rollingAppender);
    }

    private RollingFileAppender<ILoggingEvent> getLogAppender() {
        final String appendName = "ROLLING_LOG_FILE";
        PatternLayoutEncoder rollingLogEncoder = createLogEncoder(ROLLING_PATTERN);
        RollingFileAppender<ILoggingEvent> rollingFileAppender = createLogAppender(appendName, rollingLogEncoder);
        SizeAndTimeBasedRollingPolicy<RollingPolicy> rollingPolicy = createLogRollingPolicy(rollingFileAppender);

        rollingFileAppender.setRollingPolicy(rollingPolicy);
        rollingFileAppender.start();

        return rollingFileAppender;
    }

    private SizeAndTimeBasedRollingPolicy<RollingPolicy> createLogRollingPolicy(
            RollingFileAppender<ILoggingEvent> rollingLogAppender) {
        SizeAndTimeBasedRollingPolicy<RollingPolicy> policy = new SizeAndTimeBasedRollingPolicy<>();
        policy.setContext(logCtx);
        policy.setParent(rollingLogAppender);
        policy.setFileNamePattern(LOG_NAME_PATTERN);
        policy.setMaxHistory(MAX_HISTORY);
        policy.setTotalSizeCap(FileSize.valueOf(MAX_FILE_SIZE));
        policy.setMaxFileSize(FileSize.valueOf(MAX_FILE_SIZE));
        policy.start();
        return policy;
    }

    private PatternLayoutEncoder createLogEncoder(String pattern) {
        PatternLayoutEncoder encoder = new PatternLayoutEncoder();
        encoder.setContext(logCtx);
        encoder.setPattern(pattern);
        encoder.start();
        return encoder;
    }

    private RollingFileAppender<ILoggingEvent> createLogAppender(String appendName,
            PatternLayoutEncoder rollingLogEncoder) {
        RollingFileAppender<ILoggingEvent> logRollingAppender = new RollingFileAppender<>();
        logRollingAppender.setName(appendName);
        logRollingAppender.setContext(logCtx);
        logRollingAppender.setFile(FILE_NAME);
        logRollingAppender.setEncoder(rollingLogEncoder);

        return logRollingAppender;
    }
}