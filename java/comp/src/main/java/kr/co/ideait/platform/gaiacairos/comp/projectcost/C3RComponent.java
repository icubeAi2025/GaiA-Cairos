package kr.co.ideait.platform.gaiacairos.comp.projectcost;

import com.google.common.collect.Maps;
import kr.co.ideait.iframework.BizException;
import kr.co.ideait.platform.gaiacairos.core.exception.GaiaBizException;
import kr.co.ideait.platform.gaiacairos.core.util.EurecaUtil;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.eureca.CntrDtl;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.eureca.ContractSyncRequest;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.eureca.ReqreRsce;
import kr.co.ideait.platform.gaiacairos.comp.projectcost.service.C3RService;
import kr.co.ideait.platform.gaiacairos.core.base.AbstractComponent;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.projectcost.c3r.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.tika.utils.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class C3RComponent extends AbstractComponent {

    private final C3RService c3rService;

    /**
     * 내역서정보 등록
     * @param payload
     * @return
     */
    @Transactional
    public void processCntrDtlsFromEureca(ContractSyncRequest payload) {
        int insertCnt = 0;

        String cntrctChgId = payload.getCntrctChgId();
        String cntrctNo = payload.getCntrctNo();

        // 계약변경 ID 검증 - 계약변경 ID가 없다면 최초 변경ID로 조회 후 SET
        if (StringUtils.isBlank(cntrctChgId)) {
            cntrctChgId = c3rService.getLastCntrctChgId(Map.of("CNTRCT_NO", cntrctNo));
            payload.setCntrctChgId(cntrctChgId);
            if (cntrctChgId == null) throw new BizException("CAIROS에 등록된 계약정보가 없습니다.");
        }

        // [사전 데이터가공] 계약번호, 변경ID 할당
        payload.propagateCommonContractData();

        // 20250722 자원 여부 유무확인 판별변수 추가 true - 총차, false - 차수별
        boolean isRsceExists = true;    
        if (CollectionUtils.isEmpty(payload.getReqreRsceList())) {
            isRsceExists = false;
        }

        if (isRsceExists) {
            /* 2. INSERT 수행전 초기화 */
            log.info("2. EURECA TO CAIROS - INIT DATA START ");
            c3rService.clearCostDataBeforeSync(Map.of(
                    "CNTRCT_NO", cntrctNo,
                    "CNTRCT_CHG_ID", cntrctChgId,
                    "IS_RSCE_EXISTS", isRsceExists
            ));

            if (payload.getCntrDtlsList() != null) {
                // 2-1. 최초 계약내역서(bid)
                List<CnContractBid> bidList = EurecaUtil.convertList(payload.getCntrDtlsList(), EurecaUtil::toContractBidDto);
                // 2-2. 공종
                List<CntrDtl> cbsJsonList = EurecaUtil.filterByType(payload.getCntrDtlsList(), "G");
                List<CtCbs> cbsList = new ArrayList<>();

                // 2025-09-04 작업일보 수동 자원관리를 위해 추가
                CtCbs initCbs = new CtCbs();
                initCbs.setCntrctChgId(cntrctChgId);
                initCbs.setCnsttySn(0L);
                initCbs.setCnsttyNm("작업일보");
                initCbs.setCnsttyCd("0");
                initCbs.setUpCnsttyCd("");
                initCbs.setCalcExcpYn("N");
                initCbs.setMtrlAm(0L);
                initCbs.setLbrAm(0L);
                initCbs.setGnrlexpnsAm(0L);
                initCbs.setUnitCnstType("O");   // 기타 - 251013 추가. CBS Tree 구성시 필요.
                initCbs.setCnsttyLvlNum(0L);
                initCbs.setRgstrId("CAIROS");

                cbsList.add(initCbs);
                cbsList.addAll(EurecaUtil.convertList(cbsJsonList, EurecaUtil::toCtCbsDto));
                // 2-3. 세부공종
                List<CntrDtl> cbsDetailJsonList = EurecaUtil.filterByType(payload.getCntrDtlsList(), "S");
                List<CtCbsDetail> cbsDetailList = new ArrayList<>();

                // 2025-09-04 작업일보 수동 자원관리를 위해 추가
                CtCbsDetail initCbsDetail = new CtCbsDetail();
                initCbsDetail.setCntrctChgId(cntrctChgId);
                initCbsDetail.setCnsttySn(0L);
                initCbsDetail.setDtlCnsttySn(0L);
                initCbsDetail.setDtlCnsttyNm("작업일보");
                initCbsDetail.setMtrlUprc(0.0);
                initCbsDetail.setLbrUprc(0.0);
                initCbsDetail.setGnrlexpnsUprc(0.0);
                initCbsDetail.setRsceQty(BigDecimal.ZERO);
                initCbsDetail.setGovsplyMtrlYn("N");
                initCbsDetail.setRgstrId("CAIROS");

                cbsDetailList.add(initCbsDetail);
                cbsDetailList.addAll(EurecaUtil.convertList(cbsDetailJsonList, EurecaUtil::toCtCbsDetailDto));

                // 2-4. 계약내역서(BID) 입력
                List<List<CnContractBid>> partedList1 = ListUtils.partition(bidList, 100);
                for (List<CnContractBid> list : partedList1) {
                    insertCnt += c3rService.insertCnContractBidFromEureca(Map.of("list", list));
                }
                log.info("3-1. EURECA TO CAIROS - INSERT CN_CONTRACT_BID FINISHED :: {}", insertCnt);

                // 2-5. 공종 입력
                insertCnt = 0;
                List<List<CtCbs>> partedList2 = ListUtils.partition(cbsList, 100);
                for (List<CtCbs> list : partedList2) {
                    insertCnt += c3rService.insertCtCbsFromEureca(Map.of("list", list));
                }
                log.info("3-3. EURECA TO CAIROS - INSERT CT_CBS FINISHED :: {}", insertCnt);

                // 2-6. 세부공종 입력
                insertCnt = 0;
                List<List<CtCbsDetail>> partedList3 = ListUtils.partition(cbsDetailList, 100);
                for (List<CtCbsDetail> list : partedList3) {
                    insertCnt += c3rService.insertCtCbsDetailFromEureca(Map.of("list", list));
                }
                log.info("3-4. EURECA TO CAIROS - INSERT CT_CBS_DETAIL FINISHED :: {}", insertCnt);
            }

            if (payload.getCstList() != null) {
                // 2-7. 원가계산서
                List<CnContractCalculator> calcList = EurecaUtil.convertList(payload.getCstList(), EurecaUtil::toContractCalculatorDto);

                // 2-8. 원가계산서 입력
                insertCnt = c3rService.insertCnContractCalculatorFromEureca(Map.of("list", calcList));
                log.info("3-2. EURECA TO CAIROS - INSERT CN_CONTRACT_CALCULATOR FINISHED :: {}", insertCnt);
            }

            // 1. 계약구분이 장기계속계약_차수이고 2. 유레카 - 총차가아닌 경우, 자원내역 연동 X
            if (payload.getReqreRsceList() != null) {
                // 2-9. 공종자원
                List<ReqreRsce> rsceList = EurecaUtil.convertCbsResourceList(payload.getCntrDtlsList(), payload.getReqreRsceList());
                List<CtCbsResource> cbsResourceList = EurecaUtil.convertList(rsceList, EurecaUtil::toCtCbsResource);

                // 2-10. 공종자원 입력
                insertCnt = 0;
                List<List<CtCbsResource>> partedList4 = ListUtils.partition(cbsResourceList, 100);
                for (List<CtCbsResource> list : partedList4) {
                    if (!list.isEmpty()) {
                        insertCnt += c3rService.insertCtCbsResourceFromEureca(Map.of("list", list));
                    }
                }
                log.info("3-5. EURECA TO CAIROS - INSERT CT_CBS_RESOURCE FINISHED :: {}", insertCnt);

                // 20250828 - 단위수량 업데이트 처리 변경
                c3rService.updateCtCbsResourceQtyFromEureca(Map.of("cntrctChgId", cntrctChgId));
                log.info("3-6. EURECA TO CAIROS - UPDATE CT_CBS_RESOURCE (TOTAL_QTY -> UNIT_QTY) FINISHED :: {}", insertCnt);
            }
        } else {    // 차수별로 들어올 때
            if (payload.getCntrDtlsList() != null) {

                // 3-2. 세부공종
                List<CntrDtl> cbsDetailJsonList = EurecaUtil.filterByType(payload.getCntrDtlsList(), "S");
                List<CtCbsDetail> cbsDetailList = EurecaUtil.convertList(cbsDetailJsonList, EurecaUtil::toCtCbsDetailDto);


                // 3-2. 세부공종 수정
                insertCnt = 0;
                List<List<CtCbsDetail>> partedList3 = ListUtils.partition(cbsDetailList, 100);
                for (List<CtCbsDetail> list : partedList3) {
                    insertCnt += c3rService.updateCtCbsDetailFromEureca(Map.of("list", list));
                }
                log.info("3-2. EURECA TO CAIROS - UPDATE CT_CBS_DETAIL FINISHED :: {}", insertCnt);
            }
        }
    }


    // ----------------------------------------API통신--------------------------------------------

    /**
     * API 수신 처리 메서드
     *
     * CAGA0002 유레카 > 카이로스 > PGAIA 내역서정보 등록
     *
     * @param transactionId
     * @param params
     * @return
     */
    @Transactional
    public Map receiveInterfaceService(String transactionId, Map params) {
        log.info("receiveInterfaceService - {}", transactionId);
        Map<String, Object> result = Maps.newHashMap();
        result.put("resultCode", "00");
        result.put("resultMsg", "정상 처리되었습니다/");

        try {
            if ("CAGA0002".equals(transactionId)) {             // 내역서정보 등록
                ContractSyncRequest payload = objectMapper.convertValue(params.get("payload"), ContractSyncRequest.class);

                log.info("EURECA -> CAIROS -> PGAIA updateCntrDtls START");
                if (payload.getCntrDtlsList() != null) { log.info("계약내역 건수 :{} / {}", payload.getTotalCnt1(), payload.getCntrDtlsList().size()); }
                if (payload.getCstList() != null)  { log.info("원가계산 건수 :{} / {}", payload.getTotalCnt2(), payload.getCstList().size()); }
                if (payload.getReqreRsceList() != null) { log.info("자원내역 건수 :{} / {}", payload.getTotalCnt3(), payload.getReqreRsceList().size()); }

                // 내역서정보 등록
                this.processCntrDtlsFromEureca(payload);
            }
        } catch (GaiaBizException ex) {
            result.put("resultCode", "01");
            result.put("resultMsg", ex.getMessage());
        }
        return result;
    }

}
