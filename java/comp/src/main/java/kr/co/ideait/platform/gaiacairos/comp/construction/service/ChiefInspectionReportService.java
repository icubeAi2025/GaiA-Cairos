package kr.co.ideait.platform.gaiacairos.comp.construction.service;

import kr.co.ideait.platform.gaiacairos.core.base.AbstractGaiaCairosService;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CwCfInspectionReport;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CwCfInspectionReportActivity;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CwCfInspectionReportDoc;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CwDailyReport;
import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories.CwCfInspectionReportActivityRepository;
import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories.CwCfInspectionReportDocRepository;
import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories.CwCfInspectionReportRepository;
import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories.CwDailyReportRepository;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.MybatisInput;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.construction.ChiefInspectionreportMybatisParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
public class ChiefInspectionReportService extends AbstractGaiaCairosService {

    @Autowired
    CwDailyReportRepository dailyReportRepository;

    @Autowired
    CwCfInspectionReportRepository cfInspectionReportRepository;

    @Autowired
    CwCfInspectionReportActivityRepository cfInspectionReportActivityRepository;

    @Autowired
    CwCfInspectionReportDocRepository cwCfInspectionReportDocRepository;


    // 목록 -----------------
    /**
     * 책임감리일지 목록 데이터 조회
     */
    public List<ChiefInspectionreportMybatisParam.ChiefInspectionreportOutput> getReportList(ChiefInspectionreportMybatisParam.ChiefInspectionreportInput input) {
        return mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.construction.chiefInspectionreport.getChiefInspectionReportList",input);
    }

    /**
     * 책임감리일지 날짜 데이터 조회
     */
    public List<String> getReportYears(String cntrctNo) {
        return mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.construction.chiefInspectionreport.getYear", cntrctNo);
    }

    /**
     * 작업 일보 데이터 조회
     */
    public CwDailyReport getDailyReport(String cntrctNo, String dailyReportDate) {
        return dailyReportRepository.findByCntrctNoAndDailyReportDateAndDltYn(cntrctNo, dailyReportDate,"N");
    }

    /**
     * 책임감리일지 ID 찾기 (중복검사)
     */
    public Long getReportId(String cntrctNo, String dailyReportDate) {
        return cfInspectionReportRepository.findDailyReportIdByCntrctNoAndDailyReportDate(cntrctNo, dailyReportDate);
    }

    // 상세조회 -----------------
    /**
     * 책임감리일지 dailyReportDate로 데이터 조회
     */
    public CwCfInspectionReport getReportByDate(String cntrctNo, String dailyReportDate){
        return cfInspectionReportRepository.findByCntrctNoAndDailyReportDateAndDltYn(cntrctNo, dailyReportDate, "N");
    }

    /**
     * 책임감리일지 상세조회 데이터 조회
     */
    public ChiefInspectionreportMybatisParam.ChiefInspectionreportOutput getReport(MybatisInput input) {
        return mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.construction.chiefInspectionreport.getChiefInspectionReport",input);
    }

    /**
     * 책임감리일지 Activity 데이터 조회
     */
    public List<ChiefInspectionreportMybatisParam.ChiefInspectionreportActivityOutput> getActivity(MybatisInput input) {
        return mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.construction.chiefInspectionreport.getChiefInspectionReportActivity",input);
    }

    /**
     * 책임감리일지 상세조회 데이터 조회
     */
    public CwCfInspectionReport getUpdateReport(String cntrctNo, Long dailyReportId) {
        return cfInspectionReportRepository.findByCntrctNoAndDailyReportId(cntrctNo, dailyReportId);
    }

    /**
     * 책임감리일지 Activity 데이터 조회
     */
    public CwCfInspectionReportActivity getUpdateReportActivity(String cntrctNo, Long dailyReportId, Integer dailyActivityId) {
        return cfInspectionReportActivityRepository.findByCntrctNoAndDailyReportIdAndDailyActivityIdAndDltYn(cntrctNo, dailyReportId, dailyActivityId, "N");
    }

    /**
     * 책임감리일지 Activity List 데이터 조회
     */
    public List<CwCfInspectionReportActivity> getReportActivityList(String cntrctNo, Long dailyReportId) {
        return cfInspectionReportActivityRepository.findByCntrctNoAndDailyReportId(cntrctNo, dailyReportId);
    }

    /**
     * 책임감리일지 Activity 데이터 삭제
     */
    public void deleteReportActivityList(List<CwCfInspectionReportActivity> cfActivityList) {
        cfInspectionReportActivityRepository.deleteAll(cfActivityList);
    }

    /**
     * 책임감리일지 activity 저장
     */
    public void saveReportActivity(CwCfInspectionReportActivity activity) {
        cfInspectionReportActivityRepository.save(activity);
    }

    /**
     * 책임감리일지 문서 List 데이터 조회
     */
    public List<ChiefInspectionreportMybatisParam.ChiefInspectionreportDocOutput> getReportDocList(String cntrctNo, Long dailyReportId) {
        MybatisInput input = new MybatisInput().add("cntrctNo",cntrctNo).add("dailyReportId",dailyReportId);
        return mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.construction.chiefInspectionreport.getDocList",input);
    }

