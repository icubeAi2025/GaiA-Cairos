package kr.co.ideait.platform.gaiacairos.comp.design;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.Maps;
import jakarta.validation.Valid;
import kr.co.ideait.platform.gaiacairos.comp.design.helper.DesignHelper;
import kr.co.ideait.platform.gaiacairos.comp.design.service.*;
import kr.co.ideait.platform.gaiacairos.comp.project.service.InformationService;
import kr.co.ideait.platform.gaiacairos.comp.system.service.CommonCodeService;
import kr.co.ideait.platform.gaiacairos.core.base.AbstractComponent;
import kr.co.ideait.platform.gaiacairos.core.constant.CommonCodeConstants;
import kr.co.ideait.platform.gaiacairos.core.exception.ErrorType;
import kr.co.ideait.platform.gaiacairos.core.exception.GaiaBizException;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.*;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.MybatisInput;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.MybatisOutput;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.Result;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.CommonReqVo;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.design.ReviewsummaryMybatisParam;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.design.report.reviewcommentreport.ReviewCommentReportForm;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.design.report.reviewcommentreport.ReviewCommentReportMybatisParam;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.design.tool.backcheck.BackCheckForm;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.design.tool.backcheck.BackCheckMybatisParam;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.design.tool.dashboard.DesignDashboardForm;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.design.tool.designreview.DesignReviewDto;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.design.tool.designreview.DesignReviewForm;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.design.tool.designreview.DesignReviewMybatisParam;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.design.tool.evaluation.EvaluationForm;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.design.tool.evaluation.EvaluationMybatisParam;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.design.tool.responses.DesignResponsesForm;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.design.tool.responses.DesignResponsesMybatisParam;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.design.tool.setting.DesignSettingForm;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.design.tool.setting.DesignSettingMybatisParam;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.security.UserAuth;
import kr.co.ideait.platform.gaiacairos.core.type.FileUploadType;
import kr.co.ideait.platform.gaiacairos.core.type.PlatformType;
import kr.co.ideait.platform.gaiacairos.core.util.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class DesignComponent extends AbstractComponent {

	@Autowired
	FileService fileService;

	@Autowired
	DesignReviewForm designReviewForm;

    @Autowired
    ReviewCommentReportForm reviewCommentReportForm;

	@Autowired
	DesignReviewDto designReviewDto;

	@Autowired
	DesignDashboardService dashboardService;

	@Autowired
	DesignReviewService reviewService;

	@Autowired
	ReviewCommentReportService reviewCommentReportService;

    @Autowired
    ReviewsummaryService reviewsummaryService;

    @Autowired
    private EvaluationService evaluationService;

	@Autowired
	AttachmentsService attachmentsService;

	@Autowired
	CommonCodeService commonCodeService;


	/**
	 * 설계검토단계 대시보드 목록 조회
	 * @return
	 */
	public Page<MybatisOutput> getDesignDashboardData(DesignDashboardForm.DesignDashboardList dashboardList) {
		int page = dashboardList.getPage();
		int size = dashboardList.getSize();

		Pageable pageable = PageRequest.of(page - 1, size);

		return dashboardService.getDesignDashboardList(dashboardList,pageable);
	}

	/**
	 * 설계 단계 리스트 조회
	 */
	public HashMap<String,Object> getTreeData(DesignReviewForm.@Valid DsgnPhaseListGet dsgnPhaseListGet) {
		HashMap<String,Object> result = new HashMap<>();
		String cntrctNo = dsgnPhaseListGet.getCntrctNo();
		String dsgnPhaseCd = dsgnPhaseListGet.getDsgnPhaseCd();
		result.put("dsgnPhaseList",reviewService.getDsgnPhaseList(cntrctNo, dsgnPhaseCd).stream().map(designReviewDto::toDsgnPhase));
		return result;
	}

	/**
	 * 설계 검토 관리 - 설계 목록 조회
	 */
	public Page<DesignReviewMybatisParam.DesignReviewListOutput> getDsgnListDataToGrid(DesignReviewForm.@Valid DesignReviewListGet designReviewListGet, String langInfo, String usrId) {
		DesignReviewMybatisParam.DsgnSearchInput searchInput = designReviewForm.toDsgnSearchInput(designReviewListGet);

		if ("my".equals(searchInput.getRgstr())) {
			searchInput.setRgstr(usrId);
		}

		MybatisInput input = MybatisInput.of().add("cntrctNo", designReviewListGet.getCntrctNo())
				.add("dsgnPhaseNo", designReviewListGet.getDsgnPhaseNo())
				.add("lang", langInfo)
				.add("pageable", designReviewListGet.getPageable())
				.add("searchInput", searchInput)
				.add("usrId", usrId);
		input.setPageable(designReviewListGet.getPageable());

		return reviewService.getDsgnListToGrid(input);
	}

	/**
	 * 설계 검토 관리 - 설계 검토 상세 조회
	 */
	public List<DesignReviewMybatisParam.DesignReviewListOutput> getDetailDsgnWithOthersData(DesignReviewForm.DesignReviewListGet designReviewListGet, String langInfo, String usrId) {
		DesignReviewMybatisParam.DsgnSearchInput searchInput = designReviewForm.toDsgnSearchInput(designReviewListGet);
		if("my".equals(searchInput.getRgstr())){
			searchInput.setRgstr(usrId);
		}
		return reviewService.getDsgnDetail(designReviewListGet,searchInput,langInfo,usrId);
	}

	/**
	 * 설계 검토 관리 > 설계 검토 추가
	 */
	@Transactional
	public boolean registDesignReview(CommonReqVo commonReqVo, DesignReviewForm.CreateUpdateDsgn dsgn, List<MultipartFile> files, Map<String, Object> params) throws JsonProcessingException {
		Map<String, Object> result = reviewService.registDesignReview(dsgn, files, params);

//		if ( PlatformType.CAIROS.getName().equals(platform) && "Y".equals(commonReqVo.getApiYn()) ) {
		if ( PlatformType.CAIROS.getName().equals(platform) && "P".equals(commonReqVo.getPjtDiv()) && "Y".equals(commonReqVo.getApiYn()) ) {
			Map<String, Object> interfaceParams = new HashMap<>(result);
			interfaceParams.put("userId", UserAuth.get(true).getUsrId());
			interfaceParams.put("dsgn", dsgn);

			Map<String, Object> fileMap = new HashMap<>();
			fileMap.put("files", files);
//			fileMap.put("rvwPhoto", rvwPhoto);
//			fileMap.put("chgPhoto", chgPhoto);

			Map resp = invokeCairos2Pgaia("receiveByRegistDesignReview", interfaceParams, fileMap);

			if (!"00".equals( MapUtils.getString(resp, "resultCode") ) ) {
				throw new GaiaBizException(ErrorType.INTERFACE, MapUtils.getString(resp, "resultMsg"));
			}
		}

		return true;
	}
	//설계관리 > 설계 검토 툴 > 설계 검토 관리 > 추가
	@Transactional
	public Map receiveByRegistDesignReview(String transactionId, Map params) {
		if (!"receiveByRegistDesignReview".equals(transactionId)) {
			throw new GaiaBizException(ErrorType.INTERFACE, "유효하지 않은 거래ID 입니다.");
		}

		//리뷰 dto
//		DesignReviewForm.CreateUpdateDsgn dsgn = objectMapper.convertValue(params.get("dsgn"), DesignReviewForm.CreateUpdateDsgn.class);

//		List<MultipartFile> files = (List<MultipartFile>)params.get("files");
//		MultipartFile rvwPhoto = (MultipartFile)params.get("rvwPhoto");
//		MultipartFile chgPhoto = (MultipartFile) params.get("chgPhoto");

//		reviewService.registDesignReview(dsgn, files, rvwPhoto, chgPhoto, params);

		Map result = new HashMap();
		result.put("resultCode", "00");

		return result;
	}

	/**
	 * 설계 검토 관리 > 설계 검토 수정
	 */
	@Transactional
	public Map<String, Object> modifyDesignReview(DesignReviewForm.CreateUpdateDsgn updateDsgn, List<MultipartFile> newFiles, List<DmAttachments> removedFiles, Map<String, Object> params, CommonReqVo commonReqVo) throws JsonProcessingException {
		String userId = commonReqVo.getUserId();

		DmDesignReview oldDesignReview = reviewService.getDesignReview(updateDsgn.getCntrctNo(), updateDsgn.getDsgnNo());
		if (oldDesignReview == null) {
			throw new GaiaBizException(ErrorType.NO_DATA, "Design review not found");
		}

		String cntrctNo = oldDesignReview.getCntrctNo();
		String savePath = getUploadPathByWorkType(FileUploadType.DESIGN,cntrctNo);
		// 파일 삭제 처리
		if (removedFiles != null && !removedFiles.isEmpty()) {
			attachmentsService.deleteDmAttachments(removedFiles,userId);
		}

		List<DmAttachments> dmAttachmentsList = null;
		//파일들 추가
		String fileNo = oldDesignReview.getAtchFileNo();
		fileNo = fileNo == null?UUID.randomUUID().toString():fileNo;
		if(newFiles != null && !newFiles.isEmpty()) {
			dmAttachmentsList = new ArrayList<>();
			for(MultipartFile file : newFiles){
				FileService.FileMeta fileMeta = fileService.save(savePath,file);
				DmAttachments dmAttachments = new DmAttachments();
				dmAttachments.setFileNo(fileNo);
				dmAttachments.setFileKey(UUID.randomUUID().toString());
				dmAttachments.setRgstrId(userId);
				dmAttachments.setChgId(userId);
				dmAttachments.setFileNm(fileMeta.getOriginalFilename());
				dmAttachments.setFileDiskNm(fileMeta.getFileName());
				dmAttachments.setFileDiskPath(fileMeta.getDirPath());
				dmAttachments.setFileSize(fileMeta.getSize());
				dmAttachments.setFileHitNum((short)0);
				dmAttachments.setDltYn("N");

				DmAttachments savedDmAttachment = attachmentsService.insertDmAttachment(dmAttachments);
				dmAttachmentsList.add(savedDmAttachment);
			}
		}

		HashMap<String,Object> updateParams = new HashMap(params);
		updateParams.put("userId",userId);
		updateParams.put("atchFileNo",fileNo);
		Map<String, Object> result = reviewService.updateDesignReview(updateDsgn, updateParams);
		result.put("dmattachmentsList", dmAttachmentsList);

		String pjtNo = commonReqVo.getPjtNo();

//		if ( PlatformType.CAIROS.getName().equals(platform) ) {
		if ( PlatformType.CAIROS.getName().equals(platform) && "P".equals(pjtNo) && "Y".equals(commonReqVo.getApiYn()) ) {
			// API 통신
			Map<String, Object> invokeParams = Maps.newHashMap(result);
			invokeParams.put("userId", userId);
			invokeParams.put("dmAttachmentsList", dmAttachmentsList);

			Map<String, Object> invokeFileMap = Maps.newHashMap();
			invokeFileMap.put("files", newFiles);
			invokeFileMap.put("removedFiles", removedFiles);
//			invokeFileMap.put("rvwPhoto", newRvwFile);
//			invokeFileMap.put("chgPhoto", newChgFile);

			Map response = invokeCairos2Pgaia("receiveByModifyDesignReview", invokeParams, invokeFileMap);

			if (!"00".equals( MapUtils.getString(response, "resultCode") ) ) {
				throw new GaiaBizException(ErrorType.INTERFACE, MapUtils.getString(response, "resultMsg"));
			}
		}

		return result;
	}
	//설계관리 > 설계 검토 툴 > 설계 검토 단계 설정 > 수정
	@Transactional
	public Map receiveByModifyDesignReview(String transactionId, Map params) throws JsonProcessingException, InvocationTargetException, IllegalAccessException {
		if (!"receiveByModifyDesignReview".equals(transactionId)) {
			throw new GaiaBizException(ErrorType.INTERFACE, "유효하지 않은 거래ID 입니다.");
		}

		DesignReviewForm.CreateUpdateDsgn updateDsgn = objectMapper.convertValue(params.get("updateDsgn"), DesignReviewForm.CreateUpdateDsgn.class);
		List<DmAttachments> dmAttachmentsList = objectMapper.convertValue(params.get("dmAttachmentsList"), new TypeReference<List<DmAttachments>>() {});

		List<MultipartFile> files = (List<MultipartFile>)params.get("files");
		List<DmAttachments> removedFiles = objectMapper.convertValue(params.get("removedFiles"), new TypeReference<List<DmAttachments>>() {});
//		List<String> removedFileKeys = objectMapper.convertValue(params.get("removedFileKeys"), new TypeReference<List<String>>() {});
//		MultipartFile rvwPhoto = (MultipartFile)params.get("rvwPhoto");
//		MultipartFile chgPhoto = (MultipartFile)params.get("chgPhoto");
		String userId = (String)params.get("userId");

		String cntrctNo = updateDsgn.getCntrctNo();
		String savePath = getUploadPathByWorkType(FileUploadType.DESIGN,cntrctNo);
		DmDesignReview oldDesignReview = reviewService.getDesignReview(updateDsgn.getCntrctNo(), updateDsgn.getDsgnNo());
		if (oldDesignReview == null) {
			throw new GaiaBizException(ErrorType.NO_DATA, "Design review not found");
		}

		// 파일 삭제 처리
		if (removedFiles != null && !removedFiles.isEmpty()) {
			attachmentsService.deleteDmAttachments(removedFiles,userId);
		}

		//파일들 추가
		if(files != null && !files.isEmpty()) {
//			String fileNo = StringUtils.defaultString(oldDesignReview.getAtchFileNo(), UUID.randomUUID().toString());

			for(int i=0;i<files.size();i++) {
				MultipartFile file = files.get(i);
				fileService.save(savePath,file,dmAttachmentsList.get(i).getFileDiskNm());
				attachmentsService.insertDmAttachment(dmAttachmentsList.get(i));
			}
		}

		reviewService.updateDesignReview(updateDsgn, params);

		Map result = new HashMap();
		result.put("resultCode", "00");

		return result;
	}

	/**
	 * 설계 검토 삭제
	 */
	@Transactional
	public void removeDesignReviewList(List<String> dsgnNoList, CommonReqVo commonReqVo) {
		reviewService.deleteDesignReviewList(dsgnNoList);

		if ( PlatformType.CAIROS.getName().equals(platform) && "P".equals(commonReqVo.getPjtDiv()) && "Y".equals(commonReqVo.getApiYn()) ) {
			// API 통신
			Map<String, Object> invokeParams = Maps.newHashMap();
			invokeParams.put("dsgnNoList", dsgnNoList);
			invokeParams.put("userId", UserAuth.get(true).getUsrId());

			Map response = invokeCairos2Pgaia("receiveByRemoveDesignReviewList", invokeParams);

			if (!"00".equals( MapUtils.getString(response, "resultCode") ) ) {
				throw new GaiaBizException(ErrorType.INTERFACE, MapUtils.getString(response, "resultMsg"));
			}
		}
	}
	//설계관리 > 설계 검토 툴 > 설계 검토 단계 설정 > 수정
	@Transactional
	public Map receiveByRemoveDesignReviewList(String transactionId, Map params) {
		if (!"receiveByRemoveDesignReviewList".equals(transactionId)) {
			throw new GaiaBizException(ErrorType.INTERFACE, "유효하지 않은 거래ID 입니다.");
		}

		String userId = (String)params.get("userId");
		List<String> dsgnNoList = objectMapper.convertValue(params.get("dsgnNoList"), new TypeReference<List<String>>() {});

		reviewService.deleteDesignReviewList(dsgnNoList, userId);

		Map result = new HashMap();
		result.put("resultCode", "00");

		return result;
	}

	/**
	 * 설계 검토 관리 > 검토 의견 조회
	 */
	public HashMap<String, Object> getDetailDsgnData(String cntrctNo, String dsgnPhaseNo, String dsgnNo, String langInfo) {
		HashMap<String, Object> result = new HashMap<>();

		DesignReviewMybatisParam.DsgnUpdateOutPut dsgn = reviewService.getDesignReview(cntrctNo, dsgnPhaseNo, dsgnNo, langInfo); // 설계 정보 데이터
		List<DmAttachments> attachments = reviewService.getFileList(dsgn.getAtchFileNo());
		DmAttachments rvwDwgAttach = null;
		DmAttachments chgDwgAttach = null;

		if (dsgn.getRvwAtchFileNo() != null && dsgn.getRvwSno() != null) {
			rvwDwgAttach = reviewService.getDwgFile(dsgn.getRvwAtchFileNo(), dsgn.getRvwSno().shortValue());
		}

		if (dsgn.getChgAtchFileNo() != null && dsgn.getChgSno() != null) {
			chgDwgAttach = reviewService.getDwgFile(dsgn.getChgAtchFileNo(), dsgn.getChgSno().shortValue());
		}

		// if (attachments == null && attachments.size() == 0) {
		//     return Result.ok().put("dsgn", dsgn);
		// } else {
		// }
		result.put("dsgn", dsgn);
		result.put("rvwDwgAttach", rvwDwgAttach);
		result.put("attachments", attachments);
		result.put("chgDwgAttach", chgDwgAttach);
		return result;
	}

	/**
	 * 설계 검토 상세 조회 > 평가, 백체크 데이터 조회
	 */
	public HashMap<String, Object> getApprerAndBackchkData(String dsgnNo, String cntrctNo, String langInfo) {
		HashMap<String, Object> result = new HashMap<>();

		result.put("apprer",reviewService.getApprerData(dsgnNo,cntrctNo,langInfo));
		result.put("backchk",reviewService.getBackchkData(dsgnNo,cntrctNo,langInfo));

		return result;
	}

	public ReviewCommentReportMybatisParam.ReviewReportDetailOutput getDetailReviewReportData(String cntrctNo, String dsgnNo, String langInfo, String usrId) {
		MybatisInput input = MybatisInput.of().add("cntrctNo", cntrctNo)
				.add("dsgnNo", dsgnNo)
				.add("lang", langInfo)
				.add("usrId", usrId);
		return reviewCommentReportService.getReviewReportDetail(input);
	}

	public List<ReviewCommentReportMybatisParam.ReviewReportRgstrListOutput> getRgstrListData(String cntrctNo) {
		return reviewCommentReportService.getRgstrList(cntrctNo);
	}

	public List<SmComCode> getDsgnCodeList() {
		return commonCodeService.getCommonCodeListByGroupCode(CommonCodeConstants.WORKTYPE_CODE_GROUP_CODE);
	}

	public List<ReviewsummaryMybatisParam.ReviewsummaryListOutput>  getSummaryListData(String cntrctNo, String summaryType, List<String> dsgnPhaseNoList, List<String> rgstrIdList, String myDsgn, String langInfo, String usrId) {
		MybatisInput input = MybatisInput.of().add("cntrctNo", cntrctNo)
				.add("summaryType", summaryType) // 집계 구분
				.add("dsgnPhaseNoList", dsgnPhaseNoList) // 설계단계
				.add("rgstrIdList", rgstrIdList) // 검토자 목록
				.add("myDsgn", myDsgn) // 내 결함
				.add("lang", langInfo)
				.add("usrId", usrId);
		return reviewsummaryService.getSummaryList(input);
	}

	public List<ReviewCommentReportMybatisParam.ReviewReportOutput> getReviewReportListData(ReviewCommentReportForm.@Valid ReviewReportList designReviewListGet, String langInfo, String usrId) {
		ReviewCommentReportMybatisParam.ReviewReportSearchInput searchInput = reviewCommentReportForm.toReviewReportSearchInput(designReviewListGet);
		MybatisInput input = MybatisInput.of().add("cntrctNo", designReviewListGet.getCntrctNo())
				.add("usrId", usrId)
				.add("lang", langInfo)
				.add("searchInput", searchInput);

		return reviewCommentReportService.getReviewReportList(input);
	}
	public List<MybatisOutput> getWorkTypeList() {
		return evaluationService.selectDsgnCd();
	}
}
