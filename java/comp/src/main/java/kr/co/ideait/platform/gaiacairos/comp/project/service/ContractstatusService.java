package kr.co.ideait.platform.gaiacairos.comp.project.service;

import kr.co.ideait.iframework.BaseUtil;
import kr.co.ideait.iframework.BidUtil;
import kr.co.ideait.iframework.EtcUtil;
import kr.co.ideait.platform.gaiacairos.core.base.AbstractGaiaCairosService;
import kr.co.ideait.platform.gaiacairos.core.constant.CommonCodeConstants;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CnContract;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CnContractChange;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CnContractCompany;
import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories.CnContractChangeRepository;
import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories.CnContractCompanyRepository;
import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories.CnContractRepository;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.MybatisInput;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.project.contract.contractstatus.ContractstatusMybatisParam;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.project.contract.contractstatus.ContractstatusMybatisParam.*;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.security.UserAuth;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.xml.stream.XMLStreamException;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Predicate;

@Service
public class ContractstatusService extends AbstractGaiaCairosService {

    @Autowired
    CnContractRepository contractRepository;

    @Autowired
    CnContractCompanyRepository companyRepository;

    @Autowired
    CnContractChangeRepository changeRepository;

    // [DEV] EURECA - 공사담당자 ID 기본 값
    private static final String DEV_EURECA_OFCL_ID = "0000000476";

    // [PROD] EURECA - 공사담당자 ID 기본 값
    private static final String PROD_EURECA_OFCL_ID = "0000000446";

    /* ==================================================================================================================
     *
     * 계약 현황 - 계약
     *
     * ==================================================================================================================
     */

    // 프로젝트-수요기관
    public Dminstt getDminstt(String cntrctNo) {
        Map<String, Object> params = new HashMap<>();
        params.put("cntrctNo", cntrctNo);
        return mybatisSession.selectOne(
                "kr.co.ideait.platform.gaiacairos.mybatis.mappers.project.contractstatus.getDminstt", params);
    }

    /**
     * 계약 조회
     * 조회페이지용, 수정페이지용
     */
    public CnContract getByCntrctNo(String cntrctNo) { // 수정페이지
        return contractRepository.findByCntrctNo(cntrctNo);
    }

    /**
     * 계약 조회
     * 조회페이지용, 수정페이지용
     */
    public String getByCntrctNm(String cntrctNo) { // 수정페이지
        return contractRepository.findCntrctNmByCntrctNo(cntrctNo);
    }

    public Map<String, Object> getCntrctDetail( Map<String, Object> params) { // 조회페이지

        return mybatisSession.selectOne(
                "kr.co.ideait.platform.gaiacairos.mybatis.mappers.project.contractstatus.getContract", params);
    }

    /**
     * 계약 목록 조회
     */
    public List<ContractstatusOutput> getList(ContractstatusListInput input) {
        return mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.project.contractstatus.getContractStatusList", input);
    }

    /**
     * 계약 추가수정 후 저장
     *
     * @return
     */
    public CnContract saveContract(CnContract contract) {
        return contractRepository.save(contract);
    }

    /**
     * 계약 삭제
     */
    @Transactional
    public int deleteContract(MybatisInput input) {
        return mybatisSession.update("kr.co.ideait.platform.gaiacairos.mybatis.mappers.project.contractstatus.updateDeleteContractstatus", input);
    }

    /**
     * 계약 전부 삭제
     */
    public int deleteAllContract( ContractstatusMybatisParam.ContractDeleteInput deleteInput) {
        return mybatisSession.update("kr.co.ideait.platform.gaiacairos.mybatis.mappers.project.contractstatus.updateDeleteAllContract", deleteInput);
    }

    /**
     * 프로젝트 하위 계약번호 리스트 조회
     */
    public List<String> getCntrctNoList(String pjtNo) {
        return mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.project.contractstatus.getCntrctNoList", pjtNo);
    }

    /**
     * 새 계약 번호 생성
     */
    public String generateContractNumber(String pjtNo, String majorCnsttyCd) {        
        return mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.project.contractstatus.makeCntrctNo", String.format("%s.%s", pjtNo, majorCnsttyCd.substring(0, 1)));
    }

    /* ==================================================================================================================
     *
     * 계약 현황 - 도급사
     *
     * ==================================================================================================================
     */

    /**
     * 도급 조회
     */
    public ContractcompanyOutput getContractCompany(Map<String, Object> params) { // 화면 조회용
        ContractcompanyOutput contractcompanyOutput = mybatisSession.selectOne(
                "kr.co.ideait.platform.gaiacairos.mybatis.mappers.project.contractstatus.getContractCompany",
                params);

        return contractcompanyOutput;
    }

    public CnContractCompany getByCntrctId(Long cntrctId, String cntrctNo) {
        return companyRepository.findByCntrctIdAndCntrctNo(cntrctId, cntrctNo);
    }

    public String findCntrctNm(String cntrctNo) {
        return contractRepository.findCntrctNmByCntrctNo(cntrctNo);
    }

