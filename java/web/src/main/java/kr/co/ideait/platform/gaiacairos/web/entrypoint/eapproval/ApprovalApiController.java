package kr.co.ideait.platform.gaiacairos.web.entrypoint.eapproval;

import jakarta.servlet.http.HttpServletRequest;
import kr.co.ideait.iframework.annotation.Description;
import kr.co.ideait.platform.gaiacairos.comp.eapproval.ApprovalComponent;
import kr.co.ideait.platform.gaiacairos.comp.eapproval.DraftComponent;
import kr.co.ideait.platform.gaiacairos.core.base.AbstractController;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.ApDoc;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.ApLine;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.GridResult;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.MybatisInput;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.Result;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.CommonReqVo;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.eapproval.approval.ApprovalForm;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.security.UserAuth;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.log.Log;
import kr.co.ideait.platform.gaiacairos.core.type.LogType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;


@Slf4j
@RestController
@RequestMapping("/api/eapproval")
public class ApprovalApiController extends AbstractController {

	@Autowired
	DraftComponent draftComponent;

	@Autowired
	ApprovalComponent approvalComponent;
	
	@Autowired
	ApprovalForm approvalForm;


	/**
	 * 결재문서 리스트 조회
	 * @param approvalList
	 * @return
	 */
	@GetMapping("/approval/list")
	@Description(name = "결재문서 리스트 조회", description = "결재함 별 리스트 조회 시 조회 결과 및 개수 리턴", type = Description.TYPE.MEHTOD)
	public GridResult getApprovalList(CommonReqVo commonReqVo, ApprovalForm.ApprovalList approvalList) {

		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.FUNCTION.name());
		userLog.setExecType("결재문서 리스트 조회");

		systemLogComponent.addUserLog(userLog);

		approvalList.setApUsrId(UserAuth.get(true).getUsrId());
		approvalList.setPjtNo(UserAuth.get(true).getPjtNo());
		approvalList.setCntrctNo(UserAuth.get(true).getCntrctNo());
		approvalList.setPjtType(platform.toUpperCase());

