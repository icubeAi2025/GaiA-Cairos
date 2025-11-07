package kr.co.ideait.platform.gaiacairos.web.entrypoint.defecttracking;

import kr.co.ideait.iframework.annotation.Description;
import kr.co.ideait.platform.gaiacairos.comp.defecttracking.SettingComponent;
import kr.co.ideait.platform.gaiacairos.comp.defecttracking.helper.DefectTrackingHelper;
import kr.co.ideait.platform.gaiacairos.core.base.AbstractController;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.MybatisInput;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.MybatisOutput;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.Result;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.CommonReqVo;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.defecttracking.tool.setting.SettingForm;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.defecttracking.tool.setting.SettingForm.*;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.security.UserAuth;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.log.Log;
import kr.co.ideait.platform.gaiacairos.core.type.LogType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/defecttracking")
public class SettingApiController extends AbstractController {

	@Autowired
	SettingComponent settingComponent;

	@Autowired
	DefectTrackingHelper defectTrackingHelper;

	@Autowired
	SettingForm settingForm;


	/**
	 * 결함단계 목록 조회
	 * @param cntrctNo
	 * @return
	 */
	@GetMapping("/setting/list/{cntrctNo}")
	@Description(name = "결함추적 결함단계 목록 조회", description = "결함추적 결함단계 목록 조회", type = Description.TYPE.MEHTOD)
	public Result getPhaseList(CommonReqVo commonReqVo, @PathVariable("cntrctNo") String cntrctNo) {
		
		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.FUNCTION.name());
		userLog.setExecType("결함추적 결함단계 목록 조회");

		systemLogComponent.addUserLog(userLog);

		MybatisInput input = MybatisInput.of().add("cntrctNo", cntrctNo);

