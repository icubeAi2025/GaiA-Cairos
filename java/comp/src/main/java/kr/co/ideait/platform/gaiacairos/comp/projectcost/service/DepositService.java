package kr.co.ideait.platform.gaiacairos.comp.projectcost.service;

import jakarta.transaction.Transactional;
import kr.co.ideait.platform.gaiacairos.comp.eapproval.service.ApprovalRequestService;
import kr.co.ideait.platform.gaiacairos.core.base.AbstractGaiaCairosService;
import kr.co.ideait.platform.gaiacairos.core.constant.CommonCodeConstants;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.*;
import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories.CwFrontMoneyRepository;
import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories.CwPayMngRepository;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.projectcost.DepositMybatisParam.DepositFormTypeSelectInput;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.security.UserAuth;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class DepositService extends AbstractGaiaCairosService {

    @Autowired
    CwFrontMoneyRepository cwFrontMoneyRepository;

    @Autowired
    CwPayMngRepository cwPayMngRepository;

    @Autowired
    PaymentService paymentService;

    /**
     * 선급금 및 공제금 리스트 가져오기
     * 2025-05-27 수정(leejw): 파라미터 타입 변경, 추가
     *
     * @param cntrctNo
     * @return
     */
    public List selectDepositList(String cntrctNo) {
        Map<String, Object> params = new HashMap<>();
        params.put("ppaytycode", CommonCodeConstants.PPAYTY_TYPE_GROUP_CODE);
        params.put("appcode", CommonCodeConstants.APPSTATUS_CODE_GROUP_CODE);
        params.put("cntrctNo", cntrctNo);

        return mybatisSession.selectList(
                "kr.co.ideait.platform.gaiacairos.mybatis.mappers.projectcost.deposit.selectDepositList",
                params);
    }

    /**
     * 선급금 및 공제금 추가
     *
     * @param depositInsert
     * @return
     */
    @Transactional
    public CwFrontMoney addDeposit(CwFrontMoney depositInsert) {
        if (depositInsert.getPpaymnySno() == null) {
            Long maxSno = generatePpaymnySno(depositInsert.getCntrctNo());
            depositInsert.setPpaymnySno(maxSno);
        }

        Long amt = getAcmtlPpaymnyAmt(depositInsert.getCntrctNo(), depositInsert.getPayType());
        depositInsert
                .setAcmtlPpaymnyAmt(amt + (depositInsert.getPpaymnyAmt() == null ? 0 : depositInsert.getPpaymnyAmt()));

        return cwFrontMoneyRepository.save(depositInsert);
    }

    /**
     * 선급금 및 공제금 순번 증가
     *
     * @param cntrctNo
     * @return
     */
    private Long generatePpaymnySno(String cntrctNo) {
        Long maxSno = cwFrontMoneyRepository.findMaxSnoByCntrctNo(cntrctNo);
        return (maxSno == null ? 1 : maxSno + 1);

    }

    /**
     * 누계금액 가져오기
     *
     * @param cntrctNo
     * @param payType
     * @return
     */
    private Long getAcmtlPpaymnyAmt(String cntrctNo, String payType) {
        Long amt = cwFrontMoneyRepository.getSumPpaymnyAmt(cntrctNo, payType);
        return amt == null ? 0 : amt;
    }

    /**
     * 선급금 및 공제금 상세조회
     * 2025-05-27 수정(leejw): 파라미터 타입 변경, 추가
     *
     * @param cntrctNo
     * @param ppaymnySno
     * @return
     */
    public List<Map<String, ?>> getDeposit(String cntrctNo, Long ppaymnySno) {
        Map<String, Object> params = new HashMap<>();
        params.put("ppaytycode", CommonCodeConstants.PPAYTY_TYPE_GROUP_CODE);
        params.put("appcode", CommonCodeConstants.APPSTATUS_CODE_GROUP_CODE);
        params.put("cntrctNo", cntrctNo);
        params.put("ppaymnySno", ppaymnySno);

        DepositFormTypeSelectInput depositFormTypeSelectInput = new DepositFormTypeSelectInput();

        depositFormTypeSelectInput.setCntrctNo(cntrctNo);
        depositFormTypeSelectInput.setPpaymnySno(ppaymnySno);

        return mybatisSession.selectList(
                "kr.co.ideait.platform.gaiacairos.mybatis.mappers.projectcost.deposit.selectDepositDetail",
                params);
        /*
         * return cwFrontMoneyRepository.findByCntrctNoAndPayprceSno(cntrctNo,
         * payprceSno).orElse(null);
         */
    }

    /**
     * 선급금 및 공제금 조회
     *
     * @param cntrctNo
     * @param ppaymnySno
     * @return
     */
    public CwFrontMoney getDepositData(String cntrctNo, Long ppaymnySno) {
        return cwFrontMoneyRepository.findByCntrctNoAndPpaymnySno(cntrctNo, ppaymnySno).orElse(null);
    }

    /**
     * 선급금 및 공제금 리스트 조회
     *
     * @param cntrctNo
     * @param payprceSno
     * @return
     */
    public List<CwFrontMoney> getDepositDataList(String cntrctNo, Long payprceSno) {
        return cwFrontMoneyRepository.findByCntrctNoAndPayprceSnoAndDltYn(cntrctNo, payprceSno, "N");
    }

    /**
     * 선급금 및 공제금 삭제
     *
     * @param cwfrontmoney
     */
    public void delDeposit(CwFrontMoney cwfrontmoney) {
        cwFrontMoneyRepository.updateDelete(cwfrontmoney);
                
    }

    @Transactional
    public void delDepositByPayment(List<CwFrontMoney> cwfrontmoney) {
        cwfrontmoney.forEach(id -> {
            CwFrontMoney cwFrontMoney = cwFrontMoneyRepository
                    .findByCntrctNoAndPpaymnySno(id.getCntrctNo(), id.getPpaymnySno()).orElse(null);
            if (cwFrontMoney != null) {
                cwFrontMoneyRepository.updateDelAcmtlPpaymnyAmt(cwFrontMoney.getPpaymnyAmt(), cwFrontMoney.getPayType(),
                        id.getPpaymnySno());
                cwFrontMoneyRepository.updateDelete(cwFrontMoney);
            }
        });
    }



    /**
     * 선급금 및 공제금 상태 업데이트
     *
     * @param cwfrontmoney
     */
    public void updateDeposit(String apprvlStats, String usrId, LocalDateTime apprvlDt, String cntrctNo, Long ppaymnySno) {
        cwFrontMoneyRepository.updateByCntrctNoAndPpaymnySnoAnyType(apprvlStats, usrId, apprvlDt, cntrctNo, ppaymnySno);
    }
    
    // @Transactional
    // public void updateDepositList(List<CwFrontMoney> cwfrontmoney, String isApiYn, String pjtDiv) {
    //     cwfrontmoney.forEach(id -> {
    //         CwFrontMoney cwFrontMoney = cwFrontMoneyRepository.findByCntrctNoAndPpaymnySno(id.getCntrctNo(), id.getPpaymnySno()).orElse(null);
    //         if (cwFrontMoney != null) {
    //             if (id.getApprvlStats().equals("E")) {
    //                 // cwFrontMoneyRepository.updateByCntrctNoAndPpaymnySno(id.getApprvlStats(),
    //                 // UserAuth.get(true).getUsrId(), LocalDateTime.now(), id.getCntrctNo(),
    //                 // id.getPpaymnySno());

    //                 // 첫번째 결재자가 pgaia 사용자인지 체크
    //                 Map<String, Object> checkParams = new HashMap<>();
    //                 checkParams.put("pjtNo", UserAuth.get(true).getPjtNo());
    //                 checkParams.put("cntrctNo", cwFrontMoney.getCntrctNo());
    //                 checkParams.put("apType", "05");
    //                 boolean isPgaiaUser = mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.eapproval.approval.checkPgaiaFirstApprover", checkParams);
    //                 // pgaia사용자 + 카이로스 플랫폼 + pgaia 프로젝트면 API통신 진행
    //                 boolean toApi = isPgaiaUser && "cairos".equals(platform) && "Y".equals(isApiYn) && "P".equals(pjtDiv);

    //                 updateApprovalStatus(cwFrontMoney, UserAuth.get(true).getUsrId(), "E");
    //                 // 선급 연계 데이터 조회 (기성 & 기성연계데이터)
    //                 Map<String, Object> resourceMap = new HashMap<>();
    //                 if(toApi) {
    //                     resourceMap = selectDepositResource(cwFrontMoney.getCntrctNo(), cwFrontMoney.getPayprceSno());
    //                 }
    //                 approvalRequestService.insertDepositDoc(cwFrontMoney, toApi, resourceMap);
    //             }
    //             if (id.getApprvlStats().equals("A") || id.getApprvlStats().equals("R")) {
    //                 cwFrontMoneyRepository.updateByCntrctNoAndPpaymnySnoAnyType(id.getApprvlStats(),
    //                         UserAuth.get(true).getUsrId(), LocalDateTime.now(), id.getCntrctNo(), id.getPpaymnySno());
    //             }
    //         }
    //     });
    // }

    public Map<String, Object> selectDepositResource(String cntrctNo, Long payprceSno) {
        Map<String, Object> paymentMap = new HashMap<>();
        Map<String, Object> returnMap = new HashMap<>();
        CwPayMng cwPayMng = cwPayMngRepository.findByCntrctNoAndPayprceSno(cntrctNo, payprceSno).orElse(null);
        if(cwPayMng != null) {
            paymentMap = paymentService.selectPaymentResource(cwPayMng.getCntrctNo(), cwPayMng.getPayprceSno());
            returnMap.put("cwPayMng", cwPayMng);
        }

        List<CwPayCostCalculator> cwPayCostCalculator = (List<CwPayCostCalculator>) paymentMap.get("payCostCalculator");
        if(cwPayCostCalculator != null && !cwPayCostCalculator.isEmpty()) {
            returnMap.put("payCostCalculator", cwPayCostCalculator);
        }

        List<CwPayDetail> cwPayDetail = (List<CwPayDetail>) paymentMap.get("payDetail");
        if(cwPayDetail != null && !cwPayDetail.isEmpty()) {
            returnMap.put("payDetail", cwPayDetail);
        }

        List<CwPayActivity> cwPayActivity = (List<CwPayActivity>) paymentMap.get("activity");
        if(cwPayActivity != null && !cwPayActivity.isEmpty()) {
            returnMap.put("activity", cwPayActivity);
        }



        return returnMap;
    }

    /**
     * 선급금 승인요청
     *
     * @param depositInsert
     * @param diffAmt
     * @param oriType
     * @param oriAmt
     */
    @Transactional
    public void updateDeposit(CwFrontMoney depositInsert, Long diffAmt, String oriType, Long oriAmt) {
        if (oriType != null) {
            cwFrontMoneyRepository.updateDelAcmtlPpaymnyAmt(oriAmt, oriType, depositInsert.getPpaymnySno());
            Long amt = cwFrontMoneyRepository.getSumUpdatePpaymnyAmt(depositInsert.getCntrctNo(),
                    depositInsert.getPayType(), depositInsert.getPpaymnySno());
            depositInsert.setAcmtlPpaymnyAmt(amt + depositInsert.getPpaymnyAmt());
            cwFrontMoneyRepository.updateCalcAcmtlPpaymnyAmt(depositInsert.getPpaymnyAmt(), depositInsert.getPayType(),
                    depositInsert.getPpaymnySno());
        } else {
            if (diffAmt != null) {
                cwFrontMoneyRepository.updateCalcAcmtlPpaymnyAmt(diffAmt, depositInsert.getPayType(),
                        depositInsert.getPpaymnySno() - 1);
            }
        }
        cwFrontMoneyRepository.save(depositInsert);

    }

    /**
     * 선급금 승인상태 변경
     *
     * @param cwFrontMoney
     * @param usrId
     * @param apprvlStats
     */
    public void updateApprovalStatus(CwFrontMoney cwFrontMoney, String usrId, String apprvlStats) {
        cwFrontMoney.setApprvlStats(apprvlStats);
        if ("E".equals(apprvlStats)) {
            cwFrontMoney.setApprvlReqId(usrId);
            cwFrontMoney.setApprvlReqDt(LocalDateTime.now());
        } else {
            cwFrontMoney.setApprvlId(usrId);
            cwFrontMoney.setApprvlDt(LocalDateTime.now());
        }
        cwFrontMoneyRepository.save(cwFrontMoney);
    }

    /**
     * 선급금 가져오기
     *
     * @param apDocId
     * @param apUsrId
     * @param apDocStats
     */
    public void updateDepositByApDocId(String apDocId, String apUsrId, String apDocStats) {
        CwFrontMoney cwFrontMoney = cwFrontMoneyRepository.findByApDocId(apDocId).orElse(null);
        if (cwFrontMoney != null) {
            String apStats = "C".equals(apDocStats) ? "A" : "R";
            updateApprovalStatus(cwFrontMoney, apUsrId, apStats);
        }
    }

    /**
     * 선급금 데이터 조회
     * @param apDocId
     * @return
     */
    public Map<String, Object> selectDepositByApDocId(String apDocId) {
        Map<String, Object> returnMap = new HashMap<>();
        CwFrontMoney cwFrontMoney = cwFrontMoneyRepository.findByApDocId(apDocId).orElse(null);
        if(cwFrontMoney != null) {
            CwPayMng cwPayMng = cwPayMngRepository.findByCntrctNoAndPayprceSno(cwFrontMoney.getCntrctNo(), cwFrontMoney.getPpaymnySno()).orElse(null);
            if(cwPayMng != null) {
                returnMap.put("resources", cwPayMng);
            }
            returnMap.put("report", cwFrontMoney);
        }
        return returnMap;
    }

    /**
     * 결재요청 삭제 -> 선급금 컬럼 값 삭제 or 데이터 삭제
     *
     * @param apDocList
     * @param usrId
     * @param toApi
     */
    public void updateDepositApprovalReqCancel(List<ApDoc> apDocList, String usrId, boolean toApi) {
        apDocList.forEach(apDoc -> {
            CwFrontMoney cwFrontMoney = cwFrontMoneyRepository.findByApDocId(apDoc.getApDocId()).orElse(null);

            if (cwFrontMoney == null) return;

            if (toApi) {
                // api 통신 true -> 데이터 삭제
                cwFrontMoneyRepository.delete(cwFrontMoney);
            } else {
                // api 통신 false -> 컬럼 값 변경
                cwFrontMoneyRepository.updateApprovalStausCancel(null, null, null, null, cwFrontMoney.getCntrctNo(),
                        cwFrontMoney.getPpaymnySno(), usrId);
            }
        });
    }

    /**
     * 선급금 PGAIA에 추가
     *
     */
    @Transactional
    public void insertPgaiaDeposit(CwFrontMoney depositInsert, Map<String, Object> resources) {
        if(resources != null) {
            paymentService.insertPaymentResourceByDeposit(resources);
        }
        cwFrontMoneyRepository.save(depositInsert);
    }

    /**
     * 기성회차 가져오기
     */
    public List getPayprceTmnum(String col1, String col2, String tName, String[] param, String orderByCol, String orderByType){
        Map map = new HashMap();
        map.put("col1", col1);
        map.put("col2", col2);
        map.put("tName", tName);
        map.put("param1", param[0]);
        map.put("param2", Integer.parseInt(param[1]));
        map.put("orderByCol", orderByCol);

        return mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.projectcost.deposit.getPayprceTmnum", map);
    }

    /**
     * 
     */
    public void updateDelAcmtlPpaymnyAmt(Long ppaymnyAmt, String payType, Long ppaymnySno) {
        cwFrontMoneyRepository.updateDelAcmtlPpaymnyAmt(ppaymnyAmt, payType, ppaymnySno);
    }

}
