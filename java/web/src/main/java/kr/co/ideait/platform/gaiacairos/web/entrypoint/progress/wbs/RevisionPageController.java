package kr.co.ideait.platform.gaiacairos.web.entrypoint.progress.wbs;

import kr.co.ideait.platform.gaiacairos.comp.portal.PortalComponent;
import kr.co.ideait.platform.gaiacairos.comp.portal.service.PortalService;
import kr.co.ideait.platform.gaiacairos.core.base.AbstractController;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.log.Log;
import kr.co.ideait.platform.gaiacairos.core.type.LogType;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.CommonReqVo;
import kr.co.ideait.iframework.annotation.Description;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@Controller
@RequestMapping("/progress/wbs")
public class RevisionPageController extends AbstractController {

	@Autowired
    PortalService portalService;

	@Autowired
	PortalComponent portalComponent;

	@GetMapping("/revision")
	@Description(name = "revision 목록 조회 화면", description = "권한에 따른 버튼설정 후, revision 화면 페이지 반환", type = Description.TYPE.MEHTOD)
	public String getRevisionPage(CommonReqVo commonReqVo, Model model) {
		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.FUNCTION.name());
		userLog.setExecType("revision 목록 조회 화면 반환");

		systemLogComponent.addUserLog(userLog);

		// 삭제, 가져오기 페이지 이동 버튼
		String[] btnId = { "REVISION_D_01", "REVISION_C_02"};
		String[] btnClass = { "btn _outline", "btn _outline" };
		String[] btnFun = { "onclick=\"revision.delete();\"", "onclick=\"revision.loadP6Data();\"" };
		String[] btnMsg = { "btn.002", "btn.037" };

		String btnHtml = portalComponent.selectBtnAuthorityList(btnId, btnClass, btnFun, btnMsg);

		model.addAttribute("btnHtml", btnHtml);

		boolean isDelAuth = btnHtml.contains("delete");
		model.addAttribute("isDelAuth", isDelAuth);

		return "page/progress/wbs/revision/revision";
	}
	
	@GetMapping("/revision/create")
	@Description(name = "revision 생성 화면", description = "권한에 따른 버튼설정 후, revision 생성 페이지 반환", type = Description.TYPE.MEHTOD)
	public String getRevisionCreatePage(CommonReqVo commonReqVo, Model model, @RequestParam(value = "type", required = false) String type) {
		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.FUNCTION.name());
		userLog.setExecType("revision 생성 화면");

		systemLogComponent.addUserLog(userLog);

		// 팝업 유무 확인 "p" - 팝업 , "d" - 일반
		if (type == null) type = "d";

		if("p".equals(type)) {
			model.addAttribute("header", false);
		} else {
			model.addAttribute("header", true);
		}

		// 가져오기 버튼
		String[] addBtnId = { "REVISION_C_01"};
		String[] addBtnClass = { "btn _fill" };
		String[] addBtnFun = { "onclick=\"revision.validate('c');\"" };
		String[] addBtnMsg = { "btn.037" };

		String addBtnHtml = portalComponent.selectBtnAuthorityList(addBtnId, addBtnClass, addBtnFun, addBtnMsg);

		// 덮어쓰기 버튼
		String[] updateBtnId = { "REVISION_U_01"};
		String[] updateBtnClass = { "btn _fill" };
		String[] updateBtnFun = { "onclick=\"revision.validate('c');\"" };
		String[] updateBtnMsg = { "item.revision.007" };

		String updateBtnYn = portalComponent.selectBtnAuthorityList(updateBtnId, updateBtnClass, updateBtnFun, updateBtnMsg);

		if(!updateBtnYn.isEmpty()){
			StringBuilder btn = new StringBuilder();

			btn.append( " <label class=\"form_check\">" +
					" <input class=\"check_mark\" type=\"checkbox\" id=\"update_checkbox\" >\n" +
					" <span class=\"check_label\">" +
					messageSource.getMessage("item.revision.007", null, LocaleContextHolder.getLocale()) +
					" </span>\n" +
					" </label>");

			String updateBtnHtml = btn.toString();
			model.addAttribute("updateBtnHtml", updateBtnHtml);
		}

		model.addAttribute("addBtnHtml", addBtnHtml);

		boolean isAddAuth = addBtnHtml.contains("validate('c')");
		model.addAttribute("isAddAuth", isAddAuth);

		return "page/progress/wbs/revision/revision_c";
	}

}
