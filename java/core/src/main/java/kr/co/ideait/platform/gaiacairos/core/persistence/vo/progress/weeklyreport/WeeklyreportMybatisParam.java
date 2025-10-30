package kr.co.ideait.platform.gaiacairos.core.persistence.vo.progress.weeklyreport;

import java.time.LocalDateTime;
import java.util.List;

import org.apache.ibatis.type.Alias;

import kr.co.ideait.platform.gaiacairos.core.persistence.entity.PrWeeklyReportActivity;
import lombok.Data;

public interface WeeklyreportMybatisParam {

	
	@Data
	@Alias("weeklyreportListOutput")
	public class WeeklyreportListOutput {
		String cntrctChgId;
		Long weeklyReportId;
		String reportDate;
		String weeklyReportDate;
		
		double acmltPlanBohalRate;
		double acmltArsltBohalRate;
	    double thismthPlanBohalRate;
	    double thismthArsltBohalRate;
	    double lsmthPlanBohalRate;
	    double lsmthArsltBohalRate;
	    
        String apprvlStats;
        String apprvlReqId;
        LocalDateTime apprvlReqDt;
        String apDocId;
        String apprvlId;
        LocalDateTime apprvlDt;
        String dltYn;

		String rgstrNm;
		String apprvlNm;
		String apprvlStatsKrn;
		
		String rmrkCntnts;
		String title;
	}
	
	@Data
	@Alias("weeklyBohalRate")
	public class WeeklyBohalRate {
		double acmltPlanBohalRate;
		double acmltArsltBohalRate;
	    double thismthPlanBohalRate;
	    double thismthArsltBohalRate;
	    double lsmthPlanBohalRate;
	    double lsmthArsltBohalRate;
	}
	
	
	@Data
	public class DeleteWeeklyActivityList {
		String cntrctChgId;
		Long weeklyReportId;
		Integer weeklyActivityId;
		String chgId;
		String dltId;
	}
	
	@Data
	public class UpdateWeeklyActivityInput  {
		String modalType;
		List<DeleteWeeklyActivityList> delActivityList;
		List<PrWeeklyReportActivity> addActivityList;
		List<PrWeeklyReportActivity> updateActivityList;
	}
	
	@Data
	public class UpdateWeeklyProgress {
		Long weeklyCnsttyId;
		String rmk;
	}
	
	@Data
	@Alias("updateWeeklyreportInput")
	public class UpdateWeeklyreportInput {
		String cntrctChgId;
//		String cntrctNo;
		Long weeklyReportId;
		String dltId;
		String chgId;
	}
	
}
