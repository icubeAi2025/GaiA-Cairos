package kr.co.ideait.platform.gaiacairos.web.entrypoint.design;

import kr.co.ideait.iframework.annotation.Description;
import kr.co.ideait.platform.gaiacairos.comp.design.DesignSettingComponent;
import kr.co.ideait.platform.gaiacairos.core.base.AbstractController;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.Result;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.CommonReqVo;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.design.tool.setting.DesignSettingForm;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.log.Log;
import kr.co.ideait.platform.gaiacairos.core.type.LogType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/design/setting")
public class DesignSettingApiController extends AbstractController {

	@Autowired
	DesignSettingComponent designSettingComponent;

	/**
	 * 설계검토단계 목록 조회
	 * @param cntrctNo
	 * @return
	 */
	@GetMapping("/list/{cntrctNo}")
	@Description(name = "설계검토단계 목록 조회", description = "설계검토단계 목록 조회", type = Description.TYPE.MEHTOD)
	public Result getPhaseList(CommonReqVo commonReqVo, @PathVariable("cntrctNo") String cntrctNo) {
		
		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.FUNCTION.name());
		userLog.setExecType("설계검토단계 목록 조회");

		List selectPhaseList = designSettingComponent.getDesignSettingListData(cntrctNo);
		userLog.setResult("성공");
		systemLogComponent.addUserLog(userLog);
		
		return Result.ok().put("selectPhaseList",selectPhaseList);
	}

	
	/**
	 * 설계검토단계 상세조회(검토의견, 답변, 평가, 백체크의 상태조회)
	 * @param designPhaseDetail
	 * @return
	 */
	@PostMapping("/detail")
	@Description(name = "설계검토단계 상세조회", description = "설계검토단계 상세조회(검토의견, 답변, 평가, 백체크의 상태조회)", type = Description.TYPE.MEHTOD)
	public Result getDesignPhase(CommonReqVo commonReqVo, @RequestBody DesignSettingForm.DesignPhaseDetail designPhaseDetail) {
		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.FUNCTION.name());
		userLog.setExecType("설계검토단계 상세조회");
		List phase = designSettingComponent.getDetailSettingData(designPhaseDetail);

		userLog.setResult("성공");
		systemLogComponent.addUserLog(userLog);

		return Result.ok().put("phase",phase);
	}

	
	/**
	 * 설계검토단계 추가(검토의견, 답변, 평가, 백체크 기간 설정)
	 * @param designPhaseInsert
	 * @return
	 */
	@PostMapping("/create")
	@Description(name = "설계검토단계 추가", description = "설계검토단계 추가(검토의견, 답변, 평가, 백체크 기간 설정)", type = Description.TYPE.MEHTOD)
	public Result insertDesignPhase(CommonReqVo commonReqVo, @RequestBody DesignSettingForm.DesignPhaseInsert designPhaseInsert) {
		designSettingComponent.registDesignPhase(designPhaseInsert.getDmDesignPhase(), designPhaseInsert.getScheduleArr(), commonReqVo);

		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.FUNCTION.name());
		userLog.setExecType("설계검토단계 추가");
		systemLogComponent.addUserLog(userLog);

		return Result.ok();
	}

	
	/**
	 * 설계검토단계 수정(단계명, 시작일, 종료일 변경)
	 * @param designPhaseInsert
	 * @return
	 */
	@PostMapping("/update")
	@Description(name = "설계검토단계 수정", description = "설계검토단계 수정(단계명, 시작일, 종료일 변경)", type = Description.TYPE.MEHTOD)
	public Result updateDesignPhase(CommonReqVo commonReqVo, @RequestBody DesignSettingForm.DesignPhaseInsert designPhaseInsert) {
		designSettingComponent.modifyDesignPhase(designPhaseInsert.getDmDesignPhase(), designPhaseInsert.getScheduleArr(), commonReqVo);

		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.FUNCTION.name());
		userLog.setExecType("설계검토단계 수정");
		systemLogComponent.addUserLog(userLog);

		return Result.ok();
	}

	
	/**
	 * 설계검토단계 삭제(결함 단계 및 일정) -> 삭제 이후 순서 재정렬
	 * @param designPhaseDeleteList
	 * @return
	 */
	@PostMapping("/delete")
	@Description(name = "설계검토단계 삭제", description = "설계검토단계 삭제(결함 단계 및 일정) -> 삭제 이후 순서 재정렬", type = Description.TYPE.MEHTOD)
	public Result deleteDesignPhase(CommonReqVo commonReqVo, @RequestBody DesignSettingForm.DesignPhaseDeleteList designPhaseDeleteList) {
		designSettingComponent.removeDesignPhase(designPhaseDeleteList.getDelPhaseList(), designPhaseDeleteList.getCntrctNo(), commonReqVo);

		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.FUNCTION.name());
		userLog.setExecType("설계검토단계 삭제");
		systemLogComponent.addUserLog(userLog);

		return Result.ok();
	}

	
	/**
	 * 설계검토단계 순서 변경(up, down 이동)
	 * @param designDisplayOrderMove
	 * @return
	 */
	@PostMapping("/move")
	@Description(name = "설계검토단계 순서 변경", description = "설계검토단계 순서 변경(up, down 이동)", type = Description.TYPE.MEHTOD)
	public Result movePhase(CommonReqVo commonReqVo, @RequestBody List<DesignSettingForm.DesignDisplayOrderMove> designDisplayOrderMove) {
		designSettingComponent.modifyDesignPhaseDisplayOrder(designDisplayOrderMove, commonReqVo);

		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.FUNCTION.name());
		userLog.setExecType("설계검토단계 순서 변경");
		systemLogComponent.addUserLog(userLog);

		return Result.ok();
	}

}
