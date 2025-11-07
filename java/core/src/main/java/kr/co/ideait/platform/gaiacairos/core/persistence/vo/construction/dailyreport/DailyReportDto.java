package kr.co.ideait.platform.gaiacairos.core.persistence.vo.construction.dailyreport;

import kr.co.ideait.platform.gaiacairos.core.config.GlobalMapperConfig;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.MyBatisParam;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;
import org.mapstruct.Mapper;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Mapper(config = GlobalMapperConfig.class)
public interface DailyReportDto {

    @EqualsAndHashCode(callSuper = true)
    @Alias("cwDailyReportGroup")
    @Data
    class CwDailyReportGroup extends MyBatisParam {
        String cntrctNo;
        Long dailyReportId;
        String dailyReportDate;
        String reportNo;
        String title;
        String wthrSttus;
        String amWthr;
        String pmWthr;
        BigDecimal prcptRate;
        BigDecimal snowRate;
        String dlowstTmprtVal;
        String dtopTmprtVal;
        BigDecimal acmltPlanBohalRate;
        BigDecimal acmltArsltBohalRate;
        BigDecimal acmltProcess;
        BigDecimal todayPlanBohalRate;
        BigDecimal todayArsltBohalRate;
        BigDecimal todayProcess;
        BigDecimal bfrtPlanBohalRate;
        BigDecimal bfrtArsltBohalRate;
        String majorMatter;
        String sftyWorkItem;
        String rmrkCntnts;
        String apprvlStats;
        String apDocId;
        String apprvlId;
        LocalDateTime apprvlDt;
        String apprvlReqId;
        LocalDateTime apprvlReqDt;
        String dltYn;
        String todayActivityContents;
        String tomorrowActivityContents;
        String docId;
    }

    @EqualsAndHashCode(callSuper = true)
    @Alias("CwDailyReportActivityGroup")
    @Data
    class CwDailyReportActivityGroup extends MyBatisParam {
        String cntrctNo;
        Long dailyReportId;
        Integer dailyActivityId;
        String wbsCd;
        String activityId;
        String activityNm;
        String workDtType;
        String planBgnDate;
        String planEndDate;
        Long planReqreDaynum;
        String actualBgnDate;
        String actualEndDate;
        Long actualReqreDaynum;
        BigDecimal todayPlanRate;
        BigDecimal todayExeRate;
        String pstats;
        String cntrctChgId;
        String revisionId;
        String dltYn;
        String rgstrId;
        LocalDateTime rgstDt;
        String chgId;
        LocalDateTime chgDt;
        String dltId;
        LocalDateTime dltDt;
        String activityKind;
    }

    @EqualsAndHashCode(callSuper = true)
    @Alias("CwDailyReportQdbGroup")
    @Data
    class CwDailyReportQdbGroup extends MyBatisParam {
        String cntrctNo;
        Long dailyReportId;
        String revision_id;
        String activityId;
        Long dtlCnsttySn;
        String cnsttyCd;
        String rsceTpCd;
        String rsceCd;
        BigDecimal workQty;
        String orgnlRsceCd;
        String rmrk;
        String dltYn;
        String rgstrId;
        LocalDateTime rgstDt;
        String chgId;
        LocalDateTime chgDt;
        String dltId;
        LocalDateTime dltDt;
    }

}
