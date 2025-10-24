package kr.co.ideait.platform.gaiacairos.web.entrypoint.progress;

import com.fasterxml.jackson.core.JsonProcessingException;
import kr.co.ideait.iframework.annotation.Description;
import kr.co.ideait.platform.gaiacairos.comp.progress.MonthlyreportComponent;
import kr.co.ideait.platform.gaiacairos.comp.progress.service.MonthlyreportService;
import kr.co.ideait.platform.gaiacairos.core.base.AbstractController;
import kr.co.ideait.platform.gaiacairos.core.exception.ErrorType;
import kr.co.ideait.platform.gaiacairos.core.exception.GaiaBizException;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.GridResult;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.Result;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.CommonReqVo;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.progress.monthlyreport.MonthlyreportForm;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.log.Log;
import kr.co.ideait.platform.gaiacairos.core.type.LogType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/progress")
public class MonthlyreportApiController extends AbstractController {

	@Autowired
	MonthlyreportService monthlyreportService;

	@Autowired
	MonthlyreportComponent monthlyreportComponent;
	
	@Autowired
	MonthlyreportForm monthlyreportForm;

	/**
	 * 월간보고 목록 조회
	 * @param monthlyreportList
	 * @return
	 */
	@PostMapping("/monthlyreport/list")
	@Description(name = "월간보고 목록 조회", description = "월간보고 목록 조회", type = Description.TYPE.MEHTOD)
	public GridResult getMonthlyreportList(CommonReqVo commonReqVo, @RequestBody MonthlyreportForm.MonthlyreportList monthlyreportList) {

		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.FUNCTION.name());
		userLog.setExecType("월간보고 목록 조회");

		systemLogComponent.addUserLog(userLog);

		return GridResult.ok(monthlyreportService.selectMonthlyreportList(monthlyreportForm.toMonthlyreportListInput(monthlyreportList)));
	}


	/**
	 * 월간보고 상세 조회
	 * @param monthlyreportActivityDetail
	 * @return
	 */
	@PostMapping("/monthlyreport/monthlyreport-details")
	@Description(name = "월간보고 상세 조회", description = "월간보고 상세 조회", type = Description.TYPE.MEHTOD)
	public Result getMonthlyreportDetails(CommonReqVo commonReqVo, @RequestBody MonthlyreportForm.MonthlyreportActivityDetail monthlyreportActivityDetail) {

		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.FUNCTION.name());
		userLog.setExecType("월간보고 상세 조회");

		systemLogComponent.addUserLog(userLog);

		return Result.ok().put("monthlyReport", monthlyreportService.selectMonthlyreport(monthlyreportForm.toMonthlyreportActivityDetailInput(monthlyreportActivityDetail)))
				.put("progress", monthlyreportService.selectProgressStatusList(monthlyreportForm.toMonthlyreportActivityDetailInput(monthlyreportActivityDetail)))
				.put("major", monthlyreportService.selectMajorActivityList(monthlyreportForm.toMonthlyreportActivityDetailInput(monthlyreportActivityDetail)))
				.put("delay", monthlyreportService.selectDelayActivityList(monthlyreportForm.toMonthlyreportActivityDetailInput(monthlyreportActivityDetail)))
				.put("next", monthlyreportService.selectNextActivityList(monthlyreportForm.toMonthlyreportActivityDetailInput(monthlyreportActivityDetail)))
				.put("status", monthlyreportService.selectMonthlyStatusList(monthlyreportActivityDetail.getCntrctChgId(), monthlyreportActivityDetail.getMonthlyReportId()))
				.put("photo", monthlyreportService.selectMonthlyPhoto(monthlyreportActivityDetail.getCntrctChgId(), monthlyreportActivityDetail.getMonthlyReportId()));

	}


	/**
	 * 월간보고 Activity 상세조회(공정현황, 주요&지연&익월Activity)
	 * @param monthlyreportActivityDetail
	 * @return
	 */
