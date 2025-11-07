package kr.co.ideait.platform.gaiacairos.web.entrypoint.safety;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
@RequestMapping("/safety/educationdiary")
public class EducationDiaryPageController extends AbstractController {
	
	@Autowired
	PortalComponent portalComponent;
	
	/**
	 * 교육일지 목록조회페이지
	 */
	@GetMapping("")
	@Description(name = "교육일지 목록조회화면", description = "교육일지 목록조회화면이로 이동", type = Description.TYPE.MEHTOD)
	public String educationdiaryListPage(CommonReqVo commonReqVo, HttpServletRequest request, Model model) {

		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.VIEW.name());
		userLog.setExecType("교육일지 목록조회화면 이동");
		
		// 리소스 아이디
		String[] rescId = { "EDUC_AD_00", "EDUC_U_00", "EDUC_C_00" };
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
		model.addAttribute("isDelAuth", isDelAuth); // 삭제 권한이 있으면 True 없으면 False/create_ready

		return "page/safety/educationdiary/education_diary";
	}
	
	/**
	 * 교육일지 등록페이지
	 */
	@GetMapping("/create_ready")
	@Description(name = "교육일지 등록화면", description = "교육일지 등록화면으로 이동한다.", type = Description.TYPE.MEHTOD)
	public String educationdiaryCreatePage(CommonReqVo commonReqVo, HttpServletRequest request, Model model) {

		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.VIEW.name());
		userLog.setExecType("교육일지 등록화면 이동");
		
		return "page/safety/educationdiary/education_diary_c";
	}
	
	/**
	 * 교육일지 상세조회 페이지
	 */
	@GetMapping("/read/{eduId}")
	@Description(name = "교육일지 상세조회", description = "교육일지 상세조회화면으로 이동한다.", type = Description.TYPE.MEHTOD)
	public String resourcesReadPage(CommonReqVo commonReqVo, HttpServletRequest request, Model model,
            @RequestParam(value = "type", required = false) String type, @PathVariable("eduId") String eduId) {

		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.VIEW.name());
		userLog.setExecType("교육일지 교육일지아이디(" + eduId + ") 상세조회화면 이동");
		
		String btnHtml = "";
		boolean header = false;
		boolean link = false;
		
		// 새창 설정
        if (type != null) {
        	header = true;
        	link = type.equals("i") ? true : false;
        } else {
        	// 리소스 아이디 
    		String[] rescId = { "EDUC_U_00" };
    		// 버튼에 사용하는 클래스
    		String[] btnClass = { "btn _outline" };
    		// 버튼에 사용하는 함수
    		String[] btnFun = { "onclick=\"page.update()\"" };
    		// 버튼에 사용하는 메시지 아이디
    		String[] btnMsg = { "btn.003" };
    		// 버튼에 아이디 만들기
    		String[] btn_etc = { "id=\"action-button\"" };
    		
    		btnHtml = portalComponent.selectBtnAuthorityList(rescId, btnClass, btnFun, btnMsg, btn_etc);
        }
        
        model.addAttribute("btnHtml", btnHtml);
        model.addAttribute("header", header);
        model.addAttribute("openType", link);
        
		return "page/safety/educationdiary/education_diary_r";
	}
	
	/**
	 * 교육일지 수정페이지
	 */
	@GetMapping("/update_ready/{eduId}")
	@Description(name = "교육일지 수정화면", description = "교육일지 수정화면으로 이동한다.", type = Description.TYPE.MEHTOD)
	public String educationdiaryUpdateePage(CommonReqVo commonReqVo, HttpServletRequest request, Model model) {

		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.VIEW.name());
		userLog.setExecType("교육일지 등록수정화면 이동");
		
		return "page/safety/educationdiary/education_diary_u";
	}

}
