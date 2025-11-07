package kr.co.ideait.platform.gaiacairos.web.entrypoint.api;

import kr.co.ideait.iframework.BizException;
import kr.co.ideait.iframework.FormatUtil;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.eureca.ContractSyncRequest;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.eureca.SpcsCalcDtlsSyncRequest;
import kr.co.ideait.platform.gaiacairos.comp.project.service.InformationService;
import kr.co.ideait.platform.gaiacairos.comp.projectcost.C3RComponent;
import kr.co.ideait.platform.gaiacairos.comp.projectcost.service.PaymentService;
import kr.co.ideait.platform.gaiacairos.core.base.AbstractController;
import kr.co.ideait.platform.gaiacairos.core.config.property.EurecaProp;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.security.UserAuth;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.log.Log;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/eurecaWebApi")
@RequiredArgsConstructor
public class EurecaApiController extends AbstractController {

    //	private final TokenService tokenService;

    private final InformationService informationService;

    private final PaymentService paymentService;

    private final C3RComponent c3RComponent;

    private final EurecaProp eurecaProp;


    String eureca2CairosKey;

    @PostConstruct
    public void init(){
        eureca2CairosKey = eurecaProp.getEureca2CairosKey();
    }

    /* ==================================================================================================================
     *
     * EURECA -> GAIA/CAIROS
     *
     * ==================================================================================================================
     */

    /**
     * 기성내역서(갱신기성)
     * @param reqParams
     * @return
     */
    @GetMapping("/cairos/retrieveRePrgpymntDtls")
    public ResponseEntity<Map<String, Object>> retrieveRePrgpymntDtls(
            @RequestParam("serviceKey") String serviceKey
            , @RequestParam("cntrctChgId") String cntrctChgId //변경계약ID
            , @RequestParam("payprceTmnum") Long payprceTmnum //기성회차
            , @RequestParam Map<String, Object> reqParams
    ) {
        Map<String, Object> result = new HashMap<String, Object>();
        Map<String, Object> response = new HashMap<String, Object>();
        Map<String, Object> header = new HashMap<String, Object>();
        Map<String, Object> body = new HashMap<String, Object>();

        log.info("retrieveRePrgpymntDtls() serviceKey: {} cntrctChgId: {} payprceTmnum: {} reqParams: {}", serviceKey, cntrctChgId, payprceTmnum, reqParams);

        try {
            if (!this.eureca2CairosKey.equals(serviceKey)) {
                throw new BizException("서비스키가 유효하지 않습니다.");
            }

            body = paymentService.getPrgpymntDtlsForEureca(cntrctChgId, payprceTmnum);
            header.put("resultCode", "00");
        } catch (BizException bizException) {
            header.put("resultCode", "01");
            header.put("resultMsg", bizException.getMessage());
        } catch (Exception exception) {
            header.put("resultCode", "04");
            header.put("resultMsg", exception.getMessage());
        } finally {
            response.put("header", header);
            response.put("body", body);
            result.put("response", response);

            log.info("result : {}", result);
        }

        return ResponseEntity.ok(result);
    }

    @GetMapping("/cairos/pay-detail")
    public ResponseEntity<Map<String, Object>> getPayDetail(
            @RequestParam("serviceKey") String serviceKey
            , @RequestParam("cntrctChgId") String cntrctChgId //변경계약ID
            , @RequestParam("payprceTmnum") Long payprceTmnum //기성회차
            , @RequestParam Map<String, Object> reqParams
    ) {
        Map<String, Object> result = new HashMap<String, Object>();
        Map<String, Object> response = new HashMap<String, Object>();
        Map<String, Object> header = new HashMap<String, Object>();
        Map<String, Object> body = new HashMap<String, Object>();

        log.info("retrieveRePrgpymntDtls() serviceKey: {} cntrctChgId: {} payprceTmnum: {} reqParams: {}", serviceKey, cntrctChgId, payprceTmnum, reqParams);

        try {
            if (!this.eureca2CairosKey.equals(serviceKey)) {
                throw new BizException("서비스키가 유효하지 않습니다.");
            }

            body = paymentService.tempPrgpymntDtlsForEureca(cntrctChgId, payprceTmnum);
            header.put("resultCode", "00");
        } catch (BizException bizException) {
            header.put("resultCode", "01");
            header.put("resultMsg", bizException.getMessage());
        } catch (Exception exception) {
            header.put("resultCode", "04");
            header.put("resultMsg", exception.getMessage());
        } finally {
            response.put("header", header);
            response.put("body", body);
            result.put("response", response);

            log.info("result : {}", result);
        }

        return ResponseEntity.ok(result);
    }

