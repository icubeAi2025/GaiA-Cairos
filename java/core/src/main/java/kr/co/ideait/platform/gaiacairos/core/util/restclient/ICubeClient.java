package kr.co.ideait.platform.gaiacairos.core.util.restclient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.collect.Maps;
import jakarta.annotation.PostConstruct;
import kr.co.ideait.iframework.FormatUtil;
import kr.co.ideait.platform.gaiacairos.core.base.AbstractClient;
import kr.co.ideait.platform.gaiacairos.core.components.log.SystemLogComponent;
import kr.co.ideait.platform.gaiacairos.core.config.property.ICubeProp;
import kr.co.ideait.platform.gaiacairos.core.exception.GaiaBizException;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.log.Log;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.jasypt.encryption.StringEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;
import org.springframework.web.reactive.function.BodyInserters;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class ICubeClient extends AbstractClient {


    @Autowired
    private SystemLogComponent systemLogComponent;

    @Autowired
    private StringEncryptor jasyptEncryptorAES;

    @Autowired
    ICubeProp iCubeProp;

    String domain;

    @PostConstruct
    public void init() {
//        iCubeProp = properties.getDoc24();
        domain = iCubeProp.getDomain();

//        log.info("Doc24 id: {}", jasyptEncryptorAES.decrypt(iCubeProp.getUserId()));
//        log.info("Doc24 pass: {}", jasyptEncryptorAES.decrypt(iCubeProp.getUserPwd()));
    }

    /**
     * 공문 자동발송 요청. async
     * IF_CAIROS_ICUBE_001
     * @param requestParams
     * @return
     */
    public Map<String, Object> ifCairosIcube001(String userId, Map<String, Object> requestParams, List<Map<String, Object>> files) {
        log.info("domain: {} IF_DOCU24_001: {}", domain, iCubeProp.getIfCairosIcube001());

        Map<String, Object> result = new HashMap<>();

        if (requestParams == null) {
            requestParams = Maps.newHashMap();
        }

        try {
            String xAuthToken = generateSSOToken();

            Map<String, String> headers = new HashMap<>();
            headers.put("x-auth", xAuthToken);

            requestParams.put("x-auth", xAuthToken);

            MultipartBodyBuilder multipartBodyBuilder = new MultipartBodyBuilder();

            final Map<String, Object> finalRequestParams = requestParams;

            requestParams.keySet().forEach(key -> multipartBodyBuilder.part(key, finalRequestParams.get(key)));

            try {
                for (Map<String, Object> file : files) {
                    Resource resource = (Resource) file.get("resource");

                    multipartBodyBuilder
                        .part("gdoc_atch", new InputStreamResource( resource.getInputStream() ) )
                        .header("Content-Disposition", String.format("form-data; name=gdoc_atch; filename=%s", URLEncoder.encode(MapUtils.getString(file, "name"), StandardCharsets.UTF_8)));
                }
            } catch (IOException e) {
                throw new GaiaBizException(e);
            }

            Log.SmApiLogDto outApiLog = new Log.SmApiLogDto();
            outApiLog.setApiId("IF_DOCU24_001");
            outApiLog.setApiType("OUT");
            outApiLog.setServiceType("DOC24_");
            outApiLog.setServiceUuid(MapUtils.getString(requestParams, "req_cd"));
            outApiLog.setSourceSystemCode("KAIROS");
            outApiLog.setTargetSystemCode("iCube");
            outApiLog.setReqMethod("POST");
            outApiLog.setReqHeader(objectMapper.writeValueAsString(headers));
            outApiLog.setReqData(objectMapper.writeValueAsString(requestParams));
            outApiLog.setReqDt(FormatUtil.formatDate(new Date(), "yyyyMMddHHmmss"));
            outApiLog.setRgstrId(userId);
            outApiLog.setChgId(userId);
            outApiLog.setResultCode(200);
            outApiLog.setErrorYn("N");

            StopWatch stopWatch = new StopWatch();
            stopWatch.start();

            restClientUtil.postFileUpload(
                    String.format("%s/%s", domain, iCubeProp.getIfCairosIcube001())
                    , BodyInserters.fromMultipartData(multipartBodyBuilder.build())
                    , headers
                )
//                .subscribe(response -> {
//                    log.info("success: {}", response);
//
//                    stopWatch.stop();
//                    log.info(stopWatch.prettyPrint());
//                    log.info("코드 실행 시간 (s): {}", stopWatch.getTotalTimeSeconds());
//
//                    outApiLog.setResultCode(200);
//                    outApiLog.setResDt(FormatUtil.formatDate(new Date(), "yyyyMMddHHmmss"));
//
//                    if ("S".equals( MapUtils.getString(response, "res_stats") )) {
//                        outApiLog.setErrorYn("N");
//                    } else {
//                        outApiLog.setErrorYn("Y");
//                        outApiLog.setErrorReason(MapUtils.getString(response, "res_dscrpt"));
//                    }
//
//                    try {
//                        outApiLog.setResHeader(objectMapper.writeValueAsString(requestParams));
//                        outApiLog.setResData(objectMapper.writeValueAsString(response));
//                    } catch (JsonProcessingException e) {
//                        throw new RuntimeException(e);
//                    }
//
//                    systemLogComponent.modifyApiLog(outApiLog);
//                }
//                , error -> {
//                    log.error("fail", error);
//                    outApiLog.setResultCode(500);
//                    outApiLog.setResDt(FormatUtil.formatDate(new Date(), "yyyyMMddHHmmss"));
//                    outApiLog.setErrorYn("Y");
//                    outApiLog.setErrorReason(error.getMessage());
//
//                    systemLogComponent.modifyApiLog(outApiLog);
//                });
            .subscribe();

            systemLogComponent.asyncAddApiLog(outApiLog);

            result.put("resultCode", "00");
        } catch (GaiaBizException | JsonProcessingException e) {
            log.error("GaiaBizException fail : {}", e.getMessage());
            result.put("resultCode", "01");
        }

        return result;
    }

    /**
     * 문서함 목록 요청
     * IF_CAIROS_ICUBE_002
     * @param requestParams
     * @return
     */
    public Map<String, Object> ifCairosIcube002(Map<String, String> requestParams) {
        log.info("domain: {} IF_DOCU24_002: {}", domain, iCubeProp.getIfCairosIcube002());

        Map<String, Object> result = new HashMap<>();

        try {
            Map<String, String> headers = new HashMap<>();
            restClientUtil.sendPost(String.format("%s/%s", domain, iCubeProp.getIfCairosIcube002()), headers, requestParams, Map.class).getBody();

            result.put("resultCode", "00");
        } catch (GaiaBizException e) {
            result.put("resultCode", "01");
        }

        return result;
    }

    /**
     * hwpx To PDF 변환. async
     *
     * @return
     */
    public Map<String, Object> convertHwpxToPdf(MultipartBodyBuilder multipartBodyBuilder) {
        log.info("domain: {} convertHwpxToPdf: {}", domain, iCubeProp.getConvertHwpxToPdf());

        Map<String, Object> result = new HashMap<>();

        try {
            Map<String, String> headers = new HashMap<>();

//            Log.SmApiLogDto outApiLog = new Log.SmApiLogDto();
//            outApiLog.setApiId("IF_DOCU24_001");
//            outApiLog.setApiType("OUT");
//            outApiLog.setServiceType("DOC24_");
//            outApiLog.setServiceUuid(MapUtils.getString(requestParams, "req_cd"));
//            outApiLog.setSourceSystemCode("KAIROS");
//            outApiLog.setTargetSystemCode("iCube");
//            outApiLog.setReqMethod("POST");
//            outApiLog.setReqHeader(objectMapper.writeValueAsString(headers));
//            outApiLog.setReqData(objectMapper.writeValueAsString(requestParams));
//            outApiLog.setReqDt(FormatUtil.formatDate(new Date(), "yyyyMMddHHmmss"));
//            outApiLog.setRgstrId(userId);
//            outApiLog.setChgId(userId);
//            outApiLog.setResultCode(200);
//            outApiLog.setErrorYn("N");

            StopWatch stopWatch = new StopWatch();
            stopWatch.start();

            restClientUtil.postFileUpload(
                            String.format("%s/%s", domain, iCubeProp.getConvertHwpxToPdf())
                            , BodyInserters.fromMultipartData(multipartBodyBuilder.build())
                            , headers
                    )
                    .subscribe();

//            systemLogComponent.asyncAddApiLog(outApiLog);

            result.put("resultCode", "00");
        } catch (GaiaBizException e) {
            log.error("GaiaBizException fail : {}", e.getMessage());
            result.put("resultCode", "01");
        }

        return result;
    }

    /**
     * PDF 병합. async
     *
     * @return
     */
    public Map<String, Object> mergeToPdf(MultipartBodyBuilder multipartBodyBuilder) {
        log.info("domain: {} mergeToPdf: {}", domain, iCubeProp.getMergePdf());

        Map<String, Object> result = new HashMap<>();

        try {
            Map<String, String> headers = new HashMap<>();

//            Log.SmApiLogDto outApiLog = new Log.SmApiLogDto();
//            outApiLog.setApiId("IF_DOCU24_001");
//            outApiLog.setApiType("OUT");
//            outApiLog.setServiceType("DOC24_");
//            outApiLog.setServiceUuid(MapUtils.getString(requestParams, "req_cd"));
//            outApiLog.setSourceSystemCode("KAIROS");
//            outApiLog.setTargetSystemCode("iCube");
//            outApiLog.setReqMethod("POST");
//            outApiLog.setReqHeader(objectMapper.writeValueAsString(headers));
//            outApiLog.setReqData(objectMapper.writeValueAsString(requestParams));
//            outApiLog.setReqDt(FormatUtil.formatDate(new Date(), "yyyyMMddHHmmss"));
//            outApiLog.setRgstrId(userId);
//            outApiLog.setChgId(userId);
//            outApiLog.setResultCode(200);
//            outApiLog.setErrorYn("N");

            StopWatch stopWatch = new StopWatch();
            stopWatch.start();

            restClientUtil.postFileUpload(
                            String.format("%s/%s", domain, iCubeProp.getMergePdf())
                            , BodyInserters.fromMultipartData(multipartBodyBuilder.build())
                            , headers
                    )
                    .subscribe();

//            systemLogComponent.asyncAddApiLog(outApiLog);

            result.put("resultCode", "00");
        } catch (GaiaBizException e) {
            log.error("GaiaBizException fail : {}", e.getMessage());
            result.put("resultCode", "01");
        }

        return result;
    }
}