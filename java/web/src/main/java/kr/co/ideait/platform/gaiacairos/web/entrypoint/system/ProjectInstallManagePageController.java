package kr.co.ideait.platform.gaiacairos.web.entrypoint.system;

import kr.co.ideait.iframework.annotation.Description;
import kr.co.ideait.iframework.annotation.Description.TYPE;
import kr.co.ideait.platform.gaiacairos.comp.portal.service.PortalService;
import kr.co.ideait.platform.gaiacairos.core.base.AbstractController;
import kr.co.ideait.platform.gaiacairos.core.config.security.annotation.IsUser;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.CommonReqVo;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.log.Log;
import kr.co.ideait.platform.gaiacairos.core.type.LogType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@IsUser
@Controller
@RequestMapping("/system/pjinstall")
public class ProjectInstallManagePageController extends AbstractController {

	@Autowired
	PortalService portalService;

	@GetMapping("")
	@Description(name = "현장 개설 요청 관리 페이지로 이동", description = "현장 개설 요청 관리 페이지로 이동한다", type = TYPE.MEHTOD)
	public String home() {
		return "page/system/pjinstall/list";
	}

	@GetMapping("/{plcReqNo}")
	public String pjInstallDetailPage(){
		return "page/system/pjinstall/detail";
	}

	@GetMapping("/create")
	public String createProjectPage(CommonReqVo commonReqVo, @RequestParam("plcReqNo") String plcReqNo, @RequestParam("type") String type, Model model){
		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.VIEW.name());
		userLog.setExecType("사업정보 추가 화면 접속");
		systemLogComponent.addUserLog(userLog);

		String[] userParam = commonReqVo.getUserParam();

		String[] btnId = { "M010101_F_C" };
		String[] btnClass = { "btn" };
		String[] btnFun = { "onclick='page.save()'" };
		String[] btnMsg = { "btn.006" };
		String[] btnEtc = { "id='action-button'" };

		String btnHtml = portalService.selectBtnAuthorityList(userParam[1], userParam[0], userParam[2], "",
				"", "M010101", btnId, btnClass, btnFun, btnMsg, btnEtc);

//		if ("GAIA".equals(platform.toUpperCase()) || "PGAIA".equals(platform.toUpperCase()) ) {
			model.addAttribute("btnHtml", btnHtml);
//		}

		if ("d".equals(type)) {
			model.addAttribute("header", true);
		} else if ("p".equals(type)) {
			model.addAttribute("header", false);
		}

		return "page/project/information/information_c";
	}

	@GetMapping("/modify")
	public String modifyProjectPage(CommonReqVo commonReqVo, @RequestParam("pjtNo") String pjtNo, Model model){
		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.VIEW.name());
		userLog.setExecType("사업정보 수정 화면 접속");
		systemLogComponent.addUserLog(userLog);

		String[] userParam = commonReqVo.getUserParam();

		String[] btnId = { "M010101_F_C" };
		String[] btnClass = { "btn" };
		String[] btnFun = { "onclick='page.save()'" };
		String[] btnMsg = { "btn.006" };
		String[] btnEtc = { "id='action-button'" };

		String btnHtml = portalService.selectBtnAuthorityList(userParam[1], userParam[0], userParam[2], "",
				"", "M010101", btnId, btnClass, btnFun, btnMsg, btnEtc);

//		if ("GAIA".equals(platform.toUpperCase()) || "PGAIA".equals(platform.toUpperCase()) ) {
			model.addAttribute("btnHtml", btnHtml);
//		}

		model.addAttribute("header", false);

		return "page/project/information/information_u";
	}
}
