package kr.co.ideait.platform.gaiacairos.core.config;

import org.springframework.boot.web.client.ClientHttpRequestFactories;
import org.springframework.boot.web.client.ClientHttpRequestFactorySettings;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.time.Duration;

@Configuration
public class RestClientConfig {

    @Bean
    public RestClient restClient() {
        return RestClient.builder()
                .requestFactory(customRequestFactory())
                .build();
    }

    /**
     * ClientHttpRequestFactory를 생성
     *
     * @return ClientHttpRequestFactory
     */
    ClientHttpRequestFactory customRequestFactory() {
        ClientHttpRequestFactorySettings settings = ClientHttpRequestFactorySettings.DEFAULTS
                .withConnectTimeout(Duration.ofSeconds(5))  // 연결 타임아웃을 5초로 설정
                .withReadTimeout(Duration.ofSeconds(5)); // 읽기 타임아웃을 5초로 설정
        return ClientHttpRequestFactories.get(settings);
    }
}
