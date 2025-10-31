package kr.co.ideait.platform.gaiacairos.comp.projectcost;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import kr.co.ideait.iframework.BizException;
import kr.co.ideait.iframework.FormatUtil;
import kr.co.ideait.platform.gaiacairos.comp.eapproval.service.ApprovalRequestService;
import kr.co.ideait.platform.gaiacairos.comp.projectcost.service.DepositService;
import kr.co.ideait.platform.gaiacairos.comp.projectcost.service.PaymentService;
import kr.co.ideait.platform.gaiacairos.core.base.AbstractComponent;
import kr.co.ideait.platform.gaiacairos.core.components.log.SystemLogComponent;
import kr.co.ideait.platform.gaiacairos.core.exception.ErrorType;
import kr.co.ideait.platform.gaiacairos.core.exception.GaiaBizException;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CwFrontMoney;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CwPayMng;
import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories.CwPayMngRepository;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.security.UserAuth;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.log.Log;
import kr.co.ideait.platform.gaiacairos.core.type.PlatformType;
import kr.co.ideait.platform.gaiacairos.core.util.restclient.EurecaClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentComponent extends AbstractComponent {

    @Autowired
    PaymentService paymentService;

    @Autowired
    EurecaClient eurecaClient;

    @Autowired
    ApprovalRequestService approvalRequestService;

    @Autowired
    SystemLogComponent systemLogComponent;

    @Autowired
    DepositService depositService;

    @Autowired
    CwPayMngRepository cwPayMngRepository;



    /**
     * 기성 승인요청
     * @param cwpaymng
     */
    @Transactional
    public void updatePaymentList(List<CwPayMng> cwpaymng, String apiYn, String pjtDiv) {
        log.info("updatePaymentList: 기성 승인 요청 진행");
        Map<String, Object> sqlParams = Maps.newHashMap();

        cwpaymng.forEach(id -> {
            CwPayMng cwPayMng = paymentService.selectCwPayMng(id.getCntrctNo(), id.getPayprceSno());

            if (cwPayMng == null) {
                throw new BizException("기성 정보가 없습니다.");
            }

            if (!"E".equals(id.getApprvlStats())) {
                throw new BizException("올바른 결재 상태가 아닙니다.");
            }

            log.info("updatePaymentList: 기성 정보 확인 cwPayMng = {}", cwPayMng);
            //paymentService.updateByCntrctNoAndPayprceSno(id.getApprvlStats(), id.getCntrctNo(), id.getPayprceSno());

            if (PlatformType.CAIROS.getName().equals(platform) && apiYn.equals("Y")) {
                Map<String, String> eurecaParams = new HashMap<>();
                eurecaParams.put("cntrctChgId", cwPayMng.getCntrctChgId()); // 변경계약ID1
                eurecaParams.put("payprceTmnum", String.valueOf(cwPayMng.getPayprceTmnum())); // 기성회차

                Map<String, Object> eurecaResponse = eurecaClient.retrievePrgpymntDtls(eurecaParams);

                if (!"00".equals(eurecaResponse.get("resultCode"))) {
                    throw new GaiaBizException(ErrorType.ETC,
                            String.format("유레카 연동 오류: %s", eurecaResponse.get("resultMsg")));
                }

                Integer totalCnt1 = (Integer) eurecaResponse.get("totalCnt1");
                Integer totalCnt2 = (Integer) eurecaResponse.get("totalCnt2");
                List<Map> paymentList = (List<Map>) eurecaResponse.get("paymentList");
                List<Map> cstList = (List<Map>) eurecaResponse.get("cstList");

                // 유레카로부터 받은 paymentList 저장
                if (paymentList != null) {
                    List<List<Map>> subList = Lists.partition(paymentList, 100);

                    for (List<Map> payments : subList) {
                        sqlParams.clear();
                        sqlParams.put("cntrctNo", cwPayMng.getCntrctNo());
                        sqlParams.put("payprceSno", cwPayMng.getPayprceSno());
                        sqlParams.put("payments", payments);
                        sqlParams.put("usrId", UserAuth.get(true).getUsrId());

                        paymentService.insertPaymentList(sqlParams);
                    }
                }

                // 기성 원가계산서 등록
                if (cstList != null) {
                    List<List<Map>> subList = Lists.partition(cstList, 100);

                    for (List<Map> cost : subList) {
                        sqlParams.clear();
                        sqlParams.put("cntrctNo", cwPayMng.getCntrctNo());
                        sqlParams.put("payprceSno", cwPayMng.getPayprceSno());
                        sqlParams.put("costs", cost);
                        sqlParams.put("usrId", UserAuth.get(true).getUsrId());

                        paymentService.insertPaymentCostList(sqlParams);
                    }
                }

                // 승인요청 시 필요 데이터
                Map<String, Object> requestMap = new HashMap<>();
                requestMap.put("pjtNo", UserAuth.get(true).getPjtNo());
                requestMap.put("cntrctNo", cwPayMng.getCntrctNo());
                requestMap.put("isApiYn", apiYn);
                requestMap.put("pjtDiv", pjtDiv);
                requestMap.put("usrId", UserAuth.get(true).getUsrId());

                // 기성 데이터 조회 gaia 는 같은 DB 조회하므로 resourceMap 전달 불필요
                Map<String, Object> resourceMap = paymentService.selectPaymentResource(cwPayMng.getCntrctNo(), cwPayMng.getPayprceSno());

                // 전자결재 생성
                try {
                    log.info("updatePaymentList: 전자 결재 진행 resourceMap = {}", resourceMap);
                    approvalRequestService.insertPaymentDoc(cwPayMng, requestMap, resourceMap);
                } catch (RuntimeException e) {
                    log.info("updatePaymentList: 전자 결재 진행 실패, 오류 메세지 = {}", e.getMessage());
                    throw new RuntimeException(e);
                }



                // 유레카 승인 상태 전달
                eurecaParams.put("cntrctChgId", cwPayMng.getCntrctChgId()); // 변경계약ID
                eurecaParams.put("payprceTmnum", String.valueOf(cwPayMng.getPayprceTmnum())); // 기성회차
                eurecaParams.put("payprceReqDate",
                        StringUtils.defaultString(cwPayMng.getPayApprvlDate()).replaceAll("\\D", "")); // 기성신청일자
                eurecaParams.put("apprvlStats", "01"); // 승인상태


                // API 로그 적재
                Log.SmApiLogDto smApiLog = new Log.SmApiLogDto();
                smApiLog.setApiId("CAEU0001");
                smApiLog.setApiType("OUT");
                smApiLog.setSourceSystemCode("CAIROS");
                smApiLog.setTargetSystemCode("EURECA");
                smApiLog.setServiceType("CAIROStoEURECA");
                smApiLog.setServiceUuid(UUID.randomUUID().toString());
                smApiLog.setReqMethod("POST");
                smApiLog.setReqData(eurecaParams.toString());
                smApiLog.setReqDt(FormatUtil.formatDate(new Date(), "yyyyMMddHHmmss"));
                smApiLog.setRgstrId(UserAuth.get(true).getUsrId());

                // 기성상태결과 변경 (유레카 API 호출)
                try {
                    eurecaResponse = eurecaClient.updatePrgpymntAprv(eurecaParams);
                    log.info("updatePaymentList: 유레카 API 호출 결과 = ",eurecaResponse);
                    if (!"00".equals(eurecaResponse.get("resultCode"))) {
                        smApiLog.setResultCode(500);    // 서버 내부 오류
                        smApiLog.setErrorYn("Y");
                        smApiLog.setErrorReason(eurecaResponse.get("resultMsg").toString());
                        throw new GaiaBizException(ErrorType.ETC,
                                String.format("유레카 연동오류: %s", eurecaResponse.get("resultMsg")));
                    }
                    smApiLog.setResultCode(200);
                    smApiLog.setResData(eurecaResponse.toString());
                    smApiLog.setErrorYn("N");
                } catch (RuntimeException e) {
                    smApiLog.setResultCode(500);    // 서버 내부 오류
                    smApiLog.setErrorYn("Y");
                    smApiLog.setErrorReason(e.getMessage());
                    log.error("updatePaymentList: 오류 발생, 메세지 = ",e.getMessage());
                } finally {
                    smApiLog.setResDt(FormatUtil.formatDate(new Date(), "yyyyMMddHHmmss"));

                    log.info("updatePaymentList: API 로그 저장 smApiLog = {}",smApiLog);
                    systemLogComponent.asyncAddApiLog(smApiLog);
                }



            }
        });
    }


        /**
     * 기성 삭제f
     * @param cwpaymng
     */
    @Transactional
    public void delPayment(List<CwPayMng> cwpaymng) {
        cwpaymng.forEach(id -> {
            CwPayMng cwPayMng = cwPayMngRepository.findByCntrctNoAndPayprceSno(id.getCntrctNo(), id.getPayprceSno())
                    .orElse(null);

            if (cwPayMng != null) {
                cwPayMngRepository.updateDelete(cwPayMng);
                // 해당 선금 및 공제금도 같이 삭제 처리
                List<CwFrontMoney> cwFrontMoneyList = depositService.getDepositDataList(id.getCntrctNo(),
                        id.getPayprceSno());
                if (!cwFrontMoneyList.isEmpty() && cwFrontMoneyList.size() > 0) {
                    depositService.delDepositByPayment(cwFrontMoneyList);
                }

                if (cwPayMng.getApDocId() != null) {
                    approvalRequestService.deleteApDoc(cwPayMng.getApDocId());
                }
                // cwPayMngRepository.delete(cwPayMng);
            }
        });
    }

}
