package kr.co.ideait.platform.gaiacairos.web.entrypoint.progress.wbs;

import kr.co.ideait.platform.gaiacairos.comp.progress.service.ActivityQdbService;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.GridResult;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.progress.wbs.activityqdb.ActivityQdbForm;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.log.Log;
import kr.co.ideait.platform.gaiacairos.core.base.AbstractController;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.Result;
import kr.co.ideait.platform.gaiacairos.core.type.LogType;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.CommonReqVo;
import kr.co.ideait.iframework.annotation.Description;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/progress/wbs")
public class ActivityQdbApiController extends AbstractController {
	
	@Autowired
	ActivityQdbService activityQdbService;
	
	@Autowired
	ActivityQdbForm activityQdbForm;

	
	/**
	 * WBS 탭 - QDB 리스트 조회
	 * @param qdbList
	 * @return
	 */
	@PostMapping("/activityqdb/wbs-qdbList")
	@Description(name = "WBS 탭_QDB 리스트 조회", description = "WBS 탭_QDB 리스트 조회", type = Description.TYPE.MEHTOD)
	public GridResult getWbsQdbList(CommonReqVo commonReqVo, @RequestBody ActivityQdbForm.ActivityWbsQdbList activityWbsQdbList) {
		
		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.FUNCTION.name());
		userLog.setExecType("Activity 내역관리 - WBS 탭_QDB 리스트 조회");

		systemLogComponent.addUserLog(userLog);
		
		return GridResult.ok(activityQdbService.selectActivityWbsQdbList(activityQdbForm.toActivityWbsQdbListInput(activityWbsQdbList)));
	}

	/**
	 * WBS 탭 트리 조회
	 * @param activityTree
	 * @return
	 */
	@GetMapping("/activityqdb/wbsTreeList")
	@Description(name = "WBS 탭_트리 조회", description = " WBS 탭_트리 조회", type = Description.TYPE.MEHTOD)
	public Result getWbsTreeList(CommonReqVo commonReqVo, ActivityQdbForm.ActivityTreeList activityTree){

		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.FUNCTION.name());
		userLog.setExecType("Activity 내역관리 - WBS 탭_트리 조회");

		systemLogComponent.addUserLog(userLog);

		return Result.ok().put("wbsTreeList", activityQdbService.selectWbsTreeList(activityQdbForm.toActivityTreeListInput(activityTree)));
	}
	
	/**
	 * CBS 탭 - detail 리스트 조회
	 * @param activityCbsList
	 * @return
	 */
	@PostMapping("/activityqdb/cbsList")
	@Description(name = "CBS 탭_detail 리스트 조회", description = "CBS 탭_detail 리스트 조회", type = Description.TYPE.MEHTOD)
	public GridResult getCbsList(CommonReqVo commonReqVo, @RequestBody ActivityQdbForm.ActivityCbsList activityCbsList) {
		
		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.FUNCTION.name());
		userLog.setExecType("Activity 내역관리 - CBS 탭_detail 리스트 조회");

		systemLogComponent.addUserLog(userLog);
		
		return GridResult.ok(activityQdbService.selectActivityCbsList(activityQdbForm.toActivityCbsListInput(activityCbsList)));
	}

	/**
	 * CBS 탭 - QDB 리스트 조회
	 * @param activityCbsList
	 * @return
	 */
	@PostMapping("/activityqdb/cbs-qdbList")
	@Description(name = "CBS 탭_QDB 리스트 조회", description = "CBS 탭_QDB 리스트 조회", type = Description.TYPE.MEHTOD)
	public GridResult getCbsQdbList(CommonReqVo commonReqVo, @RequestBody ActivityQdbForm.ActivityCbsQdbList activityCbsList) {
		
		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.FUNCTION.name());
		userLog.setExecType("Activity 내역관리 - CBS 탭_QDB 리스트 조회");

		systemLogComponent.addUserLog(userLog);
		
		return GridResult.ok(activityQdbService.selectActivityCbsQdbList(activityQdbForm.toActivityCbsQdbListInput(activityCbsList)));
	}

	/**
	 * CBS 탭 트리 조회
	 * @param activityTree
	 * @return
	 */
	@GetMapping("/activityqdb/cbsTreeList")
	@Description(name = "CBS 탭_트리 조회", description = "CBS 탭_트리 조회", type = Description.TYPE.MEHTOD)
	public Result getCbsTreeList(CommonReqVo commonReqVo, ActivityQdbForm.ActivityTreeList activityTree){
		
		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.FUNCTION.name());
		userLog.setExecType("Activity 내역관리 - CBS 탭_트리 조회");

		systemLogComponent.addUserLog(userLog);
		
		return Result.ok().put("cbsTreeList", activityQdbService.selectCbsTreeList(activityQdbForm.toActivityTreeListInput(activityTree)));
	}
}