    /**
     * 책임감리일지 문서 데이터 조회
     */
    public CwCfInspectionReportDoc getReportDoc(String cntrctNo, Long dailyReportId, Integer docId) {
        return cwCfInspectionReportDocRepository.findByCntrctNoAndDailyReportIdAndDocId(cntrctNo, dailyReportId, docId);
    }

    /**
     * 책임감리일지 문서 docId 생성
     */
    public Integer generateDocId(String cntrctNo, Long dailyReportId) {
        Integer maxDocId = cwCfInspectionReportDocRepository.findMaxDocIdByCntrctNoAndDailyReportId(cntrctNo,dailyReportId);
        return (maxDocId == null ? 1 : maxDocId + 1);
    }

    /**
     * 책임감리일지 문서 데이터 삭제
     */
    public void deleteReportDoc(CwCfInspectionReportDoc cfDoc) {
           cwCfInspectionReportDocRepository.updateDelete(cfDoc);
    }


    /**
     * 책임감리일지 문서 저장
     */
    public void saveReportDoc(CwCfInspectionReportDoc doc) {
        cwCfInspectionReportDocRepository.save(doc);
    }

    /**
     * 책임감리일지 날짜별 감리 목록 데이터 조회
     */
    public List<ChiefInspectionreportMybatisParam.InspectionreportByChiefOutput> getReportListByDate(MybatisInput input) {
        return mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.construction.chiefInspectionreport.getInspectionReportList",input);

    }


    /**
     * 책임감리일지 저장
     */
    public void saveReport(CwCfInspectionReport report) {
        cfInspectionReportRepository.save(report);
    }


    /**
     * 책임감리일지 주요 작업상황 추가
     *
     * @return
     */
    public void createActivity(List<Map<String,Object>> activityListByDailyReport, Long dailyReportId) {
        // TD: 금일
        List<CwCfInspectionReportActivity> activityList = new ArrayList<>();

        activityListByDailyReport.forEach(activity -> {
            CwCfInspectionReportActivity cfActivity = new CwCfInspectionReportActivity();
            cfActivity.setCntrctNo(activity.get("cntrct_no").toString());
            cfActivity.setDailyReportId(dailyReportId);
            cfActivity.setDailyActivityId(Integer.parseInt(activity.get("daily_activity_id").toString()));
            cfActivity.setActivityId(activity.get("activity_id").toString());
            cfActivity.setWbsCd(activity.get("wbs_cd").toString());
            cfActivity.setTaskContent(Optional.ofNullable(activity.get("taskContent")).map(Object::toString).orElse(null));
            cfActivity.setSpecialNote(Optional.ofNullable(activity.get("specialNote")).map(Object::toString).orElse(null));
            cfActivity.setDltYn("N");

            activityList.add(cfActivity);
        });

        cfInspectionReportActivityRepository.saveAll(activityList);
    }

    /**
     * 책임감리일지 dailyReportId 순번 증가
     *
     * @return
     */
    public Long generateDailyReportId() {
        Long maxId = cfInspectionReportRepository.findMaxDailyReportId();
        return (maxId == null ? 1 : maxId + 1);
    }

    /**
     * 작업일지 dailyReportId 조회
     *
     * @return
     */
    public Long getDailyReportIdByDailyReport(String cntrctNo, String dailyReportDate) {
        return dailyReportRepository.findDailyReportDateByCntrctNo(cntrctNo,dailyReportDate);
    }

    /**
     * 책임감리일지 삭제
     */
    public void deleteReport(CwCfInspectionReport delete) {
        cfInspectionReportRepository.updateDelete(delete);
    }

    /**
     * 책임감리일지 승인상태 변경
     */
    public void updateChiefApprvlStats(MybatisInput input) {
        mybatisSession.update("kr.co.ideait.platform.gaiacairos.mybatis.mappers.construction.chiefInspectionreport.updateChiefApprvlStats", input);
    }

    /**
     * 복사전 검증
     * @param map
     * @return
     */
    public boolean checkChiefInspectionReportExists(Map map) {
        return mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.construction.chiefInspectionreport.selectChiefInspectionReportExists", map);
    }

    // pdf에 들어갈 책임감리일지 데이터 조회
    public Map<String, Object> getPdfCfDataByCfReport(MybatisInput input) {
        // 책임감리일지 정보 조회
        return mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.construction.chiefInspectionreport.getPdfDataByCfReport", input);
    }

    // pdf에 들어갈 감리일지 데이터 조회
    public List<Map<String, Object>> getPdfInsDataByCfReport(MybatisInput input) {
        // 감리일지 목록 조회
        return mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.construction.chiefInspectionreport.getPdfDataByInsReport", input);
    }

    // pdf에 들어갈 문서 데이터 조회
    public List<Map<String, Object>> getPdfDocDataByCfReport(MybatisInput input) {
        return mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.construction.chiefInspectionreport.getPdfDocDataByCfReport", input);
    }

    public CwCfInspectionReport getCfReportByPdf(Long dailyReportId) {
        MybatisInput input = new MybatisInput().add("dailyReportId", dailyReportId);
        return mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.construction.chiefInspectionreport.getCfReportByPdf", input);
    }

    public  String updateDocId(MybatisInput input) {
        int update = mybatisSession.update("kr.co.ideait.platform.gaiacairos.mybatis.mappers.construction.chiefInspectionreport.updateDocId", input);
        return update != 0 ? "success" : "fail";
    }


}