		return GridResult.ok(approvalComponent.getApprovalList(approvalForm.toApprovalListInput(approvalList)));
	}


	/**
	 * 상세검색 셀렉트옵션 - 서식 리스트 조회
	 * @return
	 */
	@GetMapping("/approval/formList")
	@Description(name = "상세검색 서식 리스트 조회", description = "상세검색 팝업_서식 셀렉트 옵션 조회", type = Description.TYPE.MEHTOD)
	public Result getFormAllList(CommonReqVo commonReqVo) {

		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.FUNCTION.name());
		userLog.setExecType("상세검색 서식 리스트 조회");

		systemLogComponent.addUserLog(userLog);

		MybatisInput input = MybatisInput.of()
				.add("pjtNo", UserAuth.get(true).getPjtNo())
				.add("cntrctNo", UserAuth.get(true).getCntrctNo());

		return Result.ok().put("formList", approvalComponent.getFormAllList(input));
	}


	/**
	 * 상세검색 셀렉트옵션 - 문서구분 조회
	 * @return
	 */
	@GetMapping("/approval/optionsList")
	@Description(name = "상세검색 문서구분 리스트 조회", description = "상세검색 팝업_문서구분 셀렉트 옵션 조회", type = Description.TYPE.MEHTOD)
	public Result getSelectOptionsList(CommonReqVo commonReqVo) {

		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.FUNCTION.name());
		userLog.setExecType("상세검색 문서구분 리스트 조회");

		systemLogComponent.addUserLog(userLog);

		return Result.ok().put("apTypeList", approvalComponent.selectApTypeOptionsList());
	}


	/**
	 * 그리드 - 결재선 모달창 조회
	 * @param commonReqVo
	 * @param request
	 * @param apDocId
	 * @return
	 */
	@GetMapping("/approval/approval-line")
	@Description(name = "결재선 모달창 조회", description = "그리드 모달_선택 문서의 결재선 조회", type = Description.TYPE.MEHTOD)
	public Result getApprovalLine(CommonReqVo commonReqVo, HttpServletRequest request, @RequestParam("apDocId") String apDocId) {

		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.FUNCTION.name());
		userLog.setExecType("결재선 모달창 조회");

		systemLogComponent.addUserLog(userLog);

		String userInfo = cookieService.getCookie(request, cookieVO.getPortalCookieName());

		String [] param = userInfo.split(":");
		String userType = param[1];

		MybatisInput input = MybatisInput.of()
				.add("apDocId", apDocId)
				.add("pjtNo", UserAuth.get(true).getPjtNo())
				.add("cntrctNo", UserAuth.get(true).getCntrctNo())
				.add("userType", userType);

		return Result.ok().put("approvalLine", approvalComponent.getApprovalLine(input));

	}


	/**
	 * 결재문서 상세 조회(결재문서, 결재선, 로그인유저 결재상태, 공유자, 첨부파일)
	 * @param commonReqVo
	 * @param apDoc
	 * @return
	 */
	@PostMapping("/approval/approval-details")
	@Description(name = "결재문서 상세 조회", description = "결재문서 상세 조회_결재문서, 결재선, 로그인유저 결재상태, 공유자, 첨부파일 리턴", type = Description.TYPE.MEHTOD)
	public Result getApprovalDetails(CommonReqVo commonReqVo, HttpServletRequest request, @RequestBody ApDoc apDoc){

		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.FUNCTION.name());
		userLog.setExecType("결재문서 상세 조회");

		systemLogComponent.addUserLog(userLog);

		String userInfo = cookieService.getCookie(request, cookieVO.getPortalCookieName());
		String [] param = userInfo.split(":");
		String userType = param[1];

		String pjtNo = apDoc.getPjtNo() == null || apDoc.getPjtNo().isEmpty() ? UserAuth.get(true).getPjtNo() : apDoc.getPjtNo();
		String cntrctNo = apDoc.getCntrctNo() == null || apDoc.getCntrctNo().isEmpty() ? UserAuth.get(true).getCntrctNo() : apDoc.getCntrctNo();
		String apDocId = apDoc.getApDocId();

		MybatisInput input = MybatisInput.of()
				.add("pjtNo", pjtNo)
				.add("cntrctNo", cntrctNo)
				.add("apDocId", apDocId)
				.add("apUsrId", UserAuth.get(true).getUsrId())
				.add("userType", userType);

		return Result.ok().put("returnMap", approvalComponent.getApprovalDetails(input));

	}



	/**
	 * 결재결과 일괄 승인 또는 반려
	 * @param approveList
	 * @return
	 */
	@PostMapping("/approval/updateList")
	@Description(name = "결재결과 일괄 승인 또는 반려", description = "결재결과 일괄 승인 또는 반려 (결재결과만 수정)", type = Description.TYPE.MEHTOD)
	public Result updateListApprovalStatus(CommonReqVo commonReqVo, @RequestBody ApprovalForm.ApproveList approveList) {

		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.FUNCTION.name());
		userLog.setExecType("결재결과 일괄 승인 또는 반려");

		systemLogComponent.addUserLog(userLog);

		Map<String, Object> reqVoMap = new HashMap<String, Object>();
		reqVoMap.put("apiYn", commonReqVo.getApiYn());
		reqVoMap.put("pjtDiv", commonReqVo.getPjtDiv());

		approvalComponent.updateApprovalList(approvalForm.toApproveListInput(approveList), reqVoMap);

		return Result.ok();
	}


	/**
	 * 상세페이지 - 결재문서 단건 업데이트(결재의견, 결재결과, 공유자)
	 * @param commonReqVo
	 * @param approveOne
	 * @return
	 */
	@PostMapping("/approval/updateOne")
	@Description(name = "결재문서 단건 업데이트", description = "상세페이지 내 결재문서 단건 업데이트(결재의견, 결재결과, 공유자 수정)", type = Description.TYPE.MEHTOD)
	public Result updateOneApprovalStatus(CommonReqVo commonReqVo, @RequestBody ApprovalForm.ApproveOne approveOne) {

		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.FUNCTION.name());
		userLog.setExecType("결재문서 단건 업데이트");

		systemLogComponent.addUserLog(userLog);

		Map<String, Object> reqVoMap = new HashMap<String, Object>();
		reqVoMap.put("apiYn", commonReqVo.getApiYn());
		reqVoMap.put("pjtDiv", commonReqVo.getPjtDiv());

		return Result.ok().put("apDoc", approvalComponent.updateApprovalOne(approvalForm.toApproveOneInput(approveOne), reqVoMap));
	}


	/**
	 * 참조자 문서확인 업데이트
	 * @param commonReqVo
	 * @param apLine
	 * @return
	 */
	@PostMapping("/approval/updateReference")
	@Description(name = "참조자 문서확인 업데이트", description = "결재선 상 참조자가 문서확인 시 결재 상태 업데이트", type = Description.TYPE.MEHTOD)
	public Result updateReperenceDate(CommonReqVo commonReqVo, @RequestBody ApLine apLine) {

		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.FUNCTION.name());
		userLog.setExecType("참조자 문서확인 업데이트");

		systemLogComponent.addUserLog(userLog);

		Map<String, Object> reqVoMap = new HashMap<String, Object>();
		reqVoMap.put("apiYn", commonReqVo.getApiYn());
		reqVoMap.put("pjtDiv", commonReqVo.getPjtDiv());

		approvalComponent.updateReferenceDate(apLine, reqVoMap);

		return Result.ok();
	}


	/**
	 * 사용자가 속한 부서 조회
	 * @param commonReqVo
	 * @return
	 */
	@GetMapping("/approval/search-deptInfo")
	@Description(name = "사용자가 속한 부서 조회", description = "공유자 검색 및 조직도 트리_사용자가 속한 부서 조회", type = Description.TYPE.MEHTOD)
	public Result getCompanyInfo(CommonReqVo commonReqVo) {

		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.FUNCTION.name());
		userLog.setExecType("사용자 부서 조회");

		systemLogComponent.addUserLog(userLog);

		MybatisInput input = MybatisInput.of()
				.add("usrId", UserAuth.get(true).getUsrId())
				.add("pjtNo", UserAuth.get(true).getPjtNo());

		// deptId: 부서 ID, deptType: 부서 타입
		if("CAIROS".equals(platform.toUpperCase())) {
			input.add("deptId", "C"+UserAuth.get(true).getCntrctNo());
			input.add("deptType", "C");
		} else {
			input.add("deptId", "G"+UserAuth.get(true).getCntrctNo());
			input.add("deptType", "G");
		}

		return Result.ok().put("deptInfo", approvalComponent.getApprovalDeptInfo(input));

	}


	/**
	 * 부서별 직원 리스트 조회
	 * @param deptId
	 * @return
	 */
	@GetMapping("/approval/search-emp")
	@Description(name = "부서별 직원 리스트 조회", description = "부서별 직원 리스트 조회", type = Description.TYPE.MEHTOD)
	public Result getEmployeeByDeptId(CommonReqVo commonReqVo, @RequestParam("deptId") String deptId) {

		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.FUNCTION.name());
		userLog.setExecType("부서별 직원 리스트 조회");

		systemLogComponent.addUserLog(userLog);

		return Result.ok().put("empList", approvalComponent.selectEmployeeList(deptId));
	}
	
}


