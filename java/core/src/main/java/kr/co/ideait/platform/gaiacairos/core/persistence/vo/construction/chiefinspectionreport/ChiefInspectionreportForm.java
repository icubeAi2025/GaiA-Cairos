package kr.co.ideait.platform.gaiacairos.core.persistence.vo.construction.chiefinspectionreport;

import kr.co.ideait.platform.gaiacairos.core.config.GlobalMapperConfig;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.*;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.construction.ChiefInspectionreportMybatisParam;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.construction.inspectionreport.InspectionreportForm;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.construction.inspectionreport.InspectionreportForm.CreateReport;
import lombok.Data;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Mapper(config = GlobalMapperConfig.class)
public interface ChiefInspectionreportForm {

    ChiefInspectionreportMybatisParam.ChiefInspectionreportInput toChiefInspectionreportInput(ChiefInspectionReport chiefInspectionReport);
    CwCfInspectionReport toCwCfInspectionReport(ChiefInspectionReport chiefInspectionReport);
    CwCfInspectionReportActivity toCwCfInspectionReportActivity(ChiefInspectionReportActivity chiefInspectionReportActivity);
    CwCfInspectionReportDoc toCwCfInspectionReportDoc(ChiefInspectionReportDoc chiefInspectionReportDoc);

    // 수정 폼
    void updateReport(ChiefInspectionReport report, @MappingTarget CwCfInspectionReport chiefInspectionreport);
    void updateInspectReport(InspectionReportByChief inspection, @MappingTarget CwInspectionReport inspectionreport);
    void updateDoctReport(ChiefInspectionReportDoc doc, @MappingTarget CwCfInspectionReportDoc chiefInspectionreportDoc);

    @Data
    class ChiefInspectionReport {

        // 검색용
        String startDate;
        String endDate;
        String searchValue;
        String selectValue;
        String year;
        String month;
        String appStatus;

        // 개요
        String cntrctNo;
        Long dailyReportId;
        String dailyReportDate;
        String reportNo;
        String title;

        // 날씨
        String wthrSttus;
        String amWthr;
        String pmWthr;
        String dlowstTmprtVal;
        String dtopTmprtVal;
        BigDecimal prcptRate;
        BigDecimal snowRate;

        //공정율
        BigDecimal todayPlanBohalRate;
        BigDecimal todayArsltBohalRate;
        BigDecimal todayProcess;
        BigDecimal acmltPlanBohalRate;
        BigDecimal acmltArsltBohalRate;
        BigDecimal acmltProcess;

        // 그 외 데이터
        String majorMatter;
        String significantNote;
        String commentResult;
        String rmrkCntnts;

        String cfMajorMatter;
        String cfRmrkCntnts;
        String cfTimeRange;

        String apprvlStats;
        String apDocId;
        String apprvlId;
        String apprvlDt;
        String apprvlReqId;
        String apprvlReqDt;
        String dltYn;
        String workCd;
        String docId;
        
        // 생성 - 복사타입구분
        String createType;
        String chiefMgr;

        // activity 목록
        List<ChiefInspectionReportActivity> activityList;
        // 감리일지 목록
        List<InspectionReportByChief> inspectList;
        // 문서 목록
        List<ChiefInspectionReportDoc> docList;
        // 삭제할 문서 목록
        List<Integer> deletedDocList;

        //삭제구분용
        String hasApprvlStats;

    }

    @Data
    class ChiefInspectionReportActivity {
        String cntrctNo;
        Long dailyReportId;
        Integer dailyActivityId;
        String wbsCd;
        String activityId;
        String taskContent;
        String specialNote;
    }

    @Data
    class InspectionReportByChief {
        String cntrctNo;
        Long dailyReportId;
        String commentResult;
    }

    @Data
    class ChiefInspectionReportDoc {
        String cntrctNo;
        Long dailyReportId;
        Integer docId;
        String docNo;
        String workType;
        String title;
        String summary;
        String docType;
        LocalDateTime date;
        String target;
        String rmrkCntnts;

    }

    @Data
    class DailyReportList {  //목록
        List<ChiefInspectionReport> reportList;
        String imgDir;
        String baseUrl;
    }

    @Data
    class DailyReportIdList {  // 승인요청 처리 목록
        String cntrctNo;
        List<Long> dailyReportIdList;
    }
}



