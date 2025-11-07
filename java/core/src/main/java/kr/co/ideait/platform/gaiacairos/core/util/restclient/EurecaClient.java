package kr.co.ideait.platform.gaiacairos.core.util.restclient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import kr.co.ideait.iframework.BizException;
import kr.co.ideait.iframework.FormatUtil;
import kr.co.ideait.platform.gaiacairos.core.base.AbstractClient;
import kr.co.ideait.platform.gaiacairos.core.components.log.SystemLogComponent;
import kr.co.ideait.platform.gaiacairos.core.config.property.EurecaProp;
import kr.co.ideait.platform.gaiacairos.core.exception.ErrorType;
import kr.co.ideait.platform.gaiacairos.core.exception.GaiaBizException;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.log.Log;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.springframework.stereotype.Component;

import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class EurecaClient extends AbstractClient {

    private final EurecaProp eurecaProp;

    private final SystemLogComponent systemLogComponent;

    String domain;

    String cairos2EurecaKey;

    String eureca2CairosKey;

    @PostConstruct
    public void init() {
        domain = eurecaProp.getDomain();
        eureca2CairosKey = eurecaProp.getEureca2CairosKey();
        cairos2EurecaKey = eurecaProp.getCairos2EurecaKey();
    }

    /**
     * 공사-최초계약 등록
     * registerCnstwk
     * @param requestParams
     * @return
     */
    public Map<String, Object> registerCnstwk(Map<String, String> requestParams, String userId) {
        log.info("eurecaDomain: {} registerCnstwk: {}", domain, eurecaProp.getApi().getRegisterCnstwk());

        Map<String, Object> result = new HashMap<>();
        Log.SmApiLogDto outApiLog = new Log.SmApiLogDto();

        try {
            if (MapUtils.isEmpty(requestParams)) {
                throw new BizException("params is empty");
            }

            // API 연동
            outApiLog.setApiId("CAEU0007");
            outApiLog.setApiType("OUT");
            outApiLog.setServiceType("registerCnstwk");
            outApiLog.setServiceUuid(UUID.randomUUID().toString());
            outApiLog.setSourceSystemCode("CAIROS");
            outApiLog.setTargetSystemCode("EURECA");
            outApiLog.setReqMethod("POST");
            outApiLog.setReqData(objectMapper.writeValueAsString(requestParams));
            outApiLog.setReqDt(FormatUtil.formatDate(new Date(), "yyyyMMddHHmmss"));
            outApiLog.setRgstrId(userId);
            outApiLog.setChgId(userId);
            outApiLog.setResultCode(200);
            outApiLog.setErrorYn("N");

            log.info("eureca 연동 requestParams : {}", requestParams);

            Map<String, String> headers = new HashMap<>();
            Map body = restClientUtil.sendPost(String.format("%s/%s?ServiceKey=%s", domain, eurecaProp.getApi().getRegisterCnstwk(), cairos2EurecaKey), headers, requestParams, Map.class).getBody();
            log.info("eureca 연동 body : {}", body);

            if (MapUtils.isEmpty(body)) {
                throw new GaiaBizException(ErrorType.INTERFACE, "response body is empty");
            }

            Map response = (Map)body.get("response");
            Map responseHeaders = (Map)response.get("header");

            boolean isSuccess = "00".equals(responseHeaders.get("resultCode"));
            if (isSuccess) {
                String resultMsg = responseHeaders.get("resultMsg") != null ? responseHeaders.get("resultMsg").toString() : "";
                outApiLog.setResData(resultMsg);
                outApiLog.setResDt(FormatUtil.formatDate(new Date(), "yyyyMMddHHmmss"));
                systemLogComponent.asyncAddApiLog(outApiLog);
            } else {
                outApiLog.setResultCode(500);
                outApiLog.setErrorYn("Y");
                outApiLog.setErrorReason(responseHeaders.get("resultMsg").toString());
                outApiLog.setResDt(FormatUtil.formatDate(new Date(), "yyyyMMddHHmmss"));
                systemLogComponent.asyncAddApiLog(outApiLog);
            }

            result.put("resultCode", isSuccess ? "00" : "01");
            result.put("resultMsg", responseHeaders.get("resultMsg"));
        } catch (GaiaBizException | JsonProcessingException e) {
            outApiLog.setResultCode(500);
            outApiLog.setErrorYn("Y");
            outApiLog.setErrorReason(e.getMessage());
            outApiLog.setResDt(FormatUtil.formatDate(new Date(), "yyyyMMddHHmmss"));
            systemLogComponent.asyncAddApiLog(outApiLog);

            result.put("resultCode", "01");
            result.put("resultMsg", e.getMessage());
        }

        return result;
    }

    /**
     * 계약정보 등록
     * registerCntrct
     * @param requestParams
     * @return
     */
    public Map<String, Object> registerCntrct(Map<String, String> requestParams, String userId) {
        log.info("eurecaDomain: {} registerCntrct: {}", domain, eurecaProp.getApi().getRegisterCntrct());

        Map<String, Object> result = new HashMap<>();
        Log.SmApiLogDto outApiLog = new Log.SmApiLogDto();

        try {
            if (MapUtils.isEmpty(requestParams)) {
                throw new BizException("params is empty");
            }

            // API 연동
            outApiLog.setApiId("CAEU0008");
            outApiLog.setApiType("OUT");
            outApiLog.setServiceType("registerCntrct");
            outApiLog.setServiceUuid(UUID.randomUUID().toString());
            outApiLog.setSourceSystemCode("CAIROS");
            outApiLog.setTargetSystemCode("EURECA");
            outApiLog.setReqMethod("POST");
            outApiLog.setReqData(objectMapper.writeValueAsString(requestParams));
            outApiLog.setReqDt(FormatUtil.formatDate(new Date(), "yyyyMMddHHmmss"));
            outApiLog.setRgstrId(userId);
            outApiLog.setChgId(userId);
            outApiLog.setResultCode(200);
            outApiLog.setErrorYn("N");

            log.info("eureca 연동 requestParams : {}", requestParams);

            Map<String, String> headers = new HashMap<>();
            Map body = restClientUtil.sendPost(String.format("%s/%s?ServiceKey=%s", domain, eurecaProp.getApi().getRegisterCntrct(), cairos2EurecaKey), headers, requestParams, Map.class).getBody();
            log.info("eureca 연동 body : {}", body);

            if (MapUtils.isEmpty(body)) {
                throw new GaiaBizException(ErrorType.INTERFACE, "response body is empty");
            }

            Map response = (Map)body.get("response");
            Map responseHeaders = (Map)response.get("header");

            boolean isSuccess = "00".equals(responseHeaders.get("resultCode"));
            if (isSuccess) {
                String resultMsg = responseHeaders.get("resultMsg") != null ? responseHeaders.get("resultMsg").toString() : "";
                outApiLog.setResData(resultMsg);
                outApiLog.setResDt(FormatUtil.formatDate(new Date(), "yyyyMMddHHmmss"));
                systemLogComponent.asyncAddApiLog(outApiLog);
            } else {
                outApiLog.setResultCode(500);
                outApiLog.setErrorYn("Y");
                outApiLog.setErrorReason(responseHeaders.get("resultMsg").toString());
                outApiLog.setResDt(FormatUtil.formatDate(new Date(), "yyyyMMddHHmmss"));
                systemLogComponent.asyncAddApiLog(outApiLog);
            }

            result.put("resultCode", isSuccess ? "00" : "01");
            result.put("resultMsg", responseHeaders.get("resultMsg"));
        } catch (GaiaBizException | JsonProcessingException e) {
            outApiLog.setResultCode(500);
            outApiLog.setErrorYn("Y");
            outApiLog.setErrorReason(e.getMessage());
            outApiLog.setResDt(FormatUtil.formatDate(new Date(), "yyyyMMddHHmmss"));
            systemLogComponent.asyncAddApiLog(outApiLog);

            result.put("resultCode", "01");
            result.put("resultMsg", e.getMessage());
        }

        return result;
    }

    /**
     * 기성내역서(작성요청)
     * registerPrgpymntDtls
     * @param requestParams
     * @return
     */
    public Map<String, Object> registerPrgpymntDtls(Map requestParams) {
        log.info("eurecaDomain: {} registerPrgpymntDtls: {}", domain, eurecaProp.getApi().getRegisterPrgpymntDtls());

        Map<String, Object> result = new HashMap<>();

        try {
            if (MapUtils.isEmpty(requestParams)) {
                throw new BizException("params is empty");
            }

            log.info("eureca 연동 requestParams : {}", requestParams);

            Map<String, String> headers = new HashMap<>();
            Map body = restClientUtil.sendPost(String.format("%s/%s?ServiceKey=%s", domain, eurecaProp.getApi().getRegisterPrgpymntDtls(), cairos2EurecaKey), headers, requestParams, Map.class).getBody();
            log.info("eureca 연동 body : {}", body);

            if (MapUtils.isEmpty(body)) {
                throw new GaiaBizException(ErrorType.INTERFACE, "response body is empty");
            }

            Map response = (Map)body.get("response");
            Map responseHeaders = (Map)response.get("header");

            result.put("resultCode", "00".equals(responseHeaders.get("resultCode")) ? "00" : "01");
            result.put("resultMsg", responseHeaders.get("resultMsg"));
        } catch (GaiaBizException e) {
            result.put("resultCode", "01");
            result.put("resultMsg", e.getMessage());
        } finally {
            log.info("eureca 연동 결과 : {}", result);
        }

        return result;
    }

    /**
     * 기성내역서(작성완료) 조회
     * registerPrgpymntDtls
     * @param requestParams
     * @return
     */
    public Map<String, Object> retrievePrgpymntDtls(Map<String, String> requestParams) {
        log.info("eurecaDomain: {} retrievePrgpymntDtls: {}", domain, eurecaProp.getApi().getRetrievePrgpymntDtls());

        Map<String, Object> result = new HashMap<>();

        try {
            if (MapUtils.isEmpty(requestParams)) {
                throw new BizException("params is empty");
            }

            requestParams.put("ServiceKey", cairos2EurecaKey);

            StringBuilder params = new StringBuilder();

            Set<Map.Entry<String, String>> entrySet = requestParams.entrySet();
            for (Map.Entry<String,String> entry : entrySet) {
                String key = entry.getKey();
                String value = entry.getValue();
                params.append("&").append(key).append("=").append(value);
            }

            Map<String, String> headers = new HashMap<>();

            Map body = restClientUtil.sendGet(String.format("%s/%s?%s", domain, eurecaProp.getApi().getRetrievePrgpymntDtls(), params.toString().replaceFirst("&", "")), headers, Map.class).getBody();

            log.info("eureca 연동 body : {}", body);

            if (MapUtils.isEmpty(body)) {
                throw new GaiaBizException(ErrorType.INTERFACE, "response body is empty");
            }

            Map response = (Map)body.get("response");
            Map responseHeaders = (Map)response.get("header");
            Map responseBody = (Map)response.get("body");

            result.put("resultCode", "00".equals(responseHeaders.get("resultCode")) ? "00" : "01");
            result.put("resultMsg", responseHeaders.get("resultMsg"));
            result.put("totalCnt1", responseHeaders.get("totalCnt1"));
            result.put("totalCnt2", responseHeaders.get("totalCnt2"));
            result.put("paymentList", responseBody.get("paymentList"));
            result.put("cstList", responseBody.get("cstList"));
        } catch (GaiaBizException e) {
            result.put("resultCode", "01");
            result.put("resultMsg", e.getMessage());
        }

        return result;
    }

    /**
     * 기성상태결과 변경
     * registerPrgpymntDtls
     * @param requestParams
     * @return
     */
    public Map<String, Object> updatePrgpymntAprv(Map<String, String> requestParams) {
        log.info("eurecaDomain: {} updatePrgpymntAprv: {}", domain, eurecaProp.getApi().getUpdatePrgpymntAprv());

        Map<String, Object> result = new HashMap<>();

        try {
            if (MapUtils.isEmpty(requestParams)) {
                throw new BizException("params is empty");
            }

            log.info("eureca 연동 requestParams : {}", requestParams);

            Map<String, String> headers = new HashMap<>();
            Map body = restClientUtil.sendPost(String.format("%s/%s?ServiceKey=%s", domain, eurecaProp.getApi().getUpdatePrgpymntAprv(), cairos2EurecaKey), headers, requestParams, Map.class).getBody();
            log.info("eureca 연동 body : {}", body);

            if (MapUtils.isEmpty(body)) {
                throw new GaiaBizException(ErrorType.INTERFACE, "response body is empty");
            }

            Map response = (Map)body.get("response");
            Map responseHeaders = (Map)response.get("header");

            result.put("resultCode", "00".equals(responseHeaders.get("resultCode")) ? "00" : "01");
            result.put("resultMsg", responseHeaders.get("resultMsg"));
        } catch (GaiaBizException e) {
            result.put("resultCode", "01");
            result.put("resultMsg", e.getMessage());
        }

        return result;
    }

    /**
     * 계약내역서(작성완료) 조회
     * retrieveCntrDtls
     * @param requestParams
     * @return
     */
    public Map<String, Object> retrieveCntrDtls(Map<String, Object> requestParams) {
        log.info("eurecaDomain: {} eurecaApiSample: {}", domain, eurecaProp.getApi().getRetrieveCntrDtls());

        Map<String, Object> result = new HashMap<>();

        try {
            if (MapUtils.isEmpty(requestParams)) {
                throw new BizException("params is empty");
            }

            requestParams.put("ServiceKey", cairos2EurecaKey);

            log.info("eureca 연동 requestParams : {}", requestParams);

            StringBuilder params = new StringBuilder();

            Set<Map.Entry<String, Object>> entrySet = requestParams.entrySet();
            for (Map.Entry<String,Object> entry : entrySet) {
                String key = entry.getKey();
                Object value = entry.getValue();
                params.append("&").append(key).append("=").append(value);
            }

            Map<String, String> headers = new HashMap<>();

            Map body = restClientUtil.sendGet(String.format("%s/%s?%s", domain, eurecaProp.getApi().getRetrieveCntrDtls(), params.toString().replaceFirst("&", "")), headers, Map.class).getBody();

            log.info("eureca 연동 body : {}", body);

            if (MapUtils.isEmpty(body)) {
                throw new GaiaBizException(ErrorType.INTERFACE, "response body is empty");
            }

            Map response = (Map)body.get("response");
            Map responseHeaders = (Map)response.get("header");

            result.put("resultCode", "00".equals(responseHeaders.get("resultCode")) ? "00" : "01");
            result.put("resultMsg", responseHeaders.get("resultMsg"));
        } catch (GaiaBizException e) {
            result.put("resultCode", "01");
            result.put("resultMsg", e.getMessage());
        }

        return result;
    }

    /**
     * 내역서 산출 상세 조회
     * retrieveSpcsCalcDtls
     * @param requestParams
     * @return
     */
    public Map<String, Object> retrieveSpcsCalcDtls(Map<String, Object> requestParams) {
        log.info("eurecaDomain: {} retrieveSpcsCalcDtls: {}", domain, eurecaProp.getApi().getRetrieveSpcsCalcDtls());

        Map<String, Object> result = new HashMap<>();

        try {
            if (MapUtils.isEmpty(requestParams)) {
                throw new BizException("params is empty");
            }

            requestParams.put("ServiceKey", cairos2EurecaKey);

            log.info("eureca 연동 requestParams : {}", requestParams);

            StringBuilder params = new StringBuilder();

            Set<Map.Entry<String, Object>> entrySet = requestParams.entrySet();
            for (Map.Entry<String,Object> entry : entrySet) {
                String key = entry.getKey();
                Object value = entry.getValue();
                params.append("&").append(key).append("=").append(value);
            }

            Map<String, String> headers = new HashMap<>();

            Map body = restClientUtil.sendGet(String.format("%s/%s?%s", domain, eurecaProp.getApi().getRetrieveSpcsCalcDtls(), params.toString().replaceFirst("&", "")), headers, Map.class).getBody();

            log.info("eureca 연동 body : {}", body);

            if (MapUtils.isEmpty(body)) {
                throw new GaiaBizException(ErrorType.INTERFACE, "response body is empty");
            }

            Map response = (Map)body.get("response");
            Map responseHeaders = (Map)response.get("header");

            result.put("resultCode", "00".equals(responseHeaders.get("resultCode")) ? "00" : "01");
            result.put("resultMsg", responseHeaders.get("resultMsg"));
            result.put("body", body);
        } catch (GaiaBizException e) {
            result.put("resultCode", "01");
            result.put("resultMsg", e.getMessage());
        }

        return result;
    }
}