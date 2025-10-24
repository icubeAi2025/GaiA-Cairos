package kr.co.ideait.platform.gaiacairos.core.persistence.vo.construction.qualityinspection;

import javax.annotation.processing.Generated;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CwCntqltyCheckList;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CwQualityActivity;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CwQualityCheckList;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CwQualityInspection;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CwQualityPhoto;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-07-23T18:31:18+0900",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.5 (Amazon.com Inc.)"
)
@Component
public class QualityinspectionFormImpl implements QualityinspectionForm {

    @Override
    public CwQualityInspection toEntity(CreateQuality createQuality) {
        if ( createQuality == null ) {
            return null;
        }

        CwQualityInspection cwQualityInspection = new CwQualityInspection();

        cwQualityInspection.setIspReqDt( QualityinspectionForm.stringToLocalDateTime( createQuality.getIspReqDt() ) );
        cwQualityInspection.setCntrctNo( createQuality.getCntrctNo() );
        cwQualityInspection.setIspDocNo( createQuality.getIspDocNo() );
        cwQualityInspection.setIspLct( createQuality.getIspLct() );
        cwQualityInspection.setCnsttyCd( createQuality.getCnsttyCd() );
        cwQualityInspection.setCnsttyCdL1( createQuality.getCnsttyCdL1() );
        cwQualityInspection.setCnsttyCdL2( createQuality.getCnsttyCdL2() );
        cwQualityInspection.setIspPart( createQuality.getIspPart() );
        cwQualityInspection.setIspIssue( createQuality.getIspIssue() );

        return cwQualityInspection;
    }

    @Override
    public CwQualityActivity toEntity(Activity activity) {
        if ( activity == null ) {
            return null;
        }

        CwQualityActivity cwQualityActivity = new CwQualityActivity();

        cwQualityActivity.setWbsCd( activity.getWbsCd() );
        cwQualityActivity.setActivityId( activity.getActivityId() );

        return cwQualityActivity;
    }

    @Override
    public CwQualityCheckList toEntity(CheckList checklist) {
        if ( checklist == null ) {
            return null;
        }

        CwQualityCheckList cwQualityCheckList = new CwQualityCheckList();

        cwQualityCheckList.setChklstId( checklist.getChklstId() );
        cwQualityCheckList.setChklstSno( checklist.getChklstSno() );
        cwQualityCheckList.setChklstBssCd( checklist.getChklstBssCd() );
        cwQualityCheckList.setCnstrtnYn( checklist.getCnstrtnYn() );
        cwQualityCheckList.setCqcYn( checklist.getCqcYn() );
        cwQualityCheckList.setActnDscrpt( checklist.getActnDscrpt() );

        return cwQualityCheckList;
    }

    @Override
    public CwCntqltyCheckList toEntity(CreateCntCheckList checkList) {
        if ( checkList == null ) {
            return null;
        }

        CwCntqltyCheckList cwCntqltyCheckList = new CwCntqltyCheckList();

        cwCntqltyCheckList.setCntrctNo( checkList.getCntrctNo() );
        cwCntqltyCheckList.setChklstId( checkList.getChklstId() );
        cwCntqltyCheckList.setCnsttyYn( checkList.getCnsttyYn() );
        cwCntqltyCheckList.setCnsttyCd( checkList.getCnsttyCd() );
        cwCntqltyCheckList.setCnsttyNm( checkList.getCnsttyNm() );
        if ( checkList.getCnsttyLvl() != null ) {
            cwCntqltyCheckList.setCnsttyLvl( checkList.getCnsttyLvl().shortValue() );
        }
        cwCntqltyCheckList.setUpCnsttyCd( checkList.getUpCnsttyCd() );
        cwCntqltyCheckList.setChklstSno( checkList.getChklstSno() );
        cwCntqltyCheckList.setChklstDscrpt( checkList.getChklstDscrpt() );
        cwCntqltyCheckList.setChklstBssCd( checkList.getChklstBssCd() );

        return cwCntqltyCheckList;
    }

