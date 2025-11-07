package kr.co.ideait.platform.gaiacairos.comp.projectcost.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.Maps;
import kr.co.ideait.iframework.BizException;
import kr.co.ideait.platform.gaiacairos.core.base.AbstractGaiaCairosService;
import kr.co.ideait.platform.gaiacairos.core.components.log.SystemLogComponent;
import kr.co.ideait.platform.gaiacairos.core.exception.ErrorType;
import kr.co.ideait.platform.gaiacairos.core.exception.GaiaBizException;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.*;
import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories.*;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.projectcost.PaymentMybatisParam;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.projectcost.PaymentMybatisParam.PaymentFormTypeSelectInput;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.security.UserAuth;
import kr.co.ideait.platform.gaiacairos.core.type.PlatformType;
import kr.co.ideait.platform.gaiacairos.core.util.restclient.EurecaClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class PaymentService extends AbstractGaiaCairosService {

    @Autowired
    CwPayMngRepository cwPayMngRepository;

    @Autowired
    CwPayDetailRepository cwPayDetailRepository;

    @Autowired
    CnContractChangeRepository cnContractChangeRepository;

    @Autowired
    CwPayCostCalculatorRepository cwPayCostCalculatorRepository;

    @Autowired
    CwPayActivityRepository cwPayActivityRepository;

    @Autowired
    EurecaClient eurecaClient;

    /**
     * 2025-02-17 회차 정보를 위한 계약변경정보 가져오기 추가
     *
     * @param cntrctNo 계약번호
     * @return
     */
    public Map getCntrctChgInfo(String cntrctNo) {
        PaymentFormTypeSelectInput paymentFormTypeSelectInput = new PaymentFormTypeSelectInput();
        paymentFormTypeSelectInput.setCntrctNo(cntrctNo);

        return mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.projectcost.Payment.getCntrctChgInfo",
                paymentFormTypeSelectInput);
    }


    /**
     * 기성 리스트 가져오기
     * @param cntrctNo
     * @return
     */
    public List selectPaymentList(String cntrctNo) {
        PaymentFormTypeSelectInput paymentFormTypeSelectInput = new PaymentFormTypeSelectInput();

        paymentFormTypeSelectInput.setCntrctNo(cntrctNo);

        return mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.projectcost.Payment.selectPaymentList",
                paymentFormTypeSelectInput);
    }


    /**
     * 기성 조회
     * @param cntrctNo
     * @param payprceSno
     * @return
     */
    public CwPayMng getPayment(String cntrctNo, Long payprceSno) {
        return cwPayMngRepository.findByCntrctNoAndPayprceSno(cntrctNo, payprceSno).orElse(null);
    }

    /**
     * 기성 상세 가져오기
     *
     * @param cntrctNo
     * @param String   (프로젝트 타입)
     * @return List
     * @throws
     * public         List<Map<String, ?>> selectPaymentDetail(String cntrctNo, Long
     *                payprceSno){
     *                PaymentFormTypeSelectInput paymentFormTypeSelectInput = new
     *                PaymentFormTypeSelectInput();
     *
     *                paymentFormTypeSelectInput.setCntrctNo(cntrctNo);
     *                paymentFormTypeSelectInput.setPayprceSno(payprceSno);
     *
     *                return mybatisSession.selectList(
     *                "kr.co.ideait.platform.gaiacairos.mybatis.mappers.projectcost.Payment.selectPaymentDetail",
     *                paymentFormTypeSelectInput);
     *                }
     */


    /**
     * 기성 회차 시퀀스 가져오기
     * @param cntrctNo
     * @return
     */
    public List<Map<String, ?>> selectPayprceTmnum(String cntrctNo) {
        PaymentFormTypeSelectInput paymentFormTypeSelectInput = new PaymentFormTypeSelectInput();

        paymentFormTypeSelectInput.setCntrctNo(cntrctNo);

        return mybatisSession.selectList(
                "kr.co.ideait.platform.gaiacairos.mybatis.mappers.projectcost.Payment.selectPayprceTmnum", paymentFormTypeSelectInput);
    }

    /**
     * 기성 추가
     *
     * @param paymentInsert
     * @param dailyReportDate
     * @return
     */
    @Transactional
    public CwPayMng addPayment(CwPayMng paymentInsert, String dailyReportDate, String prevPayprceYm) {
        log.info("addPayment: paymentInsert = {}, dailyReportDate = {}, prevPayprceYm = {}",paymentInsert, dailyReportDate, prevPayprceYm);
        CwPayMng cwPayMng = null;

        if (paymentInsert.getPayprceSno() == null) {
            Long maxSno = generatePayprceSno(paymentInsert.getCntrctNo());
            String chgId = getCntrctChgId(paymentInsert.getCntrctNo());
            paymentInsert.setPayprceSno(maxSno);
            paymentInsert.setCntrctChgId(chgId);
            paymentInsert.setEurecaSendYn("N");
        }


        if (PlatformType.CAIROS.getName().equals(platform)) {
            cwPayMng = cwPayMngRepository.saveAndFlush(paymentInsert);
            Map<String, Object> eurecaParams = this.getPrgpymntDtlsForEureca(cwPayMng.getCntrctChgId(),
                    cwPayMng.getPayprceTmnum());

            log.info("addPayment: 기성내역서 유레카 전송 eurecaParams = {}",eurecaParams);

            // 기성내역서(작성요청) 유레카 전송
            Map<String, Object> eurecaResponse = eurecaClient.registerPrgpymntDtls(eurecaParams);

            log.info("addPayment: 기성내역서 유레카 전송 결과 eurecaResponse = {}",eurecaResponse);

            if ("00".equals(eurecaResponse.get("resultCode"))) {
                cwPayMng.setEurecaSendYn("Y");
            } else {
                cwPayMng.setEurecaSendYn("N");
                log.error("addPayment: 유레카 API 송수신 실패");
//                throw new GaiaBizException(ErrorType.ETC, String.format("유레카 연동오류: %s", eurecaResponse.get("resultMsg")));
            }
            mybatisSession.update("kr.co.ideait.platform.gaiacairos.mybatis.mappers.projectcost.Payment.updateEurecaSendYn", cwPayMng);
        } else {
            log.info("addPayment: 카이로스가 아니므로 유레카에 기성내역서 전달하지 않음 platform = {}", platform);
        }

        return cwPayMng;
    }

    public Map<String, Object> tempPrgpymntDtlsForEureca(String cntrctChgId, Long payprceTmnum) {
        // log.info("mybatisSession.getConnection() : {}",
        // ToStringBuilder.reflectionToString(mybatisSession.getSqlSessionFactory().getConfiguration().getEnvironment().getDataSource())
        // );

        CwPayMng cwPayMng = cwPayMngRepository.findByCntrctChgIdAndPayprceTmnumAndDltYn(cntrctChgId, payprceTmnum, "N")
                .orElse(null);

        if (cwPayMng == null) {
            throw new GaiaBizException(ErrorType.NO_DATA,"기성내역서 없음");
        }

        PaymentMybatisParam.PaymentHistorySelectInput paymentHistorySelectInput = new PaymentMybatisParam.PaymentHistorySelectInput();
        paymentHistorySelectInput.setCntrctNo(cwPayMng.getCntrctNo()); // 계약번호
        paymentHistorySelectInput.setPayprceSno(cwPayMng.getPayprceSno());

        List<PaymentMybatisParam.PaymentHistory> paymentHistoryList = mybatisSession.selectList(
                "kr.co.ideait.platform.gaiacairos.mybatis.mappers.projectcost.Payment.selectPaymentHistoryListForEurecaTemp",
                paymentHistorySelectInput);

        Map<String, Object> prgpymnt = new HashMap<>();

        prgpymnt.put("cntrctChgId", cwPayMng.getCntrctChgId()); // 변경계약ID
        prgpymnt.put("payprceTmnum", cwPayMng.getPayprceTmnum()); // 기성회차
        prgpymnt.put("payprceYm", cwPayMng.getPayprceYm()); //
        prgpymnt.put("reqCnt", paymentHistoryList.size()); //
        prgpymnt.put("prgpymntList", paymentHistoryList); //

        return prgpymnt;
    }

    /**
     * 기성내역서 유레카 전송
     *
     * @param cntrctChgId  변경계약ID
     * @param payprceTmnum 기성회차
     * @return
     */
    public Map<String, Object> getPrgpymntDtlsForEureca(String cntrctChgId, Long payprceTmnum) {
        CwPayMng cwPayMng = cwPayMngRepository.findByCntrctChgIdAndPayprceTmnumAndDltYn(cntrctChgId, payprceTmnum, "N")
                .orElse(null);

        if (cwPayMng == null) {
            throw new GaiaBizException(ErrorType.NO_DATA,"기성내역서 없음");
        }

        PaymentMybatisParam.PaymentHistorySelectInput paymentHistorySelectInput = new PaymentMybatisParam.PaymentHistorySelectInput();
        paymentHistorySelectInput.setCntrctNo(cwPayMng.getCntrctNo()); // 계약번호
        paymentHistorySelectInput.setDailyReportDate(cwPayMng.getPayprceYm());

        List<PaymentMybatisParam.PaymentHistory> paymentHistoryList = mybatisSession.selectList(
                "kr.co.ideait.platform.gaiacairos.mybatis.mappers.projectcost.Payment.selectPaymentHistoryListForEureca",
                paymentHistorySelectInput);

        Map<String, Object> prgpymnt = new HashMap<>();

        prgpymnt.put("cntrctChgId", cwPayMng.getCntrctChgId()); // 변경계약ID
        prgpymnt.put("payprceTmnum", cwPayMng.getPayprceTmnum()); // 기성회차
        prgpymnt.put("payprceYm", cwPayMng.getPayprceYm()); //
        prgpymnt.put("reqCnt", paymentHistoryList.size()); //
        prgpymnt.put("prgpymntList", paymentHistoryList); //

        return prgpymnt;
    }

    private String checkAccessAuth(String apprvStat, String usrId, String auth, String loginUserId) {
        String result = null;
        // 테스트용 임시 코드 s
        if ("ADMIN".equals(usrId)) {
            result = "Y";
        }
        // 테스트용 임시 코드 e
        else {
            if (StringUtils.isEmpty(apprvStat)) {
                if ("U".equals(auth)) {
                    result = "Y";
                } else {
                    result = "N";
                }
            } else {
                if ("R".equals(auth)) {
                    result = "Y";
                } else {
                    result = "N";
                }
            }
        }
        return result;
    }

    public Map<String, Object> retrieveAccssAuthChk(String cntrctChgId, Long payprceTmnum, String usrId, String auth, String loginUserId) {
        CwPayMng cwPayMng = cwPayMngRepository.findByCntrctChgIdAndPayprceTmnumAndDltYn(cntrctChgId, payprceTmnum, "N")
                .orElse(null);

        if (cwPayMng == null) {
            throw new GaiaBizException(ErrorType.NO_DATA,"기성내역서 없음");
        }

        Map<String, Object> body = new HashMap<>();
        body.put("authYn", checkAccessAuth(cwPayMng.getApprvlStats(), usrId, auth, loginUserId)); //

        return body;
    }


    /**
     * 기성 수정
     * @param paymentInsert
     * @return
     */
    @Transactional
    public CwPayMng updatePayment(CwPayMng paymentInsert) {
        log.info("updatePayment: 기성 수정 진행 paymentInsert = {}", paymentInsert);
        CwPayMng cwPayMng = null;

        try {
            cwPayMng = cwPayMngRepository.save(paymentInsert);
            log.info("updatePayment: 기성 수정 저장 성공 cwPayMng = {}", cwPayMng);
        } catch (GaiaBizException e ) {
            log.info("updatePayment: 기성 수정 저장 실패, 오류 메세지 = {}", e.getMessage());
        }
        if(cwPayMng != null) {
            if (PlatformType.CAIROS.getName().equals(platform)) {
                Map<String, Object> eurecaParams = this.getPrgpymntDtlsForEureca(cwPayMng.getCntrctChgId(),
                        cwPayMng.getPayprceTmnum());

                log.info("updatePayment: 수정한 기성내역서 유레카 전송 eurecaParams = {}", eurecaParams);

                // 기성내역서(작성요청) 유레카 전송
                Map<String, Object> eurecaResponse = eurecaClient.registerPrgpymntDtls(eurecaParams);

                log.info("updatePayment: 수정한 기성내역서 유레카 전송 결과 eurecaResponse = {}", eurecaResponse);

                if ("00".equals(eurecaResponse.get("resultCode"))) {
                    log.info("updatePayment: 유레카 API 연동 성공");
                    cwPayMng.setEurecaSendYn("Y");
                } else {
                    log.info("updatePayment: 유레카 API 연동 오류로 카이로스 DB 저장만 진행");
                    cwPayMng.setEurecaSendYn("N");
//                throw new GaiaBizException(ErrorType.ETC, String.format("유레카 연동오류: %s", eurecaResponse.get("resultMsg")));
                }
                mybatisSession.update("kr.co.ideait.platform.gaiacairos.mybatis.mappers.projectcost.Payment.updateEurecaSendYn", cwPayMng);
            } else {
                log.info("updatePayment: 카이로스가 아니거나, cwPayMng가 null 상태로 유레카에 기성내역서 전달하지 않음 platform = {}, cwPayMng = {}", platform, cwPayMng);
            }
        }
        return cwPayMng;
    }


    /**
     * 기성 PGAIA 추가할 데이터 조회
     */
    @Transactional
    public Map<String, Object> selectPaymentResource(String cntrctNo, Long payprceSno) {
        List<CwPayCostCalculator> cwPayCostCalculator = cwPayCostCalculatorRepository
                .findByCntrctNoAndPayprceSno(cntrctNo, payprceSno);

        List<CwPayDetail> cwPayDetail = cwPayDetailRepository
                .findByCntrctNoAndPayprceSno(cntrctNo, payprceSno);

        List<CwPayActivity> cwPayActivity = cwPayActivityRepository
                .findByCntrctNoAndPayprceSno(cntrctNo, payprceSno);

        Map<String, Object> returnMap = new HashMap<>();
        returnMap.put("payCostCalculator", cwPayCostCalculator);
        returnMap.put("payDetail", cwPayDetail);
        returnMap.put("activity", cwPayActivity);
        return returnMap;
    }


    /**
     * 기성 PGAIA에 추가
     */
    @Transactional
    public void insertPgaiaPayment(CwPayMng cwPayMng, List<CwPayCostCalculator> cwPayCostCalculator,
                                   List<CwPayDetail> cwPayDetail,
                                   List<CwPayActivity> cwPayActivity, String userId) {

//        cwPayMng.setApprvlStats("E");
//        cwPayMng.setApprvlReqDt(LocalDateTime.now());
//        cwPayMng.setApprvlReqId(userId);
        cwPayMngRepository.save(cwPayMng);

        if (cwPayCostCalculator != null && !cwPayCostCalculator.isEmpty()) {
            for (CwPayCostCalculator calculator : cwPayCostCalculator) {
                cwPayCostCalculatorRepository.save(calculator);
            }
        }

        if (cwPayDetail != null && !cwPayDetail.isEmpty()) {
            for (CwPayDetail detail : cwPayDetail) {
                cwPayDetailRepository.save(detail);
            }
        }

        if (cwPayActivity != null && !cwPayActivity.isEmpty()) {
            for (CwPayActivity activity : cwPayActivity) {
                cwPayActivityRepository.save(activity);
            }
        }

    }

    public void insertPaymentResourceByDeposit(Map<String, Object> resources) {
        CwPayMng cwPayMng = objectMapper.convertValue(resources.get("cwPayMng"), CwPayMng.class);
        if(cwPayMng != null) {
            cwPayMngRepository.save(cwPayMng);
        }

        List<CwPayCostCalculator> cwPayCostCalculator = objectMapper.convertValue(resources.get("payCostCalculator"), new TypeReference<List<CwPayCostCalculator>>() {});
        if(cwPayCostCalculator != null && !cwPayCostCalculator.isEmpty()) {
            cwPayCostCalculatorRepository.saveAll(cwPayCostCalculator);
        }

        List<CwPayDetail> cwPayDetail = objectMapper.convertValue(resources.get("payDetail"), new TypeReference<List<CwPayDetail>>() {});
        if(cwPayDetail != null && !cwPayDetail.isEmpty()) {
            cwPayDetailRepository.saveAll(cwPayDetail);
        }

        List<CwPayActivity> cwPayActivity = objectMapper.convertValue(resources.get("activity"), new TypeReference<List<CwPayActivity>>() {});
        if(cwPayActivity != null && !cwPayActivity.isEmpty()) {
            cwPayActivityRepository.saveAll(cwPayActivity);
        }
    }


    /**
     * 기성 승인요청 취소
     * @param apDocList
     * @param usrId
     * @param toApi
     */
    @Transactional
    public void updatePaymentApprovalReqCancel(List<ApDoc> apDocList, String usrId, boolean toApi) {
        apDocList.forEach(apDoc -> {
            CwPayMng cwPayMng = cwPayMngRepository.findByApDocId(apDoc.getApDocId()).orElse(null);

            if(cwPayMng == null) return;

            String apprvlStat = cwPayMng.getApprvlStats();

            if (!"E".equals(apprvlStat)) {
                throw new GaiaBizException(ErrorType.BAD_REQUEST,"올바른 결재 상태가 아닙니다.");
            }

            if(toApi) {
                cwPayMngRepository.delete(cwPayMng);
            } else {
                cwPayMngRepository.updateApprStatusByCntrctNoAndPayprceSno(null, null, null, null, cwPayMng.getCntrctNo(), cwPayMng.getPayprceSno());
            }

            mybatisSession.delete("kr.co.ideait.platform.gaiacairos.mybatis.mappers.projectcost.Payment.deletePaymentList", cwPayMng);
            mybatisSession.delete("kr.co.ideait.platform.gaiacairos.mybatis.mappers.projectcost.Payment.deletePaymentCostList", cwPayMng);

            if (PlatformType.CAIROS.getName().equals(platform)) {
                Map<String, String> eurecaParams = Maps.newHashMap();
                eurecaParams.put("cntrctChgId", cwPayMng.getCntrctChgId()); // 변경계약ID
                eurecaParams.put("payprceTmnum", String.valueOf(cwPayMng.getPayprceTmnum())); // 기성회차
                eurecaParams.put("payprceReqDate",
                        StringUtils.defaultString(cwPayMng.getPayApprvlDate()).replaceAll("\\D", "")); // 기성신청일자
                eurecaParams.put("apprvlStats", "02"); // 승인요청취소
                // eurecaParams.put("apprvlDate", ""); //승인일자

                // 기성상태결과 변경
                Map<String, Object> eurecaResponse = eurecaClient.updatePrgpymntAprv(eurecaParams);

                if (!"00".equals(eurecaResponse.get("resultCode"))) {
                    throw new GaiaBizException(ErrorType.ETC, String.format("유레카 연동오류: %s", eurecaResponse.get("resultMsg")));
                }
            }
        });
    }

    /**
     * 기성 승인 / 반려 처리
     *
     * @param cwPayMng
     */
    @Transactional
    public void updatePaymentApprovalStatus(CwPayMng cwPayMng, String apUsrId) {
        String apprvlStat = cwPayMng.getApprvlStats();

        if (!"A".equals(apprvlStat) && !"R".equals(apprvlStat)) {
            throw new BizException("올바른 결재 상태가 아닙니다.");
        }

        cwPayMngRepository.updateByCntrctNoAndPayprceSnoAnyType(cwPayMng.getApprvlStats(),
                apUsrId, LocalDateTime.now(), cwPayMng.getCntrctNo(), cwPayMng.getPayprceSno());

        if (PlatformType.CAIROS.getName().equals(platform)) {
            Map<String, String> eurecaParams = Maps.newHashMap();
            eurecaParams.put("cntrctChgId", cwPayMng.getCntrctChgId()); // 변경계약ID
            eurecaParams.put("payprceTmnum", String.valueOf(cwPayMng.getPayprceTmnum())); // 기성회차
            eurecaParams.put("payprceReqDate",
                    StringUtils.defaultString(cwPayMng.getPayApprvlDate()).replaceAll("\\D", "")); // 기성신청일자

            if ("A".equals(apprvlStat)) {
                eurecaParams.put("apprvlStats", "03"); // 승인상태
                eurecaParams.put("apprvlDate", String.valueOf(cwPayMng.getPayprceTmnum())); // 승인일자
            } else {
                eurecaParams.put("apprvlStats", "04"); // 승인상태
            }

            // 기성상태결과 변경
            Map<String, Object> eurecaResponse = eurecaClient.updatePrgpymntAprv(eurecaParams);

            if (!"00".equals(eurecaResponse.get("resultCode"))) {
                throw new GaiaBizException(ErrorType.ETC, String.format("유레카 연동오류: %s", eurecaResponse.get("resultMsg")));
            }
        }
    }


    /**
     * 기성 순번 증가
     * @param cntrctNo
     * @return
     */
    private Long generatePayprceSno(String cntrctNo) {
        Long maxSno = cwPayMngRepository.findMaxSnoByCntrctNo(cntrctNo);
        return (maxSno == null ? 1 : maxSno + 1);
    }


    /**
     * 최종 계약 가져오기
     * @param cntrctNo
     * @return
     */
    private String getCntrctChgId(String cntrctNo) {
        String cntrctChgId = cnContractChangeRepository.findCntrctChgIdByCntrctNo(cntrctNo);
        return cntrctChgId;
    }


    /**
     * 공종 목록 가져오기
     * @param cntrctNo
     * @param payprceSno
     * @param cntrctChgId
     * @return
     */
    public List selectPaymentCbsList(String cntrctNo, Long payprceSno, String cntrctChgId) {
        PaymentFormTypeSelectInput paymentFormTypeSelectInput = new PaymentFormTypeSelectInput();

        paymentFormTypeSelectInput.setCntrctNo(cntrctNo);
        paymentFormTypeSelectInput.setPayprceSno(payprceSno);
        paymentFormTypeSelectInput.setCntrctChgId(cntrctChgId);
        return mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.projectcost.Payment.selectPaymentCbsList",
                paymentFormTypeSelectInput);
    }


    /**
     * 기성내역서 리스트 가져오기
     * @param cntrctNo
     * @param payprceSno
     * @param cntrctChgId
     * @param cnsttySnList
     * @param searchText
     * @return
     */
    public List<Map<String, ?>> selectPaymentHistoryList(String cntrctNo, Long payprceSno, String cntrctChgId,
                                                         List<Integer> cnsttySnList, String searchText) {
        log.info("selectPaymentHistoryList: 기성 내역서 리스트 조회, cntrctNo = {}, payprceSno = {}, cntrctChgId = {}, cnsttySnList = {}, searchText = {}"
                , cntrctNo, payprceSno, cntrctChgId,cntrctChgId, cntrctChgId);

        PaymentFormTypeSelectInput paymentFormTypeSelectInput = new PaymentFormTypeSelectInput();

        paymentFormTypeSelectInput.setCntrctNo(cntrctNo);
        paymentFormTypeSelectInput.setPayprceSno(payprceSno);
        paymentFormTypeSelectInput.setCntrctChgId(cntrctChgId);
        paymentFormTypeSelectInput.setCnsttySnList(cnsttySnList);
        paymentFormTypeSelectInput.setSearchText(searchText);
        List<Map<String, ?>> result = null;
        try {
            result = mybatisSession.selectList(
                    "kr.co.ideait.platform.gaiacairos.mybatis.mappers.projectcost.Payment.selectPaymentHistoryList",
                    paymentFormTypeSelectInput);
        }catch (GaiaBizException e) {
            log.info("selectPaymentHistoryList: 기성 내역서 리스트 미존재이거나 알 수 없는 오류 발생, error = {}", e.getMessage());
        }
        return  result;
    }


    /**
     * 원가내역서 리스트 가져오기
     * @param cntrctNo
     * @param payprceSno
     * @param cntrctChgId
     * @return
     */
    public List<Map<String, ?>> selectPaymentCostCalculatorList(String cntrctNo, Long payprceSno, String cntrctChgId) {
        PaymentFormTypeSelectInput paymentFormTypeSelectInput = new PaymentFormTypeSelectInput();

        paymentFormTypeSelectInput.setCntrctNo(cntrctNo);
        paymentFormTypeSelectInput.setPayprceSno(payprceSno);
        paymentFormTypeSelectInput.setCntrctChgId(cntrctChgId);

        return mybatisSession.selectList(
                "kr.co.ideait.platform.gaiacairos.mybatis.mappers.projectcost.Payment.selectPaymentCostCalculatorList",
                paymentFormTypeSelectInput);
    }


    /**
     * 기성내역서 리스트 가져오기
     * @param cntrctNo
     * @param payprceSno
     * @param cntrctChgId
     * @param searchText
     * @return
     */
    public List<Map<String, ?>> selectPaymentHistoryRecountList(String cntrctNo, Long payprceSno, String cntrctChgId,
                                                                String searchText) {
        PaymentFormTypeSelectInput paymentFormTypeSelectInput = new PaymentFormTypeSelectInput();

        paymentFormTypeSelectInput.setCntrctNo(cntrctNo);
        paymentFormTypeSelectInput.setPayprceSno(payprceSno);
        paymentFormTypeSelectInput.setCntrctChgId(cntrctChgId);
        paymentFormTypeSelectInput.setSearchText(searchText);

        return mybatisSession.selectList(
                "kr.co.ideait.platform.gaiacairos.mybatis.mappers.projectcost.Payment.selectPaymentHistoryRecountList",
                paymentFormTypeSelectInput);
    }


    /**
     * 기성 내역수량 업데이트
     * @param cwpaydetail
     */
    @Transactional
    public void updateRecountPaymentList(List<CwPayDetail> cwpaydetail,String rgstrId) {
        cwpaydetail.forEach(id -> {
            id.setRgstDt(LocalDateTime.now());
            id.setRgstrId(rgstrId);
            cwPayDetailRepository.save(id);
        });
    }


    /**
     * 기성 내역수량 내역 가져오기
     * @param cntrctNo
     * @param payprceSno
     * @param cntrctChgId
     * @param dailyReportDate
     * @param payprceYm
     * @return
     */
    public List<CwPayDetail> selectPaymentHistoryNewList(String cntrctNo, Long payprceSno, String cntrctChgId,
                                                         String dailyReportDate, String payprceYm) {
        Map map = new HashMap();
        map.put("cntrctNo", cntrctNo);
        map.put("payprceSno", payprceSno);
        map.put("cntrctChgId", cntrctChgId);
        map.put("dailyReportDate", dailyReportDate);
        map.put("payprceYm", payprceYm);

        return mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.projectcost.Payment.selectPaymentHistoryNewList",
                map);
    }

    /**
     * 기성 가져오기
     *
     * @param apDocId
     * @param apDocStats
     * @param apUsrId
     */
    public void updatePaymentByApDocId(String apDocId, String apUsrId, String apDocStats) {
        CwPayMng cwPayMng = cwPayMngRepository.findByApDocId(apDocId).orElse(null);
        if (cwPayMng != null) {
            String apStats = "C".equals(apDocStats) ? "A" : "R";
            cwPayMng.setApprvlStats(apStats);
            // 기성상태 업데이트
            updatePaymentApprovalStatus(cwPayMng, apUsrId);
        }
    }


    /**
     * 기성 데이터 조회
     * @param apDocId
     * @return
     */
    public Map<String, Object> selectPaymentByApDocId(String apDocId) {
        Map<String, Object> returnMap = new HashMap<>();
        CwPayMng cwPayMng = cwPayMngRepository.findByApDocId(apDocId).orElse(null);
        if (cwPayMng != null) {
            returnMap.put("report", cwPayMng);
            returnMap.put("resources", selectPaymentResource(cwPayMng.getCntrctNo(), cwPayMng.getPayprceSno()));
        }
        return returnMap;
    }



    public CwPayMng selectCwPayMng(String cntrctNo, Long payprceSno) {
        return cwPayMngRepository.findByCntrctNoAndPayprceSno(cntrctNo, payprceSno)
                .orElse(null);
    }
    public void updateByCntrctNoAndPayprceSno(String apprvlStats, String cntrctNo, Long payprceSno) {
        cwPayMngRepository.updateByCntrctNoAndPayprceSno(apprvlStats, UserAuth.get(true).getUsrId(),
                LocalDateTime.now(), cntrctNo, payprceSno);
    }
    public void insertPaymentList(Map<String, Object> sqlParams ) {
        mybatisSession.insert("kr.co.ideait.platform.gaiacairos.mybatis.mappers.projectcost.Payment.insertPaymentList", sqlParams);
    }
    public void insertPaymentCostList(Map<String, Object> sqlParams ) {
        mybatisSession.insert("kr.co.ideait.platform.gaiacairos.mybatis.mappers.projectcost.Payment.insertPaymentCostList", sqlParams);
    }
    public boolean CheckPgaiaFirstApprover(Map<String, Object> checkParams) {
        return mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.eapproval.approval.checkPgaiaFirstApprover", checkParams);
    }
    public List<CwPayCostCalculator>  selectCwPayCostCalculator(String cntrctNo, Long payprceSno){
        return cwPayCostCalculatorRepository.findByCntrctNoAndPayprceSno(cntrctNo, payprceSno);
    }
    public List<CwPayDetail> selectCwPayDetail(String cntrctNo, Long payprceSno) {
        return cwPayDetailRepository.findByCntrctNoAndPayprceSno(cntrctNo, payprceSno);
    }
    public List<CwPayActivity> selectCwPayActivity(String cntrctNo, Long payprceSno) {
        return cwPayActivityRepository.findByCntrctNoAndPayprceSno(cntrctNo, payprceSno);
    }


    public void updateApprovalStatus(CwPayMng cwPayMng, String usrId, String apprvlStats) {
        cwPayMng.setApprvlStats(apprvlStats);
        if ("E".equals(apprvlStats)) {
            cwPayMng.setApprvlReqId(usrId);
            cwPayMng.setApprvlReqDt(LocalDateTime.now());
        } else {
            cwPayMng.setApprvlId(usrId);
            cwPayMng.setApprvlDt(LocalDateTime.now());
        }
        cwPayMngRepository.save(cwPayMng);
    }
}
