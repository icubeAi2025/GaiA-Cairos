package kr.co.ideait.platform.gaiacairos.web.entrypoint.design;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import kr.co.ideait.iframework.annotation.Description;
import kr.co.ideait.platform.gaiacairos.comp.design.DesignComponent;
import kr.co.ideait.platform.gaiacairos.comp.design.DesignResponsesComponent;
import kr.co.ideait.platform.gaiacairos.comp.design.DesignSettingComponent;
import kr.co.ideait.platform.gaiacairos.core.base.AbstractController;
import kr.co.ideait.platform.gaiacairos.core.exception.ErrorType;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.DmAttachments;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.GridResult;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.MybatisOutput;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.Result;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.CommonReqVo;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.design.report.reviewsummary.ReviewSummaryForm;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.design.tool.dashboard.DesignDashboardForm;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.design.tool.designreview.DesignReviewForm;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.design.tool.designreview.DesignReviewMybatisParam;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.design.tool.responses.DesignResponsesForm;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.design.tool.responses.DesignResponsesMybatisParam;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.design.tool.setting.DesignSettingMybatisParam;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.security.UserAuth;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.log.Log;
import kr.co.ideait.platform.gaiacairos.core.type.LogType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/design")
public class DesignApiController extends AbstractController {

	@Autowired
	DesignComponent designComponent;

	@Autowired
	DesignResponsesComponent designResponsesComponent;

	@Autowired
	DesignSettingComponent designSettingComponent;

	/**
	 * 설계검토단계 대시보드 목록 조회
	 * @return
	 */
	@PostMapping("/dashboard")
	@Description(name = "설계검토단계 대시보드 목록 조회", description = "설계검토단계 대시보드 조회", type = Description.TYPE.MEHTOD)
	public Result getDashboardList(CommonReqVo commonReqVo, 
			@RequestBody DesignDashboardForm.DesignDashboardList dashboardList,
			HttpServletRequest request) {

		String userId = commonReqVo.getUserId();
		dashboardList.setUsrId(userId);

		Page<MybatisOutput> pageData = designComponent.getDesignDashboardData(dashboardList);
		Long totalCount = pageData.getTotalElements();

		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.FUNCTION.name());
		userLog.setExecType("설계검토단계 대시보드 목록 조회");
		systemLogComponent.addUserLog(userLog);

