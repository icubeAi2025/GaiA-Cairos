package kr.co.ideait.platform.gaiacairos.comp.eapproval;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.ideait.platform.gaiacairos.comp.construction.service.DailyreportService;
import kr.co.ideait.platform.gaiacairos.comp.construction.service.MainmtrlReqfrmService;
import kr.co.ideait.platform.gaiacairos.comp.construction.service.QualityinspectionService;
import kr.co.ideait.platform.gaiacairos.comp.document.service.DocumentService;
import kr.co.ideait.platform.gaiacairos.comp.eapproval.helper.EapprovalHelper;
import kr.co.ideait.platform.gaiacairos.comp.eapproval.service.ApprovalRequestService;
import kr.co.ideait.platform.gaiacairos.comp.eapproval.service.ApprovalService;
import kr.co.ideait.platform.gaiacairos.comp.eapproval.service.DraftService;
import kr.co.ideait.platform.gaiacairos.comp.progress.service.MonthlyreportService;
import kr.co.ideait.platform.gaiacairos.comp.progress.service.WeeklyreportService;
import kr.co.ideait.platform.gaiacairos.comp.projectcost.service.DepositService;
import kr.co.ideait.platform.gaiacairos.comp.projectcost.service.PaymentService;
import kr.co.ideait.platform.gaiacairos.comp.safety.service.SafetyDiaryIntegrationService;
import kr.co.ideait.platform.gaiacairos.comp.safety.service.SafetymgmtService;
import kr.co.ideait.platform.gaiacairos.core.base.AbstractComponent;
import kr.co.ideait.platform.gaiacairos.core.constant.CommonCodeConstants;
import kr.co.ideait.platform.gaiacairos.core.exception.ErrorType;
import kr.co.ideait.platform.gaiacairos.core.exception.GaiaBizException;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.*;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.MybatisInput;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.MybatisOutput;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.Result;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.eapproval.draft.DraftForm;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.eapproval.draft.DraftMybatisParam;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.eapproval.draft.DraftMybatisParam.ApFormListOutput;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.security.UserAuth;
import kr.co.ideait.platform.gaiacairos.core.type.FileUploadType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class DraftComponent extends AbstractComponent {

	@Autowired
	DraftService draftService;

	@Autowired
	ApprovalService approvalService;

	@Autowired
	ApprovalRequestService approvalRequestService;

	@Autowired
	PaymentService paymentService;

	@Autowired
	DepositService depositService;

	@Autowired
	DailyreportService dailyreportService;

	@Autowired
	MonthlyreportService monthlyreportService;

	@Autowired
	WeeklyreportService weeklyreportService;

	@Autowired
	QualityinspectionService qualityinspectionService;

	@Autowired
	SafetymgmtService safetymgmtService;

	@Autowired
	SafetyDiaryIntegrationService safetyDiaryIntegrationService;

	@Autowired
	DocumentService documentService;

	@Autowired
	MainmtrlReqfrmService mainmtrlReqfrmService;

	@Autowired
	EapprovalHelper eapprovalHelper;
	
	@Autowired
	DraftForm draftForm;


	/**
	 * 서식 그룹 가져오기
	 * @param input
	 * @return
	 */
	public List selectFormTypeList(MybatisInput input) {
		return draftService.selectFormTypeList(input);
	}


	/**
	 * 기안문서 서식 검색
	 * @param input
	 * @return
	 */
	public List selectFormList(MybatisInput input) {
		return draftService.selectFormList(input);
	}


	/**
	 * 최근 기안문서 조회
	 * @param input
	 * @return
	 */
	public List selectLatestFormList(MybatisInput input) {
		return draftService.selectLatestFormList(input);
	}


	/**
	 * 기안문 작성페이지 - 선택 서식 조회
	 * @param input
	 * @return
	 */
	public Map selectDraftForm(MybatisInput input) {
		return draftService.selectDraftForm(input);
	}


	/**
	 * 임시저장 문서 조회
	 * @param input
	 * @return
	 */
	public List searchTemporaryList(MybatisInput input) {
		return draftService.searchTemporaryList(input);
	}


	/**
	 * 기안문 작성페이지 - 결재선 검색(로그인 유저는 검색 시 제외)
	 * @param searchAppLine
	 * @param userType
	 * @param pjtType
	 * @return
	 */
	public List<MybatisOutput> searchDraftLineList(DraftMybatisParam.SearchAppLine searchAppLine, String userType, String pjtType) {
		String deptType = "CAIROS".equals(platform.toUpperCase()) ? "C" : "G";
		searchAppLine.setPstnCd(CommonCodeConstants.PSTN_CODE_GROUP_CODE);
		searchAppLine.setUsrId(UserAuth.get(true).getUsrId());
		searchAppLine.setDeptType(deptType);
		List<MybatisOutput> searchList = null;

		if(searchAppLine.getApCnrsRng().equals("03")) {
			// 사용자 검색
			searchList = draftService.searchUserList(searchAppLine, userType);
		} else if (searchAppLine.getApCnrsRng().equals("02")) {
			// 부서 검색
			searchList = draftService.searchDeptList(searchAppLine, userType, pjtType);
		}

		return searchList;
	}


	/**
	 * 기안문서 임시저장 or 상신
	 * @param param
	 * @param files
	 * @param reqVoMap
	 * @return
	 */
    @Transactional
	public Result insertTempDraft(Map<String, Object> param, List<MultipartFile> files, Map<String, Object> reqVoMap) throws JsonProcessingException {
		String isApiYn = (String)reqVoMap.get("apiYn");
		String pjtDiv = (String)reqVoMap.get("pjtDiv");

		try {
			//로그인유저 정보
			String userId = UserAuth.get(true).getUsrId();
			String loginId = UserAuth.get(true).getLogin_Id();
			String pjtNo = UserAuth.get(true).getPjtNo();
			String cntrctNo = UserAuth.get(true).getCntrctNo();

			ObjectMapper mapper = new ObjectMapper();
			ApDoc draftDoc = mapper.convertValue(param.get("apDoc"), ApDoc.class);
			List<DraftForm.DraftApLine> appLineList = mapper.convertValue(param.get("draftLine"), new TypeReference<List<DraftForm.DraftApLine>>() {});
			List<Integer> delFileList = mapper.convertValue(param.get("delFileList"), new TypeReference<List<Integer>>() {});
			List<ApShare> apShareList = mapper.convertValue(param.get("draftShareList"), new TypeReference<List<ApShare>>() {});
			List<ApShare> delShareList = mapper.convertValue(param.get("delShareList"), new TypeReference<List<ApShare>>() {});
			String saveType = mapper.convertValue(param.get("saveType"), new TypeReference<String>() {});
			String uploadPath = getUploadPathByWorkType(FileUploadType.EAPPROVAL, cntrctNo);

			draftDoc.setPjtType(platform.toUpperCase());
			draftDoc.setPjtNo(pjtNo);
			// draftDoc.setCntrctNo(cntrctNo);
			draftDoc.setApUsrId(userId);
			draftDoc.setApLoginId(loginId);

			int apDocNo;

			ApDoc savedApDoc = null;
			if ("NEW".equals(saveType)) {
				//최초 임시저장 또는 상신일 경우 AP_DOC_ID를 UUID로 셋팅후 AP_DOC 테이블 입력
				draftDoc.setApDocId(UUID.randomUUID().toString());    //ap-doc-id 값UUID로 셋팅
				draftDoc.setDltYn("N");
				draftDoc.setApAppDt(LocalDateTime.now());
				draftDoc.setApType("01");

				savedApDoc = draftService.createApDoc(draftDoc);
				// AP_DOC_ID(UUID)로 AP_DOC_NO 조회 후 결재라인, 첨부파일 저장을 위해 셋팅
				apDocNo = Integer.parseInt(draftService.selectApDocNo(draftDoc.getApDocId()));

			} else {
				//임시저장한 문서로 다시 임시저장 또는 상신 할 경우 AP_DOC 테이블 업데이트 처리 및 기존 결재라인 삭제 및 삭제된 기존 첨부 파일 논리 삭제처리
				draftService.updateApDoc(draftDoc);
				draftService.deleteTemporaryApLine(draftDoc.getApDocId());

				if (!delFileList.isEmpty()) {
					for (int delFileNo : delFileList) {
						draftService.deleteTemporaryApAttachment(delFileNo, userId);
					}
				}

				apDocNo = draftDoc.getApDocNo();
				savedApDoc = approvalService.getApprovalDoc(draftDoc.getApDocId());
			}

			//설정된 결재라인 수 만큼 결재라인 입력
			List<ApLine> savedApLineList = new ArrayList<ApLine>();
			if (!appLineList.isEmpty()) {
				for (ApLine apLine : draftForm.toApLineList(appLineList)) {
					apLine.setApId(UUID.randomUUID().toString());
					apLine.setApDocNo((apDocNo));
					apLine.setApDocId(draftDoc.getApDocId());

					ApLine savedApLine = draftService.createApLineList(apLine);
					if (savedApLine != null) {
						savedApLineList.add(savedApLine);
					}
				}
			}

			//화면에서 추가한 첨부파일 물리적 파일 업로드 및 파일 정보 입력 처리
			if(files != null && !files.isEmpty()) {
				eapprovalHelper.createApAttachmentsList(files, uploadPath, apDocNo, draftDoc.getApDocId(), UserAuth.get(true).getUsrId());
			}

			//공유자 삭제
			if (!"NEW".equals(saveType) && !delShareList.isEmpty()) {
				approvalService.updateDeleteApShareList(delShareList);
			}

			//공유자 추가 : 저장할 공유자 리스트 있으면 추가, 없으면 기존 리스트 조회 후 삭제
			if (!apShareList.isEmpty()) {
				apShareList.forEach(apShare -> {
					apShare.setApDocNo(apDocNo);
					apShare.setApDocId(draftDoc.getApDocId());
				});
				apShareList = approvalService.createApShareList(apShareList, pjtNo, cntrctNo);
			} else {
				approvalService.updateDeleteApShareList(apDocNo, draftDoc.getApDocId());
			}

			// 문서관리 파일 저장
			List<Integer> dcStorageList = mapper.convertValue(param.get("dcStorageList"), new TypeReference<List<Integer>>() {});
			List<ApAttachments> dcStorageToApAttachmentList = new ArrayList<>();
			if (dcStorageList != null && !dcStorageList.isEmpty()) {
				dcStorageList.forEach(id -> {
					try {
						DcStorageMain findFile = documentService.getDcStorageMain(id);
						File sourceFile = new File(findFile.getDocDiskPath(), findFile.getDocDiskNm());
						Path targetDir = Paths.get(uploadPath);
						Files.createDirectories(targetDir);

						// 파일 복사
						Path targetFilePath = targetDir.resolve(findFile.getDocDiskNm());
						Files.copy(sourceFile.toPath(), targetFilePath, StandardCopyOption.REPLACE_EXISTING);

						// DB 저장용 엔티티 생성
						ApAttachments apAttachment = new ApAttachments();
						apAttachment.setFileNm(findFile.getDocNm());
						apAttachment.setFileSize(findFile.getDocSize());
						apAttachment.setApDocId(draftDoc.getApDocId());
						apAttachment.setApDocNo(apDocNo);
						apAttachment.setFileDiskPath(uploadPath);
						apAttachment.setFileDiskNm(findFile.getDocDiskNm());
						apAttachment.setFileHitNum(0);
						apAttachment.setDltYn("N");

						dcStorageToApAttachmentList.add(apAttachment);

					} catch (GaiaBizException | IOException e) {
						log.error("기안문 작성 - 문서관리 연동 시 첨부파일 저장 실패: ", e);
					}
				});

				// DB 저장
				eapprovalHelper.saveApAttachments(dcStorageToApAttachmentList);
			}

			if("NEW".equals(saveType) && "16".equals(draftDoc.getFrmId()) && draftDoc.getUuid() != null && StringUtils.isNotEmpty(draftDoc.getUuid())) {
				Map<String, Object> shareData = new HashMap<>();
				shareData.put("apDoc", savedApDoc);
				shareData.put("apShareList", apShareList);       		// List<ApShare>
				shareData.put("apLineList", savedApLineList);         // List<ApLine>

				Map<String, String> requestParams = new HashMap<>();
				requestParams.put("shareType", "1");
				requestParams.put("shareStatus", "2");
				requestParams.put("uuid", draftDoc.getUuid());
				requestParams.put("shareTargetKey", draftDoc.getApDocId());
				requestParams.put("shareResData", objectMapper.writeValueAsString(shareData));

				documentService.updateDocSharedHistory(requestParams);
			}

			Map<String, Object> returnMap = new HashMap<String, Object>();

			// 데이터 저장하기!
			returnMap.put("apDocNo", apDocNo);
			returnMap.put("apDocId", draftDoc.getApDocId());

			if ("W".equals(draftDoc.getApDocStats())) {
				// 전결 여부 체크
				boolean isDelegatable = approvalRequestService.checkDelegate(savedApDoc, savedApLineList);
				String apDocStats = isDelegatable ? "C" : "W";
				String checkPgaiaUser = savedApLineList.get(0).getApUsrId();
				if(isDelegatable && savedApLineList.size() > 1) {
					checkPgaiaUser = savedApLineList.get(1).getApUsrId();
				}
				returnMap.put("apDocStats", apDocStats);

				// 첫번째 결재자 PGAIA 유저 체크
				boolean isPgaia = draftService.checkPgaiaFirstApproverForDraft(pjtNo, checkPgaiaUser);
				boolean isApiEnabled = "P".equals(pjtDiv) && "Y".equals(isApiYn)&& isPgaia && "CAIROS".equals(platform.toUpperCase());

				// API 통신
				if (isApiEnabled && savedApDoc != null) {
					//params 값 셋팅
					Map<String, Object> params = new HashMap<>();

					params.put("usrId", UserAuth.get(true).getUsrId());
					params.put("apDoc", savedApDoc);
					params.put("apLineList", savedApLineList);
					params.put("apShareList", apShareList);

					List<Map<String, Object>> apDocFileInfo = Collections.emptyList();
					if(!dcStorageToApAttachmentList.isEmpty()) {
						apDocFileInfo = eapprovalHelper.convertToFileInfo(dcStorageToApAttachmentList);
					}
					params.put("apDocFileInfo", apDocFileInfo);

					log.info("GAIA <-> CAIROS API통신 params : {}", params);

					Map response = invokeCairos2Pgaia("GACA8100", params, files);

					if (!"00".equals(org.apache.commons.collections4.MapUtils.getString(response, "resultCode"))) {
						throw new GaiaBizException(ErrorType.INTERFACE, org.apache.commons.collections4.MapUtils.getString(response, "resultMsg"));
					}
				}
				// 알림메시지
				eapprovalHelper.insertInitAlarm(draftDoc.getApDocId(), draftDoc.getApDocStats());
				// 최근 기안 서식 생성
				draftService.createUpdateLatestForm(draftDoc.getFrmNo(), draftDoc.getApUsrId());
				return Result.ok().put("returnMap", returnMap);
			} else {
				DcNavigation dcNavigation = null;
				if(draftDoc.getNaviId() != null) {
					dcNavigation = documentService.getNavigation(draftDoc.getNaviId());
				}
				return Result.ok()
						.put("draft", selectApDoc(draftDoc.getApDocId()))
						.put("draftApLineList", selectApLineList(draftDoc.getApDocId()))
						.put("draftApFileList", selectApFileList(draftDoc.getApDocId()))
						.put("draftApShareList", selectApShareList(draftDoc.getApDocId()))
						.put("dcNavigation", dcNavigation)
						.put("returnMap", returnMap);
			}

		} catch (GaiaBizException e) {
			log.error("기안문 저장 중 오류 발생", e);
			throw e;
		}
	}

	/**
	 * 결재문서 조회
	 * @param apDocId
	 * @return
	 */
	public List selectApDoc(String apDocId) {
		MybatisInput input = MybatisInput.of()
				.add("apDocId", apDocId)
				.add("cntrctNo", UserAuth.get(true).getCntrctNo())
				.add("pjtNo", UserAuth.get(true).getPjtNo())
				.add("cmnGrpCdPstn", CommonCodeConstants.PSTN_CODE_GROUP_CODE)
				.add("cmnGrpCdRank", CommonCodeConstants.RANK_CODE_GROUP_CODE);
		return draftService.selectApDoc(input);
	}


	/**
	 * 결재라인 조회
	 * @param apDocId
	 * @return
	 */
	public List selectApLineList(String apDocId) {
		MybatisInput input = MybatisInput.of()
				.add("apDocId", apDocId)
				.add("cntrctNo", UserAuth.get(true).getCntrctNo())
				.add("pjtNo", UserAuth.get(true).getPjtNo());
		return draftService.selectApLineList(input);
	}


	/**
	 * 첨부파일 조회
	 * @param apDocId
	 * @return
	 */
	public List selectApFileList(String apDocId) {
		return draftService.selectApFileList(apDocId);
	}


	/**
	 * 공유자 조회
	 * @param apDocId
	 * @return
	 */
	public List selectApShareList(String apDocId) {
		return draftService.selectApShareList(apDocId);
	}


	/**
	 * 기안페이지 내 임시저장 문서 선택 삭제(문서&결재라인&첨부파일)
	 * @param apDocId
	 */
	@Transactional
	public void deleteTemporary(String apDocId) {
		MybatisInput input = MybatisInput.of()
				.add("apDocId", apDocId)
				.add("usrId", UserAuth.get(true).getUsrId());
		draftService.deleteTemporaryApAttachmentsAll(input);
		draftService.deleteApDoc(input);
		draftService.deleteApLine(input);
	}


	/**
	 * 결재요청 or 임시저장 문서 연계데이터 포함 일괄 삭제 후 API 통신
	 * @param deleteApDocList
	 * @param reqVoMap
	 */
	@Transactional
	public void setDeleteList(List<ApDoc> deleteApDocList, Map<String, Object> reqVoMap) {
		String isApiYn = (String)reqVoMap.get("apiYn");
		String pjtDiv = (String)reqVoMap.get("pjtDiv");

		// 서식 그룹화 및 결재요청, 임시저장 문서 일괄 삭제
		deleteDraftList(deleteApDocList, UserAuth.get(true).getUsrId(), false);

		if(!"P".equals(pjtDiv) || !"Y".equals(isApiYn)) return;

		// PGAIA 프로젝트 체크
		List<ApDoc> deleteList = new ArrayList<>();
		deleteApDocList.forEach(apDoc -> {
			boolean toApi = false;
			if("01".equals(apDoc.getApType())) {
				Map<String, Object> checkParams = new HashMap<>();
				checkParams.put("pjtNo", UserAuth.get(true).getPjtNo());
				checkParams.put("apDocId", apDoc.getApDocId());
				boolean isPGAIA = draftService.checkPgaiaUserByApDocId(checkParams);
				toApi = isPGAIA && "CAIROS".equals(platform.toUpperCase());
			} else {
				toApi = true;
			}

			if(toApi) {
				deleteList.add(apDoc);
			}
		});

		// API통신 진행
		if(!deleteList.isEmpty()) {
			Map<String, Object> params = new HashMap<>();

			log.info("params : {}", params);
			params.put("apDocList", deleteList);
			params.put("usrId", UserAuth.get(true).getUsrId());

			Map response = invokeCairos2Pgaia("GACA8101", params);

			if (!"00".equals( org.apache.commons.collections4.MapUtils.getString(response, "resultCode") ) ) {
				throw new GaiaBizException(ErrorType.INTERFACE, org.apache.commons.collections4.MapUtils.getString(response, "resultMsg"));
			}

		}
	}


	/**
	 * 서식 그룹화 및 결재요청, 임시저장 문서 일괄 삭제
	 * @param deleteApDocList
	 * @param usrId
	 * @param toApi
	 */
	@Transactional
	public void deleteDraftList(List<ApDoc> deleteApDocList, String usrId, boolean toApi) {
		// 문서 타입별 그룹화
		Map<String, List<ApDoc>> groupedDocs = deleteApDocList.stream()
				.filter(apDoc -> !EapprovalHelper.APPROVAL_DOC.equals(apDoc.getApType()))
				.collect(Collectors.groupingBy(ApDoc::getApType));

		// 관련 문서별 승인요청상태 업데이트
		groupedDocs.forEach((apType, apDocs) -> {
			switch(apType) {
				// 기성
				case EapprovalHelper.PAYMENT_DOC:
					paymentService.updatePaymentApprovalReqCancel(apDocs, usrId, toApi);
					return;
				// 선급금
				case EapprovalHelper.DEPOSIT_DOC:
					depositService.updateDepositApprovalReqCancel(apDocs, usrId, toApi);
					return;
				// 작업일보
				case EapprovalHelper.DAILY_DOC:
					dailyreportService.updateDailyReportApprovalReqCancel(apDocs, usrId, toApi);
					return;
				// 월간보고
				case EapprovalHelper.MONTHLY_DOC:
					monthlyreportService.updateMonthlyreportApprovalReqCancel(apDocs, usrId, toApi);
					return;
				// 품질검측 검측
				case EapprovalHelper.QUALITY_ISP_DOC:
					qualityinspectionService.updateQualityIspApprovalReqCancel(apDocs, usrId, toApi);
					return;
				// 품질검측 결재
				case EapprovalHelper.QUALITY_APP_DOC:
					qualityinspectionService.updateQualityApprovalReqCancel(apDocs, usrId, toApi);
					return;
				// 안전점검
				case EapprovalHelper.SAFETY_DOC:
					safetymgmtService.updateSafetyReportReqCancel(apDocs, usrId, toApi);
					return;
				// 안전지적서
				case EapprovalHelper.SADTAG_DOC:
					safetymgmtService.updateSadtagApprovalReqCancel(apDocs, usrId, toApi);
					return;
				// 주간보고
				case EapprovalHelper.WEEKLY_DOC:
					weeklyreportService.updateWeeklyreportApprovalReqCancel(apDocs, usrId, toApi);
					return;
				// 감리일지
				// case INSPECTION_DOC: updateInspection(apDocs, usrId, toApi); return;
				// 안전점검 결과작성
				case EapprovalHelper.SAFETY_REP_DOC:
					safetymgmtService.updateSafetyReportReqCancel(apDocs, usrId, toApi);
					return;
				// 안전일지
				case EapprovalHelper.SAFETY_DIARY_DOC:
					safetyDiaryIntegrationService.updateSafetyDiaryReqCancel(apDocs, usrId, toApi);
					return;
				// 주요자재 검수요청서
				case EapprovalHelper.MAINMTRL_REQFRM_DOC:
					mainmtrlReqfrmService.updateMainmtrlReqfrmReqCancel(apDocs, usrId, toApi);
					return;
				default: throw new GaiaBizException(ErrorType.NOT_FOUND, String.format("존재하지 않는 문서 타입입니다: %s", apType));
			}
		});

		draftService.deleteAttachmentList(deleteApDocList); // 첨부파일 삭제
		draftService.deleteApDocList(deleteApDocList);		// 결재문서 삭제
		draftService.deleteApShareList(deleteApDocList);	// 공유자 삭제
		draftService.deleteApLineList(deleteApDocList);		// 결재선 삭제

	}


	/**
	 * 첨부파일 다운로드
	 * @param fileNo
	 * @param apDocId
	 * @return
	 */
	public ResponseEntity<Resource> fileDownload(Integer fileNo, String apDocId) {
		return eapprovalHelper.fileDownload(fileNo, apDocId);
	}


	/**
	 * 서식 즐겨찾기 목록 조회
	 * @return
	 */
	public List selectBookmarkList() {
		MybatisInput input = MybatisInput.of().add("usrId", UserAuth.get(true).getUsrId())
				.add("cntrctNo", UserAuth.get(true).getCntrctNo())
				.add("pjtNo", UserAuth.get(true).getPjtNo());
		return draftService.selectBookmarkList(input);
	}


	/**
	 * 서식 즐겨찾기 추가
	 * @param frmNo
	 */
	@Transactional
	public void createBookmark(Integer frmNo) {
		ApFavorites apFavorites = new ApFavorites();
		apFavorites.setFrmNo(frmNo);
		apFavorites.setFvrtsDiv("1");
		apFavorites.setUsrId(UserAuth.get(true).getUsrId());
		apFavorites.setLoginId(UserAuth.get(true).getLogin_Id());
		draftService.createBookmark(apFavorites);
	}


	/**
	 * 서식 즐겨찾기 삭제
	 * @param frmNo
	 */
	@Transactional
	public void deleteBookmark(Integer frmNo) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("frmNo", frmNo);
		params.put("userId", UserAuth.get(true).getUsrId());
		draftService.deleteBookmark(params);
	}


	/**
	 * API receive
	 * @param msgId
	 * @param params
	 * @return
	 */
	@Transactional
	public Map<String, Object> receiveApiOfDraft(String msgId, Map<String, Object> params) {
		Map<String, Object> result = new HashMap<>();

		result.put("resultCode", "00");

		try {
			// 기안문 생성
			if ("GACA8100".equals(msgId)) {
				log.info("API 연동 params : {}", params);
				// 1. 결재문서 생성
				ApDoc savedDoc = approvalService.insertApDocToApi(objectMapper.convertValue(params.get("apDoc"), ApDoc.class));

				// 2. 결재선<->apDocNo 매핑
				Integer apDocNo = savedDoc.getApDocNo();
				List<ApLine> apLineList = objectMapper.convertValue(params.get("apLineList"), new TypeReference<List<ApLine>>() {});
				apLineList.forEach(apLine -> {
					apLine.setApDocNo(apDocNo);
				});

				// 3. 결재선 생성
				approvalService.insertApLineToApi(apLineList);

				// 4. 첨부파일 생성
				List<MultipartFile> files = (List<MultipartFile>)params.get("files");
				if (files != null && !files.isEmpty()) {
					String uploadPath = getUploadPathByWorkType(FileUploadType.EAPPROVAL, savedDoc.getCntrctNo());
					eapprovalHelper.createApAttachmentsList(files, uploadPath, apDocNo, savedDoc.getApDocId(), savedDoc.getRgstrId());
				}

				// 5. 공유자생성
				List<ApShare> apShareList = objectMapper.convertValue(params.get("apShareList"), new TypeReference<List<ApShare>>() {});
				if (!apShareList.isEmpty()) {
					apShareList.forEach(apShare -> {
						apShare.setApDocNo(apDocNo);
					});
					draftService.insertApShareToApi(apShareList);
				}

				// 6.문서관리 첨부파일 저장
				List<Map<String, Object>> apDocFileInfo = (List<Map<String, Object>>) params.get("apDocFileInfo");
				if (apDocFileInfo != null && !apDocFileInfo.isEmpty()) {
					eapprovalHelper.insertApDocFileInfoToApi(savedDoc, apDocFileInfo);
				}
			}

			// 기안문 삭제
			if("GACA8101".equals(msgId)) {
				deleteDraftList(objectMapper.convertValue(params.get("apDocList"), new TypeReference<List<ApDoc>>() {}), (String) params.get("usrId"), true);
			}
		} catch (GaiaBizException e) {
			log.error(e.getMessage(), e);
			result.put("resultCode", "01");
			result.put("resultMsg", e.getMessage());
		}

		return result;
	}


	/**
	 * 전자결재 서식 조회
	 * @param input
	 * @return
	 */
	public List<ApFormListOutput> selectApFormList(MybatisInput input) {
		return draftService.selectApFormList(input);
	}


	/**
	 * 전자결재 임시저장 조회
	 * @param apDocId
	 * @return
	 */
	public Map<String, Object> selectTempDraft(String apDocId) {
		Map<String, Object> returnMap = new HashMap<>();
		List<Map<String, Object>> apDoc = selectApDoc(apDocId);
		returnMap.put("apDoc", apDoc);
		returnMap.put("draftApLineList", selectApLineList(apDocId));
		returnMap.put("draftApFileList", selectApFileList(apDocId));
		returnMap.put("draftApShareList", selectApShareList(apDocId));

		DcNavigation dcNavigation = null;
		String naviId = (String)apDoc.get(0).get("navi_id");
		if(naviId != null) {
			dcNavigation = documentService.getNavigation(naviId);
		}
		returnMap.put("dcNavigation", dcNavigation);

		return returnMap;
	}
}
