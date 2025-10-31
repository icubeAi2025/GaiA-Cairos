package kr.co.ideait.platform.gaiacairos.core.persistence.vo.progress.weeklyreport;

import kr.co.ideait.platform.gaiacairos.core.persistence.entity.PrWeeklyReport;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.PrWeeklyReportActivity;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.progress.weeklyreport.WeeklyreportMybatisParam.*;
import lombok.Data;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants.ComponentModel;

import java.util.List;

@Mapper(componentModel = ComponentModel.SPRING)
public interface WeeklyreportForm {

	UpdateWeeklyActivityInput toUpdateWeeklyActivityInput(WeeklyreportActivityUpdate weeklyreportActivityUpdate);
	
	List<UpdateWeeklyreportInput> toUpdateWeeklyreportInput (List<WeeklyreportDelete> weeklyreportDelete);
	
	List<UpdateWeeklyProgress> toUpdateWeeklyProgressList(List<WeeklyreportProgressUpdate> weeklyreportProgressUpdateList);
	
	@Data
    class WeeklyreportList {
        String cntrctNo;
        String apprvlStats;
        String searchText;
    }
	
	@Data
	class WeeklyreportInsert {
		String cntrctNo;
		PrWeeklyReport prWeeklyReport;
	}
	
	@Data
	class WeeklyreportDeleteList {
		List<WeeklyreportDelete> delList;
	}
	
	@Data
	class WeeklyreportDelete {
		String cntrctChgId;
		Long weeklyReportId; 
	}
	
	@Data
	class WeeklyreportDetail {
		String cntrctNo;
		String cntrctChgId;
		Long weeklyReportId;
		String reportDate;
	}
	
	@Data
	class WeeklyreportActivityUpdate {
		String modalType;
		List<WeeklyreportActivityDelete> delActivityList;
		List<PrWeeklyReportActivity> addActivityList;
		List<PrWeeklyReportActivity> updateActivityList;
	}
	
	@Data
	class WeeklyreportActivityDelete {
		String cntrctChgId;
		Long weeklyReportId;
		Integer weeklyActivityId;
	}
	
	@Data
	class WeeklyreportUpdate {
		PrWeeklyReport prWeeklyReport;
		List<WeeklyreportProgressUpdate> progressList;
	}
	
	@Data
	class WeeklyreportProgressUpdate {
		Long weeklyCnsttyId;
		String rmk;
	}
	
	@Data
	class WeeklyreportUpdateList {
		String cntrctNo;
		List<WeeklyreportDelete> updateReportList;
	}
}
