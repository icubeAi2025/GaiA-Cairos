package kr.co.ideait.platform.gaiacairos.core.config.logback;


import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.LoggerContextListener;
import ch.qos.logback.core.Context;
import ch.qos.logback.core.spi.ContextAwareBase;
import ch.qos.logback.core.spi.LifeCycle;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class LoggerStartupListener extends ContextAwareBase implements LoggerContextListener, LifeCycle {


    private static final String DEFAULT_LOG_FILE = "MYAPP";

    private boolean started = false;

    public Context getLoggerContext() {
        return super.getContext();
    }

    @Override
    public void start() {

//        if (started) return;

//        String logFile = System.getProperty("log.file"); // log.file is our custom jvm parameter to change log file name dynamicly if needed
//        logFile = (logFile != null && logFile.length() > 0) ? logFile : DEFAULT_LOG_FILE;

        Context context = getContext();
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();

        log.info("main() before getProperty: {} {}",context.getProperty("platform"), loggerContext.getProperty("platform"));
        context.putProperty("platform", System.getProperty("platform"));
        log.info("main() after getProperty: {} {}", context.getProperty("platform"), loggerContext.getProperty("platform"));

        started = true;
    }

    @Override
    public void stop() {
    }

    @Override
    public boolean isStarted() {
        return started;
    }

    @Override
    public boolean isResetResistant() {
        return true;
    }

    @Override
    public void onStart(LoggerContext context) {
    }

    @Override
    public void onReset(LoggerContext context) {
    }

    @Override
    public void onStop(LoggerContext context) {
    }

    @Override
    public void onLevelChange(Logger logger, Level level) {
    }
}