    /**
     * 내역서접근권한체크
     * @param reqParams
     * @return
     */
    @GetMapping("/cairos/retrieveAccssAuthChk")
    public ResponseEntity<Map<String, Object>> retrieveAccssAuthChk(
            @RequestParam("serviceKey") String serviceKey
            //변경계약번호
            , @RequestParam(value = "cntrctChgId") String cntrctChgId
            , @RequestParam("payprceTmnum") Long payprceTmnum
            //사용자ID
            , @RequestParam("usrId") String usrId
            //권한. R(읽기), U(수정)
            , @RequestParam("auth") String auth
            , @RequestParam(value = "referer", required = false) String referer
            , @RequestParam Map<String, Object> reqParams
    ) {
        Map<String, Object> result = new HashMap<String, Object>();
        Map<String, Object> response = new HashMap<String, Object>();
        Map<String, Object> header = new HashMap<String, Object>();
        Map<String, Object> body = new HashMap<String, Object>();
        UserAuth userAuth = UserAuth.get(true);
        String loginUserId = null;
        if(userAuth!= null){
            loginUserId = userAuth.getUsrId();
        }
        log.info("retrieveAccssAuthChk() serviceKey: {} cntrctChgId: {} payprceTmnum: {} usrId: {} auth: {} referer: {} reqParams: {}", serviceKey, cntrctChgId, payprceTmnum, usrId, auth, referer, reqParams);

        try {
            if (!this.eureca2CairosKey.equals(serviceKey)) {
                throw new BizException("서비스키가 유효하지 않습니다.");
            }

            body = paymentService.retrieveAccssAuthChk(cntrctChgId, payprceTmnum, usrId, auth, loginUserId);
            header.put("resultCode", "00");
        } catch (BizException bizException) {
            header.put("resultCode", "01");
            header.put("resultMsg", bizException.getMessage());
        } catch (Exception exception) {
            header.put("resultCode", "04");
            header.put("resultMsg", exception.getMessage());
        } finally {
            response.put("header", header);
            response.put("body", body);
            result.put("response", response);

            log.info("result : {}", result);
        }

        return ResponseEntity.ok(result);
    }

