package kr.co.ideait.platform.gaiacairos.web.entrypoint.progress;

import kr.co.ideait.iframework.annotation.Description;
import kr.co.ideait.platform.gaiacairos.comp.portal.PortalComponent;
import kr.co.ideait.platform.gaiacairos.comp.portal.service.PortalService;
import kr.co.ideait.platform.gaiacairos.core.base.AbstractController;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.CommonReqVo;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.log.Log;
import kr.co.ideait.platform.gaiacairos.core.type.LogType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/progress")
public class MonthlyreportPageController extends AbstractController {

	@Autowired
    PortalService portalService;

	@Autowired
	PortalComponent portalComponent;

	@GetMapping("/monthlyreport")
	@Description(name = "월간공정보고 조회 화면", description = "월간공정보고 조회 화면", type = Description.TYPE.MEHTOD)
	public String getMonthlyreport(CommonReqVo commonReqVo, Model model) {

		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.VIEW.name());
		userLog.setExecType("월간공정보고 조회 화면 접속");
		systemLogComponent.addUserLog(userLog);

		// 삭제, 추가, 수정, 승인요청 버튼
		String[] btnId = {"MNTHRPT_D_01", "MNTHRPT_U_04", "MNTHRPT_C_02", "MNTHRPT_U_03"};
		String[] btnClass = {"btn _outline", "btn _outline", "btn _fill", "btn _outline"};
		String[] btnFun = {"onclick=\"monthlyreport.delete()\"", "onclick=\"monthlyreport.modify()\"", "onclick=\"monthlyreport.add()\"", "onclick=\"monthlyreport.approval()\""};
		String[] btnMsg = {"btn.002", "btn.003", "btn.001", "btn.045"};

		String btnHtml = portalComponent.selectBtnAuthorityList(btnId, btnClass, btnFun, btnMsg);

		model.addAttribute("btnHtml", btnHtml);

		boolean isDelAuth = btnHtml.contains("delete");
		model.addAttribute("isDelAuth", isDelAuth);

		return "page/progress/monthlyreport/monthlyreport";
	}
	
	
	@GetMapping("/monthlyreport/add")
	@Description(name = "월간공정보고 추가 화면", description = "월간공정보고 추가 화면", type = Description.TYPE.MEHTOD)
	public String getMonthlyreportAddPage(CommonReqVo commonReqVo) {
		
		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.VIEW.name());
		userLog.setExecType("월간공정보고 추가 화면 접속");
		systemLogComponent.addUserLog(userLog);

		return "page/progress/monthlyreport/monthlyreport_c"; 
	}
	
	
	@GetMapping("/monthlyreport/detail") 
	@Description(name = "월간공정보고 상세 조회 화면", description = "권한 체크 후 월간공정보고 상세 조회 화면-공정현황, 금월&지연&익월 activity 조회", type = Description.TYPE.MEHTOD)
	public String getMonthlyreportDetail(CommonReqVo commonReqVo, Model model) {

		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.VIEW.name());
		userLog.setExecType("월간공정보고 상세 조회 화면 접속");
		systemLogComponent.addUserLog(userLog);

		// 수정 버튼
		String[] cuBtnId = {"MNTHRPT_U_04"};
		String[] cuBtnClass = {"btn _outline"};
		String[] cuBtnFun = {"onclick=\"reportDetail.modify()\""};
		String[] cuBtnMsg = {"btn.003"};
		String[] cuBtnEtc = {"id=\"modifyBtn\""};

		String btnHtml = portalComponent.selectBtnAuthorityList(cuBtnId, cuBtnClass, cuBtnFun, cuBtnMsg, cuBtnEtc);
		model.addAttribute("btnHtml", btnHtml);

		return "page/progress/monthlyreport/monthlyreport_r";
	}
	
	
	@GetMapping("/monthlyreport/edit")
	@Description(name = "월간공정보고 수정 화면", description = "월간공정보고 수정 화면", type = Description.TYPE.MEHTOD)
	public String getMonthlyreportEditPage(CommonReqVo commonReqVo, Model model,
											@RequestParam("pjtNo") String pjtNo,
											@RequestParam("cntrctNo") String cntrctNo) {
		
		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.VIEW.name());
		userLog.setExecType("월간공정보고 수정 화면 접속");
		systemLogComponent.addUserLog(userLog);

		return "page/progress/monthlyreport/monthlyreport_u"; 
	}
	
	
	@GetMapping("/monthlyreport/major-activity-modal")
	@Description(name = "월간공정보고 금월 액티비티 모달 화면", description = "월간공정보고 금월 추가 액티비티 모달 화면", type = Description.TYPE.MEHTOD)
	public String getMonthlyMajorModal(CommonReqVo commonReqVo) {
		
		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.VIEW.name());
		userLog.setExecType("월간공정보고 금월 액티비티 모달 화면 접속");
		systemLogComponent.addUserLog(userLog);
		
		return "page/progress/monthlyreport/monthlyreport_modal_major"; 
	}
	
	
	@GetMapping("/monthlyreport/delay-activity-modal")
	@Description(name = "월간공정보고 금월 지연 액티비티 모달 화면", description = "월간공정보고 금월 지연 액티비티 모달 화면", type = Description.TYPE.MEHTOD)
	public String getMonthlyDelayModal(CommonReqVo commonReqVo) {
		
		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.VIEW.name());
		userLog.setExecType("월간공정보고 금월 지연 액티비티 모달 화면 접속");
		systemLogComponent.addUserLog(userLog);
		
		return "page/progress/monthlyreport/monthlyreport_modal_delay"; 
	}
	
	
	@GetMapping("/monthlyreport/next-activity-modal")
	@Description(name = "월간공정보고 익월 예정 지연 액티비티 모달 화면", description = "월간공정보고 익월 예정 액티비티 모달 화면", type = Description.TYPE.MEHTOD)
	public String getMonthlyNextModal(CommonReqVo commonReqVo) {
		
		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.VIEW.name());
		userLog.setExecType("월간공정보고 익월 예정 지연 액티비티 모달 화면 접속");
		systemLogComponent.addUserLog(userLog);
		
		return "page/progress/monthlyreport/monthlyreport_modal_next"; 
	}
}