    public CnContractCompany getRprsCompany(String cntrctNo) {
        return companyRepository.findByCntrctNoAndCntrctId(cntrctNo, 1).get();
    }

    /**
     * 도급 목록 조회
     */
    public List<ContractcompanyOutput> getCompanyList(ContractcompanyListInput contractcompanyListInput) {

        return  mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.project.contractstatus.getContractCompanyList",contractcompanyListInput);

    }

    /**
     * 도급 목록 - 추가
     */
    @Transactional
    public CnContractCompany createCompany(CnContractCompany cnCompany) {

        cnCompany.setCntrctId(companyRepository.findCntrctIdByCntrctNo(cnCompany.getCntrctNo()) + 1);
        return companyRepository.save(cnCompany);

    }

    /**
     * 도급 공종코드 가져오기
     */
    public List<Map<String, ?>> getCnsttyCdList() {

        return mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.project.contractstatus.getCnsttyCdList",CommonCodeConstants.WORKTYPE_CODE_GROUP_CODE);

    }

    /**
     * 도급 수정
     */
    @Transactional
    public CnContractCompany updateCompany(CnContractCompany company) {
        return companyRepository.save(company);
    }

    @Transactional
    public void updateRprsYn(String cntrctNo, Long cntrctId) {
        companyRepository.updateRprsYn(cntrctNo, cntrctId);
    }

    /**
     * 도급 삭제
     *
     * @param companyList
     */
    @Transactional
    public void deleteCompany(List<CnContractCompany> companyList) {
        for (int i = 0; i < companyList.size(); i++) {
            CnContractCompany company = companyRepository.findByCntrctIdAndCntrctNo(companyList.get(i).getCntrctId(),
                    companyList.get(i).getCntrctNo());
            companyRepository.updateDelete(company);
        }
    }

    /**
     * 도급 전체 삭제
     */
    public void deleteAllCompany(MybatisInput input) {
        mybatisSession.update("kr.co.ideait.platform.gaiacairos.mybatis.mappers.project.contractstatus.deleteAllCompany", input);
    }

    /* ==================================================================================================================
     *
     * 계약 현황 - 계약 변경
     *
     * ==================================================================================================================
     */

    /**
     * 변경 목록 조회
     */
    public List<ContractchangeOutputList> getContractChangeList(ContractchangeListInput contractchangeListInput) {

        return mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.project.contractstatus.getContractChangeList",contractchangeListInput);

    }

    /**
     * 변경 조회
     */
    public ContractchangeOutput getContractChangeDetail(Map<String, Object> params) { // 일반 조회용
        return mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.project.contractstatus.getContractChange",params);
    }

    /**
     * 변경 조회
     */
    public CnContractChange getBycntrctChgId(String cntrctChgId, String cntrctNo) { // 기존 변경 수정용
        return changeRepository.findByCntrctChgIdAndCntrctNo(cntrctChgId, cntrctNo);
    }

    /**
     * 변경 추가페이지 조회
     */
    public ContractchangeAddOutput getContractChangeAdd(Map<String, Object> params) { // 추가 조회용

        return mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.project.contractstatus.getContractChangeAdd",params);

    }

    /**
     * 1회차 변경 수정용
     */
    public CnContractChange getFirstChange(String cntrctNo,String cntrctDivCd) {
        CnContractChange firstChange;
        if(!"0104".equals(cntrctDivCd)){
            firstChange = changeRepository.findByCntrctNoAndCntrctChgNoAndCntrctPhaseIsNullAndDltYn(cntrctNo, "1", "N");
        } else {
            firstChange = changeRepository.findByCntrctNoAndCntrctChgNoAndCntrctPhaseAndDltYn(cntrctNo, "1", 1, "N");
        }
        return firstChange;
    }

    /**
     * 계약변경 추가
     *  - 계약 추가시점 수행
     */

    @Transactional
    public CnContractChange createDefaultChange(CnContract cnContract) {
        CnContractChange cnChange = new CnContractChange();
        cnChange.setCntrctNo(cnContract.getCntrctNo());
        cnChange.setCntrctChgId(generateChangeId(cnContract.getCntrctNo()));
        cnChange.setCntrctChgDate(cnContract.getCntrctDate()); // 변경 완료일자 = 계약일자
        cnChange.setChgCbgnDate(cnContract.getCcmpltDate()); // 준공일자
        cnChange.setChgThisCbgnDate(cnContract.getThisCcmpltDate()); // 금차 준공일자
        cnChange.setCntrctAmt(cnContract.getCntrctCost()); // 계약금액
        cnChange.setThisCntrctAmt(cnContract.getThisCntrctCost()); // 금차 계약금액
        cnChange.setChgThisConPrd(cnContract.getThisConPrd()); // 금차 공사기간
        cnChange.setDltYn("N");
        cnChange.setLastChgYn("Y");

        cnChange.setCntrctChgNo("1"); // 1 회차
        /* 20250626 - 장기계속계약_차수별 인 경우에만 값을 SET */
        if ("0104".equals(cnContract.getCntrctDivCd())) {
            cnChange.setCntrctPhase(1);
        } else {
            cnChange.setCntrctPhase(null);
        }

        cnChange.setRgstrId(cnContract.getRgstrId());
        cnChange.setChgId(cnContract.getChgId());

        return changeRepository.save(cnChange);
    }

