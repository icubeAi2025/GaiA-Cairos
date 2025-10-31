package kr.co.ideait.platform.gaiacairos.web.entrypoint.progress;

import kr.co.ideait.iframework.annotation.Description;
import kr.co.ideait.platform.gaiacairos.comp.progress.WeeklyreportComponent;
import kr.co.ideait.platform.gaiacairos.comp.progress.service.WeeklyreportService;
import kr.co.ideait.platform.gaiacairos.core.base.AbstractController;
import kr.co.ideait.platform.gaiacairos.core.exception.ErrorType;
import kr.co.ideait.platform.gaiacairos.core.exception.GaiaBizException;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.GridResult;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.MybatisInput;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.Result;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.CommonReqVo;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.progress.weeklyreport.WeeklyreportForm;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.log.Log;
import kr.co.ideait.platform.gaiacairos.core.type.LogType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/progress/weeklyreport")
public class WeeklyreportApiController extends AbstractController{

	@Autowired
	WeeklyreportService weeklyreportService;

	@Autowired
	WeeklyreportComponent weeklyreportComponent;
	
	@Autowired
	WeeklyreportForm weeklyreportForm;


	/**
	 * 주간보고 목록 조회
	 * @param commonReqVo
	 * @param weeklyreportList
	 * @return
	 */
	@PostMapping("/list")
	@Description(name = "주간보고 목록 조회", description = "주간보고 목록 조회", type = Description.TYPE.MEHTOD)
	public GridResult getWeeklyreportList(CommonReqVo commonReqVo, @RequestBody WeeklyreportForm.WeeklyreportList weeklyreportList) {

		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.FUNCTION.name());
		userLog.setExecType("주간보고 목록 조회");

		systemLogComponent.addUserLog(userLog);

		MybatisInput input = MybatisInput.of().add("cntrctNo", weeklyreportList.getCntrctNo())
				.add("apprvlStats", weeklyreportList.getApprvlStats())
				.add("searchText", weeklyreportList.getSearchText());

