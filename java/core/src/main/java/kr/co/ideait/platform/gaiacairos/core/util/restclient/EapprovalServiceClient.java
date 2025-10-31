package kr.co.ideait.platform.gaiacairos.core.util.restclient;

import com.google.common.collect.Maps;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import kr.co.ideait.platform.gaiacairos.core.base.AbstractClient;
import kr.co.ideait.platform.gaiacairos.core.config.property.DocumentServiceProp;
import kr.co.ideait.platform.gaiacairos.core.exception.GaiaBizException;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.cookie.CookieVO;
import kr.co.ideait.platform.gaiacairos.core.util.CookieService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.jasypt.encryption.StringEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class EapprovalServiceClient extends AbstractClient {

    private final DocumentServiceProp documentServiceProp;

    String domain;
    String serviceId;

    @PostConstruct
    public void init() {
        cookieVO = new CookieVO(platform.toUpperCase());

        domain = documentServiceProp.getDomain();
        serviceId = documentServiceProp.getServiceId();
    }

    public Map<String, String> getHeaderMap(Map<String, String> headers) {
        Map<String, String> newHeaders = Maps.newHashMap(headers);
        newHeaders.put("serviceId", serviceId);


        return newHeaders;
    }

    public Map<String, Object> test(Map<String, String> requestParams, List<MultipartFile> files) {

        Map<String, Object> result = new HashMap<>();

        if (requestParams == null) {
            requestParams = Maps.newHashMap();
        }

        try {
            Map<String, String> headers = new HashMap<>();

            String xAuthToken = generateSSOToken();

            headers.put("x-auth", xAuthToken);
            requestParams.put("x-auth", xAuthToken);

            restClientUtil.sendPost(String.format("%s/%s", domain, documentServiceProp.getApi().getCreateFile()), getHeaderMap(headers), requestParams, Map.class).getBody();

            result.put("resultCode", "00");
        } catch (GaiaBizException e) {
            result.put("resultCode", "01");
        }

        return result;
    }

}