    /**
     * 5) 계약내역서 연계 수신
     * 대상 태이블: CN_CONTRACT_BID, CN_CONTRACT_CALCULATOR, CT_CBS, CT_CBS_DETAIL, CT_CBS_RESOURCE / 일위대가
     * @param serviceKey
     * @param payload
     * @return
     */
    @PostMapping("/cairos/updateCntrDtls")
    public ResponseEntity<Map<String, Object>> updateCntrDtls (
            @RequestParam("serviceKey") String serviceKey,
            @RequestBody ContractSyncRequest payload) {

        /* 0. 사용 변수 초기화  */
        Map<String, Object> result = new HashMap<>();
        Map<String, Object> response = new HashMap<>();
        Map<String, Object> header = new HashMap<>();
        Map<String, Object> body = new HashMap<>();

        /* LOG 작성 (EURECA -> CAIROS)  */
        Log.SmApiLogDto smApiLog = new Log.SmApiLogDto();
        smApiLog.setApiId("EUCA0005");
        smApiLog.setApiType("IN");
        smApiLog.setServiceType("registerCntrct");
        smApiLog.setServiceUuid(UUID.randomUUID().toString());
        smApiLog.setSourceSystemCode("EURECA");
        smApiLog.setTargetSystemCode("CAIROS");
        smApiLog.setReqMethod("POST");
//        smApiLog.setReqData("updateCntrDtls");
        smApiLog.setReqDt(FormatUtil.formatDate(new Date(), "yyyyMMddHHmmss"));
        smApiLog.setRgstrId("EURECA");
        smApiLog.setChgId("EURECA");
        smApiLog.setResultCode(200);
        smApiLog.setErrorYn("N");

        try {
            Map<String, Object> reqData = new HashMap<>();
            reqData.put("getCntrDtlsList", payload.getTotalCnt1());
            reqData.put("getCstList", payload.getTotalCnt2());
            reqData.put("getReqreRsceList", payload.getTotalCnt3());
            reqData.put("cntrctNo", payload.getCntrctNo());
            reqData.put("cntrctChgId", payload.getCntrctChgId());
            smApiLog.setReqData(reqData.toString());

            if (!this.eureca2CairosKey.equals(serviceKey)) {
                throw new BizException("서비스키가 유효하지 않습니다.");
            }
            log.info("EURECA -> CAIROS updateCntrDtls START");
            if (payload.getCntrDtlsList() != null) { log.info("계약내역 건수 :{} / {}", payload.getTotalCnt1(), payload.getCntrDtlsList().size()); }
            if (payload.getCstList() != null)  { log.info("원가계산 건수 :{} / {}", payload.getTotalCnt2(), payload.getCstList().size()); }
            if (payload.getReqreRsceList() != null) {
                log.info("자원내역 건수 :{} / {}", payload.getTotalCnt3(), payload.getReqreRsceList().size());
            } else {
                log.info("장기계속계약_차수 총차가아닌 - 자원내역 연동 X");
            }

            // 1. EURECA -> CAIROS 원가 내역서정보 등록
            c3RComponent.processCntrDtlsFromEureca(payload);

            header.put("resultCode", "00");

            // 사업-공공(P) , CAIROS -> PGAIA
            boolean isPgaiaProject = informationService.isPgaiaCheckByCntrctNo(payload.getCntrctNo());
            log.info("4. isPgaiaProject: {}", isPgaiaProject);
            if (isPgaiaProject) {

                log.info("5. Pgaia MSG SEND");
                Map cairos2Pgaia = invokeCairos2Pgaia("CAGA0002", Map.of("payload", payload));

                String resultCode = cairos2Pgaia.get("resultCode").toString();
                String resultMsg = cairos2Pgaia.get("resultMsg").toString();

                if (!"00".equals(resultCode)) {
                    // EURECA -> CAIROS 성공, CAIROS -> PGAIA 실패 경우, EURECA 에 성공 응답.
//                throw new GaiaBizException(ErrorType.INTERFACE, "Cairos2Pgaia - CAGA1015");
                    log.error("updateCntrDtls - cairos2Pgaia Error");

                    /* LOG 작성 (CAIROS -> PGAIA)  */
                    Log.SmApiLogDto smApiLog2 = new Log.SmApiLogDto();
                    smApiLog2.setApiId("CAGA0002");
                    smApiLog2.setApiType("OUT");
                    smApiLog2.setSourceSystemCode("CAIROS");
                    smApiLog2.setTargetSystemCode("PGAIA");
                    smApiLog2.setServiceType("CAIROStoPGAIA");
                    smApiLog2.setServiceUuid(UUID.randomUUID().toString());
                    smApiLog2.setReqMethod("POST");
                    smApiLog2.setReqData("updateCntrDtls");
                    smApiLog2.setReqDt(FormatUtil.formatDate(new Date(), "yyyyMMddHHmmss"));
                    smApiLog2.setRgstrId("EURECA");
                    smApiLog2.setResultCode(500);    // 서버 내부 오류
                    smApiLog2.setErrorYn("Y");
                    smApiLog2.setErrorReason(resultMsg);
                    systemLogComponent.asyncAddApiLog(smApiLog2);
                }
            }

        } catch (BizException bizException) {
            header.put("resultCode", "01");
            header.put("resultMsg", bizException.getMessage());
            log.error("updateCntrDtls: 오류 발생, 메세지 = ", bizException);

            smApiLog.setResultCode(500);    // 서버 내부 오류
            smApiLog.setErrorYn("Y");
            smApiLog.setErrorReason(bizException.getMessage());
            smApiLog.setResDt(FormatUtil.formatDate(new Date(), "yyyyMMddHHmmss"));
        } catch (RuntimeException e) {
            header.put("resultCode", "01");
            String errorMessage = e.getMessage();
            if (errorMessage == null || errorMessage.trim().isEmpty() || errorMessage.contains("\n")) {
                errorMessage = "API 통신이 원활하지 않습니다. 관리자에게 문의하길 바랍니다.";
            }
            header.put("resultMsg", errorMessage);
            log.error("updateCntrDtls: 오류 발생, 메세지 = ", e);

            smApiLog.setResultCode(500);    // 서버 내부 오류
            smApiLog.setErrorYn("Y");
            smApiLog.setErrorReason(e.getMessage());
            smApiLog.setResDt(FormatUtil.formatDate(new Date(), "yyyyMMddHHmmss"));
        } catch (Exception e) {
            header.put("resultCode", "04");
            header.put("resultMsg", "API 통신이 원활하지 않습니다. 관리자에게 문의하길 바랍니다.");
            log.error("updateCntrDtls: 오류 발생, 메세지 = ", e);

            smApiLog.setResultCode(500);    // 서버 내부 오류
            smApiLog.setErrorYn("Y");
            smApiLog.setErrorReason(e.getMessage());
            smApiLog.setResDt(FormatUtil.formatDate(new Date(), "yyyyMMddHHmmss"));
        } finally {
            response.put("header", header);
            response.put("body", body);
            result.put("response", response);

            log.info("result : {}", result);
            systemLogComponent.asyncAddApiLog(smApiLog);
        }



        return ResponseEntity.ok(result);
    }