    /**
     * 계약변경 추가
     * @param cnChange
     */
    @Transactional
    public CnContractChange createChange(CnContractChange cnChange) { // 새로 추가
        cnChange.setCntrctChgId(generateChangeId(cnChange.getCntrctNo()));
        cnChange.setCntrctChgNo(cnChange.getCntrctChgNo().replace("회", "").trim());
        cnChange.setDltYn("N");
        if (cnChange.getLastChgYn().equals("Y")) { // 동일 계약의 다른 변경들
            List<CnContractChange> changes = changeRepository.findByCntrctNoAndDltYn(cnChange.getCntrctNo(), "N");
            for (CnContractChange change : changes) {
                change.setLastChgYn("N");
                changeRepository.saveAndFlush(change); // 각 변경 내역을 저장
            }
        }

        CnContractChange cnContractChange = changeRepository.saveAndFlush(cnChange);

        return cnContractChange;


    }

    /**
     * 계약변경 수정
     */
    @Transactional
    public CnContractChange updateChange(CnContractChange cnChange, int changeLast) {
        if (changeLast == 1) { // 최종 변경 여부가 수정됨
            List<CnContractChange> changes = changeRepository.findAllByCntrctNoExcludingChgId(cnChange.getCntrctNo(),
                    cnChange.getCntrctChgId());
            for (CnContractChange change : changes) {
                change.setLastChgYn("N");
                changeRepository.save(change); // 각 변경 내역을 저장
            }
        }
        CnContractChange cnContractChange = changeRepository.save(cnChange);


        return cnContractChange;
    }

    /**
     * 계약변경 삭제
     */
    @Transactional
    public void deleteChange(List<CnContractChange> changeList) {
        for (int i = 0; i < changeList.size(); i++) {
            CnContractChange change = changeRepository.findByCntrctChgId(changeList.get(i).getCntrctChgId());
            changeRepository.updateDelete(change);
        }

    }

    /**
     * 계약변경 전체 삭제
     */
    public void deleteAllChange(MybatisInput input) {
        mybatisSession.update("kr.co.ideait.platform.gaiacairos.mybatis.mappers.project.contractstatus.deleteAllChange", input);
    }

    /**
     * 새 계약변경ID 생성
     */
    public String generateChangeId(String cntrctNo) {
        List<String> cntrctChgIds = changeRepository.findCntrctChgIds(cntrctNo);
        String maxChangeId = cntrctChgIds.isEmpty() ? null : cntrctChgIds.get(0);
        int nextSequenceNumber = 1; // 기본값 1

        if (maxChangeId != null && !maxChangeId.isEmpty()) {
            String[] parts = maxChangeId.split("\\.V");
            int currentMaxNumber = Integer.parseInt(parts[1]);
            nextSequenceNumber = currentMaxNumber + 1;
        }

        return cntrctNo + ".V" + String.format("%02d", nextSequenceNumber);
    }

    public String convertToEurecaContractChangeNo(Map param) {
        return mybatisSession.selectOne(
                "kr.co.ideait.platform.gaiacairos.mybatis.mappers.project.contractstatus.convertToEurecaContractChangeNo", param);
    }

    /* ==================================================================================================================
     *
     * 계약 현황 - 계약내역
     *
     * ==================================================================================================================
     */

    /**
     * Eureca 및 PCCS 연계로 인한 Deprecated 처리
     * 계약내역서 목록조회
     *
     * 변경: 2024-11-13 계약내역서 등록에 필요한 {type} parameter 추가
     */
    @Deprecated
    public List<Map<String, ?>> getContractBidList( Map<String, Object> params) {

        return mybatisSession.selectList(
                "kr.co.ideait.platform.gaiacairos.mybatis.mappers.project.contractstatus.getContractBidList",
                params);
    }

    /**
     * Eureca 및 PCCS 연계로 인한 Deprecated 처리
     * 계약 내역서 직접비 합계금액
     */
    @Deprecated
    public Map<String, ?> getContractBidCost(Map<String, Object> params) {

        return mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.project.contractstatus.getContractBidCost",params);
    }

    /**
     * Eureca 및 PCCS 연계로 인한 Deprecated 처리
     * 계약내역서 검색
     */
    @Deprecated
    public List<Map<String, ?>> getContractBidSearch(Map<String, Object> params) {

        return mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.project.contractstatus.getContractBidSearch",params);

    }

    /**
     * 원가계산서 목록조회
     */
    public List<Map<String, ?>> getCalculatorList(String cntrctNo) {

        return mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.project.contractstatus.getCalculatorList",cntrctNo);

    }

