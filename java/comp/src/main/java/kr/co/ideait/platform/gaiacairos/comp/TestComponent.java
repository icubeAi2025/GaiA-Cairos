package kr.co.ideait.platform.gaiacairos.comp;

import kr.co.ideait.iframework.BizException;
import kr.co.ideait.platform.gaiacairos.core.exception.GaiaBizException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class TestComponent {
	
	@Value("${api.gaia.domain}")
    private String apiTestDomain;
	
	@Value("${api.url}")
    private String apiUrl;
	
	@Value("${api.requestKey}")
    private String apiKey;

    public Map<String, Object> pdfMerge(String transactionId, Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();

        try {
            List<File> files = List.of(
                new File("D:/1.pdf")
                , new File("D:/2.pdf")
                , new File("D:/3.pdf")
            );

            result.put("resultCode", "00");   
            result.put("files", files);
            result.put("savedFilePath", "D:/");
            result.put("savedFileName", "merge.pdf");

        } catch (GaiaBizException e) {
            result.put("resultCode", "01");
        }

        return result;
    }

    public Map<String, Object> callApi(String msgId, Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();

        try {

        	if (MapUtils.isEmpty(params)) {
                throw new BizException("params is empty");
            }

        	if("CAGA0010".equals(msgId)) {

        	}


            result.put("resultCode", "00");
            result.put("data", params);

        } catch (GaiaBizException e) {
            result.put("resultCode", "01");
        }

        return result;
    }

    public Map<String, Object> sendApi() {
    	 Map<String, Object> result = new HashMap<>();

         try {

//             StringBuilder params = new StringBuilder();

             //params 값 셋팅

             Map<String, String> headers = new HashMap<>();
             
             headers.put("ServiceKey", apiKey);

//             Map body = restClientUtil.sendGet(String.format("%s/%s?%s", apiTestDomain, apiUrl+"CAGA0010", params), headers, Map.class).getBody();
//
//             log.info("eureca 연동 body : {}", body);
//
//             if (MapUtils.isEmpty(body)) {
//                 throw new BizException("response body is empty");
//             }
//
//             Map response = (Map)body.get("response");
//             Map responseHeaders = (Map)response.get("header");
//             Map responseBody = (Map)response.get("body");
//
//             result.put("resultCode", "00".equals(responseHeaders.get("resultCode")) ? "00" : "01");
//             result.put("totalCnt1", responseHeaders.get("totalCnt1"));
//             result.put("totalCnt2", responseHeaders.get("totalCnt2"));
//             result.put("paymentList", responseBody.get("paymentList"));
//             result.put("cstList", responseBody.get("cstList"));
         } catch (GaiaBizException e) {
             result.put("resultCode", "01");
         }

         return result;
    }
}