    /**
     * 내역서 산출상세 수신 (미사용)
     * 대상테이블: CT_UNIT_COST, CT_UNIT_COST_DETAIL
     *
     * 2025-07-02 유레카 화면제공으로 인한 해당 api 미사용
     * @param serviceKey
     * @param payload
     * @return
     */
    @Deprecated
    @PostMapping("/cairos/updateSpcsCalcDtls")
    public ResponseEntity<Map<String, Object>> updateSpcsCalcDtls(
            @RequestParam("serviceKey") String serviceKey,
            @RequestBody SpcsCalcDtlsSyncRequest payload) {

        Map<String, Object> result = new HashMap<>();
        Map<String, Object> response = new HashMap<>();
        Map<String, Object> header = new HashMap<>();
        Map<String, Object> body = new HashMap<>();

        try {
            if (!this.eureca2CairosKey.equals(serviceKey)) {
                throw new BizException("서비스키가 유효하지 않습니다.");
            }

            log.info("EURECA -> CAIROS updateSpcsCalcDtls START");
            if (payload.getUcostDtlList() != null) { log.info("일위대가 내역건수: {} / {}", payload.getUcostCnt(), payload.getUcostDtlList().size() ); }
            if (payload.getHmupcDtlList() != null) { log.info("중기단가 내역건수: {} / {}", payload.getHmupcCnt(), payload.getHmupcDtlList().size() ); }


            header.put("resultCode", "00");
        } catch (BizException e) {
            header.put("resultCode", "01");
            header.put("resultMsg", "API 통신이 원활하지 않습니다. 관리자에게 문의하길 바랍니다.");
            log.error("updateCntrDtls: 오류 발생, 메세지 = {}",e.getMessage());
        } catch (Exception e) {
            header.put("resultCode", "04");
            header.put("resultMsg", "API 통신이 원활하지 않습니다. 관리자에게 문의하길 바랍니다.");
            log.error("updateCntrDtls: 오류 발생, 메세지 = {}",e.getMessage());
        } finally {
            response.put("header", header);
            response.put("body", body);
            result.put("response", response);

            log.info("result : {}", result);
        }

        return ResponseEntity.ok(result);
    }
}
