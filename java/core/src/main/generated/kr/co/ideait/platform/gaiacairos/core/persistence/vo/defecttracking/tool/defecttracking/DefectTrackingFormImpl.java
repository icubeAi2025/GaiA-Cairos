package kr.co.ideait.platform.gaiacairos.core.persistence.vo.defecttracking.tool.defecttracking;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.DtDeficiency;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.DtDeficiencyActivity;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-07-23T18:31:18+0900",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.5 (Amazon.com Inc.)"
)
@Component
public class DefectTrackingFormImpl implements DefectTrackingForm {

    @Override
    public DefectTrackingMybatisParam.DefectTrackingListInput toDefectTrackingListInput(DefectTrackingListGet defectTrackingListGet) {
        if ( defectTrackingListGet == null ) {
            return null;
        }

        DefectTrackingMybatisParam.DefectTrackingListInput defectTrackingListInput = new DefectTrackingMybatisParam.DefectTrackingListInput();

        defectTrackingListInput.setPageable( defectTrackingListGet.getPageable() );
        defectTrackingListInput.setPage( defectTrackingListGet.getPage() );
        if ( defectTrackingListGet.getSize() != null ) {
            defectTrackingListInput.setSize( defectTrackingListGet.getSize() );
        }
        defectTrackingListInput.setDfccyPhaseNo( defectTrackingListGet.getDfccyPhaseNo() );
        defectTrackingListInput.setCntrctNo( defectTrackingListGet.getCntrctNo() );

        return defectTrackingListInput;
    }

    @Override
    public DefectTrackingMybatisParam.DfccySearchInput toDfccySearchInput(DefectTrackingListGet defectTrackingListGet) {
        if ( defectTrackingListGet == null ) {
            return null;
        }

        DefectTrackingMybatisParam.DfccySearchInput dfccySearchInput = new DefectTrackingMybatisParam.DfccySearchInput();

        dfccySearchInput.setRgstr( defectTrackingListGet.getRgstr() );
        dfccySearchInput.setDfccyCd( defectTrackingListGet.getDfccyCd() );
        dfccySearchInput.setKeyword( defectTrackingListGet.getKeyword() );
        dfccySearchInput.setRgstrMy( defectTrackingListGet.getRgstrMy() );
        dfccySearchInput.setRgstrNm( defectTrackingListGet.getRgstrNm() );
        dfccySearchInput.setStartDfccyNo( defectTrackingListGet.getStartDfccyNo() );
        dfccySearchInput.setEndDfccyNo( defectTrackingListGet.getEndDfccyNo() );
        dfccySearchInput.setActivityNm( defectTrackingListGet.getActivityNm() );
        dfccySearchInput.setRplyStatus( defectTrackingListGet.getRplyStatus() );
        dfccySearchInput.setRplyCd( defectTrackingListGet.getRplyCd() );
        dfccySearchInput.setQaStatus( defectTrackingListGet.getQaStatus() );
        dfccySearchInput.setQaCd( defectTrackingListGet.getQaCd() );
        dfccySearchInput.setSpvsStatus( defectTrackingListGet.getSpvsStatus() );
        dfccySearchInput.setSpvsCd( defectTrackingListGet.getSpvsCd() );
        dfccySearchInput.setEdCd( defectTrackingListGet.getEdCd() );
        dfccySearchInput.setEdStatus( defectTrackingListGet.getEdStatus() );
        dfccySearchInput.setStartRgstDt( defectTrackingListGet.getStartRgstDt() );
        dfccySearchInput.setEndRgstDt( defectTrackingListGet.getEndRgstDt() );
        dfccySearchInput.setAtachYn( defectTrackingListGet.getAtachYn() );
        dfccySearchInput.setCrtcIsueYn( defectTrackingListGet.getCrtcIsueYn() );
        dfccySearchInput.setMyRplyYn( defectTrackingListGet.getMyRplyYn() );
        dfccySearchInput.setStartRplyRecentDt( defectTrackingListGet.getStartRplyRecentDt() );
        dfccySearchInput.setEndRplyRecentDt( defectTrackingListGet.getEndRplyRecentDt() );
        dfccySearchInput.setPriorityCheck( defectTrackingListGet.getPriorityCheck() );

        return dfccySearchInput;
    }