//	@PostMapping("/monthlyreport/monthlyreport-activity-details")
//	@Description(name = "월간보고 Activity 상세조회", description = "월간보고 Activity 상세조회(공정현황, 주요&지연&익월Activity)", type = Description.TYPE.MEHTOD)
//	public Result getMonthlyreportActivityDetails(CommonReqVo commonReqVo, @RequestBody MonthlyreportForm.MonthlyreportActivityDetail monthlyreportActivityDetail) {
//
//		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
//		userLog.setLogType(LogType.FUNCTION.name());
//		userLog.setExecType("월간보고 Activity 상세조회");
//
//		systemLogComponent.addUserLog(userLog);
//
//		return Result.ok().put("progressStatusList", monthlyreportService.selectProgressStatusList(monthlyreportForm.toMonthlyreportActivityDetailInput(monthlyreportActivityDetail)))
//							.put("majorActivityList", monthlyreportService.selectMajorActivityList(monthlyreportForm.toMonthlyreportActivityDetailInput(monthlyreportActivityDetail)))
//							.put("delayActivityList", monthlyreportService.selectDelayActivityList(monthlyreportForm.toMonthlyreportActivityDetailInput(monthlyreportActivityDetail)))
//							.put("nextActivityList", monthlyreportService.selectNextActivityList(monthlyreportForm.toMonthlyreportActivityDetailInput(monthlyreportActivityDetail)));
//
//	}


	/**
	 * 금월 주요 실적 변경 모달 - 금월 주요 실적 및 추가 가능한 Activity 목록 조회
	 * @param commonReqVo
	 * @param monthlyreportActivityDetail
	 * @return
	 */
	@PostMapping("/monthlyreport/select-major-activty")
	@Description(name = "금월 주요 실적 리스트 조회", description = "금월 주요 실적 변경 모달 - 금월 주요 실적 및 추가 가능한 Activity 목록 조회", type = Description.TYPE.MEHTOD)
	public Result getMajorActivityList(CommonReqVo commonReqVo, @RequestBody MonthlyreportForm.MonthlyreportActivityDetail monthlyreportActivityDetail) {

		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.FUNCTION.name());
		userLog.setExecType("금월 주요 실적 리스트 조회");

		systemLogComponent.addUserLog(userLog);

		MonthlyreportForm.SearchAddActivity searchAddActivity = new MonthlyreportForm.SearchAddActivity();
		searchAddActivity.setCntrctNo(monthlyreportActivityDetail.getCntrctNo());
		searchAddActivity.setCntrctChgId(monthlyreportActivityDetail.getCntrctChgId());
		searchAddActivity.setMonthlyReportId(monthlyreportActivityDetail.getMonthlyReportId());
		searchAddActivity.setReportYm(monthlyreportActivityDetail.getReportYm());

		return Result.ok().put("majorActivityList", monthlyreportService.selectMajorActivityList(monthlyreportForm.toMonthlyreportActivityDetailInput(monthlyreportActivityDetail)))
				.put("addActivityList", monthlyreportService.selectAddMajorActivityList(monthlyreportForm.toSearchAddActivityInput(searchAddActivity)));
	}


	/**
	 * 금월 지연 실적 변경 모달 - 금월 지연 실적 및 추가 가능한 Activity 목록 조회
	 * @param commonReqVo
	 * @param monthlyreportActivityDetail
	 * @return
	 */
	@PostMapping("/monthlyreport/select-delay-activty")
	@Description(name = "금월 지연 실적 리스트 조회", description = "금월 지연 실적 변경 모달 - 금월 지연 실적 및 추가 가능한 Activity 목록 조회", type = Description.TYPE.MEHTOD)
	public Result getDelayActivityList(CommonReqVo commonReqVo, @RequestBody MonthlyreportForm.MonthlyreportActivityDetail monthlyreportActivityDetail) {

		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.FUNCTION.name());
		userLog.setExecType("금월 지연 실적 리스트 조회");

		systemLogComponent.addUserLog(userLog);

		MonthlyreportForm.SearchAddActivity searchAddActivity = new MonthlyreportForm.SearchAddActivity();
		searchAddActivity.setCntrctNo(monthlyreportActivityDetail.getCntrctNo());
		searchAddActivity.setMonthlyReportId(monthlyreportActivityDetail.getMonthlyReportId());
		searchAddActivity.setCntrctChgId(monthlyreportActivityDetail.getCntrctChgId());

		return Result.ok().put("delayActivityList", monthlyreportService.selectDelayActivityList(monthlyreportForm.toMonthlyreportActivityDetailInput(monthlyreportActivityDetail)))
				.put("addActivityList", monthlyreportService.selectAddDelayActivityList(monthlyreportForm.toSearchAddActivityInput(searchAddActivity)));
	}


	/**
	 * 익월 주요 실적 변경 모달 - 익월 주요 실적 및 추가 가능한 Activity 목록 조회
	 * @param commonReqVo
	 * @param monthlyreportActivityDetail
	 * @return
	 */
	@PostMapping("/monthlyreport/select-next-activty")
	@Description(name = "익월 주요 실적 리스트 조회", description = "익월 주요 실적 및 추가 가능한 Activity 목록 조회", type = Description.TYPE.MEHTOD)
	public Result getNextActivityList(CommonReqVo commonReqVo, @RequestBody MonthlyreportForm.MonthlyreportActivityDetail monthlyreportActivityDetail) {

		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.FUNCTION.name());
		userLog.setExecType("익월 주요 실적 리스트 조회");

		systemLogComponent.addUserLog(userLog);

		MonthlyreportForm.SearchAddActivity searchAddActivity = new MonthlyreportForm.SearchAddActivity();
		searchAddActivity.setCntrctNo(monthlyreportActivityDetail.getCntrctNo());
		searchAddActivity.setMonthlyReportId(monthlyreportActivityDetail.getMonthlyReportId());
		searchAddActivity.setCntrctChgId(monthlyreportActivityDetail.getCntrctChgId());

		return Result.ok().put("nextActivityList", monthlyreportService.selectNextActivityList(monthlyreportForm.toMonthlyreportActivityDetailInput(monthlyreportActivityDetail)))
				.put("addActivityList", monthlyreportService.selectAddNextActivityList(monthlyreportForm.toSearchAddActivityInput(searchAddActivity)));
	}



	/**
	 * 금월 추가할 Activity 리스트 조회(주요/지연/익월)
	 * @param searchAddActivity
	 * @return
	 */
	@PostMapping("/monthlyreport/search-addActivty")
	@Description(name = "금월 추가할 Activity 리스트 조회", description = "금월 추가할 Activity 리스트 조회(주요/지연/익월)", type = Description.TYPE.MEHTOD)
	public Result getAddMajorActivityList(CommonReqVo commonReqVo, @RequestBody MonthlyreportForm.SearchAddActivity searchAddActivity) {

		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.FUNCTION.name());
		userLog.setExecType("금월 추가할 Activity 리스트 조회");

		systemLogComponent.addUserLog(userLog);

		String modalType = searchAddActivity.getModalType();

		if(modalType.equals("t")) {
			return Result.ok().put("addActivityList", monthlyreportService.selectAddMajorActivityList(monthlyreportForm.toSearchAddActivityInput(searchAddActivity)));
		} else if (modalType.equals("d")) {
			return Result.ok().put("addActivityList", monthlyreportService.selectAddDelayActivityList(monthlyreportForm.toSearchAddActivityInput(searchAddActivity)));
		} else {
			return Result.ok().put("addActivityList", monthlyreportService.selectAddNextActivityList(monthlyreportForm.toSearchAddActivityInput(searchAddActivity)));
		}
	}


	/**
	 * 월간보고 추가 - 보고서 & Activity & Progress
	 * @param commonReqVo
	 * @param monthlyreportInsert
	 * @return
	 */
	@PostMapping("/monthlyreport/create")
	@Description(name = "월간보고 추가", description = "월간보고 추가 - 보고서 & Activity & Progress", type = Description.TYPE.MEHTOD)
	public Result createMonthlyreport(CommonReqVo commonReqVo,
									  @RequestPart("data") MonthlyreportForm.MonthlyreportInsert monthlyreportInsert) throws JsonProcessingException {

		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.FUNCTION.name());
		userLog.setExecType("월간보고 추가");

		systemLogComponent.addUserLog(userLog);

		int count = monthlyreportService.checkMonthlyReport(monthlyreportInsert.getCntrctNo(), monthlyreportInsert.getPrMonthlyReport().getReportYm());

		if(count < 1) {
			return Result.ok().put("report", monthlyreportService.insertMonthlyreport(
					monthlyreportInsert.getCntrctNo(),
					monthlyreportInsert.getPrMonthlyReport(),
					monthlyreportForm.toMonthlyreportStatus(monthlyreportInsert.getMonthlyreportStatusForm()),
					monthlyreportInsert.getMonthlyPhoto()));
		} else {
			throw new GaiaBizException(ErrorType.DUPLICATION_DATA);
		}
	}