		return Result.ok().put("selectPhaseList", settingComponent.selectDeficiencyPhaseList(input));
	}

	
	/**
	 * 결함단계 상세조회(결함, 답변, 확인, 종결의 상태조회)
	 * @param deficientyPhaseDetail
	 * @return
	 */
	@PostMapping("/setting/detail")
	@Description(name = "결함단계 상세조회", description = "결함단계 상세조회(결함, 답변, 확인, 종결의 상태조회)", type = Description.TYPE.MEHTOD)
	public Result getDeficiencyPhase(CommonReqVo commonReqVo, @RequestBody DeficientyPhaseDetail deficientyPhaseDetail) {
		
		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.FUNCTION.name());
		userLog.setExecType("결함추적 상세조회");

		systemLogComponent.addUserLog(userLog);

		MybatisInput input = MybatisInput.of()
				.add("cntrctNo", deficientyPhaseDetail.getCntrctNo())
				.add("dfccyPhaseNo", deficientyPhaseDetail.getDfccyPhaseNo());
		
		return Result.ok().put("phase", settingComponent.selectDeficiencyPhaseList(input));
	}


	/**
	 * 결함단계 추가 (결함, 답변, 확인, 종결의 기간 설정)
	 * @param commonReqVo
	 * @param deficientyPhaseInsert
	 * @return
	 */
	@PostMapping("/setting/create")
	@Description(name = "결함단계 추가", description = "결함단계 추가(결함, 답변, 확인, 종결의 기간 설정)", type = Description.TYPE.MEHTOD)
	public Result insertPhase(CommonReqVo commonReqVo, @RequestBody DeficientyPhaseInsert deficientyPhaseInsert) {
		
		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.FUNCTION.name());
		userLog.setExecType("결함추적 결함단계 추가");

		systemLogComponent.addUserLog(userLog);

		settingComponent.insertDeficiencyPhase(deficientyPhaseInsert.getDtDeficiencyPhase(), deficientyPhaseInsert.getScheduleArr(), defectTrackingHelper.createReqVoMap(commonReqVo));
		
		return Result.ok();
	}

	
	/**
	 * 결함단계 수정(단계명, 시작일, 종료일 변경)
	 * @param deficientyPhaseInsert
	 * @return
	 */
	@PostMapping("/setting/update")
	@Description(name = "결함단계 수정", description = "결함단계 수정(단계명, 시작일, 종료일 변경)", type = Description.TYPE.MEHTOD)
	public Result updatePhase(CommonReqVo commonReqVo, @RequestBody DeficientyPhaseInsert deficientyPhaseInsert) {
		
		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.FUNCTION.name());
		userLog.setExecType("결함추적 결함단계 수정");

		systemLogComponent.addUserLog(userLog);

		settingComponent.updateDeficiencyPhase(deficientyPhaseInsert.getDtDeficiencyPhase(), deficientyPhaseInsert.getScheduleArr(), defectTrackingHelper.createReqVoMap(commonReqVo));
		
		return Result.ok();
	}


	/**
	 * 결함단계 삭제(결함 단계 및 일정) -> 삭제 이후 순서 재정렬
	 * @param commonReqVo
	 * @param deficientyPhaseDeleteList
	 * @return
	 */
	@PostMapping("/setting/delete")
	@Description(name = "결함단계 삭제", description = "결함단계 삭제(결함 단계 및 일정) -> 삭제 이후 순서 재정렬", type = Description.TYPE.MEHTOD)
	public Result deletePhase(CommonReqVo commonReqVo, @RequestBody DeficientyPhaseDeleteList deficientyPhaseDeleteList) {
		
		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.FUNCTION.name());
		userLog.setExecType("결함추적 결함단계 삭제");

		systemLogComponent.addUserLog(userLog);

		settingComponent.deleteDeficiencyPhase(
				deficientyPhaseDeleteList.getDelPhaseList(),
				deficientyPhaseDeleteList.getCntrctNo(),
				UserAuth.get(true).getPjtNo(),
				true,
				UserAuth.get(true).getUsrId(),
				defectTrackingHelper.createReqVoMap(commonReqVo)
		);
		
		return Result.ok();
	}


	/**
	 * 결함단계 순서 변경(up, down 이동)
	 * @param commonReqVo
	 * @param displayOrderMove
	 * @return
	 */
	@PostMapping("/setting/move")
	@Description(name = "결함단계 순서 변경", description = "결함단계 순서 변경(up, down 이동)", type = Description.TYPE.MEHTOD)
	public Result movePhase(CommonReqVo commonReqVo, @RequestBody List<DisplayOrderMove> displayOrderMove) {
		
		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.FUNCTION.name());
		userLog.setExecType("결함추적 결함단계 순서 변경");

		systemLogComponent.addUserLog(userLog);

		settingComponent.updateDisplayOrder(settingForm.toDisplayOrderMoveInput(displayOrderMove),
				UserAuth.get(true).getUsrId(),
				UserAuth.get(true).getPjtNo(),
				true,
				defectTrackingHelper.createReqVoMap(commonReqVo)
		);
		return Result.ok();
	}


	/**
	 * 결함추적 대시보드 목록 조회
	 * @param commonReqVo
	 * @param dashboardList
	 * @return
	 */
	@PostMapping("/dashboard")
	@Description(name = "결함추적 대시보드 목록 조회", description = "결함추적 대시보드 목록 조회", type = Description.TYPE.MEHTOD)
	public Result getDashboardList(CommonReqVo commonReqVo, @RequestBody DashboardList dashboardList) {

		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.FUNCTION.name());
		userLog.setExecType("결함추적 대시보드 목록 조회");

		systemLogComponent.addUserLog(userLog);

		dashboardList.setUsrId(UserAuth.get(true).getUsrId());

		int page = dashboardList.getPage();
		int size = dashboardList.getSize();

		Pageable pageable = PageRequest.of(page - 1, size);

		Page<MybatisOutput> pageData = settingComponent.getDashboardList(settingForm.toDashboardListInput(dashboardList), pageable);
		Long totalCount = pageData.getTotalElements();

		return Result.ok().put("dfccyList", pageData.getContent())
				.put("totalCount", totalCount);

	}
}
