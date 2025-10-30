package kr.co.ideait.platform.gaiacairos.core.config.property;

import kr.co.ideait.platform.gaiacairos.core.config.security.TokenService;
import kr.co.ideait.platform.gaiacairos.core.exception.ErrorType;
import kr.co.ideait.platform.gaiacairos.core.exception.GaiaBizException;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;

@Data
@Component
@RequiredArgsConstructor
public class Properties implements InitializingBean {
//    [UuF]	Unused field: kr.co.ideait.platform.gaiacairos.core.config.property.Properties.prop
//    private static Properties prop;


    private final GenericApplicationContext applicationContext;

    @Autowired
    private GaiaProp gaia;

    @Autowired
    private EurecaProp eureca;

    @Autowired
    private DocumentServiceProp documentService;

    @Autowired
    private ICubeProp doc24;

    @Autowired
    private ApplicationProp app;

    @Autowired
    private EapprovalServiceProp eapprovalService;

//    public static GaiaProp getGaia() {
//        return gaia;
//    }
//
//    public static ApplicationProp getApp() {
//        return Properties.app;
//    }

    @Override
    public void afterPropertiesSet() throws Exception {
//        Properties.prop = this;

//        gaia = (GaiaProp)applicationContext.getBean("gaiaProp");
//        eureca = (EurecaProp)applicationContext.getBean("eurecaProp");
//        doc24 = (ICubeProp)applicationContext.getBean("iCubeProp");
//        app = (ApplicationProp)applicationContext.getBean("applicationProp");
    }

    public static java.util.Properties getProp(String propFile) throws IOException {
        java.util.Properties prop = new java.util.Properties();
        InputStream inputStream = TokenService.class.getClassLoader().getResourceAsStream(propFile);

        if (inputStream == null) {
            throw new GaiaBizException(ErrorType.INTERNAL_SERVER_ERROR, "프로퍼티 파일 초기화에 실패했습니다.");
        }

        try {
            prop.load(inputStream);
        } finally {
            inputStream.close();
        }

        return prop;
    }
}
