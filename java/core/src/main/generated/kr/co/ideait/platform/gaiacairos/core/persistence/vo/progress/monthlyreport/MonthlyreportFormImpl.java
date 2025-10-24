package kr.co.ideait.platform.gaiacairos.core.persistence.vo.progress.monthlyreport;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.PrMonthlyReportActivity;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-07-23T18:31:17+0900",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.5 (Amazon.com Inc.)"
)
@Component
public class MonthlyreportFormImpl implements MonthlyreportForm {

    @Override
    public MonthlyreportMybatisParam.MonthlyreportListInput toMonthlyreportListInput(MonthlyreportList monthlyreportList) {
        if ( monthlyreportList == null ) {
            return null;
        }

        MonthlyreportMybatisParam.MonthlyreportListInput monthlyreportListInput = new MonthlyreportMybatisParam.MonthlyreportListInput();

        monthlyreportListInput.setCntrctNo( monthlyreportList.getCntrctNo() );
        monthlyreportListInput.setApprvlStats( monthlyreportList.getApprvlStats() );
        monthlyreportListInput.setSearchText( monthlyreportList.getSearchText() );

        return monthlyreportListInput;
    }

    @Override
    public MonthlyreportMybatisParam.MonthlyActivityListInput toMonthlyActivityListInput(MonthlyreportActivityDetail monthlyreportActivityDetail) {
        if ( monthlyreportActivityDetail == null ) {
            return null;
        }

        MonthlyreportMybatisParam.MonthlyActivityListInput monthlyActivityListInput = new MonthlyreportMybatisParam.MonthlyActivityListInput();

        monthlyActivityListInput.setCntrctNo( monthlyreportActivityDetail.getCntrctNo() );
        monthlyActivityListInput.setReportYm( monthlyreportActivityDetail.getReportYm() );

        return monthlyActivityListInput;
    }

    @Override
    public List<MonthlyreportMybatisParam.UpdateMonthlyreportInput> toUpdateMonthlyreportInput(List<MonthlyreportUpdateDelete> monthlyreportDeleteList) {
        if ( monthlyreportDeleteList == null ) {
            return null;
        }

        List<MonthlyreportMybatisParam.UpdateMonthlyreportInput> list = new ArrayList<MonthlyreportMybatisParam.UpdateMonthlyreportInput>( monthlyreportDeleteList.size() );
        for ( MonthlyreportUpdateDelete monthlyreportUpdateDelete : monthlyreportDeleteList ) {
            list.add( monthlyreportUpdateDeleteToUpdateMonthlyreportInput( monthlyreportUpdateDelete ) );
        }

        return list;
    }

    @Override
    public MonthlyreportMybatisParam.SearchAddActivityInput toSearchAddActivityInput(SearchAddActivity searchAddActivity) {
        if ( searchAddActivity == null ) {
            return null;
        }

        MonthlyreportMybatisParam.SearchAddActivityInput searchAddActivityInput = new MonthlyreportMybatisParam.SearchAddActivityInput();

        searchAddActivityInput.setCntrctNo( searchAddActivity.getCntrctNo() );
        searchAddActivityInput.setCntrctChgId( searchAddActivity.getCntrctChgId() );
        searchAddActivityInput.setMonthlyReportId( searchAddActivity.getMonthlyReportId() );
        searchAddActivityInput.setSearchText( searchAddActivity.getSearchText() );
        searchAddActivityInput.setStartDate( searchAddActivity.getStartDate() );
        searchAddActivityInput.setEndDate( searchAddActivity.getEndDate() );
        searchAddActivityInput.setReportYm( searchAddActivity.getReportYm() );

        return searchAddActivityInput;
    }

    @Override
    public MonthlyreportMybatisParam.MonthlyreportActivityDetailInput toMonthlyreportActivityDetailInput(MonthlyreportActivityDetail monthlyreportActivityDetail) {
        if ( monthlyreportActivityDetail == null ) {
            return null;
        }

        MonthlyreportMybatisParam.MonthlyreportActivityDetailInput monthlyreportActivityDetailInput = new MonthlyreportMybatisParam.MonthlyreportActivityDetailInput();

        monthlyreportActivityDetailInput.setCntrctChgId( monthlyreportActivityDetail.getCntrctChgId() );
        monthlyreportActivityDetailInput.setCntrctNo( monthlyreportActivityDetail.getCntrctNo() );
        monthlyreportActivityDetailInput.setReportYm( monthlyreportActivityDetail.getReportYm() );
        monthlyreportActivityDetailInput.setMonthlyReportId( monthlyreportActivityDetail.getMonthlyReportId() );

        return monthlyreportActivityDetailInput;
    }

