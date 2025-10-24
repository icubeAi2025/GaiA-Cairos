package kr.co.ideait.platform.gaiacairos.core.persistence.vo.construction.inspectionreport;

import javax.annotation.processing.Generated;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CwInspectionReport;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CwInspectionReportActivity;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CwInspectionReportPhoto;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-07-23T18:31:18+0900",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.5 (Amazon.com Inc.)"
)
@Component
public class InspectionreportFormImpl implements InspectionreportForm {

    @Override
    public CwInspectionReport toEntity(CreateReport createReport) {
        if ( createReport == null ) {
            return null;
        }

        CwInspectionReport cwInspectionReport = new CwInspectionReport();

        cwInspectionReport.setCntrctNo( createReport.getCntrctNo() );
        cwInspectionReport.setDailyReportId( createReport.getDailyReportId() );
        cwInspectionReport.setDailyReportDate( createReport.getDailyReportDate() );
        cwInspectionReport.setReportNo( createReport.getReportNo() );
        cwInspectionReport.setTitle( createReport.getTitle() );
        cwInspectionReport.setAmWthr( createReport.getAmWthr() );
        cwInspectionReport.setPmWthr( createReport.getPmWthr() );
        cwInspectionReport.setPrcptRate( createReport.getPrcptRate() );
        cwInspectionReport.setSnowRate( createReport.getSnowRate() );
        cwInspectionReport.setDlowstTmprtVal( createReport.getDlowstTmprtVal() );
        cwInspectionReport.setDtopTmprtVal( createReport.getDtopTmprtVal() );
        cwInspectionReport.setAcmltPlanBohalRate( createReport.getAcmltPlanBohalRate() );
        cwInspectionReport.setAcmltArsltBohalRate( createReport.getAcmltArsltBohalRate() );
        cwInspectionReport.setAcmltProcess( createReport.getAcmltProcess() );
        cwInspectionReport.setTodayPlanBohalRate( createReport.getTodayPlanBohalRate() );
        cwInspectionReport.setTodayArsltBohalRate( createReport.getTodayArsltBohalRate() );
        cwInspectionReport.setTodayProcess( createReport.getTodayProcess() );
        cwInspectionReport.setMajorMatter( createReport.getMajorMatter() );
        cwInspectionReport.setSignificantNote( createReport.getSignificantNote() );
        cwInspectionReport.setCommentResult( createReport.getCommentResult() );

        return cwInspectionReport;
    }

    @Override
    public CwInspectionReportActivity toEntity(CreateActivity createReport) {
        if ( createReport == null ) {
            return null;
        }

        CwInspectionReportActivity cwInspectionReportActivity = new CwInspectionReportActivity();

        cwInspectionReportActivity.setCntrctNo( createReport.getCntrctNo() );
        if ( createReport.getDailyReportId() != null ) {
            cwInspectionReportActivity.setDailyReportId( createReport.getDailyReportId().longValue() );
        }
        cwInspectionReportActivity.setDailyActivityId( createReport.getDailyActivityId() );
        cwInspectionReportActivity.setWbsCd( createReport.getWbsCd() );
        cwInspectionReportActivity.setActivityId( createReport.getActivityId() );
        cwInspectionReportActivity.setActivityNm( createReport.getActivityNm() );
        cwInspectionReportActivity.setWorkDtType( createReport.getWorkDtType() );
        cwInspectionReportActivity.setPlanBgnDate( createReport.getPlanBgnDate() );
        cwInspectionReportActivity.setPlanEndDate( createReport.getPlanEndDate() );
        cwInspectionReportActivity.setPlanReqreDaynum( createReport.getPlanReqreDaynum() );
        cwInspectionReportActivity.setActualBgnDate( createReport.getActualBgnDate() );
        cwInspectionReportActivity.setActualEndDate( createReport.getActualEndDate() );
        cwInspectionReportActivity.setActualReqreDaynum( createReport.getActualReqreDaynum() );
        cwInspectionReportActivity.setTodayPlanRate( createReport.getTodayPlanRate() );
        cwInspectionReportActivity.setTodayExeRate( createReport.getTodayExeRate() );
        cwInspectionReportActivity.setInspectionItem( createReport.getInspectionItem() );
        cwInspectionReportActivity.setInspectionNote( createReport.getInspectionNote() );
        cwInspectionReportActivity.setCntrctChgId( createReport.getCntrctChgId() );
        cwInspectionReportActivity.setRevisionId( createReport.getRevisionId() );

        return cwInspectionReportActivity;
    }

    @Override
    public CwInspectionReportPhoto toEntity(Photo photo) {
        if ( photo == null ) {
            return null;
        }

        CwInspectionReportPhoto cwInspectionReportPhoto = new CwInspectionReportPhoto();

        cwInspectionReportPhoto.setCntrctNo( photo.getCntrctNo() );
        cwInspectionReportPhoto.setDailyReportId( photo.getDailyReportId() );
        cwInspectionReportPhoto.setCnsttyPhtSno( photo.getCnsttyPhtSno() );
        cwInspectionReportPhoto.setActivityId( photo.getActivityId() );
        cwInspectionReportPhoto.setTitlNm( photo.getTitlNm() );
        cwInspectionReportPhoto.setDscrpt( photo.getDscrpt() );
        cwInspectionReportPhoto.setShotDate( photo.getShotDate() );

        return cwInspectionReportPhoto;
    }

