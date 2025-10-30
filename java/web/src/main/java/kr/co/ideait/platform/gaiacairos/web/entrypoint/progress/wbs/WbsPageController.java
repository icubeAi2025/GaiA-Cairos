package kr.co.ideait.platform.gaiacairos.web.entrypoint.progress.wbs;

import jakarta.servlet.http.HttpServletRequest;
import kr.co.ideait.platform.gaiacairos.comp.portal.service.PortalService;
import kr.co.ideait.platform.gaiacairos.core.base.AbstractController;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.log.Log;
import kr.co.ideait.platform.gaiacairos.core.type.LogType;
import kr.co.ideait.platform.gaiacairos.core.util.CookieService;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.CommonReqVo;
import kr.co.ideait.iframework.annotation.Description;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/progress/wbs/wbs")
public class WbsPageController extends AbstractController {

	@Autowired
	PortalService portalService;

	@Autowired
	CookieService cookieService;

	@GetMapping("")
	@Description(name = "공정관리 > WBS 관리 화면", description = "공정관리 > WBS 관리 화면 페이지", type = Description.TYPE.MEHTOD)
	public String getWbs(CommonReqVo commonReqVo) {
		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.VIEW.name());
		userLog.setExecType("WBS 관리 화면 접속");
		systemLogComponent.addUserLog(userLog);

		return "page/progress/wbs/wbs/wbs";
	}

}