    /**
     * Eureca 및 PCCS 연계로 인한 Deprecated 처리
     * 비드파일을 계약내역서 로 넣기
     */
    @Deprecated
    public void insertCtrDtlstt(String cntrctNo, File bidFile, String type) throws IOException, XMLStreamException {
        // 1. 비드 파일 읽어서 corpBidInfo1 에 담는다.
        BidUtil.BidFileInfo bidFileInfo = BidUtil.getBidFileInfoWithTtag(bidFile);
        Map<String, List> tListMap = bidFileInfo.getTtagListMap();

        String getCntrctCnstType = "kr.co.ideait.platform.gaiacairos.mybatis.mappers.project.contractstatus.getCntrctCnstType";
        List<Map<String, Object>> cntrctCnstTypeMap = mybatisSession.selectList(getCntrctCnstType);

        ContractstatusMybatisParam.CorpBidInfo corpBidInfo1 = makeCorpBidInfo(tListMap, cntrctCnstTypeMap);

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("CNTRCT_NO", cntrctNo);
        paramMap.put("SYSTEM_ID", Objects.requireNonNull(UserAuth.get(true)).getUsrId());
        paramMap.put("TYPE", type);

        // 2. Insert 이전 Delete 먼저 수행 - 대상: CN_CONTRACT_BID_HISTORY
        String deleteHistoryStatement = "kr.co.ideait.platform.gaiacairos.mybatis.mappers.project.contractstatus.deleteContractBidHistory";
        mybatisSession.delete(deleteHistoryStatement, paramMap);

        // 단위공사 insert
        List<Map> unitConstList = corpBidInfo1.getUnitConstList();
        for (Map it : unitConstList) {
            it.putAll(paramMap);

            // 20241122 계약 단위공사 -> 코드로 변경처리
            // mybatisSession.insert("kr.co.codefarm.svcm.cmis.gaia.ctrdtlsttreg.insertCtrUnitConst",
            // it);
        }

        int CBS_SNO = 1;
        // 세부공종 항목
        List<Map> detailList = corpBidInfo1.getDetailList();
        for (Map it : detailList) {
            it.putAll(paramMap);
            it.put("CBS_SNO", CBS_SNO);
            it.put("EXPNSS_YN", "N");
            CBS_SNO++;
        }
        // 제경비 항목
        List<Map> expnssList = corpBidInfo1.getExpnssList();
        for (Map it : expnssList) {
            it.putAll(paramMap);
            it.put("CBS_SNO", CBS_SNO);
            it.put("EXPNSS_YN", "Y");
            it.put("DCNSTTY_LVL_NUM", 1);
            it.put("QTY", 1);
            it.put("BUYTAX_OBJ_YN", "N");
            it.put("OQTY_CHG_PERMSN_YN", "N");
            CBS_SNO++;
        }
        // 세부공종 + 제경비
        List<Map> allList = new ArrayList<>();
        allList.addAll(detailList);
        allList.addAll(expnssList);

        // 속도위해 sqlBatch 사용 Insert - 대상: CN_CONTRACT_BID_HISTORY
        // AS-IS
        String bidHistoryStatement = "kr.co.ideait.platform.gaiacairos.mybatis.mappers.project.contractstatus.insertContractBid";
        // BaseUtil.updateListBySqlBatch(mybatisSession, allList, 200,
        // bidHistoryStatement, null);

        // TO-BE 20250211 속도 향상을 위해 변환
        List<List<Map>> partedList = ListUtils.partition(allList, 100);// 한꺼번에 하면 pgsql 에러
        for (List<Map> list : partedList) {
            mybatisSession.insert(bidHistoryStatement, EtcUtil.map("regList", list));
        }

        if ("bid".equals(type)) {
            // 하위 내역서(원가) 삭제 수행 - C3R 업무 연관
            String deletePlandtlstt = "kr.co.ideait.platform.gaiacairos.mybatis.mappers.project.contractstatus.deletePlandtlstt";
            mybatisSession.delete(deletePlandtlstt, paramMap);

            // Delete & Insert - 대상: CN_CONTRACT_BID
            String deleteBidStatement = "kr.co.ideait.platform.gaiacairos.mybatis.mappers.project.contractstatus.deleteContractBid";
            mybatisSession.delete(deleteBidStatement, paramMap);
            String insertBidStatement = "kr.co.ideait.platform.gaiacairos.mybatis.mappers.project.contractstatus.copyToContractBid";
            mybatisSession.insert(insertBidStatement, paramMap);

            // 원가계산서 업무 START - 기존 js func getMyCstbill()
            // String insertCalculator = "";
            // mybatisSession.insert(insertCalculator, paramMap);

            // String getCodeList =
            // "kr.co.ideait.platform.gaiacairos.mybatis.mappers.project.contractstatus.getExpnssList";
            String getExpnssList = "kr.co.ideait.platform.gaiacairos.mybatis.mappers.project.contractstatus.getExpnssList";
            String getDatcAmtList = "kr.co.ideait.platform.gaiacairos.mybatis.mappers.project.contractstatus.getDatcAmtList";

            // 순수제경비 목록조회
            List<Map<String, Object>> calcExpnssList = EtcUtil
                    .changeToUpperMapList(mybatisSession.selectList(getExpnssList, paramMap));
            // 내역제경비 목록조회
            List<Map<String, Object>> calcDatcAmtList = EtcUtil
                    .changeToUpperMapList(mybatisSession.selectList(getDatcAmtList, paramMap));
            List<Map<String, Object>> calcList = new ArrayList<>();

            // 직공비 추가
            var dirList = calcDatcAmtList.stream() // 직공비 목록
                    .filter(elem -> elem.get("DIR_YN").equals("Y"))
                    .toList();

            List<String> sumPropList = new ArrayList<String>();
            sumPropList.add("DSGN_MTRLCST");
            sumPropList.add("DSGN_LBRCST");
            sumPropList.add("DSGN_GNRLEXPNS");
            Map<String, BigDecimal> dirSumItem = makeCondSumItem(dirList, null, sumPropList);

            calcList.add(makeExpnssItem(10, "1", "직접재료비", dirSumItem.get("DSGN_MTRLCST")));
            calcList.add(makeExpnssItem(20, "2", "직접노무비", dirSumItem.get("DSGN_LBRCST")));
            calcList.add(makeExpnssItem(30, "3", "산출경비", dirSumItem.get("DSGN_GNRLEXPNS")));

            // 순수제경비 추가
            var loc5List = calcDatcAmtList.stream() // 직공비 목록
                    .filter(elem -> elem.get("LOC5_YN").equals("Y"))
                    .toList();
            calcList.addAll(calcExpnssList);

            // TODO 항목확인
            // 내역제경비 추가
            int temp1 = 111;
            for (int idx = 0; idx < loc5List.size(); idx++) {
                Map<String, Object> elem = loc5List.get(idx);
                int expnssSno = temp1 + (idx * 100);
                String expnssNm = elem.get("DCNSTTY_AMT_TY_CD").toString();
                // 20241209 - 추가

                if ("4".equals(elem.get("DCNSTTY_AMT_TY_CD"))) {
                    expnssNm = "미확정설계공종";
                    expnssSno = 3500;
                }
                if ("5".equals(elem.get("DCNSTTY_AMT_TY_CD"))) {
                    expnssNm = "제비율적용제외공종";
                    expnssSno = 3600;
                }

                var expnssItem = makeExpnssItem(expnssSno, "5", expnssNm, (BigDecimal) elem.get("DSGN_SUM_AMT"));
                calcList.add(expnssItem);
            }
            // IntStream.range(0, loc5List.size()).forEach(idx -> { });

            // 순공/재 소계, 순공/노 소계, 순공/경 소계, 순공사비 계, 총합계, 도급공사비 추가
            List<Map<String, Object>> subSumList = new ArrayList<>();
            BigDecimal amt = null;

            amt = makeSumExpnss(calcList, it -> it.get("CST_BILL_LCT_CD").equals("1"));
            subSumList.add(makeSubSumItem("Y", "순공사원가", "재료비", "재료비", 1, amt, "1"));
            amt = makeSumExpnss(calcList, it -> it.get("CST_BILL_LCT_CD").equals("2"));
            subSumList.add(makeSubSumItem("Y", "순공사원가", "노무비", "노무비", 2, amt, "2"));
            amt = makeSumExpnss(calcList, it -> it.get("CST_BILL_LCT_CD").equals("3"));
            subSumList.add(makeSubSumItem("Y", "순공사원가", "경비", "경비", 3, amt, "3"));

            amt = makeSumExpnss(calcList,
                    it -> Arrays.asList(new String[] { "1", "2", "3" }).contains(it.get("CST_BILL_LCT_CD")));
            subSumList.add(makeSubSumItem("Y", "순공사원가", "순공사원가", "순공사원가", 0, amt, "0"));
            amt = makeSumExpnss(calcList,
                    it -> Arrays.asList(new String[] { "1", "2", "3", "4", "5" }).contains(it.get("CST_BILL_LCT_CD")));
            subSumList.add(makeSubSumItem("Y", "", "", "총합계", 5.1, amt, "8000"));
            amt = makeSumExpnss(calcList, null);
            subSumList.add(makeSubSumItem("Y", "", "", "총공사비", 6.1, amt, "9000"));

            // IntStream.range(0, calcList.size()).forEach(idx -> {...}) immutable
            // collection
            ListIterator<Map<String, Object>> iterator = calcList.listIterator();

            while (iterator.hasNext()) {
                Map<String, Object> elem = iterator.next();

                elem.put("order1", Double.parseDouble(elem.get("CST_BILL_LCT_CD").toString()));
                elem.put("order2", Double.parseDouble(elem.get("EXPNSS_SNO").toString()));

                // 정렬을 위해 값 set
                if (Arrays.asList(new String[] { "직접재료비", "직접노무비", "산출경비" }).contains(elem.get("EXPNSS_NM"))) {
                    elem.put("order2", 1);
                }

                // 2024-12-05 기존 로직 미사용으로 인한 주석처리
                // if (elem.get("CST_BILL_LCT_CD").equals("1")) {
                // elem.put("soonName1", "순공사원가");
                // elem.put("soonName2", "재료비");
                // elem.put("UP_CST_CALC_IT_CD", "SD");
                // } else if (elem.get("CST_BILL_LCT_CD").equals("2")) {
                // elem.put("soonName1", "순공사원가");
                // elem.put("soonName2", "노무비");
                // elem.put("UP_CST_CALC_IT_CD", "SL");
                // } else if (elem.get("CST_BILL_LCT_CD").equals("3")) {
                // elem.put("soonName1", "순공사원가");
                // elem.put("soonName2", "경비");
                // elem.put("UP_CST_CALC_IT_CD", "SE");
                // }
            }

            calcList.addAll(subSumList);

            // 정렬
            calcList = calcList.stream()
                    .sorted(
                            Comparator.comparing(
                                    (Map<String, Object> map) -> Double.parseDouble(map.get("order1").toString())) // 첫
                                                                                                                   // 번째
                                                                                                                   // 조건
                                                                                                                   // 오름차순
                                    .thenComparing(map -> Double.parseDouble(map.get("order2").toString())) // 두 번째 조건:
                                                                                                            // 내림차순
                    ).toList();

            var insCalclist = new ArrayList<ContractstatusMybatisParam.ContractCalculator>();
            calcList.forEach(elem -> {
                var calc = new ContractstatusMybatisParam.ContractCalculator();
                calc.setCntrctNo(paramMap.get("CNTRCT_NO").toString());
                calc.setCstCalcItCd(elem.get("EXPNSS_SNO").toString());
                calc.setCstCalcItNm(elem.get("EXPNSS_NM").toString());
                calc.setOvrhdcstPt(EtcUtil.zeroConvertDouble(elem.get("EXPNSS_BSCRT_PCT")));
                calc.setDrcnstcostCmprPt(EtcUtil.zeroConvertDouble(elem.get("DRCT_CNSTCST_PCT")));
                calc.setCostAm(Double.parseDouble(elem.get("DSGN_EXPNSS").toString()));
                calc.setRgstrId(paramMap.get("SYSTEM_ID").toString());
                insCalclist.add(calc);
            });

            // 원가계산서 삭제 (초기화) 후 추가
            String deleteCalc = "kr.co.ideait.platform.gaiacairos.mybatis.mappers.project.contractstatus.deleteContractCalculator";
            String insertCalc = "kr.co.ideait.platform.gaiacairos.mybatis.mappers.project.contractstatus.insertContractCalculator";
            String updateCalc = "kr.co.ideait.platform.gaiacairos.mybatis.mappers.project.contractstatus.updateContractCalculatorItem";
            mybatisSession.delete(deleteCalc, paramMap);
            mybatisSession.insert(insertCalc, Map.of("insCalclist", insCalclist));
            mybatisSession.update(updateCalc, paramMap);

        }

    }

