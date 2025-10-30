package kr.co.ideait.platform.gaiacairos.core.config.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "icube")
public class ICubeProp {
    String domain;

    String userId;

    String userPwd;

    String ifCairosIcube001;

    String ifCairosIcube002;

    String convertHwpxToPdf;

    String mergePdf;
}