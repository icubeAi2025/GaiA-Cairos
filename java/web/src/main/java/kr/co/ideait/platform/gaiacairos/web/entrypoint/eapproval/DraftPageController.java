package kr.co.ideait.platform.gaiacairos.web.entrypoint.eapproval;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import kr.co.ideait.iframework.annotation.Description;
import kr.co.ideait.platform.gaiacairos.core.base.AbstractController;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.DcStorageMain;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.CommonReqVo;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.security.UserAuth;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.log.Log;
import kr.co.ideait.platform.gaiacairos.core.type.LogType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping({"/eapproval", "/interface/page/eapproval"})
public class DraftPageController extends AbstractController {
	
	/**
     * 전자결재 
     */
    @GetMapping("/main")
    @Description(name = "전자결재 대시보드 화면", description = "전자결재 대시보드 화면 (결재요청, 결재대기, 결재진행, 참조/공유)", type = Description.TYPE.MEHTOD)
    public String getMain(CommonReqVo commonReqVo){
    	
    	Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.VIEW.name());
		userLog.setExecType("전자결재 대시보드 화면 접속");
		systemLogComponent.addUserLog(userLog);
		
        return "page/eapproval/draft/app_00_X";
    }
    
    
    /**
     * 전자결재 > 기안문 작성
     */
    @GetMapping("/draft/select-draft")
    @Description(name = "기안문 선택 화면", description = "기안문 작성 페이지 진입 전 기안문 선택 화면", type = Description.TYPE.MEHTOD)
    public String selectDraft(CommonReqVo commonReqVo){
    	
    	Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.VIEW.name());
		userLog.setExecType("기안문 선택 화면 접속");
		systemLogComponent.addUserLog(userLog);
		
        return "page/eapproval/draft/draft_01_S";
    }
    
    
    /**
     * 전자결재 > 기안문 작성
     */
    @GetMapping("/draft/create-draft")
    @Description(name = "기안문 작성 화면", description = "기안문 작성 화면", type = Description.TYPE.MEHTOD)
    public String createDraft(CommonReqVo commonReqVo, @RequestParam(name="url")  String url,
    							@RequestParam(name="frm_no")  String frm_no, 
    							@RequestParam(name="frm_id")  String frm_id,
    							Model model){

    	Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.VIEW.name());
		userLog.setExecType("기안문 작성 화면 접속");
		systemLogComponent.addUserLog(userLog);
		
    	model.addAttribute("url", url);
    	model.addAttribute("frm_no", frm_no);
    	model.addAttribute("frm_id", frm_id);
    	model.addAttribute("USER_ID", UserAuth.get(true).getUsrId());
    	
        return "page/eapproval/draft/form/" + url;
    }
    
    
    /**
     * 전자결재 > 기안문 작성
     */
    @GetMapping("/draft/view-tempDraft")
    @Description(name = "임시저장 기안문 작성 화면", description = "임시저장 기안문 작성 페이지", type = Description.TYPE.MEHTOD)
    public String viewTempDraft(CommonReqVo commonReqVo,
								@RequestParam(name="url")  String url,
    							@RequestParam(name="frmNo")  String frm_no,
    							@RequestParam(name="frmId")  String frm_id,
								@RequestParam(name="apDocId")  String ap_doc_id,
								@RequestParam(name="apDocNo")  String ap_doc_no,
								Model model){

    	Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.VIEW.name());
		userLog.setExecType("임시저장 기안문 작성 화면 접속");
		systemLogComponent.addUserLog(userLog);
		
    	model.addAttribute("url", url);
    	model.addAttribute("frm_no", frm_no);
    	model.addAttribute("frm_id", frm_id);
    	model.addAttribute("tempDocId", ap_doc_id);
    	model.addAttribute("tempDocNo", ap_doc_no);
    	model.addAttribute("USER_ID", UserAuth.get(true).getUsrId());
    	
        return "page/eapproval/draft/form/" + url;
    }
    

    /**
     * 전자결재 > 결재 요청
     * @return
     */
    @GetMapping("/draft/request")
    @Description(name = "결재요청 목록 조회 화면", description = "사용자가 결재 요청한 문서 목록 조회 화면", type = Description.TYPE.MEHTOD)
    public String getRequestPage(CommonReqVo commonReqVo) {
    	
    	Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.VIEW.name());
		userLog.setExecType("결재요청 목록 조회 화면 접속");
		systemLogComponent.addUserLog(userLog);
		
    	return "page/eapproval/draft/draft_02";
    }
    
    
    /**
     * 전자결재 > 임시저장
     * @return
     */
    @GetMapping("/draft/temporary")
    @Description(name = "임시저장 문서 목록 조회 화면", description = "사용자가 임시저장한 문서 목록 조회 화면", type = Description.TYPE.MEHTOD)
    public String getTemporaryPage(CommonReqVo commonReqVo) {
    	
    	Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.VIEW.name());
		userLog.setExecType("임시저장 문서 목록 조회 화면 접속");
		systemLogComponent.addUserLog(userLog);
		
    	return "page/eapproval/draft/draft_03";
    }



	/////////////////////////// interface ///////////////////////////
	/**
	 * 문서관리 -> 전자결재 기안문 작성 페이지 이동
	 * @param commonReqVo
	 * @param requestParams
	 * @param model
	 * @return
	 * @throws JsonProcessingException
	 */
	@PostMapping("/draft/render-draft")
    @Description(name = "문서관리 연동 -> 기안문 작성 화면", description = "문서관리 연동 -> 기안문 작성 화면", type = Description.TYPE.MEHTOD)
    public String renderDraft(CommonReqVo commonReqVo, @RequestParam("requestParams") String requestParams, Model model) throws JsonProcessingException {

    	Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.VIEW.name());
		userLog.setExecType("문서관리 연동 -> 기안문 작성 화면 접속");
		systemLogComponent.addUserLog(userLog);

		Map<String, Object> params = objectMapper.readValue(requestParams, new TypeReference<Map<String, Object>>() {});
		List<DcStorageMain> sharedDcStorageMainList = objectMapper.convertValue(params.get("sharedDcStorageMainList"), new TypeReference<List<DcStorageMain>>() {});

		String uuid = (String) params.get("uuid");
		String frmId = (String) params.get("frmId");
		Integer frmNo = (Integer) params.get("frmNo");
		String sharedDcStorageMainListJson = null;
		if(sharedDcStorageMainList != null && !sharedDcStorageMainList.isEmpty()) {
			sharedDcStorageMainListJson = objectMapper.writeValueAsString(sharedDcStorageMainList).replace("\\", "\\\\");;
		}

		model.addAttribute("sharedDcStorageMainList", sharedDcStorageMainListJson);
		model.addAttribute("url", "draft_01_C");
    	model.addAttribute("frm_no", frmNo);
    	model.addAttribute("frm_id", frmId);
    	model.addAttribute("uuid", uuid);
    	model.addAttribute("cntrctNoFromDoc", (String) params.get("cntrctNo"));
    	model.addAttribute("USER_ID", UserAuth.get(true).getUsrId());

        return "page/eapproval/draft/form/draft_01_C";
    }


	/**
	 * 기안문 작성 > 문서함 조회 팝업
	 * @param commonReqVo
	 * @return
	 */
	@GetMapping("/draft/docBox-popup")
	@Description(name = "기안문 작성 시 문서함 조회 팝업", description = "문서관리 연동 -> 기안문 작성 화면", type = Description.TYPE.MEHTOD)
	public String getDocBoxPopup(CommonReqVo commonReqVo) {
		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.VIEW.name());
		userLog.setExecType("기안문 작성 시 문서함 조회 팝업");
		systemLogComponent.addUserLog(userLog);

		return "page/eapproval/draft/draft_docBox_popup";
	}
}