    /**
     * Eureca 및 PCCS 연계로 인한 Deprecated 처리
     * Bid 생성 내부함수 (업체 비드정보 생성)
     * @param tListMap
     * @param cntrctCnstTypeMap
     */
    @Deprecated
    private static ContractstatusMybatisParam.CorpBidInfo makeCorpBidInfo(Map<String, List> tListMap,
            List<Map<String, Object>> cntrctCnstTypeMap) {
        List<Map> unitConstList = new ArrayList<>();// 단위공사
        List<Map> detailList = new ArrayList<>();// 내역서
        List<Map> detailAddList = new ArrayList<>();// 내역서
        List<Map> expnssList = new ArrayList<>();// 제경비
        // List<Map> scIndList = new ArrayList<>();// 하도급업종
        // List<Map> scDetailList = new ArrayList<>();// 하도급내역서
        // List<Map> scExpnssList = new ArrayList<>();// 하도급제경비

        List<Map> list_T2 = tListMap.get("T2");// ce_gbid_part 업체단위공사
        BaseUtil.checkNotEmpty(list_T2, "단위공사 부분이 없습니다.");
        for (Map T2 : list_T2) {
            Map it1 = new HashMap<>();
            it1.put("UNIT_CNSTWK_SNO", T2.get("C1"));
            it1.put("UNIT_CNSTWK_NM", T2.get("C4"));
            it1.put("GRP_UNIT_CNSTWK_NM", T2.get("C3"));

            // 20241122 계약 단위공사 -> 코드로 변경처리
            Map<String, Object> code = cntrctCnstTypeMap.stream()
                    .filter(item -> item.get("cmn_cd_nm_krn").equals(T2.get("C3"))).toList().getFirst();
            it1.put("CNTRCT_CNST_TYPE", code.get("cmn_cd"));

            // grp_unit_cnstwk_cd
            // ofcl_id
            // vat_inclsn_govsply_mtrcst
            unitConstList.add(it1);
        }

        List<Map> list_T3 = tListMap.get("T3");// ce_gbid_detail 업체내역서
        BaseUtil.checkNotEmpty(list_T2, "내역서 부분이 없습니다.");
        for (Map T3 : list_T3) {
            Map it1 = new HashMap<>();

            Map<String, Object> unit = unitConstList.stream()
                    .filter(item -> item.get("UNIT_CNSTWK_SNO").equals(T3.get("C1"))).toList().getFirst();
            it1.put("CNTRCT_CNST_TYPE", unit.get("CNTRCT_CNST_TYPE"));

            it1.put("CNTRCT_UNIT_CNSTWK_SNO", T3.get("C1"));
            it1.put("CNTRCT_DCNSTTY_SNO", T3.get("C2"));
            it1.put("UP_CNTRCT_DCNSTTY_SNO", T3.get("C3"));
            it1.put("CNSTTY_DTLS_DIV_CD", T3.get("C5"));
            it1.put("DCNSTTY_LVL_NUM", T3.get("C11"));
            it1.put("PRDNM", T3.get("C12"));
            it1.put("SPEC", T3.get("C13"));
            it1.put("UNIT", T3.get("C14"));
            it1.put("QTY", T3.get("C15"));
            it1.put("MTRLCST_UPRC", T3.get("C16"));
            it1.put("LBRCST_UPRC", T3.get("C17"));
            it1.put("GNRLEXPNS_UPRC", T3.get("C18"));
            it1.put("SUM_UPRC", T3.get("C19"));
            it1.put("MTRLCST_AMT", T3.get("C20"));
            it1.put("LBRCST_AMT", T3.get("C21"));
            it1.put("GNRLEXPNS_AMT", T3.get("C22"));
            it1.put("SUM_AMT", T3.get("C23"));
            it1.put("RMRK", T3.get("C32"));
            it1.put("DCNSTTY_AMT_TY_CD", T3.get("C7"));
            it1.put("STD_MRKT_UPRC_CD", T3.get("C10"));
            it1.put("CST_RSCE_CD", T3.get("C9"));
            it1.put("BUYTAX_OBJ_YN", "1".equals(T3.get("C41")) ? "Y" : "N");
            it1.put("OQTY_CHG_PERMSN_YN", "0".equals(T3.get("C42")) ? "Y" : "N");
            // cst_cnstty_sno_val
            // cst_unit_cnstwk_no
            // cst_cnstty_sno
            // cst_dcnstty_sno
            // cst_rsce_ty_cd
            detailList.add(it1);
        }

        // TODO T14 추가내역서는? <-- 기존 TODO

        List<Map> list_T5 = tListMap.get("T5");// ce_gbid_sundry_expenses
        BaseUtil.checkNotEmpty(list_T5, "제경비 부분이 없습니다.");
        for (Map T5 : list_T5) {
            Map it1 = new HashMap<>();
            it1.put("EXPNSS_SNO", T5.get("C2"));
            it1.put("CST_BILL_LCT_CD", T5.get("C3"));
            it1.put("EXPNSS_KIND_CD", T5.get("C10"));
            it1.put("EXPNSS_BSCRT_PCT", T5.get("C6"));
            it1.put("DRCT_CNSTCST_PCT", T5.get("C7"));
            it1.put("EXPNSS_CALCFRMLA_CD", T5.get("C5"));
            it1.put("PRDNM", T5.get("C4"));
            it1.put("SUM_AMT", T5.get("C8"));
            // C1 공종별 번호
            expnssList.add(it1);
        }

        ContractstatusMybatisParam.CorpBidInfo corpBidInfo1 = new ContractstatusMybatisParam.CorpBidInfo();
        corpBidInfo1.setUnitConstList(unitConstList);
        corpBidInfo1.setDetailList(detailList);
        corpBidInfo1.setDetailAddList(detailAddList);
        corpBidInfo1.setExpnssList(expnssList);
        // corpBidInfo1.setCorpInfo(corpInfo);
        // corpBidInfo1.setScIndList(scIndList);
        // corpBidInfo1.setScDetailList(scDetailList);
        // corpBidInfo1.setScExpnssList(scExpnssList);
        return corpBidInfo1;
    }

