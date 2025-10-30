package kr.co.ideait.platform.gaiacairos.web.entrypoint.eapproval;


import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import kr.co.ideait.iframework.annotation.Description;
import kr.co.ideait.platform.gaiacairos.comp.document.DocumentComponent;
import kr.co.ideait.platform.gaiacairos.comp.eapproval.ApprovalComponent;
import kr.co.ideait.platform.gaiacairos.comp.eapproval.DraftComponent;
import kr.co.ideait.platform.gaiacairos.comp.system.service.DepartmentService;
import kr.co.ideait.platform.gaiacairos.core.base.AbstractController;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.ApDoc;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.MybatisInput;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.Result;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.CommonReqVo;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.eapproval.approval.ApprovalMybatisParam.ApprovalListInput;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.eapproval.approval.ApprovalMybatisParam.ApprovalListOutput;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.eapproval.draft.DraftForm;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.security.UserAuth;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.log.Log;
import kr.co.ideait.platform.gaiacairos.core.type.LogType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/eapproval")
public class DraftApiController extends AbstractController {
	
	@Autowired
	DraftComponent draftComponent;

	@Autowired
	ApprovalComponent approvalComponent;

	@Autowired
	DocumentComponent documentComponent;

	@Autowired
	DepartmentService departmentService;
	
	@Autowired
	DraftForm draftForm;


	/**
	 * 전자결재 대시보드 페이지
	 * @param dashoardForm
	 * @return
	 */
	@PostMapping("/main/dashBoard")
	@Description(name = "전자결재 대시보드 페이지", description = "전자결재 대시보드 페이지 조회(결재요청, 결재대기, 결재진행, 참조/공유)", type = Description.TYPE.MEHTOD)
	public Result getDashBoardList(CommonReqVo commonReqVo, @RequestBody DraftForm.DashoardForm dashoardForm) {

		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.FUNCTION.name());
		userLog.setExecType("전자결재 대시보드 목록 조회");

		systemLogComponent.addUserLog(userLog);

		int page = dashoardForm.getPage();
		int size = dashoardForm.getSize();

		String [] dataArrs = {"pending", "waiting", "progress", "shared"};

		ApprovalListInput approvalListInput = new ApprovalListInput();
		approvalListInput.setApUsrId(UserAuth.get(true).getUsrId());
		approvalListInput.setCntrctNo(UserAuth.get(true).getCntrctNo());
		approvalListInput.setPjtNo(UserAuth.get(true).getPjtNo());
		approvalListInput.setPjtType(platform.toUpperCase());

		Pageable pageable = PageRequest.of(page - 1, size);
		approvalListInput.setPageable(pageable);

		Map<String, Object> returnMap = new HashMap<String, Object>();

		for (String data : dataArrs) {
			approvalListInput.setData(data);
			Page<ApprovalListOutput> pageData = approvalComponent.getApprovalList(approvalListInput);
			long totalCount = pageData.getTotalElements();

			Map<String, Object> dataGroup = new HashMap<String, Object>();
			dataGroup.put("list", pageData.getContent());
			dataGroup.put("totalCount", totalCount);

			returnMap.put(data, dataGroup);
		}

