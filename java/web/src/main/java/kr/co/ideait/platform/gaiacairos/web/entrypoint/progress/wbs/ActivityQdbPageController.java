package kr.co.ideait.platform.gaiacairos.web.entrypoint.progress.wbs;

import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.log.Log;
import kr.co.ideait.platform.gaiacairos.core.base.AbstractController;
import kr.co.ideait.platform.gaiacairos.core.type.LogType;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.CommonReqVo;
import kr.co.ideait.iframework.annotation.Description;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/progress/wbs")
public class ActivityQdbPageController extends AbstractController {
	
	@GetMapping("/activityqdb")
	@Description(name = "Activity 내역관리 조회 화면", description = "Activity 내역관리 조회 화면", type = Description.TYPE.MEHTOD)
	public String getActivityqdbPage(CommonReqVo commonReqVo) {
		
		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.VIEW.name());
		userLog.setExecType("Activity 내역 관리 조회 화면 접속");
		systemLogComponent.addUserLog(userLog);
		
		return "page/progress/wbs/activityqdb/activityqdb";
	}
	
}
