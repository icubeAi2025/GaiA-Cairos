package kr.co.ideait.platform.gaiacairos.web.entrypoint.progress;

import kr.co.ideait.platform.gaiacairos.comp.portal.PortalComponent;
import kr.co.ideait.platform.gaiacairos.comp.portal.service.PortalService;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.log.Log;
import kr.co.ideait.platform.gaiacairos.core.base.AbstractController;
import kr.co.ideait.platform.gaiacairos.core.type.LogType;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.CommonReqVo;
import kr.co.ideait.iframework.annotation.Description;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/progress")
public class WeeklyreportPageController extends AbstractController{

	@Autowired
    PortalService portalService;

	@Autowired
	PortalComponent portalComponent;
	
	@GetMapping("/weeklyreport")
	@Description(name = "주간공정보고 조회 화면", description = "주간공정보고 조회 화면", type = Description.TYPE.MEHTOD)
	public String getWeeklyreport(CommonReqVo commonReqVo, Model model) {

		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.VIEW.name());
		userLog.setExecType("주간공정보고 조회 화면 접속");
		systemLogComponent.addUserLog(userLog);

		// 삭제, 추가, 수정, 승인요청 버튼
		String[] btnId = {"WKLYRPT_D_01", "WKLYRPT_U_04", "WKLYRPT_C_02", "WKLYRPT_U_03"};
		String[] btnClass = {"btn _outline", "btn _outline", "btn _fill", "btn _outline"};
		String[] btnFun = {"onclick=\"report.delete()\"", "onclick=\"report.modify()\"", "onclick=\"report.add()\"", "onclick=\"report.approval()\""};
		String[] btnMsg = {"btn.002", "btn.003", "btn.001", "btn.045"};

		String btnHtml = portalComponent.selectBtnAuthorityList(btnId, btnClass, btnFun, btnMsg);

		model.addAttribute("btnHtml", btnHtml);

		boolean isDelAuth = btnHtml.contains("delete");
		model.addAttribute("isDelAuth", isDelAuth);

		return "page/progress/weeklyreport/weeklyreport"; 
	}

	@GetMapping("/weeklyreport/detail")
	@Description(name = "주간공정보고 상세 조회 화면", description = "주간공정보고 상세 조회 화면", type = Description.TYPE.MEHTOD)
	public String getWeeklyreportDetail(CommonReqVo commonReqVo, Model model) {

		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.VIEW.name());
		userLog.setExecType("주간공정보고 상세 조회 화면 접속");
		systemLogComponent.addUserLog(userLog);


		// 수정 페이지 이동 버튼
		String[] cuBtnId = { "WKLYRPT_U_04" };
		String[] cuBtnClass = { "btn _outline" };
		String[] cuBtnFun = { "onclick=\"reportDetail.modify()\"" };
		String[] cuBtnMsg = { "btn.003" };
		String[] cuBtnEtc = { "id=\"modifyBtn\"" };

		String btnHtml = portalComponent.selectBtnAuthorityList(cuBtnId, cuBtnClass, cuBtnFun, cuBtnMsg, cuBtnEtc);
		model.addAttribute("btnHtml", btnHtml);

		return "page/progress/weeklyreport/weeklyreport_r";
	}

	@GetMapping("/weeklyreport/add")
	@Description(name = "주간공정보고 추가 화면", description = "주간공정보고 추가 화면", type = Description.TYPE.MEHTOD)
	public String getWeeklyreportAddPage(CommonReqVo commonReqVo) {
		
		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.VIEW.name());
		userLog.setExecType("주간공정보고 추가 화면 접속");
		systemLogComponent.addUserLog(userLog);

		return "page/progress/weeklyreport/weeklyreport_c"; 
	}
	
	@GetMapping("/weeklyreport/edit")
	@Description(name = "주간공정보고 수정 화면", description = "주간공정보고 수정 화면", type = Description.TYPE.MEHTOD)
	public String getWeeklyreportEditPage(CommonReqVo commonReqVo) {
		
		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.VIEW.name());
		userLog.setExecType("주간공정보고 수정 화면 접속");
		systemLogComponent.addUserLog(userLog);

		return "page/progress/weeklyreport/weeklyreport_u"; 
	}
	
	
	@GetMapping("/weeklyreport/major-activity-modal")
	@Description(name = "주간공정보고 금주 액티비티 모달 화면", description = "주간공정보고 금주 추가 액티비티 모달 화면", type = Description.TYPE.MEHTOD)
	public String getWeeklyMajorModal(CommonReqVo commonReqVo) {
		
		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.VIEW.name());
		userLog.setExecType("주간공정보고 금주 액티비티 모달 화면 접속");
		systemLogComponent.addUserLog(userLog);
		
		return "page/progress/weeklyreport/weeklyreport_modal_major"; 
	}
	
	
	@GetMapping("/weeklyreport/delay-activity-modal")
	@Description(name = "주간공정보고 금주 지연 액티비티 모달 화면", description = "주간공정보고 금주 지연 액티비티 모달 화면", type = Description.TYPE.MEHTOD)
	public String getWeeklyDelayModal(CommonReqVo commonReqVo) {
		
		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.VIEW.name());
		userLog.setExecType("주간공정보고 금주 지연 액티비티 모달 화면 접속");
		systemLogComponent.addUserLog(userLog);
		
		return "page/progress/weeklyreport/weeklyreport_modal_delay"; 
	}
	
	
	@GetMapping("/weeklyreport/next-activity-modal")
	@Description(name = "주간공정보고 차주 예정 지연 액티비티 모달 화면", description = "주간공정보고 차주 예정 액티비티 모달 화면", type = Description.TYPE.MEHTOD)
	public String getWeeklyNextModal(CommonReqVo commonReqVo) {
		
		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.VIEW.name());
		userLog.setExecType("주간공정보고 차주 예정 지연 액티비티 모달 화면 접속");
		systemLogComponent.addUserLog(userLog);
		
		return "page/progress/weeklyreport/weeklyreport_modal_next"; 
	}
}
