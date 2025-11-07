package kr.co.ideait.platform.gaiacairos.comp.projectcost.service;

import kr.co.ideait.platform.gaiacairos.core.base.AbstractGaiaCairosService;
import kr.co.ideait.platform.gaiacairos.core.exception.ErrorType;
import kr.co.ideait.platform.gaiacairos.core.exception.GaiaBizException;
import kr.co.ideait.iframework.EtcUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class C3RCalService extends AbstractGaiaCairosService {

    private static final String DEFAULT_MAPPER_PATH = "kr.co.ideait.platform.gaiacairos.mybatis.mappers.projectcost.c3rCalculate";

    private static BigDecimal DEVIDE_100 = new BigDecimal(100);

    private static final int CD_RSCE_LEN = 16;
    private static final int CD_UNIN_LEN = 12;
    private static final int CD_MCHNE_LEN = 17;

    /**
     * 소요자원 계산
     */
    @SuppressWarnings("unchecked")
    public void getCstReqreRsceRecalc(Map<String, Object> param) throws RuntimeException {
        // FIXME 1120 -
        //String errMsg = getChkCycleUninRsce(param);	// 순환참조 확인
        String errMsg = "";
        int maxCalcLvlNum = 0;
        Map<String, Object> dtaCalcUntMap = new HashMap<String, Object>();
        List<Map<String, Object>> rsceQtyRsltList = new ArrayList<Map<String, Object>>();

        if(!"".equals(errMsg)) {
            // 20250227 - 정적검사 수정 [Correctness] RANGE_STRING_INDEX
            if (errMsg.length() > 1000) {
                errMsg = errMsg.substring(0, 1000);
            }
            throw new GaiaBizException(ErrorType.INTERNAL_SERVER_ERROR, "내역서 오류\n" + errMsg);
        }

        // 임시조합자원테이블 생성(세션 종료 후 삭제됨)
        mybatisSession.insert(DEFAULT_MAPPER_PATH + ".createTmpUninRsceRlt", param);

        // 1. 일위대가, 중기단가산출 관련 원가정보를 임시조합자원결과 테이블에 등록
        param.put("DTA_TP_CD", "R");
        mybatisSession.insert(DEFAULT_MAPPER_PATH + ".insertCstUninRsceTmpRltUcost", param);

        // 2. 다음 처리 진행을 3번 반복
        for(int idx=1; idx <= 4; idx++) {
            int loopCnt = 0;
            List<Map<String, Object>> dtlList;

            if(idx == 1) {
                // 2-1. 반복 첫번째인 경우, 임시조합자원결과 테이블에서 최대계산레벨수 조회하여, 최대계산레벨수 변수에 저장
                maxCalcLvlNum = EtcUtil.zeroConvertInt(mybatisSession.selectOne(DEFAULT_MAPPER_PATH + ".getCstTmpRltCalcLvlNum", param));
            } else {
                // 2-2. 그 외 경우, 최대계산레벨수 변수에 1 저장
                maxCalcLvlNum = 1;
            }

            // 2-3. 최대계산레벨수 변수가 0보다 큰 동안 처리 진행 반복
            while(maxCalcLvlNum > 0) {
                // 2-3-1. 3번 반복하는 중 상세 자원정보 조회
                switch(idx) {
                    case 1:		// 조합자원(일위대가, 중기단가산출) 상세 정보 조회
                        param.put("calcLvlNum", maxCalcLvlNum);
                        param.put("itemNum", loopCnt);
                        dtlList = mybatisSession.selectList(DEFAULT_MAPPER_PATH + ".getCstTmpRltUnitDtlList", param);
                        break;
                    case 2:		// 원가 관련 세부공종 및 손료 정보 조회
                        dtlList = mybatisSession.selectList(DEFAULT_MAPPER_PATH + ".getCstCnsttyDtlInfoList", param);
                        break;
                    case 3:		// 원가 관련 공종 정보 조회
                        dtlList = mybatisSession.selectList(DEFAULT_MAPPER_PATH + ".getCstCnsttyInfoList", param);
                        break;
                    default:	// 원가 세부공종 정보 조회
                        dtlList = mybatisSession.selectList(DEFAULT_MAPPER_PATH + ".getCstDtlCnsttyInfoList", param);
                        break;
                }

                // 2-3-1. 조회된 결과가 없는 경우, 최대계산레벨수 변수가 1 초과 시, 최대계산레벨수 변수 - 1 후 조회부터 다시 진행, 최대계산레벨수 변수 1 이하이면 반복 종료
                if(ObjectUtils.isEmpty(dtlList)) {
                    if(maxCalcLvlNum > 1) {
                        maxCalcLvlNum--;
                        loopCnt = 0;
                        continue;
                    } else {
                        break;
                    }
                }

                // 2-3-2. 조회결과에 대한 처리 진행
                int untStIdx = -1, untEdIdx = -1, maxLosscstCalcLvl = 0;
                String oldUpRsceCd = "";
                for(int i=0; i < dtlList.size(); i++) {
                    Map<String, Object> dtlMap = EtcUtil.changeToUpperMapKey(dtlList.get(i));
                    String up_rsce_cd = EtcUtil.nullConvert(dtlMap.get("UP_RSCE_CD"));
                    String rsce_cd = EtcUtil.nullConvert(dtlMap.get("RSCE_CD"));
                    String mlg_cd = EtcUtil.nullConvert(dtlMap.get("MLG_CD"));
                    String ucost_mlg_cd = EtcUtil.nullConvert(dtlMap.get("UCOST_MLG_CD"));
                    BigDecimal mtrl_qty = EtcUtil.expToBig(dtlMap.get("MTRL_QTY"));
                    BigDecimal lbr_qty = EtcUtil.expToBig(dtlMap.get("LBR_QTY"));
                    BigDecimal gnrlexpns_qty = EtcUtil.expToBig(dtlMap.get("GNRLEXPNS_QTY"));
                    int losscst_calc_lvl_num = EtcUtil.zeroConvertInt(dtlMap.get("LOSSCST_CALC_LVL_NUM"));

                    if("3011150510068426".equals(rsce_cd)) {
                        // System.out.println("dddd");
                    }

                    // 2-3-5-1. 이전상위자원코드 변수와 해당 상위자원코드가 다른 경우
                    if(!oldUpRsceCd.equals(up_rsce_cd)) {
                        untEdIdx = i - 1;

                        Map<String, Object> tmpMap = new HashMap<String, Object>();
                        tmpMap.put("untStIdx", untStIdx);
                        tmpMap.put("untEdIdx", untEdIdx);
                        tmpMap.put("maxLosscstCalcLvl", maxLosscstCalcLvl);
                        dtaCalcUntMap = setCalcLosscstCnt(tmpMap, dtlList, dtaCalcUntMap);

                        oldUpRsceCd = up_rsce_cd;
                        maxLosscstCalcLvl = 0;
                        untStIdx = i;
                    }

                    // 2-3-5-2. 자원유형코드가 손료(R)인 경우, 최대손료계산레벨수와 해당 손료계산레벨수 중 큰 값을 변수에 저장
                    if("R".equals(dtlMap.get("RSCE_TP_CD"))) {
                        if(losscst_calc_lvl_num > maxLosscstCalcLvl) {
                            maxLosscstCalcLvl = losscst_calc_lvl_num;
                        }
                    } else {
                        if("N".equals(dtlMap.get("CALC_EXCP_YN")) && "N".equals(dtlMap.get("GOVSPLY_MTRL_YN"))) {
                            if("T".equals(ucost_mlg_cd)) {
                                dtaCalcUntMap = setRsceQtyList(up_rsce_cd + "=" + (!"".equals(mlg_cd) ? mlg_cd : "M"), rsce_cd + "=M", mtrl_qty, dtaCalcUntMap);
                                dtaCalcUntMap = setRsceQtyList(up_rsce_cd + "=" + (!"".equals(mlg_cd) ? mlg_cd : "L"), rsce_cd + "=L", lbr_qty, dtaCalcUntMap);
                                dtaCalcUntMap = setRsceQtyList(up_rsce_cd + "=" + (!"".equals(mlg_cd) ? mlg_cd : "E"), rsce_cd + "=E", gnrlexpns_qty, dtaCalcUntMap);
                            } else {
                                dtaCalcUntMap = setRsceQtyList(up_rsce_cd + "=" + ucost_mlg_cd, rsce_cd + "=M", mtrl_qty, dtaCalcUntMap);
                                dtaCalcUntMap = setRsceQtyList(up_rsce_cd + "=" + ucost_mlg_cd, rsce_cd + "=L", lbr_qty, dtaCalcUntMap);
                                dtaCalcUntMap = setRsceQtyList(up_rsce_cd + "=" + ucost_mlg_cd, rsce_cd + "=E", gnrlexpns_qty, dtaCalcUntMap);
                            }
                        }
                    }
                }

                // 2-3-6. 최대손료계산레셀수 변수가 0보다 큰 경우
                if(maxLosscstCalcLvl > 0) {
                    Map<String, Object> tmpMap = new HashMap<String, Object>();
                    tmpMap.put("untStIdx", untStIdx);
                    tmpMap.put("untEdIdx", dtlList.size() - 1);
                    tmpMap.put("maxLosscstCalcLvl", maxLosscstCalcLvl);

                    dtaCalcUntMap = setCalcLosscstCnt(tmpMap, dtlList, dtaCalcUntMap);
                }

                loopCnt++;

                // 조합자원(일위대가, 중기단가산출) 외에는 반복하지 않음
                if(idx > 1) {
                    break;
                }
            }
        }


        // 3. 일위대가 소요자원 등록
        // 3-1. 원가 관련 일위대가코드 조회
        List<Map<String, Object>> rsceList = mybatisSession.selectList(DEFAULT_MAPPER_PATH + ".getCstUcostList", param);
        for(Map<String, Object> rMap : rsceList) {
            rsceQtyRsltList = setRsltList(rMap, "M", dtaCalcUntMap, rsceQtyRsltList);
            rsceQtyRsltList = setRsltList(rMap, "L", dtaCalcUntMap, rsceQtyRsltList);
            rsceQtyRsltList = setRsltList(rMap, "E", dtaCalcUntMap, rsceQtyRsltList);
        }

//        // 3-2. 일위대가 소요자원 정보 삭제
//        mybatisSession.delete(DEFAULT_MAPPER_PATH + ".deleteCstUcostReqreRsce", param);
//
//        // 3-3. 생성한 수량 목록을 일위대가 소요자원으로 등록
//        List<List<Map<String, Object>>> partedList = ListUtils.partition(rsceQtyRsltList, 100);// 한꺼번에 하면 pgsql 에러
//        for(List<Map<String, Object>> list: partedList) {
//            mybatisSession.insert(DEFAULT_MAPPER_PATH + ".insertCstUcostReqreRsce", EtcUtil.map("USER_ID", param.get("USER_ID"),"MAJOR_CNSTTY_CD", param.get("MAJOR_CNSTTY_CD"), "regList", list));
//        }
//
//        // 4. 중기단가산출 소요자원 등록
//        // 4-1. 원가 관련 중기단가산출코드 조회
//        rsceQtyRsltList.clear();
//        rsceList = mybatisSession.selectList(DEFAULT_MAPPER_PATH + ".getCstHmupcList", param);
//        for(Map<String, Object> rMap : rsceList) {
//            rsceQtyRsltList = setRsltList(rMap, "M", dtaCalcUntMap, rsceQtyRsltList);
//            rsceQtyRsltList = setRsltList(rMap, "L", dtaCalcUntMap, rsceQtyRsltList);
//            rsceQtyRsltList = setRsltList(rMap, "E", dtaCalcUntMap, rsceQtyRsltList);
//        }
//
//        // 4-2. 중기단가산출 소요자원 정보 삭제
//        mybatisSession.delete(DEFAULT_MAPPER_PATH + ".deleteCstHmupcReqreRsce", param);
//
//        // 4-3. 생성한 수량 목록을 중기단가산출 소요자원으로 등록
//        partedList = ListUtils.partition(rsceQtyRsltList, 100);// 한꺼번에 하면 pgsql 에러
//        for(List<Map<String, Object>> list: partedList) {
//            mybatisSession.insert(DEFAULT_MAPPER_PATH + ".insertCstHmupcReqreRsce", EtcUtil.map("USER_ID", param.get("USER_ID"), "MAJOR_CNSTTY_CD", param.get("MAJOR_CNSTTY_CD"), "regList", list));
//        }

        // 5. 세부공종 소요자원 등록
        // 5-1. 원가 관련 세부공종 조회
        rsceQtyRsltList.clear();
        rsceList = mybatisSession.selectList(DEFAULT_MAPPER_PATH + ".getCstDtlCnsttyList", param);
        for(Map<String, Object> rMap : rsceList) {
            rsceQtyRsltList = setRsltList(rMap, "M", dtaCalcUntMap, rsceQtyRsltList);
            rsceQtyRsltList = setRsltList(rMap, "L", dtaCalcUntMap, rsceQtyRsltList);
            rsceQtyRsltList = setRsltList(rMap, "E", dtaCalcUntMap, rsceQtyRsltList);
        }

        // 5-2. 세부공종 소요자원 정보 삭제
        mybatisSession.delete(DEFAULT_MAPPER_PATH + ".deleteCstDtlCnsttyReqreRsce", param);

        /**
         * 5-3. 생성한 수량 목록을 세부공종 소요자원으로 등록
         * 변경사항:
         *      2024-12-10
         *      기존 - CBS_DETAIL 목록 조회 후 INSERT
         *      변경 - CBS_DETAIL ~ UNIT_COST_DETAIL 조인 목록 조회 INSERT 변경
         */
        /* AS-IS */
//        // 한꺼번에 하면 pgsql 에러
//        partedList = ListUtils.partition(rsceQtyRsltList, 100);
//        for(List<Map<String, Object>> list: partedList) {
//            mybatisSession.insert(DEFAULT_MAPPER_PATH + ".insertCstDtlCnsttyReqreRsce", EtcUtil.map("USER_ID", param.get("USER_ID"), "regList", list));
//        }

        /* TO-BE */
//        mybatisSession.selectList(DEFAULT_MAPPER_PATH + ".debugCstDtlCnsttyReqreRsce", param);
        mybatisSession.insert(DEFAULT_MAPPER_PATH + ".insertCstDtlCnsttyReqreRsce", param);

        // 6. 공종 소요자원 계산
        // 6-1. 원가 관련 공종순번, 공종코드 조회
        /* TODO [확인필요]
        rsceQtyRsltList.clear();
        rsceList = mybatisSession.selectList(DEFAULT_MAPPER_PATH + ".getCstCnsttyList", param);
        for(Map<String, Object> rMap : rsceList) {
            rsceQtyRsltList = setRsltList(rMap, "M", dtaCalcUntMap, rsceQtyRsltList);
            rsceQtyRsltList = setRsltList(rMap, "L", dtaCalcUntMap, rsceQtyRsltList);
            rsceQtyRsltList = setRsltList(rMap, "E", dtaCalcUntMap, rsceQtyRsltList);
        }

        // 6-2. 공종 소요자원 정보 삭제
        mybatisSession.delete(DEFAULT_MAPPER_PATH + ".deleteCstCnsttyReqreRsce", param);

        // 6-3. 생성한 수량 목록을 공종 소요자원으로 등록
        partedList = ListUtils.partition(rsceQtyRsltList, 100);// 한꺼번에 하면 pgsql 에러
        for(List<Map<String, Object>> list: partedList) {
            mybatisSession.insert(DEFAULT_MAPPER_PATH + ".insertCstCnsttyReqreRsce", EtcUtil.map("USER_ID", param.get("USER_ID"), "regList", list));
        }

        */
    }

    /**
     * 조합자원 순환참조 확인
     */
    private String getChkCycleUninRsce(Map<String, Object> param) throws RuntimeException {
        String errMsg = "";

        // 1. 기계경비상세 원가정보 중 자원유형코드가 일위대가, 중기단가산출, 표준시장단가, 시장시공가격, 견적항목인 정보 10건 조회
        List<Map<String, Object>> errList = mybatisSession.selectList(DEFAULT_MAPPER_PATH + ".getCstMchneDtlErrList", param);
        for(Map<String, Object> errMap : errList) {
            // 1-1. 오류메시지 추가
            errMsg = errMsg + EtcUtil.nullConvert(errMap.get("RSCE_CD")) + " 부적절한 세부항목 사용 " + EtcUtil.nullConvert(errMap.get("ERR_MSG")) + "\n";
        }

        // 2. 오류 메시지가 있는 경우, 메시지 반환
        if(errMsg.length() > 10) {
            return errMsg;
        }

        // 3. 기계경비상세 순환참조 목록 10건 조회
        List<Map<String, Object>> cycleList = mybatisSession.selectList(DEFAULT_MAPPER_PATH + ".getCstMchneDtlCycleList", param);
        for(Map<String, Object> cycleMap : cycleList) {
            // 3-1. 오류메시지 추가
            errMsg = errMsg + "항목 = " + EtcUtil.nullConvert(cycleMap.get("UP_RSCE_CD")) + ", 하위 산출항목 " + EtcUtil.nullConvert(cycleMap.get("RSCE_CD")) + "\n";
        }

        // 4. 오류 메시지가 있는 경우, 메시지 반환
        if(errMsg.length() > 10) {
            return "조합자원(기계경비) 순환참조 오류 발생\n" + errMsg;
        }

        // 5. 일위대가, 중기단가산출 원가정보 중 순환 참조된 정보 10건 조회
        cycleList = mybatisSession.selectList(DEFAULT_MAPPER_PATH + ".getCstUninRsceDtlCycleList", param);
        for(Map<String, Object> cycleMap : cycleList) {
            // 3-1. 오류메시지 추가
            errMsg = errMsg + "항목 = " + EtcUtil.nullConvert(cycleMap.get("UP_RSCE_CD")) + ", 하위 산출항목 " + EtcUtil.nullConvert(cycleMap.get("RSCE_CD")) + "\n";
        }

        // 6. 오류 메시지가 있는 경우, 메시지 반환
        if(errMsg.length() > 10) {
            return "조합자원(일위대가, 중기단가산출) 순환참조 오류 발생\n" + errMsg;
        }

        return errMsg;
    }

    /**
     * 손료 수량 계산
     */
    private Map<String, Object> setCalcLosscstCnt(Map<String, Object> param, List<Map<String, Object>> dtlList, Map<String, Object> dtaCalcUntMap) throws RuntimeException {
        int losscstCalcLvl = 1;
        int untStIdx = (int) EtcUtil.ifnull(param.get("untStIdx"), -1);
        int untEdIdx = (int) EtcUtil.ifnull(param.get("untEdIdx"), -1);
        int maxLosscstCalcLvl = EtcUtil.zeroConvertInt(param.get("maxLosscstCalcLvl"));
        Map<String, Object> losscstCalcUntMap = new HashMap<String, Object>();
        Map<String, Object> pMap = new HashMap<String, Object>();

        if(untEdIdx < 0 || untStIdx < 0 || untStIdx >= untEdIdx) {
            return dtaCalcUntMap;
        }

        for(int i=losscstCalcLvl; i <= maxLosscstCalcLvl; i++) {
            for(int j=untStIdx; j <= untEdIdx; j++) {
                Map<String, Object> dtlMap = dtlList.get(j);

                // 1. 자원유형코드가 손료(R)이고, 손료계산레벨수가 손료계산레벨수 변수와 같은 경우
                if("R".equals(dtlMap.get("RSCE_TP_CD")) && i == EtcUtil.zeroConvertInt(dtlMap.get("LOSSCST_CALC_LVL_NUM"))) {
                    String up_rsce_cd = EtcUtil.nullConvert(dtlMap.get("UP_RSCE_CD"));
                    String rsce_cd = EtcUtil.nullConvert(dtlMap.get("RSCE_CD"));
                    String calc_mlg_cd = EtcUtil.nullConvert(dtlMap.get("CALC_MLG_CD"));
                    String rslt_mlg_cd = EtcUtil.nullConvert(dtlMap.get("RSLT_MLG_CD"));
                    BigDecimal mtrl_qty = EtcUtil.expToBig(dtlMap.get("MTRL_QTY"));
                    BigDecimal lbr_qty = EtcUtil.expToBig(dtlMap.get("LBR_QTY"));
                    BigDecimal gnrlexpns_qty = EtcUtil.expToBig(dtlMap.get("GNRLEXPNS_QTY"));
                    BigDecimal losscst_aply_pt = EtcUtil.expToBig(dtlMap.get("LOSSCST_APLY_PT")).divide(DEVIDE_100);

                    for(int k=untStIdx; k <= untEdIdx; k++) {
                        Map<String, Object> subDtlMap = dtlList.get(k);
                        String rsceCd = EtcUtil.nullConvert(subDtlMap.get("RSCE_CD"));
                        String losscstCdList = EtcUtil.nullConvert(subDtlMap.get("LOSSCST_CD_LIST"));
                        BigDecimal mtrlQty = EtcUtil.expToBig(subDtlMap.get("MTRL_QTY"));
                        BigDecimal lbrQty = EtcUtil.expToBig(subDtlMap.get("LBR_QTY"));
                        BigDecimal gnrlexpnsQty = EtcUtil.expToBig(subDtlMap.get("GNRLEXPNS_QTY"));

                        // 1-1. 손료 대상 목록이 없는 경우 continue
                        if(ObjectUtils.isEmpty(losscstCdList)) continue;

                        // 1-2. 자원코드가 손료대상목록에 있거나 손료코드와 동일한 경우
                        if(losscstCdList.indexOf(rsce_cd) > -1 && !rsce_cd.equals(subDtlMap.get("LOSSCST_CD"))) {
                            // 자재 자원 수량
                            if("M|T".indexOf(calc_mlg_cd) > -1 && mtrlQty.compareTo(BigDecimal.ZERO) != 0) {
                                pMap.clear();
                                pMap.put("UP_RSCE_CD", rsce_cd + "=" + ("T".equals(rslt_mlg_cd) ? "M" : rslt_mlg_cd));
                                pMap.put("RSCE_CD", rsceCd + "=M");
                                pMap.put("RSCE_TP_CD", subDtlMap.get("RSCE_TP_CD"));
                                pMap.put("RSCE_QTY", mtrlQty.multiply(losscst_aply_pt).multiply(mtrl_qty));

                                losscstCalcUntMap = setLosscstRsceQtyList(pMap, dtaCalcUntMap, losscstCalcUntMap);
                            }

                            // 노무 자원 수량
                            if("L|T".indexOf(calc_mlg_cd) > -1 && lbrQty.compareTo(BigDecimal.ZERO) != 0) {
                                pMap.clear();
                                pMap.put("UP_RSCE_CD", rsce_cd + "=" + ("T".equals(rslt_mlg_cd) ? "L" : rslt_mlg_cd));
                                pMap.put("RSCE_CD", rsceCd + "=L");
                                pMap.put("RSCE_TP_CD", subDtlMap.get("RSCE_TP_CD"));
                                pMap.put("RSCE_QTY", lbrQty.multiply(losscst_aply_pt).multiply(lbr_qty));

                                losscstCalcUntMap = setLosscstRsceQtyList(pMap, dtaCalcUntMap, losscstCalcUntMap);
                            }

                            // 경비항목 자원 수량
                            if("E|T".indexOf(calc_mlg_cd) > -1 && gnrlexpnsQty.compareTo(BigDecimal.ZERO) != 0) {
                                pMap.clear();
                                pMap.put("UP_RSCE_CD", rsce_cd + "=" + ("T".equals(rslt_mlg_cd) ? "E" : rslt_mlg_cd));
                                pMap.put("RSCE_CD", rsceCd + "=E");
                                pMap.put("RSCE_TP_CD", subDtlMap.get("RSCE_TP_CD"));
                                pMap.put("RSCE_QTY", gnrlexpnsQty.multiply(losscst_aply_pt).multiply(gnrlexpns_qty));

                                losscstCalcUntMap = setLosscstRsceQtyList(pMap, dtaCalcUntMap, losscstCalcUntMap);
                            }
                        }
                    }

                    // 1-3. 계산제외여부가 'N'이고 관급여부가 'N'인 경우
                    if("N".equals(dtlMap.get("CALC_EXCP_YN")) && "N".equals(dtlMap.get("GOVSPLY_MTRL_YN"))) {
                        // 손료 계산 목록에 있는 자재 관련 정보
                        List<Map<String, Object>> existList = (List<Map<String, Object>>) losscstCalcUntMap.get(rsce_cd + "=M");
                        if(!ObjectUtils.isEmpty(existList)) {
                            for(Map<String, Object> eMap : existList) {
                                dtaCalcUntMap = setRsceQtyList(up_rsce_cd + "=M", EtcUtil.nullConvert(eMap.get("RSCE_CD")), EtcUtil.expToBig(eMap.get("RSCE_QTY")), dtaCalcUntMap);
                            }
                        }

                        // 손료 계산 목록에 있는 노무 관련 정보
                        existList = (List<Map<String, Object>>) losscstCalcUntMap.get(rsce_cd + "=L");
                        if(!ObjectUtils.isEmpty(existList)) {
                            for(Map<String, Object> eMap : existList) {
                                dtaCalcUntMap = setRsceQtyList(up_rsce_cd + "=L", EtcUtil.nullConvert(eMap.get("RSCE_CD")), EtcUtil.expToBig(eMap.get("RSCE_QTY")), dtaCalcUntMap);
                            }
                        }

                        // 손료 계산 목록에 있는 경비항목 관련 정보
                        existList = (List<Map<String, Object>>) losscstCalcUntMap.get(rsce_cd + "=E");
                        if(!ObjectUtils.isEmpty(existList)) {
                            for(Map<String, Object> eMap : existList) {
                                dtaCalcUntMap = setRsceQtyList(up_rsce_cd + "=E", EtcUtil.nullConvert(eMap.get("RSCE_CD")), EtcUtil.expToBig(eMap.get("RSCE_QTY")), dtaCalcUntMap);
                            }
                        }
                    }
                }
            }
        }

        return dtaCalcUntMap;
    }

    /**
     * 손료 수량 계산
     */
    private Map<String, Object> setLosscstRsceQtyList(Map<String, Object> param, Map<String, Object> dtaCalcUntMap, Map<String, Object> losscstCalcUntMap) throws RuntimeException {
        String up_rsce_cd = EtcUtil.nullConvert(param.get("UP_RSCE_CD"));
        String rsce_cd = EtcUtil.nullConvert(param.get("RSCE_CD"));
        String rsce_tp_cd = EtcUtil.nullConvert(param.get("RSCE_TP_CD"));
        BigDecimal rsce_qty = EtcUtil.expToBig(param.get("RSCE_QTY"));

        // 1. 계산할 수량이 없으면 진행 안 함
        if(rsce_qty.compareTo(BigDecimal.ZERO) == 0) {
            return losscstCalcUntMap;
        }

        List<Map<String, Object>> existList = (List<Map<String, Object>>) losscstCalcUntMap.get(rsce_cd);
        if("R".equals(rsce_tp_cd) && !ObjectUtils.isEmpty(existList)) {
            for(Map<String, Object> eMap : existList) {
                losscstCalcUntMap = setRsceQtyInfo(up_rsce_cd, EtcUtil.nullConvert(eMap.get("RSCE_CD")), rsce_qty.multiply(EtcUtil.expToBig(eMap.get("RSCE_QTY"))), losscstCalcUntMap);
            }
        } else {
            if(!"R".equals(rsce_tp_cd)) {
                existList = (List<Map<String, Object>>) dtaCalcUntMap.get(rsce_cd);

                if(ObjectUtils.isEmpty(existList)) {
                    losscstCalcUntMap = setRsceQtyInfo(up_rsce_cd, rsce_cd, rsce_qty, losscstCalcUntMap);
                } else {
                    for(Map<String, Object> eMap : existList) {
                        losscstCalcUntMap = setRsceQtyInfo(up_rsce_cd, EtcUtil.nullConvert(eMap.get("RSCE_CD")), rsce_qty.multiply(EtcUtil.expToBig(eMap.get("RSCE_QTY"))), losscstCalcUntMap);
                    }
                }
            }
        }

        return losscstCalcUntMap;
    }

    /**
     * 수량 계산
     */
    private Map<String, Object> setRsceQtyInfo(String upRsceCd, String rsceCd, BigDecimal qty, Map<String, Object> calcUntMap) throws RuntimeException {
        if(qty.compareTo(BigDecimal.ZERO) != 0) {
            int listIdx = -1;
            List<Map<String, Object>> calcUntList = (List<Map<String, Object>>) calcUntMap.get(upRsceCd);

            if(calcUntList == null) calcUntList = new ArrayList<Map<String, Object>>();

            List<Map<String, Object>> existList = calcUntList.stream().filter(it -> rsceCd.equals(it.get("RSCE_CD"))).collect(Collectors.toList());
            Map<String, Object> rMap = new HashMap<String, Object>();

            // 동일한 자원코드에 해당하는 수량정보가 있는 경우, 기존 수량에 합산
            if(!ObjectUtils.isEmpty(existList)) {
                rMap.putAll(existList.get(0));
                listIdx = EtcUtil.zeroConvertInt(rMap.get("LIST_IDX"));

                rMap.put("RSCE_QTY", EtcUtil.expToBig(rMap.get("RSCE_QTY")).add(qty));
            }

            if(listIdx >= 0) {
                calcUntList.set(listIdx, rMap);
            } else {
                // 목록에 없는 정보는 추가
                Map<String, Object> tMap = EtcUtil.map(
                        "LIST_IDX", calcUntList.size(),
                        "UP_RSCE_CD", upRsceCd,
                        "RSCE_CD", rsceCd,
                        "RSCE_QTY", qty
                );

                calcUntList.add(tMap);
            }

            calcUntMap.put(upRsceCd, calcUntList);
        }

        return calcUntMap;
    }

    /**
     * 수량 계산하여 목록 추가
     */
    private Map<String, Object> setRsceQtyList(String upRsceCd, String rsceCd, BigDecimal qty, Map<String, Object> dtaCalcUntMap) throws RuntimeException {
        if(qty.compareTo(BigDecimal.ZERO) != 0) {
            List<Map<String, Object>> existList = (List<Map<String, Object>>) dtaCalcUntMap.get(rsceCd);

            if(!ObjectUtils.isEmpty(existList)) {
                for(Map<String, Object> eMap : existList) {
                    dtaCalcUntMap = setRsceQtyInfo(upRsceCd, EtcUtil.nullConvert(eMap.get("RSCE_CD")), qty.multiply(EtcUtil.expToBig(eMap.get("RSCE_QTY"))), dtaCalcUntMap);
                }
            } else {
                String rsce_cd = rsceCd.substring(0, rsceCd.length() - 2);

                existList = (List<Map<String, Object>>) dtaCalcUntMap.get(rsce_cd + "=M");

                if(ObjectUtils.isEmpty(existList)) existList = (List<Map<String, Object>>) dtaCalcUntMap.get(rsce_cd + "=L");
                if(ObjectUtils.isEmpty(existList)) existList = (List<Map<String, Object>>) dtaCalcUntMap.get(rsce_cd + "=E");

                if(ObjectUtils.isEmpty(existList)) {
                    if(!"GJ=".equals(rsce_cd.substring(0, 3))) {
                        dtaCalcUntMap = setRsceQtyInfo(upRsceCd, rsceCd, qty, dtaCalcUntMap);
                    }
                }
            }
        }

        return dtaCalcUntMap;
    }

    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> setRsltList(Map<String, Object> param, String mlgCd, Map<String, Object> dtaCalcUntMap, List<Map<String, Object>> rsceQtyRsltList) throws RuntimeException {
        param = EtcUtil.changeToUpperMapKey(param);
        String up_rsce_cd = EtcUtil.nullConvert(param.get("RSCE_CD"));
        String cnstty_cd = "GJ=" + EtcUtil.nullConvert(param.get("ORIGNL_CNSTTY_CD"));
        String sortCd = ("".equals(up_rsce_cd) ? cnstty_cd : up_rsce_cd) + "=" + mlgCd;

        List<Map<String, Object>> existList = (List<Map<String, Object>>) dtaCalcUntMap.get(sortCd);
        List<Map<String, Object>> rsltList = rsceQtyRsltList;


        if(!ObjectUtils.isEmpty(existList)) {
            for(Map<String, Object> eMap : existList) {
                String rsce_cd = EtcUtil.nullConvert(eMap.get("RSCE_CD"));
                String mlg_cd = rsce_cd.substring(rsce_cd.length() - 1);
                BigDecimal rsce_qty = EtcUtil.expToBig(eMap.get("RSCE_QTY"));
                Map<String, Object> tMap = new HashMap<String, Object> ();

                tMap.put("CNTRCT_CHG_ID", param.get("CNTRCT_CHG_ID"));
                tMap.put("CNSTTY_SN", param.get("CNSTTY_SN"));
                tMap.put("DTL_CNSTTY_SN", param.get("DTL_CNSTTY_SN"));
                tMap.put("UP_RSCE_CD", up_rsce_cd);
                tMap.put("REQRE_RSCE_SN", rsceQtyRsltList.size());
                tMap.put("RSCE_CD", rsce_cd.substring(0, rsce_cd.length() - 2));
                tMap.put("MTRL_QTY", "M".equals(mlg_cd) ? rsce_qty : BigDecimal.ZERO);
                tMap.put("LBR_QTY", "L".equals(mlg_cd) ? rsce_qty : BigDecimal.ZERO);
                tMap.put("GNRLEXPNS_QTY", "E".equals(mlg_cd) ? rsce_qty : BigDecimal.ZERO);
                tMap.put("MLG_CD", mlgCd);

                // 20241208 insertCstDtlCnsttyReqreRsce 세부공종자원 활용을 위해 추가
                tMap.put("RSCE_QTY", rsce_qty);
                
                // 20241226 추가
                tMap.put("RSCE_NM", param.get("RSCE_NM"));
                tMap.put("UNIT", param.get("UNIT"));
                tMap.put("SPEC_NM", param.get("SPEC_NM"));

                rsltList.add(tMap);
            }
        }

        return rsltList;
    }
}