    @Override
    public void updateReport(CreateReport report, CwInspectionReport inspectionreport) {
        if ( report == null ) {
            return;
        }

        if ( report.getCntrctNo() != null ) {
            inspectionreport.setCntrctNo( report.getCntrctNo() );
        }
        if ( report.getDailyReportId() != null ) {
            inspectionreport.setDailyReportId( report.getDailyReportId() );
        }
        if ( report.getDailyReportDate() != null ) {
            inspectionreport.setDailyReportDate( report.getDailyReportDate() );
        }
        if ( report.getReportNo() != null ) {
            inspectionreport.setReportNo( report.getReportNo() );
        }
        if ( report.getTitle() != null ) {
            inspectionreport.setTitle( report.getTitle() );
        }
        if ( report.getAmWthr() != null ) {
            inspectionreport.setAmWthr( report.getAmWthr() );
        }
        if ( report.getPmWthr() != null ) {
            inspectionreport.setPmWthr( report.getPmWthr() );
        }
        if ( report.getPrcptRate() != null ) {
            inspectionreport.setPrcptRate( report.getPrcptRate() );
        }
        if ( report.getSnowRate() != null ) {
            inspectionreport.setSnowRate( report.getSnowRate() );
        }
        if ( report.getDlowstTmprtVal() != null ) {
            inspectionreport.setDlowstTmprtVal( report.getDlowstTmprtVal() );
        }
        if ( report.getDtopTmprtVal() != null ) {
            inspectionreport.setDtopTmprtVal( report.getDtopTmprtVal() );
        }
        if ( report.getAcmltPlanBohalRate() != null ) {
            inspectionreport.setAcmltPlanBohalRate( report.getAcmltPlanBohalRate() );
        }
        if ( report.getAcmltArsltBohalRate() != null ) {
            inspectionreport.setAcmltArsltBohalRate( report.getAcmltArsltBohalRate() );
        }
        if ( report.getAcmltProcess() != null ) {
            inspectionreport.setAcmltProcess( report.getAcmltProcess() );
        }
        if ( report.getTodayPlanBohalRate() != null ) {
            inspectionreport.setTodayPlanBohalRate( report.getTodayPlanBohalRate() );
        }
        if ( report.getTodayArsltBohalRate() != null ) {
            inspectionreport.setTodayArsltBohalRate( report.getTodayArsltBohalRate() );
        }
        if ( report.getTodayProcess() != null ) {
            inspectionreport.setTodayProcess( report.getTodayProcess() );
        }
        if ( report.getMajorMatter() != null ) {
            inspectionreport.setMajorMatter( report.getMajorMatter() );
        }
        if ( report.getSignificantNote() != null ) {
            inspectionreport.setSignificantNote( report.getSignificantNote() );
        }
        if ( report.getCommentResult() != null ) {
            inspectionreport.setCommentResult( report.getCommentResult() );
        }
    }

    @Override
    public void updateReportActivity(CreateActivity activity, CwInspectionReportActivity inspectionreportActivity) {
        if ( activity == null ) {
            return;
        }

        if ( activity.getCntrctNo() != null ) {
            inspectionreportActivity.setCntrctNo( activity.getCntrctNo() );
        }
        if ( activity.getDailyReportId() != null ) {
            inspectionreportActivity.setDailyReportId( activity.getDailyReportId().longValue() );
        }
        if ( activity.getDailyActivityId() != null ) {
            inspectionreportActivity.setDailyActivityId( activity.getDailyActivityId() );
        }
        if ( activity.getWbsCd() != null ) {
            inspectionreportActivity.setWbsCd( activity.getWbsCd() );
        }
        if ( activity.getActivityId() != null ) {
            inspectionreportActivity.setActivityId( activity.getActivityId() );
        }
        if ( activity.getActivityNm() != null ) {
            inspectionreportActivity.setActivityNm( activity.getActivityNm() );
        }
        if ( activity.getWorkDtType() != null ) {
            inspectionreportActivity.setWorkDtType( activity.getWorkDtType() );
        }
        if ( activity.getPlanBgnDate() != null ) {
            inspectionreportActivity.setPlanBgnDate( activity.getPlanBgnDate() );
        }
        if ( activity.getPlanEndDate() != null ) {
            inspectionreportActivity.setPlanEndDate( activity.getPlanEndDate() );
        }
        if ( activity.getPlanReqreDaynum() != null ) {
            inspectionreportActivity.setPlanReqreDaynum( activity.getPlanReqreDaynum() );
        }
        if ( activity.getActualBgnDate() != null ) {
            inspectionreportActivity.setActualBgnDate( activity.getActualBgnDate() );
        }
        if ( activity.getActualEndDate() != null ) {
            inspectionreportActivity.setActualEndDate( activity.getActualEndDate() );
        }
        if ( activity.getActualReqreDaynum() != null ) {
            inspectionreportActivity.setActualReqreDaynum( activity.getActualReqreDaynum() );
        }
        if ( activity.getTodayPlanRate() != null ) {
            inspectionreportActivity.setTodayPlanRate( activity.getTodayPlanRate() );
        }
        if ( activity.getTodayExeRate() != null ) {
            inspectionreportActivity.setTodayExeRate( activity.getTodayExeRate() );
        }
        if ( activity.getInspectionItem() != null ) {
            inspectionreportActivity.setInspectionItem( activity.getInspectionItem() );
        }
        if ( activity.getInspectionNote() != null ) {
            inspectionreportActivity.setInspectionNote( activity.getInspectionNote() );
        }
        if ( activity.getCntrctChgId() != null ) {
            inspectionreportActivity.setCntrctChgId( activity.getCntrctChgId() );
        }
        if ( activity.getRevisionId() != null ) {
            inspectionreportActivity.setRevisionId( activity.getRevisionId() );
        }
    }
}
