package kr.co.ideait.platform.gaiacairos.core.util.restclient;

import com.google.common.collect.Maps;
import jakarta.annotation.PostConstruct;
import kr.co.ideait.platform.gaiacairos.core.base.AbstractClient;
import kr.co.ideait.platform.gaiacairos.core.exception.GaiaBizException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;

import java.util.Map;


@Slf4j
@Component
@RequiredArgsConstructor
public class InvokeServiceClient extends AbstractClient {

    @Value("${machine:node1}")
    String machine;

    @PostConstruct
    public void init() {
    }

    /**
     *
     * @return
     */
    public Map<String, Object> invoke(final String apiDomain, Map<String, String> headers, Map<String, Object> params) {
        Map<String, Object> result = Maps.newHashMap();

        try {
            if (headers == null) {
                headers = Maps.newHashMap();
            }
            if (params == null) {
                params = Maps.newHashMap();
            }

            String xAuthToken = generateSSOToken();

            headers.put(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
            headers.put("x-auth", xAuthToken);

            Map<String,Object> newParams = Maps.newHashMap(params);
            newParams.put("x-auth", xAuthToken);

            result = restClientUtil.sendPost(apiDomain, headers, newParams, Map.class).getBody();

        } catch (GaiaBizException e) {
            log.error("GaiaBizException fail", e);
        }

        return result;
    }

    /**
     *
     * @return
     */
    public Map<String, Object> upload(final String url, Map<String, String> headers, MultipartBodyBuilder multipartBodyBuilder) {
        Map<String, Object> result = Maps.newHashMap();

        try {
            headers.put(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
            headers.put("_CSRF_TOKEN", "dfwer");

            result = restClientUtil.postFileUpload(url, BodyInserters.fromMultipartData(multipartBodyBuilder.build()), headers).block();

        } catch (GaiaBizException e) {
            log.error("GaiaBizException fail", e);
        }

        return result;
    }

}