		return Result.ok().put("dsgnList", pageData.getContent())
				.put("totalCount", totalCount);
	}
	/**
	 * 설계 단계 리스트 조회
	 */
	@GetMapping("/dsgnPhase/list")
	@Description(name = "설계 단계 리스트 조회", description = "설계단계 설정에서 정의한 설계 단계 리스트 조회", type = Description.TYPE.MEHTOD)
	public Result dsgnPhaseList(CommonReqVo commonReqVo, @Valid DesignReviewForm.DsgnPhaseListGet dsgnPhaseListGet) {
		HashMap<String,Object> result = designComponent.getTreeData(dsgnPhaseListGet);

		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.FUNCTION.name());
		userLog.setExecType("설계 단계 리스트 조회");
		systemLogComponent.addUserLog(userLog);

		return Result.ok().put("dsgnPhaseList",result.get("dsgnPhaseList"));
	}

	/**
	 * 검토 분류 셀렉트
	 */
	@GetMapping("/dsgnCd/list")
	@Description(name = "설계 검토 분류 조회", description = "계약의 설계 검토 분류 조회", type = Description.TYPE.MEHTOD)
	public Result dsgnCdList(CommonReqVo commonReqVo) {

		return Result.ok().put("dsgnCdList", designComponent.getDsgnCodeList());
	}

	/**
	 * 검토자 셀렉트
	 */
	@GetMapping("/rgstr/list")
	@Description(name = "설계 검토 분류 조회", description = "계약의 설계 검토 분류 조회", type = Description.TYPE.MEHTOD)
	public Result rgstrList(CommonReqVo commonReqVo, @Valid ReviewSummaryForm.ReviewsummaryList summaryListGet) {

		return Result.ok().put("rgstrList", designComponent.getRgstrListData(summaryListGet.getCntrctNo()));
	}


	/**
	 * 설계 검토 관리 - 설계 목록 조회
	 */
	@GetMapping("/review/list")
	@Description(name = "설계 검토 목록 조회", description = "해당 계약의 설계 검토 데이터 리스트 조회 - tuiGrid 반환 구조에 맞춰서 반환.", type = Description.TYPE.MEHTOD)
	public GridResult getDsgnList(CommonReqVo commonReqVo, @Valid DesignReviewForm.DesignReviewListGet designReviewListGet,
								  @CookieValue(name = "lang", required = false, defaultValue = "ko") String langInfo, UserAuth user) {

		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.FUNCTION.name());
		userLog.setExecType("설계 목록 조회");
		systemLogComponent.addUserLog(userLog);

		return GridResult.ok(designComponent.getDsgnListDataToGrid(designReviewListGet,langInfo, user.getUsrId()));
	}

	/**
	 * 설계 검토 관리 - 설계 검토 상세 조회
	 */
	@PostMapping("/review/detail")
	@Description(name = "설계 검토 상세 조회", description = "해당하는 설계 검토 데이터 상세 조회 - 답변, 평가, 백체크 데이터 포함 조회", type = Description.TYPE.MEHTOD)
	public Result getDsgnDetail(CommonReqVo commonReqVo, @RequestBody DesignReviewForm.DesignReviewListGet designReviewListGet,
								@CookieValue(name = "lang", required = false, defaultValue = "ko") String langInfo,
								UserAuth user) {

		List<DesignReviewMybatisParam.DesignReviewListOutput> dsgn = designComponent.getDetailDsgnWithOthersData(designReviewListGet,langInfo,user.getUsrId());

		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.FUNCTION.name());
		userLog.setExecType("설계 검토 상세 조회");
		systemLogComponent.addUserLog(userLog);

		return Result.ok().put("dsgnDetail", dsgn);
	}
	/**
	 * 설계 검토 상세 조회 > 평가, 백체크 데이터 조회
	 */
	@PostMapping("/review/detail/sub-data")
	@Description(name = "설계 검토 상세 조회 > 평가, 백체크 데이터 조회", description = "설계 검토 상세 조회 시, 평가자와 백체크 데이터 조회", type = Description.TYPE.MEHTOD)
	public Result getApprerAndBackchkData(CommonReqVo commonReqVo, @RequestBody DesignReviewForm.DesignReviewListGet designReviewListGet, @CookieValue(name = "lang", required = false, defaultValue = "ko") String langInfo) {
		if (designReviewListGet.getCntrctNo() == null || designReviewListGet.getDsgnNo() == null) {
			return Result.nok(ErrorType.INVAILD_INPUT_DATA, "계약번호와 설계 번호는 필수입니다.");
		}
		HashMap<String,Object> result = designComponent.getApprerAndBackchkData(designReviewListGet.getDsgnNo(), designReviewListGet.getCntrctNo(), langInfo);

		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.FUNCTION.name());
		userLog.setExecType("설계 검토 평가, 백체크 데이터 조회");
		systemLogComponent.addUserLog(userLog);

		return Result.ok().put("apprer", result.get("apprer"))
				.put("backchk", result.get("backchk"));
	}

	/**
	 * 설계 검토 관리 > 설계 검토 추가
	 */
	@PostMapping("/review/create")
	@Description(name = "설계 검토 추가", description = "설계 검토 추가할 때, 검토 정보와 도서 사진 데이터 저장", type = Description.TYPE.MEHTOD)
	public Result createDsgn(CommonReqVo commonReqVo,
							 @RequestPart("dsgn") DesignReviewForm.CreateUpdateDsgn dsgn,
							 @RequestPart(value = "files", required = false) List<MultipartFile> files,
							 @RequestParam Map<String, Object> params) throws JsonProcessingException {

		log.info("params: {}", params);
		designComponent.registDesignReview(commonReqVo, dsgn,files,params);

		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.FUNCTION.name());
		userLog.setExecType("설계검토 추가");

		return Result.ok();
	}

	/**
	 * 설계 검토 관리 > 설계 검토 수정
	 */
	@PostMapping("/review/update")
	@Description(name = "설계 검토 관리 > 설계 검토 수정", description = "설계 검토를 수정할 때, 검토 정보와 파일, 이미지를 저장", type = Description.TYPE.MEHTOD)
	public Result updateDsgn(CommonReqVo commonReqVo,
							 @RequestPart("dsgn") DesignReviewForm.CreateUpdateDsgn updateDsgn,
							 @RequestPart(value = "files", required = false) List<MultipartFile> newFiles,
							 @RequestPart(value = "removedFiles", required = false) List<DmAttachments> removedFiles,
							 @RequestParam Map<String, Object> params
	) throws JsonProcessingException {
log.info("params: {}", params);
		Map<String, Object> result = designComponent.modifyDesignReview(updateDsgn,newFiles,removedFiles,params, commonReqVo);

		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.FUNCTION.name());
		userLog.setExecType("설계 검토 수정");
		systemLogComponent.addUserLog(userLog);

		return Result.ok(result);
	}
	/**
	 * 설계 검토 삭제
	 */
	@PostMapping("/review/delete")
	@Description(name = "설계 검토 삭제", description = "설계 검토 삭제", type = Description.TYPE.MEHTOD)
	public Result deleteDesignReviewList(CommonReqVo commonReqVo, @RequestBody @Valid DesignReviewForm.DsgnNoList dsgnNoList) {

		if(dsgnNoList.getDsgnNoList().size() == 0) {
			return Result.nok(ErrorType.NOT_FOUND,"선택된 설계 검토가 없습니다.");
		}

		designComponent.removeDesignReviewList(dsgnNoList.getDsgnNoList(), commonReqVo);

		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.FUNCTION.name());
		userLog.setExecType("설계 검토 삭제");
		systemLogComponent.addUserLog(userLog);

		return Result.ok();
	}

	/**
	 * 설계 검토 관리 > 검토 의견 조회
	 */
	@GetMapping("/review/{cntrctNo}/{dsgnPhaseNo}/{dsgnNo}")
	@Description(name = "설계 검토 관리 > 설계 검토 수정 - 설계 검토 상세 조회", description = "설계 검토 수정 시, 해당 설계 검토의 기존 데이터 정보 조회", type = Description.TYPE.MEHTOD)
	public Result getDsgn(CommonReqVo commonReqVo, @PathVariable("cntrctNo") String cntrctNo, @PathVariable("dsgnPhaseNo") String dsgnPhaseNo, @PathVariable("dsgnNo") String dsgnNo,
						  @CookieValue(name = "lang", required = false, defaultValue = "ko") String langInfo) {

		HashMap<String,Object> result = designComponent.getDetailDsgnData(cntrctNo, dsgnPhaseNo, dsgnNo, langInfo);

		if(result != null && result.size() != 0){
			return Result.ok().put("dsgn", result.get("dsgn"))
					.put("attachments", result.get("attachments"))
					.put("rvwDwgAttach", result.get("rvwDwgAttach"))
					.put("chgDwgAttach", result.get("chgDwgAttach"));
		}

		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.FUNCTION.name());
		userLog.setExecType("검토 의견 조회");
		systemLogComponent.addUserLog(userLog);

		return Result.nok(ErrorType.DATABSE_ERROR);
	}

	/**
	 * 답변 - 입력창 기본데이터
	 */
	@PostMapping("/responses/detail")
	@Description(name = "설계 검토 답변 상세조회", description = "계약의 설계 검토 답변 상세조회", type = Description.TYPE.MEHTOD)
	public Result getResponese(CommonReqVo commonReqVo, @RequestBody DesignResponsesForm.DesignResponsesGet responsesGet) {

		DesignSettingMybatisParam.DesignPhaseDetailInput phaseInput = new DesignSettingMybatisParam.DesignPhaseDetailInput();
		DesignResponsesMybatisParam.DesignResponsesInput responsesInput = new DesignResponsesMybatisParam.DesignResponsesInput();

		phaseInput.setCntrctNo(responsesGet.getCntrctNo());
		phaseInput.setDsgnPhaseNo(responsesGet.getDsgnPhaseNo());
		responsesInput.setDsgnNo(responsesGet.getDsgnNo());
		responsesInput.setResSeq(responsesGet.getResSeq());


		HashMap<String,Object> result = designResponsesComponent.getDesignResponsesData(responsesInput,phaseInput);

		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.FUNCTION.name());
		userLog.setExecType("설계 검토 답변 상세조회");
		systemLogComponent.addUserLog(userLog);

		return Result.ok()
				.put("phase", result.get("phase"))
				.put("response", result.get("response"))
				.put("files", result.get("files"))
				.put("dwgFiles", result.get("dwgFiles"))
				.put("dwg", result.get("dwg"));
	}

	/**
	 * 답변 - 입력창 추가 및 수정
	 */
	@PostMapping("/responses/save")
	@Description(name = "설계 검토 답변 추가/수정", description = "계약의 설계 검토 답변 추가/수정", type = Description.TYPE.MEHTOD)
	public Result saveResponses(CommonReqVo commonReqVo, @RequestPart("data") DesignResponsesForm.DesignResponsesSave responses,
								@RequestPart(value = "files", required = false) List<MultipartFile> files,
								@RequestPart(value = "dwgFile", required = false) Map<String,Object> dwgFile) throws JsonProcessingException {



		Map<String,Object> result = designResponsesComponent.saveResponses(responses, files, dwgFile, commonReqVo);
//
		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.FUNCTION.name());
		userLog.setExecType("설계 검토 답변 추가/수정");
		systemLogComponent.addUserLog(userLog);

		return Result.ok()
				.put("response", result.get("response"))
				.put("saveType", result.get("saveType"));
	}

	/**
	 * 답변 삭제
	 */
	@PostMapping("/responses/delete")
	@Description(name = "설계 검토 답변 삭제", description = "계약의 설계 검토 답변 삭제", type = Description.TYPE.MEHTOD)
	public Result deleteResponses(CommonReqVo commonReqVo, @RequestBody @Valid DesignResponsesForm.DesignResponsesList responsesList) {
		designResponsesComponent.removeResponses(responsesList.getResponsesList(), commonReqVo);

		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.FUNCTION.name());
		userLog.setExecType("설계 검토 답변 삭제");
		systemLogComponent.addUserLog(userLog);

		return Result.ok();
	}


}
