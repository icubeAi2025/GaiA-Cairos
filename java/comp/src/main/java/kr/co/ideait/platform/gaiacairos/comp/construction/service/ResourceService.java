package kr.co.ideait.platform.gaiacairos.comp.construction.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kr.co.ideait.platform.gaiacairos.core.base.AbstractGaiaCairosService;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.construction.resource.ResourceMybatisParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kr.co.ideait.platform.gaiacairos.core.persistence.vo.construction.resource.ResourceMybatisParam.RawGovsplyMtrlItem;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.construction.resource.ResourceMybatisParam.RawGovsplyMtrlList;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.construction.resource.ResourceMybatisParam.RawLbrEqDate;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.construction.resource.ResourceMybatisParam.RawLbrEqItem;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.construction.resource.ResourceMybatisParam.RawLbrEqList;
import kr.co.ideait.platform.gaiacairos.comp.progress.service.WbsService;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.MybatisInput;
import lombok.extern.slf4j.Slf4j;
import kr.co.ideait.platform.gaiacairos.core.constant.CommonCodeConstants;

@Slf4j
@Service
public class ResourceService extends AbstractGaiaCairosService {

    @Autowired
    WbsService wbsService;

    private final static String DEFAULT_MAPPER_PATH = "kr.co.ideait.platform.gaiacairos.mybatis.mappers.construction.resource";
    
    /**
     * 최종 계약변경ID 조회
     * 
     * @param cntrctNo
     * @return cntrctChgId
     */
    public String getCntrctChgId(String cntrctNo) {

        ResourceMybatisParam.ResourceFormTypeSelectInput resourceForm = new ResourceMybatisParam.ResourceFormTypeSelectInput();
        resourceForm.setCntrctNo(cntrctNo);
        String statement = DEFAULT_MAPPER_PATH + ".selectContractChgId";

        return mybatisSession.selectOne(statement, resourceForm);
    }

    /**
     * 자원투입현황 (Monthly / Daily) 가져오기
     * 
     * @param cntrctNo
     * @param currentMonth
     * @param currentDay
     * @return
     */
    public Map<String, Object> selectResourceList(String cntrctNo, String currentMonth, String currentDay,
            String searchText) {
        Map<String, Object> paramMap = new HashMap<String, Object>();

        // 1. 계약 번호에 따른 최종 변경ID 조회
        ResourceMybatisParam.ResourceFormTypeSelectInput resourceForm = new ResourceMybatisParam.ResourceFormTypeSelectInput();
        resourceForm.setCntrctNo(cntrctNo);
        String cntrctChgId = mybatisSession
                .selectOne(DEFAULT_MAPPER_PATH + ".selectContractChgId", resourceForm);

        // 2. Activity List 조회
        ResourceMybatisParam.ResourceActivity resourceActivity = new ResourceMybatisParam.ResourceActivity();
        resourceActivity.setCntrctChgId(cntrctChgId);
        String activityStatement = DEFAULT_MAPPER_PATH + ".selectResourceActivityList";
        List<ResourceMybatisParam.ResourceActivity> activityList = mybatisSession.selectList(activityStatement,
                resourceActivity);

        // 3. Activity 를 통한 QDB - Resource 조회
        ResourceMybatisParam.ResourceQdbParam qdbParam = new ResourceMybatisParam.ResourceQdbParam();
        qdbParam.setCntrctChgId(cntrctChgId);
        qdbParam.setActivityList(activityList);
        String qdbStatement = DEFAULT_MAPPER_PATH + ".selectResourceQdbList";
        List<ResourceMybatisParam.ResourceQdb> qdbList = mybatisSession.selectList(qdbStatement, qdbParam);

        // 4. 해당 달의 Daily - 조회
        String conditionDate = currentMonth + "-01";
        qdbParam.setConditionDate(conditionDate); // TRUNC_DATE 조회를 위한 변수
        String dailyResourceStatement = DEFAULT_MAPPER_PATH + ".selectResourceList";

        // 4-2. 검색조건 추가
        qdbParam.setSearchText(searchText);

        qdbParam.setRsceTpCd("M");
        List<ResourceMybatisParam.ResourceQdb> dailyRscM = mybatisSession.selectList(dailyResourceStatement, qdbParam);

        qdbParam.setRsceTpCd("L");
        List<ResourceMybatisParam.ResourceQdb> dailyRscL = mybatisSession.selectList(dailyResourceStatement, qdbParam);

        qdbParam.setRsceTpCd("E");
        List<ResourceMybatisParam.ResourceQdb> dailyRscE = mybatisSession.selectList(dailyResourceStatement, qdbParam);

        paramMap.put("activityList", activityList);
        paramMap.put("qdbList", qdbList);
        paramMap.put("mData", dailyRscM);
        paramMap.put("lData", dailyRscL);
        paramMap.put("eData", dailyRscE);

        return paramMap;
    }

