package kr.co.ideait.platform.gaiacairos.web.config;

import io.pebbletemplates.pebble.extension.AbstractExtension;
import io.pebbletemplates.pebble.extension.Extension;
import io.pebbletemplates.pebble.extension.Function;
import kr.co.ideait.platform.gaiacairos.web.config.pebble.BtnFunction;
import kr.co.ideait.platform.gaiacairos.web.config.pebble.TestFunction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;


@Slf4j
@Configuration
public class PebbleConfig {

    // @Bean
    // public Extension userExtention() {
    //     return new MyUserExtension1();
    // }

//    @Bean
//    public PebbleEngine pebbleEngine(CustomExtension customExtension) {
//        return new PebbleEngine.Builder()
//                .extension(customExtension)  // CustomExtension을 Pebble에 등록
//                .build();
//    }

    private final ApplicationContext applicationContext;

    public PebbleConfig(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Bean
    public Extension customExtension() {
        return new CustomExtension(applicationContext);
    }

    public static class CustomExtension extends AbstractExtension {

        private final ApplicationContext applicationContext;

        public CustomExtension(ApplicationContext applicationContext) {
            this.applicationContext = applicationContext;
        }

        @Override
        public Map<String, Function> getFunctions() {
            Map<String, Function> functions = new HashMap<String, Function>();
            functions.put("testFunction", new TestFunction());

            //HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
            //functions.put("btnFunction", new BtnFunction());
            functions.put("btnFunction", applicationContext.getBean(BtnFunction.class));

            return functions;
        }
    }
}
