package kr.co.ideait.platform.gaiacairos.core.persistence.vo.progress.wbs.activity;

import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-07-23T18:31:16+0900",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.5 (Amazon.com Inc.)"
)
@Component
public class ActivityFormImpl implements ActivityForm {

    @Override
    public ActivityMybatisParam.ActivityListInput toActivityListInput(ActivityListGet activityListGet) {
        if ( activityListGet == null ) {
            return null;
        }

        ActivityMybatisParam.ActivityListInput activityListInput = new ActivityMybatisParam.ActivityListInput();

        activityListInput.setWbsCd( activityListGet.getWbsCd() );
        activityListInput.setCntrctChgId( activityListGet.getCntrctChgId() );
        activityListInput.setSearchType( activityListGet.getSearchType() );
        activityListInput.setSearchText( activityListGet.getSearchText() );
        activityListInput.setSearchTerm( activityListGet.getSearchTerm() );
        activityListInput.setStartDate( activityListGet.getStartDate() );
        activityListInput.setEndDate( activityListGet.getEndDate() );
        activityListInput.setWbsNm( activityListGet.getWbsNm() );
        activityListInput.setActivityId( activityListGet.getActivityId() );
        activityListInput.setActivityNm( activityListGet.getActivityNm() );

        return activityListInput;
    }
}