		return GridResult.ok(weeklyreportService.selectWeeklyreportList(input));
	}


	/**
	 * 주간보고 상세 조회
	 * @param commonReqVo
	 * @param weeklyreportDetail
	 * @return
	 */
	@PostMapping("/weeklyreport-details")
	@Description(name = "주간보고 상세 조회", description = "주간보고 상세 조회", type = Description.TYPE.MEHTOD)
	public Result getWeeklyreportDetails(CommonReqVo commonReqVo, @RequestBody WeeklyreportForm.WeeklyreportDetail weeklyreportDetail) {

		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.FUNCTION.name());
		userLog.setExecType("주간보고 상세 조회");

		systemLogComponent.addUserLog(userLog);

		MybatisInput input = MybatisInput.of().add("cntrctChgId", weeklyreportDetail.getCntrctChgId())
				.add("weeklyReportId", weeklyreportDetail.getWeeklyReportId());

		return Result.ok().put("weeklyReport", weeklyreportService.selectWeeklyreportDetail(input))
				.put("progress", weeklyreportService.selectWeeklyProgress(input))
				.put("major", weeklyreportService.selectWeeklyMajorActivityList(input))
				.put("delay", weeklyreportService.selectWeeklyDelayActivityList(input))
				.put("next", weeklyreportService.selectNextWeekActivityList(input));

	}


	/**
	 * 금주 주요 작업 변경 모달 - 금주 주요 실적 및 추가 가능한 Activity 목록 조회
	 * @param commonReqVo
	 * @param weeklyreportDetail
	 * @return
	 */
	@PostMapping("/select-major-activty")
	@Description(name = "금주 주요 실적 리스트 조회", description = "금주 주요 실적 변경 모달 - 금주 주요 실적 및 추가 가능한 Activity 목록 조회", type = Description.TYPE.MEHTOD)
	public Result getMajorActivityList(CommonReqVo commonReqVo, @RequestBody WeeklyreportForm.WeeklyreportDetail weeklyreportDetail) {

		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.FUNCTION.name());
		userLog.setExecType("금주 주요 실적 리스트 조회");

		systemLogComponent.addUserLog(userLog);

		MybatisInput input = MybatisInput.of().add("cntrctChgId", weeklyreportDetail.getCntrctChgId())
				.add("weeklyReportId", weeklyreportDetail.getWeeklyReportId())
				.add("cntrctNo", weeklyreportDetail.getCntrctNo())
				.add("reportDate", weeklyreportDetail.getReportDate());

		return Result.ok().put("majorActivityList", weeklyreportService.selectWeeklyMajorActivityList(input))
				.put("addActivityList", weeklyreportService.selectWeeklyAddMajorActivityList(input));
	}


	/**
	 * 금주 지연 실적 변경 모달 - 금주 지연 실적 및 추가 가능한 Activity 목록 조회
	 * @param commonReqVo
	 * @param weeklyreportDetail
	 * @return
	 */
	@PostMapping("/select-delay-activty")
	@Description(name = "금주 지연 실적 리스트 조회", description = "금주 지연 실적 변경 모달 - 금주 지연 실적 및 추가 가능한 Activity 목록 조회", type = Description.TYPE.MEHTOD)
	public Result getDelayActivityList(CommonReqVo commonReqVo, @RequestBody WeeklyreportForm.WeeklyreportDetail weeklyreportDetail) {

		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.FUNCTION.name());
		userLog.setExecType("금주 지연 실적 리스트 조회");

		systemLogComponent.addUserLog(userLog);

		MybatisInput input = MybatisInput.of().add("cntrctChgId", weeklyreportDetail.getCntrctChgId())
				.add("weeklyReportId", weeklyreportDetail.getWeeklyReportId())
				.add("cntrctNo", weeklyreportDetail.getCntrctNo())
				.add("reportDate", weeklyreportDetail.getReportDate());

		return Result.ok().put("delayActivityList", weeklyreportService.selectWeeklyDelayActivityList(input))
				.put("addActivityList", weeklyreportService.selectWeeklyAddDelayActivityList(input));
	}


	/**
	 * 차주 주요 실적 변경 모달 - 차주 주요 실적 및 추가 가능한 Activity 목록 조회
	 * @param commonReqVo
	 * @param weeklyreportDetail
	 * @return
	 */
	@PostMapping("/select-next-activty")
	@Description(name = "차주 주요 실적 리스트 조회", description = "차주 주요 실적 및 추가 가능한 Activity 목록 조회", type = Description.TYPE.MEHTOD)
	public Result getNextActivityList(CommonReqVo commonReqVo, @RequestBody WeeklyreportForm.WeeklyreportDetail weeklyreportDetail) {

		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.FUNCTION.name());
		userLog.setExecType("차주 주요 실적 리스트 조회");

		systemLogComponent.addUserLog(userLog);

		MybatisInput input = MybatisInput.of().add("cntrctChgId", weeklyreportDetail.getCntrctChgId())
				.add("weeklyReportId", weeklyreportDetail.getWeeklyReportId())
				.add("cntrctNo", weeklyreportDetail.getCntrctNo())
				.add("reportDate", weeklyreportDetail.getReportDate());

		return Result.ok().put("nextActivityList", weeklyreportService.selectNextWeekActivityList(input))
				.put("addActivityList", weeklyreportService.selectAddNextWeekActivityList(input));
	}


	/**
	 * 주간보고 추가 - 보고서 & Activity & Progress
	 * @param commonReqVo
	 * @param weeklyreportInsert
	 * @return
	 */
	@PostMapping("/create")
	@Description(name = "주간보고 추가", description = "주간보고 추가 - 보고서 & Activity & Progress", type = Description.TYPE.MEHTOD)
	public Result createWeeklyreport(CommonReqVo commonReqVo, @RequestBody WeeklyreportForm.WeeklyreportInsert weeklyreportInsert) {

		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.FUNCTION.name());
		userLog.setExecType("주간보고 추가");

		systemLogComponent.addUserLog(userLog);

		int count = weeklyreportService.checkWeeklyReport(weeklyreportInsert.getCntrctNo(), weeklyreportInsert.getPrWeeklyReport().getReportDate());

		if(count < 1) {
			return Result.ok().put("report", weeklyreportService.insertWeeklyreport(weeklyreportInsert.getCntrctNo(), weeklyreportInsert.getPrWeeklyReport()));
		} else {
			throw new GaiaBizException(ErrorType.DUPLICATION_DATA);
		}
	}


	/**
	 * 주간보고 업데이트 - 보고서(비고, 날짜) / 공정현황
	 * @param commonReqVo
	 * @param weeklyreportUpdate
	 * @return
	 */
	@PostMapping("/update")
	@Description(name = "주간보고 업데이트", description = "주간보고 업데이트 - 보고서(비고, 날짜) / 공정현황", type = Description.TYPE.MEHTOD)
	public Result updateWeeklyreport(CommonReqVo commonReqVo, @RequestBody WeeklyreportForm.WeeklyreportUpdate weeklyreportUpdate) {

		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.FUNCTION.name());
		userLog.setExecType("주간보고 업데이트");

		systemLogComponent.addUserLog(userLog);

		return Result.ok().put("report", weeklyreportService.updateWeeklyreport(weeklyreportUpdate.getPrWeeklyReport(), weeklyreportForm.toUpdateWeeklyProgressList(weeklyreportUpdate.getProgressList())));
	}


	/**
	 * 주간보고 Activity 업데이트(삭제 / 추가)
	 * @param commonReqVo
	 * @param updateActivity
	 * @return
	 */
	@PostMapping("/update-activity")
	@Description(name = "주간보고 Activity 업데이트", description = "주간보고 Activity 업데이트(삭제 / 추가)", type = Description.TYPE.MEHTOD)
	public Result updateActivity(CommonReqVo commonReqVo, @RequestBody WeeklyreportForm.WeeklyreportActivityUpdate updateActivity) {

		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.FUNCTION.name());
		userLog.setExecType("주간보고 Activity 업데이트");

		systemLogComponent.addUserLog(userLog);

		weeklyreportService.updateActivity(weeklyreportForm.toUpdateWeeklyActivityInput(updateActivity));

		return Result.ok();
	}


	/**
	 * 주간보고 삭제 - 보고서 & Activity & Progress
	 * @param commonReqVo
	 * @param weeklyreportDeleteList
	 * @return
	 */
	@PostMapping("/delete")
	@Description(name = "주간보고 삭제", description = "주간보고 삭제 - 보고서 & Activity & Progress", type = Description.TYPE.MEHTOD)
	public Result deleteWeeklyreportList(CommonReqVo commonReqVo, @RequestBody WeeklyreportForm.WeeklyreportDeleteList weeklyreportDeleteList) {

		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.FUNCTION.name());
		userLog.setExecType("주간보고 삭제");

		systemLogComponent.addUserLog(userLog);

		weeklyreportComponent.deleteWeeklyreport(weeklyreportForm.toUpdateWeeklyreportInput(weeklyreportDeleteList.getDelList()));

		return Result.ok();
	}


	/**
	 * 주간보고 전자결재 승인요청 -> API 통신
	 * @param commonReqVo
	 * @param weeklyreportUpdateList
	 * @return
	 */
	@PostMapping("/request-approval")
	@Description(name = "주간보고 전자결재 승인요청", description = "주간보고 전자결재 승인요청 -> API 통신", type = Description.TYPE.MEHTOD)
	public Result requestApprovalMonthlyreport(CommonReqVo commonReqVo, @RequestBody WeeklyreportForm.WeeklyreportUpdateList weeklyreportUpdateList) {

		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.FUNCTION.name());
		userLog.setExecType("주간보고 전자결재 승인요청");

		systemLogComponent.addUserLog(userLog);

		weeklyreportComponent.requestApprovalWeeklyreport(weeklyreportForm.toUpdateWeeklyreportInput(weeklyreportUpdateList.getUpdateReportList()), weeklyreportUpdateList.getCntrctNo(), commonReqVo.getApiYn(), commonReqVo.getPjtDiv());

		return Result.ok();
	}
	
}
