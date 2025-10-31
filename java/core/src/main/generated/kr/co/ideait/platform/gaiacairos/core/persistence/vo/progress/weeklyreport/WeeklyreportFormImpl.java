package kr.co.ideait.platform.gaiacairos.core.persistence.vo.progress.weeklyreport;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.PrWeeklyReportActivity;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-07-23T18:31:17+0900",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.5 (Amazon.com Inc.)"
)
@Component
public class WeeklyreportFormImpl implements WeeklyreportForm {

    @Override
    public WeeklyreportMybatisParam.UpdateWeeklyActivityInput toUpdateWeeklyActivityInput(WeeklyreportActivityUpdate weeklyreportActivityUpdate) {
        if ( weeklyreportActivityUpdate == null ) {
            return null;
        }

        WeeklyreportMybatisParam.UpdateWeeklyActivityInput updateWeeklyActivityInput = new WeeklyreportMybatisParam.UpdateWeeklyActivityInput();

        updateWeeklyActivityInput.setModalType( weeklyreportActivityUpdate.getModalType() );
        updateWeeklyActivityInput.setDelActivityList( weeklyreportActivityDeleteListToDeleteWeeklyActivityListList( weeklyreportActivityUpdate.getDelActivityList() ) );
        List<PrWeeklyReportActivity> list1 = weeklyreportActivityUpdate.getAddActivityList();
        if ( list1 != null ) {
            updateWeeklyActivityInput.setAddActivityList( new ArrayList<PrWeeklyReportActivity>( list1 ) );
        }
        List<PrWeeklyReportActivity> list2 = weeklyreportActivityUpdate.getUpdateActivityList();
        if ( list2 != null ) {
            updateWeeklyActivityInput.setUpdateActivityList( new ArrayList<PrWeeklyReportActivity>( list2 ) );
        }

        return updateWeeklyActivityInput;
    }

    @Override
    public List<WeeklyreportMybatisParam.UpdateWeeklyreportInput> toUpdateWeeklyreportInput(List<WeeklyreportDelete> weeklyreportDelete) {
        if ( weeklyreportDelete == null ) {
            return null;
        }

        List<WeeklyreportMybatisParam.UpdateWeeklyreportInput> list = new ArrayList<WeeklyreportMybatisParam.UpdateWeeklyreportInput>( weeklyreportDelete.size() );
        for ( WeeklyreportDelete weeklyreportDelete1 : weeklyreportDelete ) {
            list.add( weeklyreportDeleteToUpdateWeeklyreportInput( weeklyreportDelete1 ) );
        }

        return list;
    }

    @Override
    public List<WeeklyreportMybatisParam.UpdateWeeklyProgress> toUpdateWeeklyProgressList(List<WeeklyreportProgressUpdate> weeklyreportProgressUpdateList) {
        if ( weeklyreportProgressUpdateList == null ) {
            return null;
        }

        List<WeeklyreportMybatisParam.UpdateWeeklyProgress> list = new ArrayList<WeeklyreportMybatisParam.UpdateWeeklyProgress>( weeklyreportProgressUpdateList.size() );
        for ( WeeklyreportProgressUpdate weeklyreportProgressUpdate : weeklyreportProgressUpdateList ) {
            list.add( weeklyreportProgressUpdateToUpdateWeeklyProgress( weeklyreportProgressUpdate ) );
        }

        return list;
    }

    protected WeeklyreportMybatisParam.DeleteWeeklyActivityList weeklyreportActivityDeleteToDeleteWeeklyActivityList(WeeklyreportActivityDelete weeklyreportActivityDelete) {
        if ( weeklyreportActivityDelete == null ) {
            return null;
        }

        WeeklyreportMybatisParam.DeleteWeeklyActivityList deleteWeeklyActivityList = new WeeklyreportMybatisParam.DeleteWeeklyActivityList();

        deleteWeeklyActivityList.setCntrctChgId( weeklyreportActivityDelete.getCntrctChgId() );
        deleteWeeklyActivityList.setWeeklyReportId( weeklyreportActivityDelete.getWeeklyReportId() );
        deleteWeeklyActivityList.setWeeklyActivityId( weeklyreportActivityDelete.getWeeklyActivityId() );

        return deleteWeeklyActivityList;
    }

    protected List<WeeklyreportMybatisParam.DeleteWeeklyActivityList> weeklyreportActivityDeleteListToDeleteWeeklyActivityListList(List<WeeklyreportActivityDelete> list) {
        if ( list == null ) {
            return null;
        }

        List<WeeklyreportMybatisParam.DeleteWeeklyActivityList> list1 = new ArrayList<WeeklyreportMybatisParam.DeleteWeeklyActivityList>( list.size() );
        for ( WeeklyreportActivityDelete weeklyreportActivityDelete : list ) {
            list1.add( weeklyreportActivityDeleteToDeleteWeeklyActivityList( weeklyreportActivityDelete ) );
        }

        return list1;
    }

    protected WeeklyreportMybatisParam.UpdateWeeklyreportInput weeklyreportDeleteToUpdateWeeklyreportInput(WeeklyreportDelete weeklyreportDelete) {
        if ( weeklyreportDelete == null ) {
            return null;
        }

        WeeklyreportMybatisParam.UpdateWeeklyreportInput updateWeeklyreportInput = new WeeklyreportMybatisParam.UpdateWeeklyreportInput();

        updateWeeklyreportInput.setCntrctChgId( weeklyreportDelete.getCntrctChgId() );
        updateWeeklyreportInput.setWeeklyReportId( weeklyreportDelete.getWeeklyReportId() );

        return updateWeeklyreportInput;
    }

    protected WeeklyreportMybatisParam.UpdateWeeklyProgress weeklyreportProgressUpdateToUpdateWeeklyProgress(WeeklyreportProgressUpdate weeklyreportProgressUpdate) {
        if ( weeklyreportProgressUpdate == null ) {
            return null;
        }

        WeeklyreportMybatisParam.UpdateWeeklyProgress updateWeeklyProgress = new WeeklyreportMybatisParam.UpdateWeeklyProgress();

        updateWeeklyProgress.setWeeklyCnsttyId( weeklyreportProgressUpdate.getWeeklyCnsttyId() );
        updateWeeklyProgress.setRmk( weeklyreportProgressUpdate.getRmk() );

        return updateWeeklyProgress;
    }
}
