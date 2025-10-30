package kr.co.ideait.platform.gaiacairos.core.config.property;

import kr.co.ideait.platform.gaiacairos.core.config.property.eureca.Api;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "eureca")
public class EurecaProp {
    String domain;

    String cairos2EurecaKey;

    String eureca2CairosKey;

    String contractListView;

    String paymentListView;

    @SuppressWarnings(value = "Generated code")
    Api api;
}