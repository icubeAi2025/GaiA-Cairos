package kr.co.ideait.platform.gaiacairos.core.util;

import kr.co.ideait.platform.gaiacairos.core.persistence.vo.eureca.Calculator;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.eureca.CntrDtl;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.eureca.ReqreRsce;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.projectcost.c3r.*;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
public class EurecaUtil {

    static final String EURECA = "EURECA";

    public static CnContractBid toContractBidDto (CntrDtl vo) {
        CnContractBid dto = new CnContractBid();
        dto.setCntrctNo(vo.getCntrctNo());                         // 계약번호
        dto.setCbsSno(vo.getDtlsSn());                             // CBS 순번
        dto.setCntrctCnstType(vo.getUnitCnstwkDivCd());            // 계약세부공종코드(공사구분)
        dto.setCntrctUnitCnstwkSno(vo.getBidUnitCnstwkSn());       // 계약단위공사순번
        dto.setCntrctDcnsttySno(vo.getBidDtlsSn());                // 계약세부공종순번
        dto.setCnsttyDtlsDivCd(vo.getDtlsCnsttyDivCd());           // 공종 내역구분코드
        dto.setPrdnm(vo.getDtlsNm());                              // 품명
        dto.setSpec(vo.getSpec());                                 // 규격
        dto.setUnit(vo.getUnit());                                 // 단위
        dto.setQty(vo.getDtlsQty());                               // 수량
        dto.setMtrlcstUprc(vo.getMtrlUprc());                      // 재료비단가
        dto.setLbrcstUprc(vo.getLbrUprc());                        // 노무비단가
        dto.setGnrlexpnsUprc(vo.getGnrlexpnsUprc());               // 경비단가
        dto.setMtrlcstAmt(vo.getMtrlAmt());                        // 재료비금액
        dto.setLbrcstAmt(vo.getLbrAmt());                          // 노무비금액
        dto.setGnrlexpnsAmt(vo.getGnrlexpnsAmt());                 // 경비금액
        dto.setRgstrId(EURECA);

        return dto;
    }

    public static CnContractCalculator toContractCalculatorDto (Calculator vo) {
        CnContractCalculator dto = new CnContractCalculator();
        dto.setCntrctNo(vo.getCntrctNo());                          // 계약번호
        dto.setCstCalcItCd(vo.getCstCalcItCd());                    // 원가산출항목명
        dto.setUpCstCalcItCd(vo.getUpCstCalcItCd());                // 상위원가산출항목코드
        dto.setDsplyOrdr(vo.getDsplySn());                          // 표시순서
        dto.setCstCalcMthdNm(vo.getCalcMthdNm());                   // 원가산출방법수식
        dto.setCstCalcMthdNomfrmCntnts(vo.getCalcMthdCntnts());     // 원가산출방법수식
        dto.setCstCalcbllDsplyVal("");                              // 원가계산서표시값
        dto.setCostAm(vo.getCostAmt());                             // 비용금액
        dto.setRgstrId(EURECA);

        return dto;
    }

    public static CtCbs toCtCbsDto (CntrDtl vo) {
        CtCbs dto = new CtCbs();
        dto.setCntrctChgId(vo.getCntrctChgId());                    // 계약변경ID
        dto.setCnsttySn(vo.getDtlsSn());                            // 공종순서
        dto.setUnitCnstType(vo.getUnitCnstwkDivCd());               // 공사구분코드
        dto.setCalcExcpYn(vo.getCalcExcpYn());                      // 계산제외여부
        dto.setMtrlAm(vo.getMtrlAmt());                             // 재료
        dto.setLbrAm(vo.getLbrAmt());
        dto.setGnrlexpnsAm(vo.getGnrlexpnsAmt());
        dto.setCnsttyCd(vo.getCnsttyCd());
        dto.setUpCnsttyCd(vo.getUpCnsttyCd());
        dto.setCnsttyNm(vo.getDtlsNm());
        dto.setCnsttyLvlNum(vo.getLvlNum());                        // 20251021 - 공종레벨 추가
        dto.setRgstrId(EURECA);

        return dto;
    }