    /**
     * Eureca 및 PCCS 연계로 인한 Deprecated 처리
     * 원가계산서 사용 내부 클래스 - 노드 항목
     */
    @Deprecated
    public Map<String, Object> makeExpnssItem(
            int EXPNSS_SNO, String CST_BILL_LCT_CD, String EXPNSS_NM, BigDecimal DSGN_EXPNSS) {

        // Map.of return immutable object
        return new HashMap<>(
                Map.of(
                        "EXPNSS_SNO", EXPNSS_SNO,
                        "CST_BILL_LCT_CD", CST_BILL_LCT_CD,
                        "EXPNSS_NM", EXPNSS_NM,
                        "DSGN_EXPNSS", DSGN_EXPNSS));

    }

    /**
     * Eureca 및 PCCS 연계로 인한 Deprecated 처리
     * 원가계산서 사용 내부 클래스 - 그룹/소계 항목
     */
    @Deprecated
    public Map<String, Object> makeSubSumItem(
            String sumYn, String soonName1, String soonName2, String EXPNSS_NM,
            double order1, BigDecimal DSGN_EXPNSS, String EXPNSS_SNO) {

        // Map.of return immutable object
        return new HashMap<>(
                Map.ofEntries(
                        Map.entry("sumYn", sumYn),
                        Map.entry("soonName1", soonName1),
                        Map.entry("soonName2", soonName2),
                        Map.entry("EXPNSS_NM", EXPNSS_NM),
                        Map.entry("order1", order1),
                        Map.entry("order2", 0),
                        Map.entry("DSGN_EXPNSS", DSGN_EXPNSS),
                        Map.entry("EXPNSS_SNO", EXPNSS_SNO),

                        Map.entry("EXPNSS_BSCRT_PCT", 0),
                        Map.entry("DRCT_CNSTCST_PCT", 0)));
    }

