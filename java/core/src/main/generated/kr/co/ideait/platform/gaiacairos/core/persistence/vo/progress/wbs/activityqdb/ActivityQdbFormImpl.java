package kr.co.ideait.platform.gaiacairos.core.persistence.vo.progress.wbs.activityqdb;

import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-07-23T18:31:17+0900",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.5 (Amazon.com Inc.)"
)
@Component
public class ActivityQdbFormImpl implements ActivityQdbForm {

    @Override
    public ActivityQdbMybatisParam.ActivityWbsListInput toActivityWbsListInput(ActivityWbsList activityWbsList) {
        if ( activityWbsList == null ) {
            return null;
        }

        ActivityQdbMybatisParam.ActivityWbsListInput activityWbsListInput = new ActivityQdbMybatisParam.ActivityWbsListInput();

        activityWbsListInput.setCntrctChgId( activityWbsList.getCntrctChgId() );
        activityWbsListInput.setWbsCd( activityWbsList.getWbsCd() );
        activityWbsListInput.setSearchText( activityWbsList.getSearchText() );

        return activityWbsListInput;
    }

    @Override
    public ActivityQdbMybatisParam.ActivityWbsQdbListInput toActivityWbsQdbListInput(ActivityWbsQdbList activityWbsQdbList) {
        if ( activityWbsQdbList == null ) {
            return null;
        }

        ActivityQdbMybatisParam.ActivityWbsQdbListInput activityWbsQdbListInput = new ActivityQdbMybatisParam.ActivityWbsQdbListInput();

        activityWbsQdbListInput.setCntrctChgId( activityWbsQdbList.getCntrctChgId() );
        activityWbsQdbListInput.setActivityId( activityWbsQdbList.getActivityId() );
        activityWbsQdbListInput.setSearchText( activityWbsQdbList.getSearchText() );

        return activityWbsQdbListInput;
    }

    @Override
    public ActivityQdbMybatisParam.ActivityCbsListInput toActivityCbsListInput(ActivityCbsList activityCbsList) {
        if ( activityCbsList == null ) {
            return null;
        }

        ActivityQdbMybatisParam.ActivityCbsListInput activityCbsListInput = new ActivityQdbMybatisParam.ActivityCbsListInput();

        activityCbsListInput.setCntrctChgId( activityCbsList.getCntrctChgId() );
        activityCbsListInput.setUnitCnstType( activityCbsList.getUnitCnstType() );
        activityCbsListInput.setCnsttyCd( activityCbsList.getCnsttyCd() );
        activityCbsListInput.setSearchText( activityCbsList.getSearchText() );

        return activityCbsListInput;
    }

    @Override
    public ActivityQdbMybatisParam.ActivityCbsQdbListInput toActivityCbsQdbListInput(ActivityCbsQdbList activityCbsQdbList) {
        if ( activityCbsQdbList == null ) {
            return null;
        }

        ActivityQdbMybatisParam.ActivityCbsQdbListInput activityCbsQdbListInput = new ActivityQdbMybatisParam.ActivityCbsQdbListInput();

        activityCbsQdbListInput.setCntrctChgId( activityCbsQdbList.getCntrctChgId() );
        activityCbsQdbListInput.setCnsttySn( activityCbsQdbList.getCnsttySn() );
        activityCbsQdbListInput.setDtlCnsttySn( activityCbsQdbList.getDtlCnsttySn() );
        activityCbsQdbListInput.setSearchText( activityCbsQdbList.getSearchText() );

        return activityCbsQdbListInput;
    }

    @Override
    public ActivityQdbMybatisParam.ActivityTreeListInput toActivityTreeListInput(ActivityTreeList activityTreeList) {
        if ( activityTreeList == null ) {
            return null;
        }

        ActivityQdbMybatisParam.ActivityTreeListInput activityTreeListInput = new ActivityQdbMybatisParam.ActivityTreeListInput();

        activityTreeListInput.setCntrctChgId( activityTreeList.getCntrctChgId() );

        return activityTreeListInput;
    }
}