    /**
     * 자원투입현황 (Monthly) 상세 가져오기
     * 
     * @param cntrctNo
     * @param rsceTpCd
     * @param currentMonth
     * @param searchText
     * @return
     */
    public Map<String, Object> selectMonthlyResourceDetail(String cntrctNo, String rsceTpCd, String currentMonth,
            String searchText) {
        Map<String, Object> paramMap = new HashMap<String, Object>();

        // 1. 계약 번호에 따른 최종 변경ID 조회
        ResourceMybatisParam.ResourceFormTypeSelectInput resourceForm = new ResourceMybatisParam.ResourceFormTypeSelectInput();
        resourceForm.setCntrctNo(cntrctNo);
        String cntrctChgId = mybatisSession
                .selectOne(DEFAULT_MAPPER_PATH + ".selectContractChgId", resourceForm);

        ResourceMybatisParam.ResourceActivity resourceActivity = new ResourceMybatisParam.ResourceActivity();
        resourceActivity.setCntrctChgId(cntrctChgId);
        String activityStatement = DEFAULT_MAPPER_PATH + ".selectResourceActivityList";
        List<ResourceMybatisParam.ResourceActivity> activityList = mybatisSession.selectList(activityStatement,
                resourceActivity);

        // 2. 해당 달의 선택 자원 조회
        ResourceMybatisParam.ResourceQdbParam qdbParam = new ResourceMybatisParam.ResourceQdbParam();

        if (rsceTpCd.equals("M"))
            qdbParam.setRsceTpCd("M");
        if (rsceTpCd.equals("L"))
            qdbParam.setRsceTpCd("L");
        if (rsceTpCd.equals("E"))
            qdbParam.setRsceTpCd("E");

        qdbParam.setCntrctChgId(cntrctChgId);
        qdbParam.setActivityList(activityList);
        qdbParam.setReturnType("group");
        qdbParam.setMonthlyDetailCurrentDate(currentMonth);

        // 3-2. 검색조건 추가
        if (searchText != null && !searchText.isEmpty())
            qdbParam.setSearchText(searchText);

        String monthlyDetailStatement =DEFAULT_MAPPER_PATH + ".selectResourceList";
        List<ResourceMybatisParam.ResourceQdb> resourcelist = mybatisSession.selectList(monthlyDetailStatement,
                qdbParam);

        paramMap.put("activityList", activityList);
        paramMap.put("data", resourcelist);

        return paramMap;
    }

    /**
     * 자원투입현황 (Daily) 상세 가져오기
     * 
     * @param cntrctNo
     * @return
     */
    public Map<String, Object> selectDailyResourceDetail(String cntrctNo, String rsceCd, String searchText) {

        Map<String, Object> paramMap = new HashMap<String, Object>();

        // 1. 계약 번호에 따른 최종 변경ID 조회
        ResourceMybatisParam.ResourceFormTypeSelectInput resourceForm = new ResourceMybatisParam.ResourceFormTypeSelectInput();

        resourceForm.setCntrctNo(cntrctNo);
        String cntrctChgId = mybatisSession
                .selectOne(DEFAULT_MAPPER_PATH + ".selectContractChgId", resourceForm);

        // 2. 선택 자원 세부 Activity, 공종 조회
        ResourceMybatisParam.ResourceQdbParam qdbParam = new ResourceMybatisParam.ResourceQdbParam();
        qdbParam.setCntrctChgId(cntrctChgId);
        qdbParam.setRsceCd(rsceCd);

        // 3. Search
        if (searchText != null && !searchText.isEmpty())
            qdbParam.setSearchText(searchText);

        String dailyDetailStatement = DEFAULT_MAPPER_PATH + ".selectDailyResourceDetail";
        List<ResourceMybatisParam.ResourceQdb> resourcelist = mybatisSession.selectList(dailyDetailStatement, qdbParam);

        paramMap.put("data", resourcelist);

        return paramMap;
    }

    // 착공계 관급자재수급계획서 데이터
    public RawGovsplyMtrlItem getGovsplyMtrlList(String cntrctNo, String unitCnstType) {
        MybatisInput input = new MybatisInput();
        input.add("cntrctNo", cntrctNo);
        input.add("unitCnstType", unitCnstType);
        List<RawGovsplyMtrlList> govsplyMtrlList = mybatisSession
                .selectList(DEFAULT_MAPPER_PATH + ".getGovsplyMtrlList", input);

        RawGovsplyMtrlItem govsplyMtrlItem = mybatisSession
                .selectOne(DEFAULT_MAPPER_PATH + ".getGovsplyMtrlDate", input);

        if (govsplyMtrlItem == null) {
            govsplyMtrlItem = new RawGovsplyMtrlItem();
        }

        govsplyMtrlItem.setRawGovsplyMtrlList(govsplyMtrlList);
        return govsplyMtrlItem;
    }

    // 노무/장비 데이터
    public RawLbrEqList getLbrEqItem(String cntrctNo) {
        Map<String, Object> params = new HashMap<>();
        params.put("cntrctNo", cntrctNo);
        params.put("majorcode", CommonCodeConstants.MAJOR_CNSTTY_CODE_GROUP_CODE);

        List<RawLbrEqItem> lbrEqItemList = mybatisSession
                .selectList(DEFAULT_MAPPER_PATH + ".getLbrEqItem", params);
        List<RawLbrEqDate> lbrEqDateList = mybatisSession
                .selectList(DEFAULT_MAPPER_PATH + ".getLbrEqDate", cntrctNo);

        RawLbrEqList rawLbrEqList = new RawLbrEqList();
        rawLbrEqList.setRawLbrEqLists(lbrEqItemList);
        rawLbrEqList.setRawLbrEqDates(lbrEqDateList);
        return rawLbrEqList;
    }
}
