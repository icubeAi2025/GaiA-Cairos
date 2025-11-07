package kr.co.ideait.platform.gaiacairos.web.entrypoint.progress;

import kr.co.ideait.iframework.annotation.Description;
import kr.co.ideait.platform.gaiacairos.comp.portal.PortalComponent;
import kr.co.ideait.platform.gaiacairos.comp.portal.service.PortalService;
import kr.co.ideait.platform.gaiacairos.core.base.AbstractController;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.CommonReqVo;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.log.Log;
import kr.co.ideait.platform.gaiacairos.core.type.LogType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/progress/monthlyreportadmin")
public class MonthlyreportAdminPageController extends AbstractController {

	@Autowired
    PortalService portalService;

	@Autowired
	PortalComponent portalComponent;

	@Value("${gaia.path.previewPath}")
	String imgDir;

	@GetMapping("/list")
	@Description(name = "월간공정보고 관리관용 목록 조회 화면", description = "월간공정보고 관리관용 목록 조회 화면", type = Description.TYPE.MEHTOD)
	public String monthlyreportAdminList(CommonReqVo commonReqVo, Model model) {

		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.VIEW.name());
		userLog.setExecType("월간공정보고관리관 목록 조회 화면 접속");
		systemLogComponent.addUserLog(userLog);

		// 삭제, 추가, 수정, 승인요청 버튼
		String[] btnId = {"MNTH_ADMIN_D_01", "MNTH_ADMIN_U_01", "MNTH_ADMIN_C_01"};
		String[] btnClass = {"btn _outline", "btn _outline", "btn _fill"};
		String[] btnFun = {"onclick=\"monthlyReportadminGrid.delete()\"", "onclick=\"monthlyReportadminGrid.update()\"", "onclick=\"monthlyReportadminGrid.create()\""};
		String[] btnMsg = {"btn.002", "btn.003", "btn.001"};

		String btnHtml = portalComponent.selectBtnAuthorityList(btnId, btnClass, btnFun, btnMsg);

		model.addAttribute("btnHtml", btnHtml);

		boolean isDelAuth = btnHtml.contains("delete");
		model.addAttribute("isDelAuth", isDelAuth);
		model.addAttribute("imgDir",imgDir);

		return "page/progress/monthlyreportadmin/monthlyreportadmin";
	}

	@GetMapping("/detail")
	@Description(name = "월간공정보고 관리관용 상세조회 화면", description = "월간공정보고 관리관용 상세조회 화면", type = Description.TYPE.MEHTOD)
	public String monthlyreportAdminDetail(CommonReqVo commonReqVo, Model model) {

		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.VIEW.name());
		userLog.setExecType("월간공정보고관리관 상세조회 화면 접속");
		systemLogComponent.addUserLog(userLog);

		// 삭제, 추가, 수정, 승인요청 버튼
		String[] btnId = { "MNTH_ADMIN_U_01" };
		String[] btnClass = { "btn _outline" };
		String[] btnFun = { "onclick=\"page.update()\"" };
		String[] btnMsg = { "btn.003" };

		String btnHtml = portalComponent.selectBtnAuthorityList(btnId, btnClass, btnFun, btnMsg);

		model.addAttribute("btnHtml", btnHtml);

		return "page/progress/monthlyreportadmin/monthlyreportadmin_r";
	}

	@GetMapping("/create")
	@Description(name = "월간공정보고 관리관용 추가 화면", description = "월간공정보고 관리관용 추가 화면", type = Description.TYPE.MEHTOD)
	public String createMonthlyreportAdmin(CommonReqVo commonReqVo) {

		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.VIEW.name());
		userLog.setExecType("월간공정보고관리관 추가 화면 접속");
		systemLogComponent.addUserLog(userLog);

		return "page/progress/monthlyreportadmin/monthlyreportadmin_c";
	}

	@GetMapping("/update")
	@Description(name = "월간공정보고 관리관용 수정 화면", description = "월간공정보고 관리관용 수정 화면", type = Description.TYPE.MEHTOD)
	public String updateMonthlyreportAdmin(CommonReqVo commonReqVo) {

		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.VIEW.name());
		userLog.setExecType("월간공정보고관리관 수정 화면 접속");
		systemLogComponent.addUserLog(userLog);

		return "page/progress/monthlyreportadmin/monthlyreportadmin_u";
	}
}
