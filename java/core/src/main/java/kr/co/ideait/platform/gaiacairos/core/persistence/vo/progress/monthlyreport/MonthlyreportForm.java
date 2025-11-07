package kr.co.ideait.platform.gaiacairos.core.persistence.vo.progress.monthlyreport;

import kr.co.ideait.platform.gaiacairos.core.persistence.entity.PrMonthlyReport;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.PrMonthlyReportActivity;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.PrMonthlyReportPhoto;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.PrMonthlyReportProgress;
import lombok.Data;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants.ComponentModel;

import java.util.List;
import java.util.Map;

@Mapper(componentModel = ComponentModel.SPRING)
public interface MonthlyreportForm {

	MonthlyreportMybatisParam.MonthlyreportListInput toMonthlyreportListInput(MonthlyreportList monthlyreportList);

	MonthlyreportMybatisParam.MonthlyActivityListInput toMonthlyActivityListInput(MonthlyreportActivityDetail monthlyreportActivityDetail);

	List<MonthlyreportMybatisParam.UpdateMonthlyreportInput> toUpdateMonthlyreportInput (List<MonthlyreportUpdateDelete> monthlyreportDeleteList);

	MonthlyreportMybatisParam.SearchAddActivityInput toSearchAddActivityInput(SearchAddActivity searchAddActivity);

	MonthlyreportMybatisParam.MonthlyreportActivityDetailInput toMonthlyreportActivityDetailInput (MonthlyreportActivityDetail monthlyreportActivityDetail);

	MonthlyreportMybatisParam.UpdateActivityListInput toUpdateActivityListInput(MonthlyreportActivityUpdate monthlyreportActivityUpdateList);

	List<MonthlyreportMybatisParam.UpdateMonthlyProgress> toUpdateMonthlyProgressList(List<MonthlyreportProgressUpdate> monthlyreportProgressUpdateList);

	List<MonthlyreportMybatisParam.MonthlyreportStatus> toMonthlyreportStatus(List<MonthlyreportStatusForm> monthlyreportStatusForm);

	@Data
    class MonthlyreportList {
        String cntrctNo;
        String apprvlStats;
        String searchText;
    }

	@Data
	class MonthlyreportInsert {
		String cntrctNo;
		PrMonthlyReport prMonthlyReport;
		List<MonthlyreportStatusForm> monthlyreportStatusForm;
		List<MonthlyPhoto> monthlyPhoto;
//		String reportYm;
//		String monthlyReportDate;
//		String rmrkCntnts;
	}

	@Data
	class MonthlyreportActivityDetail {
		String cntrctNo;
		String cntrctChgId;
		Long monthlyReportId;
		String reportYm;
	}

	@Data
	class MonthlyreportUpdateList {
		String cntrctNo;
		List<MonthlyreportUpdateDelete> updateReportList;
	}

	@Data
	class MonthlyreportUpdateDelete {
		String cntrctChgId;
//		String cntrctNo;
		Long monthlyReportId;
	}

	@Data
	class SearchAddActivity {
		String cntrctNo;
		String cntrctChgId;
		Long monthlyReportId;
        String searchText;
        String startDate;
        String endDate;
        String reportYm;
        String modalType;
    }


	@Data
	class MonthlyreportActivityDelete {
		String cntrctChgId;
		Long monthlyReportId;
		Integer monthlyActivityId;
	}

	@Data
	class MonthlyreportActivityUpdate {
		String modalType;
		List<MonthlyreportActivityDelete> delActivityList;
		List<PrMonthlyReportActivity> addActivityList;
		List<PrMonthlyReportActivity> updateActivityList;
	}

	@Data
	class MonthlyreportUpdate {
		String cntrctNo;
		PrMonthlyReport prMonthlyReport;
		List<PrMonthlyReportProgress> progressList;
		List<MonthlyreportStatusForm> monthlyreportStatusForm;
		List<PrMonthlyReportPhoto> delPhotoList;
		List<MonthlyPhoto> monthlyPhoto;
	}

	@Data
	class MonthlyreportProgressUpdate {
		Long monthlyCnsttyId;
		String rmk;
	}

	@Data
	class MonthlyreportStatusForm {
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
	class MonthlyPhoto {
		String titlNm;
		String dscrpt;
		String shotDate;
		Map<String, Object> meta;
	}
}
