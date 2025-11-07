package kr.co.ideait.platform.gaiacairos.core.persistence.vo.construction;

import lombok.Data;
import org.apache.ibatis.type.Alias;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface ChiefInspectionreportMybatisParam {

    @Data
    @Alias("chiefInspectionreportInput")
    public class ChiefInspectionreportInput {
        String cntrctNo;
        String startDate;
        String endDate;
        String appStatus;
        String searchValue;
        String selectValue;
        String year;
        String month;
        String code;

        String apprvlId;
        String apprvlStats;
    }

    @Data
    @Alias("chiefInspectionreportOutput")
    public class ChiefInspectionreportOutput {
        String cntrctNo;
        Long dailyReportId;
        String dailyReportDate;
        String reportNo;
        String title;
        String chiefMgr;

        String apprvlNm;
        String apprvlStats;
        String apprvlStatsNm;

        //날씨
        String amWthr;
        String pmWthr;
        BigDecimal prcptRate;
        BigDecimal snowRate;
        String dlowstTmprtVal;
        String dtopTmprtVal;

        // 공정율
        BigDecimal acmltPlanBohalRate;
        BigDecimal acmltArsltBohalRate;
        BigDecimal acmltProcess;
        BigDecimal todayPlanBohalRate;
        BigDecimal todayArsltBohalRate;
        BigDecimal todayProcess;

        //주요작업상황
        String majorMatter;
        String significantNote;
        String commentResult;
        String workCd;

        //책임기술인
        String cfMajorMatter;
        String cfRmrkCntnts;
        String cfTimeRange;

        //기술인별 업무수행내용
        String usrNm;
        String fileNo;

        //기타
        String wthrSttus; // 날씨 상태 요약
        String rmrkCntnts; // 특이사항
        String docId;

    }

    @Data
    @Alias("chiefInspectionreportActivityOutput")
    public class ChiefInspectionreportActivityOutput {
        String wbs;
        Integer dailyActivityId;
        String cntrctChgId;
        String revisionId;
        String activityId;
        String activityNm;
        String wbsCd;
        String workDtType;
        String planStart;
        String planFinish;
        String exptCost;
        String planBgnDate;
        String planEndDate;
        String planReqreDaynum;
        String actualBgnDate;
        String actualEndDate;
        String actualReqreDaynum;
        String pstats;
        String taskContent;
        String specialNote;
        BigDecimal todayPlanRate;
        BigDecimal todayExeRate;
    }

    @Data
    @Alias("inspectionreportByChiefOutput")
    public class InspectionreportByChiefOutput {
        Long dailyReportId;
        String apprvlStats;
        String apprvlStatsNm;
        String workCd;
        String workNm;
        String significantNote;
        String majorMatter;
        String commentResult;
        String usrNm;
        String fileDiskNm;
        String fileDiskPath;
        String fileOrgNm;
        Integer fileNo;
    }

    @Data
    @Alias("chiefInspectionreportDocOutput")
    public class ChiefInspectionreportDocOutput {
        String cntrctNo;
        Long dailyReportId;
        Integer docId;
        String docNo;
        String workType;
        String title;
        String summary;
        String docType;
        String date;
        String target;
        String rmrkCntnts;
    }
}

