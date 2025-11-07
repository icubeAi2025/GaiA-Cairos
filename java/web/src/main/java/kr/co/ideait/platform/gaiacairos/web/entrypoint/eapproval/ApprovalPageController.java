package kr.co.ideait.platform.gaiacairos.web.entrypoint.eapproval;

import kr.co.ideait.iframework.annotation.Description;
import kr.co.ideait.platform.gaiacairos.comp.eapproval.service.ApprovalService;
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

import java.util.Map;

@Controller
@RequestMapping("/eapproval/approval")
public class ApprovalPageController extends AbstractController {

	@Autowired
	ApprovalService approvalService;

	//상세조회
	@GetMapping("/detail")
	@Description(name = "결재문서 상세 조회 화면", description = "결재문서 상세 조회 화면", type = Description.TYPE.MEHTOD)
	public String getDetailPage(CommonReqVo commonReqVo,
								Model model,
								@RequestParam(value = "page", required = false) String page,
								@RequestParam("apDocId") String apDocId,
								@RequestParam("frmId") String frmId){
		
		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.VIEW.name());
		userLog.setExecType("결재문서 상세 조회 화면 접속");
		systemLogComponent.addUserLog(userLog);

		String pjtNo = commonReqVo.getPjtNo();
		String sendDept = null;
		boolean officialDoc = false;
		if("16".equals(frmId) && ("P202506508".equals(pjtNo) || "P202506503".equals(pjtNo))) {
			Map<String, Object> sendDeptId = approvalService.checkSendDeptId(apDocId);
			if (sendDeptId != null) {
				sendDept = (String)sendDeptId.get("sender_dept");
				officialDoc = true;
				model.addAttribute("sendDept", sendDept);
			}
		}

		if ("p".equals(page)) {
            model.addAttribute("header", false);
        } else {
            model.addAttribute("header", true);
        }

		String imgDir = previewPath.replaceAll("(upload[/\\\\]?).*$", "");
		model.addAttribute("imgDir", imgDir);

		return  officialDoc ? "page/eapproval/approval/approval_O" : "page/eapproval/approval/approval_ru";
	}
	
	//대기
	@GetMapping("/waiting")
	@Description(name = "결재대기 목록 조회 화면", description = "결재가 대기 중인 문서 목록 조회 화면", type = Description.TYPE.MEHTOD)
	public String getWaitingPage(CommonReqVo commonReqVo){
		
		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.VIEW.name());
		userLog.setExecType("결재대기 목록 조회 화면 접속");
		systemLogComponent.addUserLog(userLog);
		
		return "page/eapproval/approval/approval_W";
	}

	//진행
	@GetMapping("/progress")
	@Description(name = "결재진행 목록 조회 화면", description = "결재가 진행 중인 문서 목록 조회 화면", type = Description.TYPE.MEHTOD)
	public String getProgressPage(CommonReqVo commonReqVo){
		
		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.VIEW.name());
		userLog.setExecType("결재진행 목록 조회 화면 접속");
		systemLogComponent.addUserLog(userLog);
		
		return "page/eapproval/approval/approval_P";
	}

	//완료
	@GetMapping("/closed")
	@Description(name = "결재완료 목록 조회 화면", description = "결재가 완료된 목록 조회 화면", type = Description.TYPE.MEHTOD)
	public String getClosedPage(CommonReqVo commonReqVo) {
		
		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.VIEW.name());
		userLog.setExecType("결재완료 목록 조회 화면 접속");
		systemLogComponent.addUserLog(userLog);
		
		return "page/eapproval/approval/approval_01";
	}

	//반려
	@GetMapping("/rejected")
	@Description(name = "결재반려 목록 조회 화면", description = "결재가 반려된 문서 목록 조회 화면", type = Description.TYPE.MEHTOD)
	public String getRejectedPage(CommonReqVo commonReqVo) {
		
		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.VIEW.name());
		userLog.setExecType("결재반려 목록 조회 화면 접속");
		systemLogComponent.addUserLog(userLog);
		
		return "page/eapproval/approval/approval_02";
	}
	
	//참조/공유
	@GetMapping("/shared")
	@Description(name = "참조/공유 문서 조회 화면", description = "결재 완료 후 참조 및 공유된 결재 문서 목록 조회 화면", type = Description.TYPE.MEHTOD)
	public String getSharedPage(CommonReqVo commonReqVo) {
		
		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.VIEW.name());
		userLog.setExecType("참조/공유 목록 조회 화면 접속");
		systemLogComponent.addUserLog(userLog);
		
		return "page/eapproval/approval/approval_03";
	}


}
