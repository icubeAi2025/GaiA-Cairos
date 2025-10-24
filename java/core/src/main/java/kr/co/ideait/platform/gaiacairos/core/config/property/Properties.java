package kr.co.ideait.platform.gaiacairos.core.config.property;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.stereotype.Component;

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
}
