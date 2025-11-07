package kr.co.ideait.platform.gaiacairos.web.entrypoint.system;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpServletRequest;
import kr.co.ideait.iframework.annotation.Description;
import kr.co.ideait.platform.gaiacairos.comp.portal.PortalComponent;
import kr.co.ideait.platform.gaiacairos.core.base.AbstractController;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.CommonReqVo;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.log.Log;
import kr.co.ideait.platform.gaiacairos.core.type.LogType;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/system/resources")
public class ResourcesPageController extends AbstractController {
	
	@Autowired
	PortalComponent portalComponent;
	
	/**
	 * 프로그램관리 기본페이지
	 */
	@GetMapping("")
	@Description(name = "프로그램 목록조회화면", description = "프로그램 목록조회화면이로 이동", type = Description.TYPE.MEHTOD)
	public String resourcesListPage(CommonReqVo commonReqVo, HttpServletRequest request, Model model) {

		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.VIEW.name());
		userLog.setExecType("프로그램 목록조회화면 이동");
		
		// 리소스 아이디
		String[] rescId = { "RESC_D_00", "RESC_U_00", "RESC_C_00" };
		// 버튼에 사용하는 클래스
		String[] btnClass = { "btn", "btn", "btn _fill" };
		// 버튼에 사용하는 함수
		String[] btnFun = { "onclick=\"page.delete()\"", "onclick=\"page.update()\"",
				"onclick=\"page.create()\"" };
		// 버튼에 사용하는 메시지 아이디
		String[] btnMsg = { "btn.002", "btn.003", "btn.001"};
		
		String btnHtml = portalComponent.selectBtnAuthorityList(rescId, btnClass, btnFun, btnMsg);
		boolean isDelAuth = btnHtml.contains("page.delete()");
		
		model.addAttribute("btnHtml", btnHtml);
		model.addAttribute("isDelAuth", isDelAuth); // 삭제 권한이 있으면 True 없으면 False

		return "page/system/resources/resources";
	}
	
	/**
	 * 프로그램관리 입력페이지
	 */
	@GetMapping("/create_ready")
	@Description(name = "프로그램 등록화면", description = "프로그램 등록화면으로 이동", type = Description.TYPE.MEHTOD)
	public String resourcesCreatePage(CommonReqVo commonReqVo, HttpServletRequest request, Model model) {

		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.VIEW.name());
		userLog.setExecType("프로그램 등록화면 이동");
		
		return "page/system/resources/resources_c";
	}
	
	/**
	 * 메뉴명 검색팝업
	 */
	@GetMapping("/menu-search-popup")
	@Description(name = "메뉴 검색팝업", description = "프로그램 등록화면에서 메뉴명 검색 팝업", type = Description.TYPE.MEHTOD)
	public String menuSearchPage(CommonReqVo commonReqVo, HttpServletRequest request, Model model) {

		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.VIEW.name());
		userLog.setExecType("프로그램등록화면에서 메뉴 검색 팝업");
		
		return "page/system/resources/resources_c_popup";
	}
	
	/**
	 * 프로그램 상세조회 페이지
	 */
	@GetMapping("/read/{rescId}")
	@Description(name = "프로그램 상세조회", description = "프로그램 상세조회 페이지 이동", type = Description.TYPE.MEHTOD)
	public String resourcesReadPage(CommonReqVo commonReqVo, HttpServletRequest request, Model model, @PathVariable("rescId") String viewRescId) {

		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.VIEW.name());
		userLog.setExecType("프로그램 아이디(" + viewRescId + ") 상세조회화면 이동");
		
		// 리소스 아이디
		String[] rescId = { "RESC_U_00" };
		// 버튼에 사용하는 클래스
		String[] btnClass = { "btn _outline" };
		// 버튼에 사용하는 함수
		String[] btnFun = { "onclick=\"page.update()\"" };
		// 버튼에 사용하는 메시지 아이디
		String[] btnMsg = { "btn.003" };
		// 버튼에 아이디 만들기
		String[] btn_etc = { "id=\"action-button\"" };
		
		String btnHtml = portalComponent.selectBtnAuthorityList(rescId, btnClass, btnFun, btnMsg, btn_etc);
		
		model.addAttribute("btnHtml", btnHtml);

		return "page/system/resources/resources_r";
	}
	
	/**
	 * 프로그램 수정 페이지
	 */
	@GetMapping("/update/{rescId}")
	@Description(name = "프로그램 수정", description = "프로그램 수정 페이지 이동", type = Description.TYPE.MEHTOD)
	public String resourcesUpdatePage(CommonReqVo commonReqVo, HttpServletRequest request, Model model, @PathVariable("rescId") String rescId) {

		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.VIEW.name());
		userLog.setExecType("프로그램 아이디(" + rescId + ") 수정페이지 이동");

		return "page/system/resources/resources_u";
	}

}