    public static CtCbsDetail toCtCbsDetailDto (CntrDtl vo) {
        CtCbsDetail dto = new CtCbsDetail();
        dto.setCntrctChgId(vo.getCntrctChgId());
        dto.setCnsttySn(vo.getUpDtlsSn());              // FIXME
        dto.setDtlCnsttySn(vo.getDtlsSn());
        dto.setMtrlUprc(vo.getMtrlUprc());
        dto.setLbrUprc(vo.getLbrUprc());
        dto.setGnrlexpnsUprc(vo.getGnrlexpnsUprc());
        dto.setRsceTpCd(vo.getRsceTpCd());
        dto.setRsceCd(vo.getRsceCd());
        dto.setDtlCnsttyNm(vo.getDtlsNm());
        dto.setSpecNm(vo.getSpec());
        dto.setUnit(vo.getUnit());
        dto.setRsceQty(vo.getDtlsQty());        // 자원 수량 (총차)
        dto.setThisRsceQty(vo.getDtlsQty());    // 금차 수량 (금차)
        dto.setUnitCnstType(vo.getUnitCnstwkDivCd());

        dto.setCalcExcpYn(vo.getCalcExcpYn());
        dto.setGovsplyMtrlYn(vo.getGovsplyMtrlYn());
        dto.setEcoFriendlyYn("N");
        dto.setFtaxDtlCnsttyYn("N");
        dto.setStdDtlCnsttyYn("N");
        dto.setRgstrId(EURECA);

        return dto;
    }

    public static CtCbsResource toCtCbsResource (ReqreRsce vo) {
        CtCbsResource dto = new CtCbsResource();
        dto.setCntrctChgId(vo.getCntrctChgId());
        dto.setDtlCnsttySn(vo.getDtlsSn());
        dto.setRsceTpCd(vo.getRsceTpCd());
        dto.setGnrlexpnsCd(vo.getRsceCd());
        dto.setRsceNm(vo.getRsceNm());
        dto.setSpecNm(vo.getSpecNm());
        dto.setUnit(vo.getUnit());
        dto.setTotalQty(vo.getRsceQty());   // 20250828 unitQty -> totalQty 변경처리
        dto.setCnsttySn(vo.getUpDtlSn());
        dto.setGovsplyMtrlYn(vo.getGovsplyMtrlYn());

        dto.setRgstrId(EURECA);

        return dto;
    }


//    public static List<ContractBid> toContractBidDtoList(String cntrctNo, List<EurecaDTO.CntrDtl> voList) {
//        return voList.stream()
//                .map(EurecaUtil::toContractBidDto)
//                .collect(Collectors.toList());
//    }


    /**
     * 유레카 계약내역서 (공종, 세부공종) 필터 변환
     * @param list
     * @param type
     * @return
     */
    public static List<CntrDtl> filterByType(List<CntrDtl> list, String type) {
        // 세부공종 인 경우 상위 순번에 대한 처리
//        if ("S".equals(type)) {
//            Long currentGSn = null;
//            Long currentLvlNum = null;
//
//            for (CntrDtl item : list) {
//                // 공종인 경우 - 세부공종에서 활용할 변수 SET
//                if ("G".equals(item.getDtlsCnsttyDivCd())) {
//                    currentGSn = item.getDtlsSn();
//                    currentLvlNum = item.getLvlNum();
//                } else {
//                    // 세부공종인 경우
//                    if (currentGSn != null) {
//                        item.setUpDtlsSn(currentGSn);
//                        item.setLvlNum(currentLvlNum);
//                    }
//                }
//            }
//        }

        return list.stream()
                .filter(d -> type.equalsIgnoreCase(d.getDtlsCnsttyDivCd()))
                .toList();
    }

    /**
     * 공종자원 입력시 활용
     * 세부순번(key) - 상위순번(value)
     * @param originList
     * @return
     */
    public static List<ReqreRsce> convertCbsResourceList(List<CntrDtl> originList, List<ReqreRsce> targetList) {
        Map<Long, Long> sToGMap = new HashMap<>();

        for (CntrDtl dtl : originList) {
            if ("S".equals(dtl.getDtlsCnsttyDivCd())) {
                sToGMap.put(dtl.getDtlsSn(), dtl.getUpDtlsSn());
            }
        }

        for (ReqreRsce target : targetList) {
            Long upper = sToGMap.get(target.getDtlsSn());
            if (upper != null) {
                target.setUpDtlSn(upper);
            } else {
                target.setUpDtlSn(0L);
            }
        }

        return targetList;
    }

    /**
     * BaseConverter
     * Json - Eureca List 를 CAIROS 리스트로 변환할때 활용
     * @param list
     * @param mapper
     * @return
     * @param <T>
     * @param <R>
     */
    public static <T, R> List<R> convertList(List<T> list, Function<T, R> mapper) {
        if (list == null || list.isEmpty()) {
            log.info("리스트가 null 또는 비어있습니다.");
            return Collections.emptyList();
        }

        return list.stream()
                .map(mapper)                        // 각 요소에 mapper 적용 (T → R)
                .collect(Collectors.toList());      // 다시 List<R>로 수집
    }
}
