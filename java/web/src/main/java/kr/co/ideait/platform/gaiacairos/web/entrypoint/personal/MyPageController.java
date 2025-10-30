package kr.co.ideait.platform.gaiacairos.web.entrypoint.personal;

import kr.co.ideait.platform.gaiacairos.core.base.AbstractController;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.CommonReqVo;
import kr.co.ideait.iframework.annotation.Description;
import kr.co.ideait.iframework.annotation.Description.TYPE;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.log.Log;
import kr.co.ideait.platform.gaiacairos.core.type.LogType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequestMapping("/")
public class MyPageController extends AbstractController {

	/**
	 * 마이페이지 이동
	 */
	
	@GetMapping("/mypage")
	@Description(name = "마이페이지 이동", description = "마이페이지로 이동", type = TYPE.MEHTOD)
	public String mypage(CommonReqVo commonReqVo) {
		// 공통로그
		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.VIEW.name());
		userLog.setExecType("마이페이지");
		systemLogComponent.addUserLog(userLog);

		return "page/portal/mypage";
	}

	@GetMapping("/checkpw")
	public String checkpw(CommonReqVo commonReqVo) {
		return "sub/password_check";
	}
}