    @Override
    public CwQualityPhoto toEntity(Photo photo) {
        if ( photo == null ) {
            return null;
        }

        CwQualityPhoto cwQualityPhoto = new CwQualityPhoto();

        cwQualityPhoto.setShotDate( QualityinspectionForm.stringToLocalDateTime( photo.getShotDate() ) );
        cwQualityPhoto.setPhtSno( photo.getPhtSno() );
        cwQualityPhoto.setTitlNm( photo.getTitlNm() );
        cwQualityPhoto.setDscrpt( photo.getDscrpt() );

        return cwQualityPhoto;
    }

    @Override
    public void updateQuality(UpdateQuality quality, CwQualityInspection qualityInspection) {
        if ( quality == null ) {
            return;
        }

        if ( quality.getIspReqDt() != null ) {
            qualityInspection.setIspReqDt( QualityinspectionForm.stringToLocalDateTime( quality.getIspReqDt() ) );
        }
        if ( quality.getRsltDt() != null ) {
            qualityInspection.setRsltDt( QualityinspectionForm.stringToLocalDateTime( quality.getRsltDt() ) );
        }
        if ( quality.getApprvlDt() != null ) {
            qualityInspection.setApprvlDt( QualityinspectionForm.stringToLocalDateTime( quality.getApprvlDt() ) );
        }
        if ( quality.getQltyIspId() != null ) {
            qualityInspection.setQltyIspId( quality.getQltyIspId() );
        }
        if ( quality.getCntrctNo() != null ) {
            qualityInspection.setCntrctNo( quality.getCntrctNo() );
        }
        if ( quality.getIspDocNo() != null ) {
            qualityInspection.setIspDocNo( quality.getIspDocNo() );
        }
        if ( quality.getIspLct() != null ) {
            qualityInspection.setIspLct( quality.getIspLct() );
        }
        if ( quality.getCnsttyCd() != null ) {
            qualityInspection.setCnsttyCd( quality.getCnsttyCd() );
        }
        if ( quality.getCnsttyCdL1() != null ) {
            qualityInspection.setCnsttyCdL1( quality.getCnsttyCdL1() );
        }
        if ( quality.getCnsttyCdL2() != null ) {
            qualityInspection.setCnsttyCdL2( quality.getCnsttyCdL2() );
        }
        if ( quality.getIspPart() != null ) {
            qualityInspection.setIspPart( quality.getIspPart() );
        }
        if ( quality.getIspIssue() != null ) {
            qualityInspection.setIspIssue( quality.getIspIssue() );
        }
        if ( quality.getRsltDocNo() != null ) {
            qualityInspection.setRsltDocNo( quality.getRsltDocNo() );
        }
        if ( quality.getRsltCd() != null ) {
            qualityInspection.setRsltCd( quality.getRsltCd() );
        }
        if ( quality.getOrdeOpnin() != null ) {
            qualityInspection.setOrdeOpnin( quality.getOrdeOpnin() );
        }
        if ( quality.getApprvlId() != null ) {
            qualityInspection.setApprvlId( quality.getApprvlId() );
        }
        if ( quality.getApprvlStats() != null ) {
            qualityInspection.setApprvlStats( quality.getApprvlStats() );
        }
    }

    @Override
    public void updateActivity(Activity activity, CwQualityActivity qualityActivity) {
        if ( activity == null ) {
            return;
        }

        if ( activity.getWbsCd() != null ) {
            qualityActivity.setWbsCd( activity.getWbsCd() );
        }
        if ( activity.getActivityId() != null ) {
            qualityActivity.setActivityId( activity.getActivityId() );
        }
    }

    @Override
    public void updateCheckList(CheckList checkList, CwQualityCheckList qualityCheckList) {
        if ( checkList == null ) {
            return;
        }

        if ( checkList.getChklstId() != null ) {
            qualityCheckList.setChklstId( checkList.getChklstId() );
        }
        qualityCheckList.setChklstSno( checkList.getChklstSno() );
        if ( checkList.getChklstBssCd() != null ) {
            qualityCheckList.setChklstBssCd( checkList.getChklstBssCd() );
        }
        if ( checkList.getCnstrtnYn() != null ) {
            qualityCheckList.setCnstrtnYn( checkList.getCnstrtnYn() );
        }
        if ( checkList.getCqcYn() != null ) {
            qualityCheckList.setCqcYn( checkList.getCqcYn() );
        }
        if ( checkList.getActnDscrpt() != null ) {
            qualityCheckList.setActnDscrpt( checkList.getActnDscrpt() );
        }
    }
}
