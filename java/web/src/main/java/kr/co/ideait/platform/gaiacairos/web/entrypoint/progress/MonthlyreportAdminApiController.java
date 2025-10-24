package kr.co.ideait.platform.gaiacairos.web.entrypoint.progress;

import kr.co.ideait.iframework.annotation.Description;
import kr.co.ideait.platform.gaiacairos.comp.progress.MonthlyreportAdminComponent;
import kr.co.ideait.platform.gaiacairos.comp.progress.service.MonthlyreportAdminService;
import kr.co.ideait.platform.gaiacairos.comp.progress.service.MonthlyreportService;
import kr.co.ideait.platform.gaiacairos.core.base.AbstractController;
import kr.co.ideait.platform.gaiacairos.core.exception.ErrorType;
import kr.co.ideait.platform.gaiacairos.core.exception.GaiaBizException;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.GridResult;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.Result;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.CommonReqVo;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.progress.monthlyreport.MonthlyreportAdminForm;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.progress.monthlyreport.MonthlyreportForm;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.log.Log;
import kr.co.ideait.platform.gaiacairos.core.type.LogType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/progress/monthlyreportadmin")
public class MonthlyreportAdminApiController extends AbstractController {

	@Autowired
	MonthlyreportAdminComponent monthlyreportAdminComponent;

	@Autowired
	MonthlyreportAdminForm monthlyreportAdminForm;

	/**
	 * 월간보고 관리관용 목록 조회
	 * @param input
	 * @return
	 */
	@PostMapping("/list")
	@Description(name = "월간보고 관리관용 목록 조회", description = "월간보고 관리관용 목록 조회", type = Description.TYPE.MEHTOD)
	public GridResult getMonthlyreportList(CommonReqVo commonReqVo, @RequestBody MonthlyreportAdminForm.MonthlyreportAdmin input) {

		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.FUNCTION.name());
		userLog.setExecType("월간보고 관리관용 목록 조회");

		systemLogComponent.addUserLog(userLog);

		return GridResult.ok(monthlyreportAdminComponent.getMonthlyreportAdminList(monthlyreportAdminForm.toMonthlyreportAdminInput(input)));
	}

	/**
	 * 월간보고 관리관용 상세조회
	 * @param input
	 * @return
	 */
	@PostMapping("/detail")
	@Description(name = "월간보고 관리관용 상세조회", description = "월간보고 관리관용 상세조회", type = Description.TYPE.MEHTOD)
	public Result monthlyreportAdminDetail(CommonReqVo commonReqVo, @RequestBody MonthlyreportAdminForm.MonthlyreportAdmin input) {

		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.FUNCTION.name());
		userLog.setExecType("월간보고 관리관용 상세조회");

		return Result.ok().put("result",monthlyreportAdminComponent.getMonthlyreportAdminDetail(monthlyreportAdminForm.toMonthlyreportAdminInput(input)));
	}

	/**
	 * 월간보고 관리관용 추가
	 * @param input
	 * @return
	 */
	@PostMapping("/create")
	@Description(name = "월간보고 관리관용 추가", description = "월간보고 관리관용 추가", type = Description.TYPE.MEHTOD)
	public Result createMonthlyreportAdmin(CommonReqVo commonReqVo, @RequestBody MonthlyreportAdminForm.MonthlyreportAdmin input) {

		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.FUNCTION.name());
		userLog.setExecType("월간보고 관리관용 추가");

		input.setUsrId(commonReqVo.getUserId());

		return Result.ok().put("result",monthlyreportAdminComponent.createMonthlyreportAdmin(monthlyreportAdminForm.toMonthlyreportAdminInput(input)));
	}

	/**
	 * 월간보고 관리관용 수정
	 * @param input
	 * @return
	 */
	@PostMapping("/update")
	@Description(name = "월간보고 관리관용 수정", description = "월간보고 관리관용 수정", type = Description.TYPE.MEHTOD)
	public Result updateMonthlyreportAdmin(CommonReqVo commonReqVo, @RequestBody MonthlyreportAdminForm.MonthlyreportAdmin input) {

		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.FUNCTION.name());
		userLog.setExecType("월간보고 관리관용 수정");

		input.setUsrId(commonReqVo.getUserId());
		return Result.ok().put("result",monthlyreportAdminComponent.updateMonthlyreportAdmin(monthlyreportAdminForm.toMonthlyreportAdminInput(input)));
	}

	/**
	 * 월간보고 관리관용 삭제
	 * @param input
	 * @return
	 */
	@PostMapping("/delete")
	@Description(name = "월간보고 관리관용 삭제", description = "월간보고 관리관용 삭제", type = Description.TYPE.MEHTOD)
	public Result deleteMonthlyreportAdmin(CommonReqVo commonReqVo, @RequestBody MonthlyreportAdminForm.MonthlyreportAdmin input) {

		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.FUNCTION.name());
		userLog.setExecType("월간보고 관리관용 삭제");

		return Result.ok().put("result",monthlyreportAdminComponent.deleteMonthlyreportAdmin(input.getReportAdminList(),commonReqVo.getUserId()));
	}

}