		return Result.ok().put("returnMap", returnMap);
	}


	/**
	 * 전자결재 대시보드 문서 별 페이징 조회
	 * @param commonReqVo
	 * @param dashoardForm
	 * @return
	 */
	@PostMapping("/main/dashBoard-paging")
	@Description(name = "전자결재 대시보드 문서 별 페이징 조회", description = "전자결재 대시보드 문서 별 페이징 조회(결재요청, 결재대기, 결재진행, 참조/공유)", type = Description.TYPE.MEHTOD)
	public Result getDashBoardPagingList(CommonReqVo commonReqVo, @RequestBody DraftForm.DashoardForm dashoardForm) {

		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.FUNCTION.name());
		userLog.setExecType("전자결재 대시보드 문서 별 페이징 조회");

		systemLogComponent.addUserLog(userLog);

		int page = dashoardForm.getPage();
		int size = dashoardForm.getSize();

		ApprovalListInput approvalListInput = new ApprovalListInput();
		approvalListInput.setApUsrId(UserAuth.get(true).getUsrId());
		approvalListInput.setCntrctNo(UserAuth.get(true).getCntrctNo());
		approvalListInput.setPjtNo(UserAuth.get(true).getPjtNo());
		approvalListInput.setData(dashoardForm.getData());
		approvalListInput.setPjtType(platform.toUpperCase());

		Pageable pageable = PageRequest.of(page - 1, size);
		approvalListInput.setPageable(pageable);

		Page<ApprovalListOutput> pageData = approvalComponent.getApprovalList(approvalListInput);
		long totalCount = pageData.getTotalElements();

		return Result.ok().put("list", pageData.getContent()).put("totalCount", totalCount);
	}


	/**
	 * 기안문서 선택페이지 조회(서식그룹, 서식목록, 최근기안서식)
	 * @param commonReqVo
	 * @return
	 */
	@PostMapping("/draft/draft-main")
	@Description(name = "기안문서 선택페이지 조회", description = "기안문서 선택페이지 조회(서식그룹, 서식목록, 최근기안서식)", type = Description.TYPE.MEHTOD)
	public Result getDraftMainList(CommonReqVo commonReqVo) {

		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.FUNCTION.name());
		userLog.setExecType("기안문서 선택페이지 조회");

		systemLogComponent.addUserLog(userLog);

		MybatisInput input = MybatisInput.of()
				.add("pjtNo", UserAuth.get(true).getPjtNo())
				.add("cntrctNo", UserAuth.get(true).getCntrctNo())
				.add("usrId", UserAuth.get(true).getUsrId())
				.add("pjtType", platform.toUpperCase());

		return Result.ok()
				.put("formTypeList", draftComponent.selectFormTypeList(input))
				.put("formList", draftComponent.selectFormList(input))
				.put("latestFormList", draftComponent.selectLatestFormList(input));

	}


	/**
	 * 기안문서 서식 검색
	 * @param commonReqVo
	 * @param draftFormList
	 * @return
	 */
	@PostMapping("/draft/search-draftForm")
	@Description(name = "기안문서 서식 검색", description = "기안문서 서식 검색", type = Description.TYPE.MEHTOD)
	public Result getDraftFormList(CommonReqVo commonReqVo, @RequestBody DraftForm.DraftFormList draftFormList) {

		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.FUNCTION.name());
		userLog.setExecType("기안문서 서식 검색");

		systemLogComponent.addUserLog(userLog);

		MybatisInput input = MybatisInput.of()
				.add("pjtNo", UserAuth.get(true).getPjtNo())
				.add("cntrctNo", UserAuth.get(true).getCntrctNo())
				.add("usrId", UserAuth.get(true).getUsrId())
				.add("pjtType", platform.toUpperCase())
				.add("searchText", draftFormList.getSearchText())
				.add("searchCheckBox", draftFormList.getSearchCheckBox());

		return Result.ok().put("formList", draftComponent.selectFormList(input));
	}


	/**
	 * 기안문 작성페이지 - 선택 서식 조회
	 * @param frmNo
	 * @return
	 */
	@GetMapping("/draft/select-form")
	@Description(name = "기안문 선택 서식 조회", description = "기안문 작성페이지 - 선택 서식 조회", type = Description.TYPE.MEHTOD)
	public Result getDraftForm(CommonReqVo commonReqVo, @RequestParam("frmNo") Integer frmNo) {

		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.FUNCTION.name());
		userLog.setExecType("기안문 선택 서식 조회");

		systemLogComponent.addUserLog(userLog);

		MybatisInput input = MybatisInput.of()
				.add("frmNo", frmNo)
				.add("usrId", UserAuth.get(true).getUsrId());

		return Result.ok().put("selectForm", draftComponent.selectDraftForm(input));
	}


	/**
	 * 임시저장 문서 조회
	 * @param commonReqVo
	 * @param frmNo
	 * @return
	 */
	@GetMapping("/draft/search-TemporaryList")
	@Description(name = "임시저장 문서 조회", description = "임시저장 문서 조회", type = Description.TYPE.MEHTOD)
	public Result getDraftTemporaryList(CommonReqVo commonReqVo, @RequestParam("frmNo") Integer frmNo) {

		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.FUNCTION.name());
		userLog.setExecType("기안문 작성 임시저장 문서 조회");

		systemLogComponent.addUserLog(userLog);

		MybatisInput input = MybatisInput.of()
				.add("frmNo", frmNo)
				.add("apUsrId", UserAuth.get(true).getUsrId())
				.add("apLoginId", UserAuth.get(true).getLogin_Id())
				.add("pjtNo", UserAuth.get(true).getPjtNo())
				.add("cntrctNo", UserAuth.get(true).getCntrctNo());

		return Result.ok().put("temporaryApDocList", draftComponent.searchTemporaryList(input));
	}


	/**
	 * 기안문 작성페이지 - 결재선 검색(로그인 유저는 검색 시 제외)
	 * @param drafLineSearch
	 * @param request
	 * @return
	 */
	@PostMapping("/draft/search-approver")
	@Description(name = "기안문 작성 결재선 검색", description = "기안문 작성페이지 - 결재선 검색(로그인 유저는 검색 시 제외)", type = Description.TYPE.MEHTOD)
	public Result getDraftLineList(CommonReqVo commonReqVo,
								   @RequestBody @Valid DraftForm.DrafLineSearch drafLineSearch,
								   HttpServletRequest request) {

		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.FUNCTION.name());
		userLog.setExecType("기안문 작성 결재선 검색");

		systemLogComponent.addUserLog(userLog);

		String userInfo = cookieService.getCookie(request, cookieVO.getPortalCookieName());
		String [] param = userInfo.split(":");

		String pjtType = platform.toUpperCase();

		drafLineSearch.setCntrctNo(UserAuth.get(true).getCntrctNo());
		drafLineSearch.setPjtNo(UserAuth.get(true).getPjtNo());
		drafLineSearch.setDeptType("CAIROS".equals(pjtType) ? "C" : "G");

		return Result.ok().put("searchList", draftComponent.searchDraftLineList(draftForm.toDraftLineSearch(drafLineSearch), param[1], pjtType));
	}


	/**
	 * 기안문서 임시저장 or 상신
	 * @param commonReqVo
	 * @param param
	 * @param files
	 * @return
	 */
	@PostMapping("/draft/create-draft")
	@Description(name = "기안문서 임시저장 or 상신", description = "기안문서 임시저장 or 상신(임시저장 시 저장 정보 리턴)", type = Description.TYPE.MEHTOD)
	public Result insertTempDraft(CommonReqVo commonReqVo,
								  @RequestPart(value = "param") Map<String, Object> param,
								  @RequestPart(value = "files",required = false) List<MultipartFile> files) throws JsonProcessingException {

		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.FUNCTION.name());
		userLog.setExecType("기안문서 임시저장 or 상신");

		systemLogComponent.addUserLog(userLog);

		Map<String, Object> reqVoMap = new HashMap<String, Object>();
		reqVoMap.put("apiYn", commonReqVo.getApiYn());
		reqVoMap.put("pjtDiv", commonReqVo.getPjtDiv());

		return draftComponent.insertTempDraft(param, files, reqVoMap);
	}


	/**
	 * 임시저장문서 or 결재 상신문서 상세조회
	 * @param apDocId
	 * @return
	 */
	@GetMapping("/draft/select-draft")
	@Description(name = "임시저장문서 상세조회", description = "임시저장문서 or 결재 상신문서 상세조회", type = Description.TYPE.MEHTOD)
	public Result getTempDraft(CommonReqVo commonReqVo, @RequestParam("apDocId") String apDocId) {

		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.FUNCTION.name());
		userLog.setExecType("임시저장문서 상세조회");

		systemLogComponent.addUserLog(userLog);

		return Result.ok().put("tempDraft", draftComponent.selectTempDraft(apDocId));
	}


	/**
	 * 기안페이지 내 임시저장 문서 선택 삭제(문서&결재라인&첨부파일)
	 * @param commonReqVo
	 * @param apDocId
	 * @return
	 */
	@GetMapping("/draft/delete-tempdraft")
	@Description(name = "임시저장 문서 삭제", description = "기안페이지 내 임시저장 문서 선택 삭제(문서&결재라인&첨부파일)", type = Description.TYPE.MEHTOD)
	public Result deleteTempDraft(CommonReqVo commonReqVo, @RequestParam("apDocId") String apDocId) {

		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.FUNCTION.name());
		userLog.setExecType("임시저장 문서 삭제");

		systemLogComponent.addUserLog(userLog);

		draftComponent.deleteTemporary(apDocId);

		return Result.ok();
	}


	/**
	 * 결재요청 or 임시저장 문서 연계데이터 포함 일괄 삭제 후 API 통신
	 * @param deleteList
	 * @return
	 */
	@PostMapping("/draft/delete-list")
	@Description(name = "결재요청 or 결과작성 요청 or 임시저장 문서 일괄 삭제", description = "결재요청 or 결과작성 요청 or 임시저장 문서 연계데이터 포함 일괄 삭제 후 API 통신", type = Description.TYPE.MEHTOD)
	public Result deleteDraftAll(CommonReqVo commonReqVo, @RequestBody List<ApDoc> deleteList) {

		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.FUNCTION.name());
		userLog.setExecType("결재요청 or 결과작성 요청 or 임시저장 문서 일괄 삭제");

		systemLogComponent.addUserLog(userLog);

		// 삭제 ID 설정
		deleteList.forEach(apDoc -> {
			apDoc.setDltId(UserAuth.get(true).getUsrId());
		});

		Map<String, Object> reqVoMap = new HashMap<String, Object>();
		reqVoMap.put("apiYn", commonReqVo.getApiYn());
		reqVoMap.put("pjtDiv", commonReqVo.getPjtDiv());

		draftComponent.setDeleteList(deleteList, reqVoMap);
		return Result.ok();
	}


	/**
	 * 첨부파일 다운로드
	 * @param fileNo
	 * @param apDocId
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@GetMapping("/draft/{fileNo}/{apDocId}/file-download")
	@Description(name = "전자결재 첨부파일 다운로드", description = "전자결재 첨부파일 다운로드", type = Description.TYPE.MEHTOD)
	public ResponseEntity<Resource> fileDownLoad(CommonReqVo commonReqVo,
												 @PathVariable("fileNo") Integer fileNo,
												 @PathVariable("apDocId") String apDocId) {

		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.FUNCTION.name());
		userLog.setExecType("전자결재 첨부파일 다운로드");

		systemLogComponent.addUserLog(userLog);

		return draftComponent.fileDownload(fileNo, apDocId);

	}


	/**
	 * 서식 즐겨찾기 목록 조회
	 * @return
	 */
	@GetMapping("/draft/refresh-bookmark")
	@Description(name = "서식 즐겨찾기 목록 조회", description = "서식 즐겨찾기 목록 조회", type = Description.TYPE.MEHTOD)
	public Result getBookmarkList(CommonReqVo commonReqVo) {

		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.FUNCTION.name());
		userLog.setExecType("서식 즐겨찾기 목록 조회");

		systemLogComponent.addUserLog(userLog);

		return Result.ok().put("bookmarkList", draftComponent.selectBookmarkList());
	}


	/**
	 * 서식 즐겨찾기 추가
	 * @param frmNo
	 * @return
	 */
	@GetMapping("/draft/create-bookmark")
	@Description(name = "서식 즐겨찾기 추가", description = "서식 즐겨찾기 추가", type = Description.TYPE.MEHTOD)
	public Result createBookmark(CommonReqVo commonReqVo, @RequestParam("frmNo") Integer frmNo) {

		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.FUNCTION.name());
		userLog.setExecType("서식 즐겨찾기 추가");

		systemLogComponent.addUserLog(userLog);

		draftComponent.createBookmark(frmNo);

		return Result.ok();
	}


	/**
	 * 서식 즐겨찾기 삭제
	 * @param frmNo
	 * @return
	 */
	@GetMapping("/draft/delete-bookmark")
	@Description(name = "서식 즐겨찾기 삭제", description = "서식 즐겨찾기 삭제", type = Description.TYPE.MEHTOD)
	public Result deleteBookmark(CommonReqVo commonReqVo, @RequestParam("frmNo") Integer frmNo) {

		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.FUNCTION.name());
		userLog.setExecType("서식 즐겨찾기 삭제");

		systemLogComponent.addUserLog(userLog);

		draftComponent.deleteBookmark(frmNo);

		return Result.ok();
	}


	/**
	 * 전자결재 서식 조회(TODO: 인터페이스 구조 확정 시 삭제예정)
	 * @param commonReqVo
	 * @return
	 */
	@GetMapping("/select-apFormList")
	@Description(name = "전자결재 서식 조회", description = "전자결재 서식 조회", type = Description.TYPE.MEHTOD)
	public Result getApFormList(CommonReqVo commonReqVo, @RequestParam Map<String, String> requestParams) {
		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.FUNCTION.name());
		userLog.setExecType("전자결재 서식 조회");

		MybatisInput input = MybatisInput.of()
			.add("pjtNo", requestParams.get("pjtNo"))
			.add("cntrctNo", requestParams.get("cntrctNo"))
			.add("lang", requestParams.get("lang"));

		return Result.ok().put("apFormList", draftComponent.selectApFormList(input));
	}


	/**
	 * 문서함 리스트 조회
	 * @param commonReqVo
	 * @param cntrctNo
	 * @return
	 */
	@GetMapping("/draft/select-docBox")
	@Description(name = "네비게이션 리스트 조회", description = "문서 네비게이션 리스트 조회", type = Description.TYPE.MEHTOD)
    public Result documentNaviListToApproval(CommonReqVo commonReqVo,
											 @RequestParam String cntrctNo,
											 @RequestParam String deptId) {

        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("전자결재 문서 네비게이션 리스트 조회");
        systemLogComponent.addUserLog(userLog);

        String naviDiv = "01"; // 통합문서관리
        String naviId = String.format("%s_%s", naviDiv, cntrctNo);
        String loginId = UserAuth.get(true).getLogin_Id();

        return Result.ok()
				.put("navigationList", documentComponent.getDocumentNavigationList(commonReqVo.getAdmin(), naviId, loginId))
				.put("svrType", departmentService.containConstructionSvrType(deptId, cntrctNo));
    }


}