    /**
     * Eureca 및 PCCS 연계로 인한 Deprecated 처리
     * Bid 생성 내부함수
     * @param result
     * @param item
     * @param props
     */
    @Deprecated
    public static void sumItemFn(Map<String, BigDecimal> result, Map<String, Object> item, List<String> props) {
        for (String prop : props) {
            // 현재 값 가져오기 (없으면 0으로 초기화)
            BigDecimal currentValue = result.getOrDefault(prop, BigDecimal.ZERO);
            // 추가할 값 가져오기
            BigDecimal itemValue = new BigDecimal(item.getOrDefault(prop, 0).toString());
            // 합산 후 결과 저장
            result.put(prop, currentValue.add(itemValue));
        }
    }

    /**
     * Eureca 및 PCCS 연계로 인한 Deprecated 처리
     * Bid 생성 내부함수
     * @param list
     * @param condFn
     * @param sumPropList
     */
    @Deprecated
    public static Map<String, BigDecimal> makeCondSumItem(
            List<Map<String, Object>> list,
            Predicate<Map<String, Object>> condFn,
            List<String> sumPropList) {
        Map<String, BigDecimal> result = new HashMap<>();

        for (Map<String, Object> item : list) {
            // 조건 검사
            if (condFn == null || condFn.test(item)) {
                sumItemFn(result, item, sumPropList);
            }
        }

        return result;
    }

