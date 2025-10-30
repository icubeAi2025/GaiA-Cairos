package kr.co.ideait.platform.gaiacairos.core.util.restclient;

import com.google.common.collect.Maps;
import jakarta.annotation.PostConstruct;
import kr.co.ideait.platform.gaiacairos.core.base.AbstractClient;
import kr.co.ideait.platform.gaiacairos.core.exception.GaiaBizException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.apache.tika.utils.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Slf4j
@Component
@RequiredArgsConstructor
public class Doc24Client extends AbstractClient {

    @Value("${machine:node1}")
    String machine;

    @PostConstruct
    public void init() {
    }

    /**
     *
     */
//    @Scheduled(cron = "0 * * * * *")
    @Scheduled(cron = "0 0 0 * * *")
    public void syncReceiveAgencyByDoc24() {
        if ("node1".equals(machine)) {
            log.info("syncReceiveAgencyByDoc24");
        }
//        List<Map<String, Object> list = getOrganization("0000000");

    }

    /**
     * 기관 검색
     * @param keyword
     * @return
     */
    public Map<String, Object> searchOrganization(String keyword, Integer currentPage) {
        log.info("keyword: {}", keyword);
        Map<String, Object> result = Maps.newHashMap();

        try {
            Map<String, String> headers = new HashMap<>();
//            headers.put(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_HTML_VALUE);
            headers.put(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
//            headers.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/135.0.0.0 Safari/537.36");

            MultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
            requestParams.add("searchOrgNm", URLEncoder.encode(keyword, Charset.forName("UTF-8")));
            requestParams.add("orderNm", "0");
            requestParams.add("currentPage", String.valueOf(currentPage));
            requestParams.add("currentMyPage", "1");
            requestParams.add("currentFavPage", "1");
            requestParams.add("currentRecvGroupPage", "1");

//            UriComponents uriComponents = UriComponentsBuilder.newInstance().queryParams(requestParams).build();
//            String response = restClientUtil.sendPost("https://docu.gdoc.go.kr/cmm/ldap/selectLdapList.do", headers, uriComponents.toUriString(), String.class).getBody();

//            Map<String, String> requestParams = new HashMap<>();
//            requestParams.put("searchOrgNm", URLEncoder.encode(keyword, Charset.forName("UTF-8")));
//            requestParams.put("orderNm", "0");
//            requestParams.put("currentPage", String.valueOf(currentPage));
//            requestParams.put("currentMyPage", "1");
//            requestParams.put("currentFavPage", "1");
//            requestParams.put("currentRecvGroupPage", "1");
//
//            String response = restClientUtil.sendPost("https://docu.gdoc.go.kr/cmm/ldap/selectLdapList.do", headers, requestParams, String.class).getBody();


        } catch (GaiaBizException e) {
            log.error("GaiaBizException fail", e);
        }

        List<Map<String, Object>> list = getOrganizationByTree(null, keyword);

        result.put("list", list);
        result.put("totalCount", list.size());

        return result;
    }

    /**
     * 조직도 조회
     * @param parentOuCode
     * @return
     */
    public List<Map<String, Object>> getOrganizationByTree(String parentOuCode, String searchTreeOrgNm) {
        log.info("parentOuCode: {} searchTreeOrgNm: {}", parentOuCode, searchTreeOrgNm);
        List<Map<String, Object>> list = Lists.newArrayList();

        try {
            Map<String, String> headers = new HashMap<>();
            Map<String, String> requestParams = new HashMap<>();

            if (!StringUtils.isEmpty(parentOuCode)) {
                requestParams.put("parentoucode", parentOuCode);
            } else {
                requestParams.put("searchTreeOrgNm", searchTreeOrgNm);
            }

            requestParams.put("orderNm", "1");

//            Thread.sleep(500);
            list = restClientUtil.sendPost("https://docu.gdoc.go.kr/cmm/ldap/jstree/search", headers, requestParams, List.class).getBody();
        } catch (GaiaBizException e) {
            log.error("GaiaBizException fail", e);
        }

        return list;
    }
}