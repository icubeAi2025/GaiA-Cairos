package kr.co.ideait.platform.gaiacairos.web.entrypoint.project;

import jakarta.servlet.http.HttpServletRequest;
import kr.co.ideait.iframework.annotation.Description;
import kr.co.ideait.platform.gaiacairos.comp.portal.PortalComponent;
import kr.co.ideait.platform.gaiacairos.comp.portal.service.PortalService;
import kr.co.ideait.platform.gaiacairos.core.base.AbstractController;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.CommonReqVo;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.log.Log;
import kr.co.ideait.platform.gaiacairos.core.type.LogType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@Controller
@RequestMapping("/project/information")
public class InformationPageController extends AbstractController {

	@Autowired
	PortalComponent portalComponent;

	@Autowired
	PortalService portalService;

	/**
	 * 사업정보 기본페이지
	 */
	@GetMapping("")
	@Description(name = "사업정보 목록 조회 화면", description = "사업정보 목록 조회 화면", type = Description.TYPE.MEHTOD)
	public String information(CommonReqVo commonReqVo,	 @RequestParam(value = "pjtNo", required = false) String pjtNo,
							  @RequestParam(value = "cntrctNo", required = false) String cntrctNo, HttpServletRequest request, Model model) {

		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.VIEW.name());
		userLog.setExecType("사업정보 목록 조회 화면 접속");
		systemLogComponent.addUserLog(userLog);


		// 메뉴 관리에서 등록한 버튼 아이디
		String[] btnId = { "INFO_D_01", "INFO_U_02", "INFO_C_02" };
		// 버튼에 사용하는 클래스
		String[] btnClass = { "btn _outline", "btn _outline", "btn _fill" };
		// 버튼에 사용하는 함수
		String[] btnFun = { "onclick=\"page.info.delete()\"", "onclick=\"page.info.update()\"",
				"onclick=\"page.info.register()\"" };
		// 버튼에 사용하는 메시지 아이디
		String[] btnMsg = { "btn.002", "btn.003", "btn.001" };
		String btnHtml = portalComponent.selectBtnAuthorityList(btnId, btnClass, btnFun, btnMsg);

		boolean isDelAuth = btnHtml.contains("delete");
		model.addAttribute("btnHtml", btnHtml);
		model.addAttribute("isDelAuth", isDelAuth);


		return "page/project/information/information";
	}

	/**
	 * 사업정보 등록
	 */
	@GetMapping("/register")
	@Description(name = "사업정보 추가 화면", description = "사업정보 추가 화면", type = Description.TYPE.MEHTOD)
	public String geRegistertInformation(CommonReqVo commonReqVo, Model model, @RequestParam(value = "type", required = false) String type) {

		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.VIEW.name());
		userLog.setExecType("사업정보 추가 화면 접속");
		systemLogComponent.addUserLog(userLog);

		if ("d".equals(type)) {
			model.addAttribute("header", true);
		} else if ("p".equals(type)) {
			model.addAttribute("header", false);
		}

		return "page/project/information/information_c";
	}

	/**
	 * 사업정보 수정
	 */
	@GetMapping("/update")
	@Description(name = "사업정보 수정 화면", description = "사업정보 수정 화면", type = Description.TYPE.MEHTOD)
	public String getUpdateInformation(CommonReqVo commonReqVo, Model model, @RequestParam(value = "type", required = false) String type) {

		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.VIEW.name());
		userLog.setExecType("사업정보 수정 화면 접속");
		systemLogComponent.addUserLog(userLog);

		if ("d".equals(type)) {
			model.addAttribute("header", true);
		} else if ("p".equals(type)) {
			model.addAttribute("header", false);
		}

		return "page/project/information/information_u";
	}

	/**
	 * 사업정보 조회
	 */
	@GetMapping("/read")
	@Description(name = "사업정보 상세 조회 화면", description = "사업정보 상세 조회 화면", type = Description.TYPE.MEHTOD)
	public String getReadInformation(CommonReqVo commonReqVo, Model model,
									 @RequestParam(value = "type", required = false) String type,
									 @RequestParam(value = "pjtNo", required = false) String pjtNo,
									 @RequestParam(value = "cntrctNo", required = false) String cntrctNo) {

		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.VIEW.name());
		userLog.setExecType("사업정보 상세 조회 화면 접속");
		systemLogComponent.addUserLog(userLog);

		if ("d".equals(type)) {
			model.addAttribute("header", true);
		} else if ("p".equals(type)) {
			model.addAttribute("header", false);
		}

		String[] btnId = {"INFO_U_02"};
		String[] btnClass = {"btn"};
		String[] btnFun = {"onclick='page.enableInputs()'"};
		String[] btnMsg = {"btn.003"};
		String[] btnEtc = {"id='action-button'"};

		String btnHtml = portalComponent.selectBtnAuthorityList(btnId, btnClass, btnFun, btnMsg, btnEtc);
		model.addAttribute("btnHtml", btnHtml);


		return "page/project/information/information_r";
	}
}
