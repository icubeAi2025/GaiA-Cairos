package kr.co.ideait.platform.gaiacairos.core.config.property;

import kr.co.ideait.platform.gaiacairos.core.config.property.document.Api;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "document")
public class DocumentServiceProp {
    String domain;

    String serviceId;


    Api api;
}