    @Override
    public DtDeficiency toDeficiency(CreateUpdateDfccy deficiency) {
        if ( deficiency == null ) {
            return null;
        }

        DtDeficiency dtDeficiency = new DtDeficiency();

        dtDeficiency.setDfccyNo( deficiency.getDfccyNo() );
        dtDeficiency.setCntrctNo( deficiency.getCntrctNo() );
        dtDeficiency.setDfccyPhaseNo( deficiency.getDfccyPhaseNo() );
        dtDeficiency.setTitle( deficiency.getTitle() );
        dtDeficiency.setDfccyCd( deficiency.getDfccyCd() );
        dtDeficiency.setDfccyLct( deficiency.getDfccyLct() );
        dtDeficiency.setCrtcIsueYn( deficiency.getCrtcIsueYn() );
        dtDeficiency.setDfccyCntnts( deficiency.getDfccyCntnts() );
        dtDeficiency.setPriorityCheck( deficiency.getPriorityCheck() );

        return dtDeficiency;
    }

    @Override
    public DtDeficiencyActivity toDeficiencyActivity(Activity activity) {
        if ( activity == null ) {
            return null;
        }

        DtDeficiencyActivity dtDeficiencyActivity = new DtDeficiencyActivity();

        dtDeficiencyActivity.setWbsCd( activity.getWbsCd() );
        dtDeficiencyActivity.setActivityId( activity.getActivityId() );

        return dtDeficiencyActivity;
    }

    @Override
    public List<DtDeficiencyActivity> toDeficiencyActivityList(List<Activity> activity) {
        if ( activity == null ) {
            return null;
        }

        List<DtDeficiencyActivity> list = new ArrayList<DtDeficiencyActivity>( activity.size() );
        for ( Activity activity1 : activity ) {
            list.add( toDeficiencyActivity( activity1 ) );
        }

        return list;
    }

    @Override
    public DtDeficiency updateDeficiency(CreateUpdateDfccy update, DtDeficiency oldDeficiency) {
        if ( update == null ) {
            return oldDeficiency;
        }

        if ( update.getDfccyNo() != null ) {
            oldDeficiency.setDfccyNo( update.getDfccyNo() );
        }
        if ( update.getCntrctNo() != null ) {
            oldDeficiency.setCntrctNo( update.getCntrctNo() );
        }
        if ( update.getDfccyPhaseNo() != null ) {
            oldDeficiency.setDfccyPhaseNo( update.getDfccyPhaseNo() );
        }
        if ( update.getTitle() != null ) {
            oldDeficiency.setTitle( update.getTitle() );
        }
        if ( update.getDfccyCd() != null ) {
            oldDeficiency.setDfccyCd( update.getDfccyCd() );
        }
        if ( update.getDfccyLct() != null ) {
            oldDeficiency.setDfccyLct( update.getDfccyLct() );
        }
        if ( update.getCrtcIsueYn() != null ) {
            oldDeficiency.setCrtcIsueYn( update.getCrtcIsueYn() );
        }
        if ( update.getDfccyCntnts() != null ) {
            oldDeficiency.setDfccyCntnts( update.getDfccyCntnts() );
        }
        oldDeficiency.setPriorityCheck( update.getPriorityCheck() );

        return oldDeficiency;
    }

    @Override
    public void updateActivity(Activity activity, DtDeficiencyActivity deficiencyActivity) {
        if ( activity == null ) {
            return;
        }

        if ( activity.getWbsCd() != null ) {
            deficiencyActivity.setWbsCd( activity.getWbsCd() );
        }
        if ( activity.getActivityId() != null ) {
            deficiencyActivity.setActivityId( activity.getActivityId() );
        }
    }
}
