package kr.co.ideait.platform.gaiacairos.core.persistence.vo.construction;

import lombok.Data;
import org.apache.ibatis.type.Alias;

import java.math.BigDecimal;

public interface InspectionreportMybatisParam {

    @Data
    @Alias("inspectionreportOutput")
    public class InspectionreportOutput {
        String cntrctNo;
        BigDecimal dailyReportId;
        String dailyReportDate;
        String reportNo;
        String title;
        String apprvlStats;
        String apprvl;
        String amWthr;
        String pmWthr;
        BigDecimal prcptRate;
        BigDecimal snowRate;
        String dlowstTmprtVal;
        String dtopTmprtVal;
        
        String rmrkCntnts;
        String majorMatter;
        String significantNote;
        String commentResult;

        String rgstrId;
        String apprvlId;
        String rgstr;
        String apprvlr;
        String workCd;
        String workType;
        String docId;
        String hasCfReport;
    }
}