//	@PostMapping("/monthlyreport/create")
//	@Description(name = "월간보고 추가", description = "월간보고 추가 - 보고서 & Activity & Progress", type = Description.TYPE.MEHTOD)
//	public Result createMonthlyreport(CommonReqVo commonReqVo,
//									  @RequestPart("data") MonthlyreportForm.MonthlyreportInsert monthlyreportInsert,
//									  @RequestPart(value = "photoMeta", required = false) List<Map<String,Object>> photoMeta) throws JsonProcessingException {
//
//		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
//		userLog.setLogType(LogType.FUNCTION.name());
//		userLog.setExecType("월간보고 추가");
//
//		systemLogComponent.addUserLog(userLog);
//
//		int count = monthlyreportService.checkMonthlyReport(monthlyreportInsert.getCntrctNo(), monthlyreportInsert.getPrMonthlyReport().getReportYm());
//
//		if(count < 1) {
//			return Result.ok().put("report", monthlyreportService.insertMonthlyreport(
//					monthlyreportInsert.getCntrctNo(),
//					monthlyreportInsert.getPrMonthlyReport(),
//					monthlyreportForm.toMonthlyreportStatus(monthlyreportInsert.getMonthlyreportStatusForm()),
//					photoMeta));
//		} else {
//			throw new GaiaBizException(ErrorType.DUPLICATION_DATA);
//		}
//	}


	/**
	 * 월간보고 업데이트 - 보고서(비고, 날짜) / 공정현황
	 * @param monthlyreportUpdate
	 * @return
	 */
	@PostMapping("/monthlyreport/update")
	@Description(name = "월간보고 업데이트", description = "월간보고 업데이트 - 보고서(비고, 날짜) / 공정현황", type = Description.TYPE.MEHTOD)
	public Result updateMonthlyreport(CommonReqVo commonReqVo, @RequestPart("data") MonthlyreportForm.MonthlyreportUpdate monthlyreportUpdate) throws JsonProcessingException {

		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.FUNCTION.name());
		userLog.setExecType("월간보고 업데이트");

		systemLogComponent.addUserLog(userLog);

		return Result.ok().put("report", monthlyreportService.updateMonthlyreport(
				monthlyreportUpdate.getCntrctNo(),
				monthlyreportUpdate.getPrMonthlyReport(),
				monthlyreportUpdate.getProgressList(),
				monthlyreportForm.toMonthlyreportStatus(monthlyreportUpdate.getMonthlyreportStatusForm()),
				monthlyreportUpdate.getDelPhotoList(),
				monthlyreportUpdate.getMonthlyPhoto()));
	}


	/**
	 * 월간보고 삭제 - 보고서 & Activity & Progress
	 * @param commonReqVo
	 * @param monthlyreportUpdateList
	 * @return
	 */
	@PostMapping("/monthlyreport/delete")
	@Description(name = "월간보고 삭제", description = "월간보고 삭제 - 보고서 & Activity & Progress", type = Description.TYPE.MEHTOD)
	public Result deleteMonthlyreportList(CommonReqVo commonReqVo, @RequestBody MonthlyreportForm.MonthlyreportUpdateList monthlyreportUpdateList) {

		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.FUNCTION.name());
		userLog.setExecType("월간보고 삭제");

		systemLogComponent.addUserLog(userLog);

		monthlyreportComponent.deleteMonthlyreport(monthlyreportForm.toUpdateMonthlyreportInput(monthlyreportUpdateList.getUpdateReportList()));

		return Result.ok();
	}


	/**
	 * 월간보고 Activity 업데이트(삭제 / 추가)
	 * @param updateActivity
	 * @return
	 */
	@PostMapping("/monthlyreport/update-activity")
	@Description(name = "월간보고 Activity 업데이트", description = "월간보고 Activity 업데이트(삭제 / 추가)", type = Description.TYPE.MEHTOD)
	public Result updateActivity(CommonReqVo commonReqVo, @RequestBody MonthlyreportForm.MonthlyreportActivityUpdate updateActivity) {

		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.FUNCTION.name());
		userLog.setExecType("월간보고 Activity 업데이트");

		systemLogComponent.addUserLog(userLog);

		monthlyreportService.updateActivity(monthlyreportForm.toUpdateActivityListInput(updateActivity));

		return Result.ok();
	}



	/**
	 * 월간보고 전자결재 승인요청 -> API 통신
	 * @param monthlyreportUpdateList
	 * @return
	 */
	@PostMapping("/monthlyreport/request-approval")
	@Description(name = "월간보고 전자결재 승인요청", description = "월간보고 전자결재 승인요청 -> API 통신", type = Description.TYPE.MEHTOD)
	public Result requestApprovalMonthlyreport(CommonReqVo commonReqVo, @RequestBody MonthlyreportForm.MonthlyreportUpdateList monthlyreportUpdateList) {

		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.FUNCTION.name());
		userLog.setExecType("월간보고 전자결재 승인요청");

		systemLogComponent.addUserLog(userLog);

		monthlyreportComponent.requestApprovalMonthlyreport(monthlyreportForm.toUpdateMonthlyreportInput(monthlyreportUpdateList.getUpdateReportList()), monthlyreportUpdateList.getCntrctNo(), commonReqVo.getApiYn(), commonReqVo.getPjtDiv());

		return Result.ok();
	}


	/**
	 * 월간보고 공사종류 조회
	 * @param commonReqVo
	 * @param cntrctNo
	 * @return
	 */
	@GetMapping("/monthlyreport/select-unitCnstType")
	@Description(name = "월간보고 공사종류 조회", description = "월간보고 공사종류 조회", type = Description.TYPE.MEHTOD)
	public Result getUnitCnstType(CommonReqVo commonReqVo, @RequestParam String cntrctNo) {
		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("월간보고 공사종류 조회");
        systemLogComponent.addUserLog(userLog);

		return Result.ok().put("unitCnstType", monthlyreportService.getUnitCnstType(cntrctNo));
	}
}