    /**
     * Eureca 및 PCCS 연계로 인한 Deprecated 처리
     * Bid 생성 내부함수
     * @param list
     * @param condFn
     */
    @Deprecated
    public static BigDecimal makeSumExpnss(
            List<Map<String, Object>> list,
            Predicate<Map<String, Object>> condFn) {

        // 합산 대상 속성 정의
        List<String> sumPropList = Collections.singletonList("DSGN_EXPNSS");

        // makeCondSumItem 호출
        Map<String, BigDecimal> result = makeCondSumItem(list, condFn, sumPropList);

        // 결과에서 'DSGN_EXPNSS' 값 반환 (없으면 0)
        return result.getOrDefault("DSGN_EXPNSS", BigDecimal.ZERO);
    }

    /* ==================================================================================================================
     *
     * 착공계
     *
     * ==================================================================================================================
     */
    /**
     * 원가계산서 목록조회
     */
    public List<RawCostItem> getCalculatorConstructionList(String cntrctNo) {
        Map<String, Object> params = new HashMap<>();
        params.put("cntrctNo", cntrctNo);
        return mybatisSession.selectList(
                "kr.co.ideait.platform.gaiacairos.mybatis.mappers.project.contractstatus.getCalculatorConstructionList",
                params);
    }

    /**
     * 계약내역서 목록조회
     *
     */
    public List<RawContractItem> getContractBidConstructionList(String cntrctNo) {
        Map<String, Object> params = new HashMap<>();
        params.put("cntrctNo", cntrctNo);

        return mybatisSession.selectList(
                "kr.co.ideait.platform.gaiacairos.mybatis.mappers.project.contractstatus.getContractBidConstructionList",
                params);
    }

    /**
     * 금차내역서 목록조회
     *
     */
    public List<RawCbsItem> getCbsConstructionList(String cntrctNo) {
        Map<String, Object> params = new HashMap<>();
        String cntrctChgId = cntrctNo + ".V01"; // 금차 계약변경ID
        params.put("cntrctChgId", cntrctChgId);

        return mybatisSession.selectList(
                "kr.co.ideait.platform.gaiacairos.mybatis.mappers.project.contractstatus.getCbsConstructionList",
                params);
    }

    /* ==================================================================================================================
     *
     * 계약관리 - 하도급
     *
     * ==================================================================================================================
     */

    /**
     * 도급 목록 조회
     */
    public List<ContractcompanyOutput> getSubcontractCompanyList(String cntrctNo) {
        List<ContractcompanyOutput> contractcompanyOutput = mybatisSession.selectList(
                "kr.co.ideait.platform.gaiacairos.mybatis.mappers.project.contractstatus.getSubcontractCompanyList",
                cntrctNo);
        return contractcompanyOutput;
    }

    /*==================================================================================================================
     *
     * 재해일지 관련
     *
     * ==================================================================================================================
     */

    /**
     * 안전사고일자 수정
     * @param cntrctNo
     * @param recentlydisasterDate
     */
    public void updateSftyAcdntDt(String cntrctNo, LocalDateTime recentlydisasterDate) {
        Map<String, Object> params = new HashMap<>();
        params.put("cntrctNo", cntrctNo);
        params.put("recentlydisasterDate", recentlydisasterDate);
        mybatisSession.update("kr.co.ideait.platform.gaiacairos.mybatis.mappers.project.contractstatus.updateSftyAcdntDt", params);
    }
}
