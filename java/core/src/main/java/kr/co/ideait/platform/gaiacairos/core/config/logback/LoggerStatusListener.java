package kr.co.ideait.platform.gaiacairos.core.config.logback;


import ch.qos.logback.core.Context;
import ch.qos.logback.core.status.OnConsoleStatusListener;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LoggerStatusListener extends OnConsoleStatusListener {

    private static final String DEFAULT_LOG_FILE = "MYAPP";

    private boolean started = false;

    @Override
    public void start() {
//        if (started) return;

//        String logFile = System.getProperty("log.file"); // log.file is our custom jvm parameter to change log file name dynamicly if needed
//        logFile = (logFile != null && logFile.length() > 0) ? logFile : DEFAULT_LOG_FILE;

        Context context = getContext();

        String platform = System.getProperty("platform");
        context.putProperty("platform", platform);
//        context.putProperty("LOG_FILE", logFile);

        log.info("Platform: " + platform + " context.getProperty: " + context.getProperty("platform"));

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
}
