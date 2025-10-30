package kr.co.ideait.platform.gaiacairos.core.persistence.vo.progress.monthlyreport;

import kr.co.ideait.platform.gaiacairos.core.persistence.entity.PrMonthlyReportActivity;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.CommonForm;
import lombok.Data;
import org.apache.ibatis.type.Alias;

import java.time.LocalDateTime;
import java.util.List;

public interface MonthlyreportMybatisParam {

	@Data
	@Alias("monthlyreportListInput")
    public class MonthlyreportListInput {
        String cntrctNo;
        String apprvlStats;
        String cmnGrpCd;
        String searchText;
    }

	@Data
	@Alias("monthlyActivityListInput")
	public class MonthlyActivityListInput {
		String cntrctNo;
		String reportYm;
		String monthlyReportDate;
	}


	@Data
	@Alias("monthlyreportActivityDetailInput")
	public class MonthlyreportActivityDetailInput extends CommonForm {
		String cntrctChgId;
		String cntrctNo;
		String reportYm;
		Long monthlyReportId;
		String cmnGrpCd;
	}

	@Data
	@Alias("updateMonthlyreportInput")
	public class UpdateMonthlyreportInput {
		String cntrctChgId;
//		String cntrctNo;
		Long monthlyReportId;
		String dltId;
		String chgId;
	}


	@Data
	@Alias("searchAddActivityInput")
	public class SearchAddActivityInput {
		String cntrctNo;
		String cntrctChgId;
		Long monthlyReportId;
        String searchText;
        String startDate;
        String endDate;
        String cmnGrpCd;
        String reportYm;
	}


	@Data
	@Alias("monthlyreportListOutput")
	public class MonthlyreportListOutput {
		String cntrctChgId;
		Long monthlyReportId;
		String reportYm;
		String monthlyReportDate;

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
	@Alias("insertMonthlyDataInput")
	public class InsertMonthlyDataInput {
		String cntrctNo;
		Long monthlyReportId;
		String cntrctChgId;
		String reportYm;
		String rgstrId;
	}


	@Data
	@Alias("bohalRateInput")
	public class BohalRateInput {
		String cntrctChgId;
		Long monthlyReportId;

		public BohalRateInput(String cntrctChgId, Long monthlyReportId) {
			this.cntrctChgId = cntrctChgId;
			this.monthlyReportId = monthlyReportId;
		}
	}


	@Data
	@Alias("bohalRate")
	public class BohalRate {
		double acmltPlanBohalRate;
		double acmltArsltBohalRate;
	    double thismthPlanBohalRate;
	    double thismthArsltBohalRate;
	    double lsmthPlanBohalRate;
	    double lsmthArsltBohalRate;
	}


	@Data
	public class DeleteActivityList extends CommonForm {
		String cntrctChgId;
		Long monthlyReportId;
		Integer monthlyActivityId;
//		String activityId;
//		String workDtType;
		String chgId;
		String dltId;
	}

	@Data
	public class UpdateActivityListInput extends CommonForm {
		String modalType;
		List<DeleteActivityList> delActivityList;
		List<PrMonthlyReportActivity> addActivityList;
		List<PrMonthlyReportActivity> updateActivityList;
	}

	@Data
	public class UpdateMonthlyProgress extends CommonForm {
		Long monthlyCnsttyId;
		String rmk;
	}

	@Data
	@Alias("unitCnstTypeOutput")
	class UnitCnstTypeOutput {
		String cntrctChgId;
        String unitCnstType;
		String unitCnstNm;
	}

	@Data
	@Alias("monthlyreportStatus")
	class MonthlyreportStatus {
		String cntrctChgId;
		Long monthlyReportId;
		String cnstrctCd;
		String cnstrctNm;
		String cnstrctPerf;
		String cnstrctPlan;
		String cnstrctNote;
		String currentIssues;
		String currentActionPlan;
		String currentNote;
		String dltYn;
		String rgstrId;
		String chgId;
		String dltId;
	}

	@Data
	@Alias("monthlyPhotoOutput")
	class MonthlyPhotoOutput {
		String titlNm;
		String dscrpt;
		String shotDate;
		Integer fileNo;
		Short sno;
		String fileNm;
		String fileDiskNm;
		String fileDiskPath;
	}
}