    @Override
    public MonthlyreportMybatisParam.UpdateActivityListInput toUpdateActivityListInput(MonthlyreportActivityUpdate monthlyreportActivityUpdateList) {
        if ( monthlyreportActivityUpdateList == null ) {
            return null;
        }

        MonthlyreportMybatisParam.UpdateActivityListInput updateActivityListInput = new MonthlyreportMybatisParam.UpdateActivityListInput();

        updateActivityListInput.setModalType( monthlyreportActivityUpdateList.getModalType() );
        updateActivityListInput.setDelActivityList( monthlyreportActivityDeleteListToDeleteActivityListList( monthlyreportActivityUpdateList.getDelActivityList() ) );
        List<PrMonthlyReportActivity> list1 = monthlyreportActivityUpdateList.getAddActivityList();
        if ( list1 != null ) {
            updateActivityListInput.setAddActivityList( new ArrayList<PrMonthlyReportActivity>( list1 ) );
        }
        List<PrMonthlyReportActivity> list2 = monthlyreportActivityUpdateList.getUpdateActivityList();
        if ( list2 != null ) {
            updateActivityListInput.setUpdateActivityList( new ArrayList<PrMonthlyReportActivity>( list2 ) );
        }

        return updateActivityListInput;
    }

    @Override
    public List<MonthlyreportMybatisParam.UpdateMonthlyProgress> toUpdateMonthlyProgressList(List<MonthlyreportProgressUpdate> monthlyreportProgressUpdateList) {
        if ( monthlyreportProgressUpdateList == null ) {
            return null;
        }

        List<MonthlyreportMybatisParam.UpdateMonthlyProgress> list = new ArrayList<MonthlyreportMybatisParam.UpdateMonthlyProgress>( monthlyreportProgressUpdateList.size() );
        for ( MonthlyreportProgressUpdate monthlyreportProgressUpdate : monthlyreportProgressUpdateList ) {
            list.add( monthlyreportProgressUpdateToUpdateMonthlyProgress( monthlyreportProgressUpdate ) );
        }

        return list;
    }

    protected MonthlyreportMybatisParam.UpdateMonthlyreportInput monthlyreportUpdateDeleteToUpdateMonthlyreportInput(MonthlyreportUpdateDelete monthlyreportUpdateDelete) {
        if ( monthlyreportUpdateDelete == null ) {
            return null;
        }

        MonthlyreportMybatisParam.UpdateMonthlyreportInput updateMonthlyreportInput = new MonthlyreportMybatisParam.UpdateMonthlyreportInput();

        updateMonthlyreportInput.setCntrctChgId( monthlyreportUpdateDelete.getCntrctChgId() );
        updateMonthlyreportInput.setMonthlyReportId( monthlyreportUpdateDelete.getMonthlyReportId() );

        return updateMonthlyreportInput;
    }

    protected MonthlyreportMybatisParam.DeleteActivityList monthlyreportActivityDeleteToDeleteActivityList(MonthlyreportActivityDelete monthlyreportActivityDelete) {
        if ( monthlyreportActivityDelete == null ) {
            return null;
        }

        MonthlyreportMybatisParam.DeleteActivityList deleteActivityList = new MonthlyreportMybatisParam.DeleteActivityList();

        deleteActivityList.setCntrctChgId( monthlyreportActivityDelete.getCntrctChgId() );
        deleteActivityList.setMonthlyReportId( monthlyreportActivityDelete.getMonthlyReportId() );
        deleteActivityList.setMonthlyActivityId( monthlyreportActivityDelete.getMonthlyActivityId() );

        return deleteActivityList;
    }

    protected List<MonthlyreportMybatisParam.DeleteActivityList> monthlyreportActivityDeleteListToDeleteActivityListList(List<MonthlyreportActivityDelete> list) {
        if ( list == null ) {
            return null;
        }

        List<MonthlyreportMybatisParam.DeleteActivityList> list1 = new ArrayList<MonthlyreportMybatisParam.DeleteActivityList>( list.size() );
        for ( MonthlyreportActivityDelete monthlyreportActivityDelete : list ) {
            list1.add( monthlyreportActivityDeleteToDeleteActivityList( monthlyreportActivityDelete ) );
        }

        return list1;
    }

    protected MonthlyreportMybatisParam.UpdateMonthlyProgress monthlyreportProgressUpdateToUpdateMonthlyProgress(MonthlyreportProgressUpdate monthlyreportProgressUpdate) {
        if ( monthlyreportProgressUpdate == null ) {
            return null;
        }

        MonthlyreportMybatisParam.UpdateMonthlyProgress updateMonthlyProgress = new MonthlyreportMybatisParam.UpdateMonthlyProgress();

        updateMonthlyProgress.setMonthlyCnsttyId( monthlyreportProgressUpdate.getMonthlyCnsttyId() );
        updateMonthlyProgress.setRmk( monthlyreportProgressUpdate.getRmk() );

        return updateMonthlyProgress;
    }
}
