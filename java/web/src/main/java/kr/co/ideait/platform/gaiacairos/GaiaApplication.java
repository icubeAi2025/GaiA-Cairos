package kr.co.ideait.platform.gaiacairos;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.Context;
import kr.co.ideait.iframework.SpringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.Arrays;

@Slf4j
@SpringBootApplication(scanBasePackages = {
        "kr.co.ideait.iframework", "kr.co.ideait.platform.gaiacairos"
})
@PropertySources({
        @PropertySource(value = "classpath:application.properties", ignoreResourceNotFound = false),
        @PropertySource(value = "classpath:application-${spring.profiles.active}-${platform}.properties", ignoreResourceNotFound = false)
})
@EnableScheduling
public class GaiaApplication implements CommandLineRunner {

    private Environment environment;

    @Autowired
    private SpringUtils springUtils;

    @Value("${spring.application.name}")
    String applicationName;

    @Value("${server.port}")
    String serverPort;

    @Value("${platform}")
    String platform;

    @Value("${machine:node1}")
    String machine;

    @Autowired
    public GaiaApplication(Environment environment) {
        this.environment = environment;
    }

    private static void setPropertyForArgs(String... args) {
        boolean hasPlatform = !StringUtils.isEmpty(System.getProperty("platform"));

        if (!hasPlatform) {
            if (args.length == 0) {
                throw new IllegalArgumentException("No arguments provided");
            }

            for (String arg : args) {
                String[] keyValue = arg.split("=");

                if (!hasPlatform && keyValue[0].contains("platform")) {
                    hasPlatform = true;
                }

                System.setProperty(keyValue[0].replaceAll("-D", "").replaceAll("--", ""), keyValue[1]);
            }
        }

        if (!hasPlatform) {
            throw new IllegalArgumentException("No platform provided");
        }
    }

    public static void main(String[] args) {
        log.info("GaiaApplication.main() args: " + Arrays.asList(args) + " " + Arrays.toString(args));

        //파라미터를 시스템 프로퍼티로 세팅.
        setPropertyForArgs(args);

        log.info("GaiaApplication.main() platform: " + System.getProperty("platform"));
        log.info("GaiaApplication.main() machine: " + System.getProperty("machine"));

        // SpringApplication.run(GaiaApplication.class, args);
        SpringApplication app = new SpringApplication(GaiaApplication.class);
        app.run(args);

//        GaiaApplication da = new GaiaApplication(environment);
//        da.contextLoads();
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("platform: {} machine: {} application: {} serverPort: {}", platform, machine, applicationName, serverPort);

        Context loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        log.info("run() loggerContext.getProperty: {}" , loggerContext.getProperty("platform"));

//        Map<String, Map> descriptions = springUtils.getDescriptionMappingList("classpath*:kr/co/ideait/**/*Controller.class");
//        Map<String, Map> descriptions = springUtils.getMappingListByType(Description.class);

//        log.info("descriptions: {}", descriptions);
//        log.info("urls: {}", springUtils.getUrls(handlerMapping));

//        StringBuilder sb = new StringBuilder();
//
//        descriptions.keySet().forEach(key -> {
//            Map desc = descriptions.get(key);
//
//            sb.append(key);
//
//            if (desc != null) {
//                sb.append("\t").append(desc.get("name")).append("\t").append(desc.get("description")).append("\n");
//            } else {
//                sb.append("\n");
//            }
//        });

//        FileUtils.writeByteArrayToFile(new File("D:/usecase.txt"), sb.toString().getBytes());
    }

    public void contextLoads() {
        log.info("GaiaApplication 실행");
        log.info("profile 값 :: " + environment.getProperty("spring.profiles.active"));

        String platform = environment.getProperty("platform");
        log.info("platform :: " + platform);
    }
}
