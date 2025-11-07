package kr.co.ideait.platform.gaiacairos.web.entrypoint.eapproval;

import kr.co.ideait.platform.gaiacairos.comp.portal.PortalComponent;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.log.Log;
import kr.co.ideait.platform.gaiacairos.core.base.AbstractController;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.security.UserAuth;
import kr.co.ideait.platform.gaiacairos.core.type.LogType;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.CommonReqVo;
import kr.co.ideait.iframework.annotation.Description;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/eapproval/lineset")
public class LineSetPageController extends AbstractController {

	@Autowired
	PortalComponent portalComponent;

	/**
	 * 나의 결재선 조회
	 * @param model
	 * @return
	 */
	@GetMapping("/my")
	@Description(name = "전자결재 나의 결재선 조회 화면", description = "전자결재 나의 결재선 조회 화면", type = Description.TYPE.MEHTOD)
	public String getMyLineSet(CommonReqVo commonReqVo, Model model) {

		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.VIEW.name());
		userLog.setExecType("전자결재 나의 결재선 조회 화면 접속");
		systemLogComponent.addUserLog(userLog);

		model.addAttribute("USER_ID", UserAuth.get(true).getUsrId());

		return "page/eapproval/lineset/my/my_lineset";
	}


	/**
	 * 나의 결재선 추가 모달창
	 * @return
	 */
	@GetMapping("/my-lineset-create")
	@Description(name = "전자결재 나의 결재선 추가 화면", description = "전자결재 나의 결재선 추가 화면", type = Description.TYPE.MEHTOD)
	public String myLinesetCreate(CommonReqVo commonReqVo) {

		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.VIEW.name());
		userLog.setExecType("전자결재 나의 결재선 추가 화면 접속");
		systemLogComponent.addUserLog(userLog);

		return "page/eapproval/lineset/my/my_lineset_C";
	}


	/**
	 * 나의 결재선 수정 모달창
	 * @param model
	 * @param apLineNo
	 * @return
	 */
	@GetMapping("/my-lineset-update/{apLineNo}")
	@Description(name = "전자결재 나의 결재선 수정 화면", description = "전자결재 나의 결재선 수정 화면", type = Description.TYPE.MEHTOD)
	public String myLinesetUpdate(CommonReqVo commonReqVo, Model model,
								  @PathVariable("apLineNo") Integer apLineNo) {

		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.VIEW.name());
		userLog.setExecType("전자결재 나의 결재선 수정 화면 접속");
		systemLogComponent.addUserLog(userLog);

		model.addAttribute("apLineNo", apLineNo);

		return "page/eapproval/lineset/my/my_lineset_U";
	}


	/**
	 * 관리자 결재선 조회
	 * @return
	 */
	@GetMapping("/admin")
	@Description(name = "전자결재 관리자 결재선 조회 화면", description = "전자결재 관리자 결재선 조회 화면", type = Description.TYPE.MEHTOD)
	public String getAdminLineSet(CommonReqVo commonReqVo, Model model,
								  @RequestParam(value = "pjtNo", required = false) String pjtNo,
								  @RequestParam(value = "cntrctNo", required = false) String cntrctNo) {

		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.VIEW.name());
		userLog.setExecType("전자결재 관리자 결재선 조회 화면 접속");
		systemLogComponent.addUserLog(userLog);

		String[] rescId = { "AP_LINESET_ADM_D", "AP_LINESET_ADM_U_01", "AP_LINESET_ADM_C_01" };
		String[] btnClass = { "btn", "btn", "btn _fill" };
		String[] btnFun = { "onclick=\"page.delete()\"", "onclick=\"page.modify()\"", "onclick=\"page.add()\"" };
		String[] btnMsg = { "btn.002", "btn.003", "btn.001" };

		String btnHtml = portalComponent.selectBtnAuthorityList(rescId, btnClass, btnFun, btnMsg);
		model.addAttribute("btnHtml", btnHtml);

		boolean isDelAuth = btnHtml.contains("delete");
		model.addAttribute("isDelAuth", isDelAuth);

		return "page/eapproval/lineset/admin/admin_lineset";
	}


	/**
	 * 관리자 결재선 추가 모달창
	 * @return
	 */
	@GetMapping("/admin-lineset-create")
	@Description(name = "전자결재 관리자 결재선 추가 화면", description = "전자결재 관리자 결재선 추가 화면", type = Description.TYPE.MEHTOD)
	public String adminLinesetCreate(CommonReqVo commonReqVo, Model model,
									 @RequestParam(value = "pjtNo", required = false) String pjtNo,
									 @RequestParam(value = "cntrctNo", required = false) String cntrctNo) {

		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.VIEW.name());
		userLog.setExecType("전자결재 관리자 결재선 추가 화면 접속");
		systemLogComponent.addUserLog(userLog);

		return "page/eapproval/lineset/admin/admin_lineset_C";
	}


	/**
	 * 관리자 결재선 수정 모달창
	 * @param model
	 * @param apLineNo
	 * @return
	 */
	@GetMapping("/admin-lineset-update/{apLineNo}")
	@Description(name = "전자결재 관리자 결재선 수정 화면", description = "전자결재 관리자 결재선 수정 화면", type = Description.TYPE.MEHTOD)
	public String adminLinesetUpdate(CommonReqVo commonReqVo, Model model,
									 @RequestParam(value = "pjtNo", required = false) String pjtNo,
									 @RequestParam(value = "cntrctNo", required = false) String cntrctNo,
									 @PathVariable("apLineNo") Integer apLineNo) {

		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.VIEW.name());
		userLog.setExecType("전자결재 관리자 결재선 수정 화면 접속");
		systemLogComponent.addUserLog(userLog);

		model.addAttribute("apLineNo", apLineNo);

		return "page/eapproval/lineset/admin/admin_lineset_U";
	}

}
