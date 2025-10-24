package kr.co.ideait.platform.gaiacairos.core.persistence.vo.construction.mainphoto;

import javax.annotation.processing.Generated;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CwDailyReport;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CwDailyReportActivity;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-07-23T18:31:18+0900",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.5 (Amazon.com Inc.)"
)
@Component
public class MainphotoFormImpl implements MainphotoForm {

    @Override
    public CwDailyReport DailyReportInsert(DailyReportInsert dailyReportInsert) {
        if ( dailyReportInsert == null ) {
            return null;
        }

        CwDailyReport cwDailyReport = new CwDailyReport();

        cwDailyReport.setCntrctNo( dailyReportInsert.getCntrctNo() );
        cwDailyReport.setDailyReportId( dailyReportInsert.getDailyReportId() );
        cwDailyReport.setDailyReportDate( dailyReportInsert.getDailyReportDate() );
        cwDailyReport.setReportNo( dailyReportInsert.getReportNo() );
        cwDailyReport.setTitle( dailyReportInsert.getTitle() );
        cwDailyReport.setAmWthr( dailyReportInsert.getAmWthr() );
        cwDailyReport.setPmWthr( dailyReportInsert.getPmWthr() );
        if ( dailyReportInsert.getPrcptRate() != null ) {
            cwDailyReport.setPrcptRate( dailyReportInsert.getPrcptRate().longValue() );
        }
        if ( dailyReportInsert.getSnowRate() != null ) {
            cwDailyReport.setSnowRate( dailyReportInsert.getSnowRate().longValue() );
        }
        cwDailyReport.setDlowstTmprtVal( dailyReportInsert.getDlowstTmprtVal() );
        cwDailyReport.setDtopTmprtVal( dailyReportInsert.getDtopTmprtVal() );
        if ( dailyReportInsert.getAcmltPlanBohalRate() != null ) {
            cwDailyReport.setAcmltPlanBohalRate( dailyReportInsert.getAcmltPlanBohalRate().doubleValue() );
        }
        if ( dailyReportInsert.getAcmltArsltBohalRate() != null ) {
            cwDailyReport.setAcmltArsltBohalRate( dailyReportInsert.getAcmltArsltBohalRate().doubleValue() );
        }
        if ( dailyReportInsert.getTodayPlanBohalRate() != null ) {
            cwDailyReport.setTodayPlanBohalRate( dailyReportInsert.getTodayPlanBohalRate().doubleValue() );
        }
        if ( dailyReportInsert.getTodayArsltBohalRate() != null ) {
            cwDailyReport.setTodayArsltBohalRate( dailyReportInsert.getTodayArsltBohalRate().doubleValue() );
        }
        cwDailyReport.setMajorMatter( dailyReportInsert.getMajorMatter() );
        cwDailyReport.setSftyWorkItem( dailyReportInsert.getSftyWorkItem() );
        cwDailyReport.setApprvlStats( dailyReportInsert.getApprvlStats() );
        cwDailyReport.setApprvlId( dailyReportInsert.getApprvlId() );
        cwDailyReport.setApprvlDt( dailyReportInsert.getApprvlDt() );
        cwDailyReport.setApprvlReqId( dailyReportInsert.getApprvlReqId() );
        cwDailyReport.setApprvlReqDt( dailyReportInsert.getApprvlReqDt() );
        cwDailyReport.setDltYn( dailyReportInsert.getDltYn() );

        return cwDailyReport;
    }

    @Override
    public CwDailyReportActivity DailyReportActivityInsert(DailyReportInsert dailyReportInsert) {
        if ( dailyReportInsert == null ) {
            return null;
        }

        CwDailyReportActivity cwDailyReportActivity = new CwDailyReportActivity();

        cwDailyReportActivity.setCntrctNo( dailyReportInsert.getCntrctNo() );
        cwDailyReportActivity.setDailyReportId( dailyReportInsert.getDailyReportId() );
        cwDailyReportActivity.setWorkDtType( dailyReportInsert.getWorkDtType() );
        cwDailyReportActivity.setDltYn( dailyReportInsert.getDltYn() );

        return cwDailyReportActivity;
    }

    @Override
    public void toUpdateCwDailyReport(DailyReportInsert dailyreport, CwDailyReport cwDailyReport) {
        if ( dailyreport == null ) {
            return;
        }

        cwDailyReport.setCntrctNo( dailyreport.getCntrctNo() );
        cwDailyReport.setDailyReportId( dailyreport.getDailyReportId() );
        cwDailyReport.setDailyReportDate( dailyreport.getDailyReportDate() );
        cwDailyReport.setReportNo( dailyreport.getReportNo() );
        cwDailyReport.setTitle( dailyreport.getTitle() );
        cwDailyReport.setAmWthr( dailyreport.getAmWthr() );
        cwDailyReport.setPmWthr( dailyreport.getPmWthr() );
        if ( dailyreport.getPrcptRate() != null ) {
            cwDailyReport.setPrcptRate( dailyreport.getPrcptRate().longValue() );
        }
        else {
            cwDailyReport.setPrcptRate( null );
        }
        if ( dailyreport.getSnowRate() != null ) {
            cwDailyReport.setSnowRate( dailyreport.getSnowRate().longValue() );
        }
        else {
            cwDailyReport.setSnowRate( null );
        }
        cwDailyReport.setDlowstTmprtVal( dailyreport.getDlowstTmprtVal() );
        cwDailyReport.setDtopTmprtVal( dailyreport.getDtopTmprtVal() );
        if ( dailyreport.getAcmltPlanBohalRate() != null ) {
            cwDailyReport.setAcmltPlanBohalRate( dailyreport.getAcmltPlanBohalRate().doubleValue() );
        }
        else {
            cwDailyReport.setAcmltPlanBohalRate( null );
        }
        if ( dailyreport.getAcmltArsltBohalRate() != null ) {
            cwDailyReport.setAcmltArsltBohalRate( dailyreport.getAcmltArsltBohalRate().doubleValue() );
        }
        else {
            cwDailyReport.setAcmltArsltBohalRate( null );
        }
        if ( dailyreport.getTodayPlanBohalRate() != null ) {
            cwDailyReport.setTodayPlanBohalRate( dailyreport.getTodayPlanBohalRate().doubleValue() );
        }
        else {
            cwDailyReport.setTodayPlanBohalRate( null );
        }
        if ( dailyreport.getTodayArsltBohalRate() != null ) {
            cwDailyReport.setTodayArsltBohalRate( dailyreport.getTodayArsltBohalRate().doubleValue() );
        }
        else {
            cwDailyReport.setTodayArsltBohalRate( null );
        }
        cwDailyReport.setMajorMatter( dailyreport.getMajorMatter() );
        cwDailyReport.setSftyWorkItem( dailyreport.getSftyWorkItem() );
        cwDailyReport.setApprvlStats( dailyreport.getApprvlStats() );
        cwDailyReport.setApprvlId( dailyreport.getApprvlId() );
        cwDailyReport.setApprvlDt( dailyreport.getApprvlDt() );
        cwDailyReport.setApprvlReqId( dailyreport.getApprvlReqId() );
        cwDailyReport.setApprvlReqDt( dailyreport.getApprvlReqDt() );
        cwDailyReport.setDltYn( dailyreport.getDltYn() );
    }
}
