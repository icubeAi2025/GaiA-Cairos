package kr.co.ideait.platform.gaiacairos.comp.eapproval;


import com.fasterxml.jackson.core.type.TypeReference;
import kr.co.ideait.iframework.BizException;
import kr.co.ideait.platform.gaiacairos.comp.construction.service.DailyreportService;
import kr.co.ideait.platform.gaiacairos.comp.construction.service.MainmtrlReqfrmService;
import kr.co.ideait.platform.gaiacairos.comp.construction.service.QualityinspectionService;
import kr.co.ideait.platform.gaiacairos.comp.document.service.DocumentService;
import kr.co.ideait.platform.gaiacairos.comp.eapproval.helper.EapprovalHelper;
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
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.eapproval.approval.ApprovalMybatisParam.*;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.security.UserAuth;
import kr.co.ideait.platform.gaiacairos.core.util.restclient.DocumentServiceClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class ApprovalComponent extends AbstractComponent {

	@Autowired
	EapprovalHelper eapprovalHelper;

	@Autowired
	ApprovalService approvalService;

	@Autowired
	DraftService draftService;

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
	DocumentServiceClient documentServiceClient;

	@Autowired
	DocumentService documentService;

	@Autowired
	SafetyDiaryIntegrationService safetyDiaryIntegrationService;

	@Autowired
	MainmtrlReqfrmService mainmtrlReqfrmService;


	// Common Code
	String cmnGrpCdDcsts = CommonCodeConstants.DCSTS_CODE_GROUP_CODE;	// 결재문서상태(대기W, 진행중I, 완료C, 반려R, 임시저장T)
	String cmnGrpCdType = CommonCodeConstants.TYPE_CODE_GROUP_CODE;		// 결재문서 유형



    public Page<ApprovalListOutput> getApprovalList(ApprovalListInput approvalListInput) {
        String status = approvalListInput.getData();
		List<ApprovalListOutput> approvalListOutput = null;
		Long totalCount = null;
		approvalListInput.setCmnGrpCdDcsts(cmnGrpCdDcsts);
		approvalListInput.setCmnGrpCdType(cmnGrpCdType);
		switch (status) {
			// 요청
			case "request":
				approvalListOutput = approvalService.selectRequestList(approvalListInput);
				totalCount = approvalService.selectRequestListCount(approvalListInput);
				break;
			// 임시저장
			case "temporary":
				approvalListOutput = approvalService.selectTemporaryList(approvalListInput);
				totalCount = approvalService.selectTemporaryListCount(approvalListInput);
				break;
			// 대기
			case "waiting":
				approvalListOutput = approvalService.selectWaitingList(approvalListInput);
				totalCount = approvalService.selectWaitingListCount(approvalListInput);
				break;
			// 진행
			case "progress":
				approvalListOutput = approvalService.selectProgressList(approvalListInput);
				totalCount = approvalService.selectProgressListCount(approvalListInput);
				break;
			// 완료
			case "closed":
				approvalListOutput = approvalService.selectClosedList(approvalListInput);
				totalCount = approvalService.selectClosedListCount(approvalListInput);
				break;
			// 반려
			case "rejected":
				approvalListOutput = approvalService.selectRejectedList(approvalListInput);
				totalCount = approvalService.selectRejectedListCount(approvalListInput);
				break;
			// 참조,공유
			case "shared":
				approvalListOutput = approvalService.selectSharedList(approvalListInput);
				totalCount = approvalService.selectSharedListCount(approvalListInput);
				break;
			// 대시보드 결재요청(미결만 조회)
			case "pending":
				approvalListOutput = approvalService.selectPendingList(approvalListInput);
				totalCount = approvalService.selectPendingListCount(approvalListInput);
				break;
			default:
				approvalListOutput = new ArrayList<>();
				totalCount = 0L;
				break;
		}
		return new PageImpl<>(approvalListOutput, approvalListInput.getPageable(), totalCount);
    }


	/**
	 * 상세검색 셀렉트옵션 - 서식 리스트 조회
	 * @param input
	 * @return
	 */
	public List<ApprovalFormListOutput> getFormAllList(MybatisInput input) {
		return approvalService.getFormAllList(input);
	}

	public List selectApTypeOptionsList() {
		return approvalService.selectApTypeOptionsList(cmnGrpCdType);
	}

	public List<MybatisOutput> getApprovalLine(MybatisInput input) {
		return approvalService.getApprovalLine(input);
	}

	public ApprovalDetailOutput getApDocDetail(MybatisInput input) {
		input.add("cmnGrpCdDcsts", cmnGrpCdDcsts);
		return approvalService.getApDocDetail(input);
	}

	public List<MybatisOutput> getApLineDetail(MybatisInput input) {
		return approvalService.getApLineDetail(input);
	}


	public List<MybatisOutput> getMyApLine(MybatisInput input) {
		return approvalService.getMyApLine(input);
	}

	public List<ApprovalShareListOutput> getApShareDetail(String apDocId) {
		return approvalService.getApShareDetail(apDocId);
	}


	/**
	 * 결재결과 일괄 승인 또는 반려
	 * @param approveList
	 * @param reqVoMap
	 */
	@Transactional
	public void updateApprovalList(ApproveListInput approveList, Map<String, Object> reqVoMap) {
		String isApiYn = (String)reqVoMap.get("apiYn");
		String pjtDiv = (String)reqVoMap.get("pjtDiv");
		String platformType = platform.toUpperCase();

		List<ApLineUpdate> updateApLineList = approveList.getApproveDocList();
		String apUsrId = UserAuth.get(true).getUsrId();
		String apStats = approveList.getApStats();

		// 1. 결재선(ApLine) 상태 update -> 승인 or 반려
		updateApLineList.forEach(target -> {
			updateApLineStatus(target, apStats, apUsrId);
		});

		// 2. 알림 생성 -> 결재가 진행 중(I)인 경우
		List<ApLineUpdate> createAlarmsList = updateApLineList.stream()
				.filter(apDoc -> apDoc.getApDocStats() == null || "I".equals(apDoc.getApDocStats()))
				.collect(Collectors.toList());
		if (!createAlarmsList.isEmpty()) {
			createAlarmsList.forEach(alarm -> {
				eapprovalHelper.createNextAlarms(alarm.getApDocId());
			});
		}

		// 3. 결재선(ApLine) 리스트 -> 결재문서(ApDoc) 리스트로 변환
		List<ApLineUpdate> updateApDocList = updateApLineList.stream()
				.filter(apDoc -> apDoc.getApDocStats() != null)
				.collect(Collectors.toList());

		if (!updateApDocList.isEmpty()) {
			// 승인상태 변경할 결재문서(ApDoc), 알림 생성 여부, 결재자 id
			try {
				updateApDocStatus(updateApDocList, true, apUsrId);
			} catch (GaiaBizException e) {
				log.error("전자결재 리스트 승인/반려 시 오류 발생", e);
			}
		}

		// 민간gaia 플랫폼 or cairos플랫폼&반려처리 or api통신제외 or 민간 프로젝트면 return
		if("GAIA".equals(platformType) || ("CAIROS".equals(platformType) && "R".equals(apStats)) || "N".equals(isApiYn) || !"P".equals(pjtDiv)) return;

		String pjtNo = UserAuth.get(true).getPjtNo();
		String usrId = UserAuth.get(true).getUsrId();

		Map<String, Object> response = new HashMap<>();

		// 4. cairos이고 승인일 때
		boolean isSupervisor = approvalService.checkSupervisor(pjtNo, usrId);
		if("CAIROS".equals(platformType) && isSupervisor) {
			// 카이로스 + 관리관인 경우
			Map<String, Object> params = new HashMap<>();

			params.put("updateApLineList", updateApLineList);
			params.put("updateApDocList", updateApDocList);
			params.put("usrId", UserAuth.get(true).getUsrId());
			params.put("isList", true);

			log.info("send params : {}", params);

			response = invokeCairos2Pgaia("GACA8201", params); //receiveApiOfApprovalUpdate

			if (!"00".equals( org.apache.commons.collections4.MapUtils.getString(response, "resultCode") ) ) {
				throw new GaiaBizException(ErrorType.INTERFACE, org.apache.commons.collections4.MapUtils.getString(response, "resultMsg"));
			}

		} else if("CAIROS".equals(platformType) && !"R".equals(apStats)) {
			for(ApLineUpdate item : updateApDocList) {

				String apDocId = item.getApDocId();
				String apType = item.getApType();

				// 승인요청 데이터
				Map<String, Object> params = new HashMap<>();
				params.put("apStats", apStats);
				params.put("apDocStats", item.getApDocStats());
				params.put("usrId", usrId);
				params.put("reportType", apType);
				params.put("isList", true);

				// 승인요청 데이터 조회
				if(!EapprovalHelper.APPROVAL_DOC.equals(apType)) {
					Map<String, Object> returnMap = selectApTypeResources(apDocId, apType);
					params.put("report", returnMap.get("report"));
					params.put("resources", returnMap.get("resources"));
				}

				// 다음 결재자가 pgaia 유저이면 전자결재 데이터 전송
				Map<String, Object> checkParams = new HashMap<>();
				checkParams.put("pjtNo", pjtNo);
				checkParams.put("apDocId", apDocId);
				checkParams.put("usrId", usrId);
				boolean isPGAIA = approvalService.checkPgaiaApprover(checkParams);

				// 다음 결재자가 pgaia 유저 → 전자결재 데이터까지 전송
				if (isPGAIA) {
					ApDoc apDoc = approvalService.getApprovalDoc(apDocId);
					List<ApLine> apLineList = approvalService.getApLineByApDocId(apDocId);
					List<ApShare> apShareList = approvalService.getApShareByApDocId(apDocId);
					List<ApAttachments> apDocFiles = eapprovalHelper.getApAttachmentsByApDocId(apDocId);

					List<Map<String, Object>> apDocFileInfo = Collections.emptyList();
					if(apDocFiles != null && !apDocFiles.isEmpty()) {
						apDocFileInfo = eapprovalHelper.convertToFileInfo(apDocFiles);
					}
					params.put("apDoc", apDoc);
					params.put("apLineList", apLineList);
					params.put("apShareList", apShareList);
					params.put("apDocFileInfo", apDocFileInfo);
					params.put("cntrctNo", apDoc.getCntrctNo());
				} else if(!"C".equals(item.getApDocStats())) {
					continue;
				}

				log.info("[CAIROS→PGAIA] 승인요청 데이터 전송: apDocId = {}", apDocId);
				log.info("전송 데이터 : params = {}", params);

				// 카이로스 유저가 결재 승인 시 요청 데이터(+다음 결재자 pgaia 유저이면 결재 데이터) 인서트
				response = invokeCairos2Pgaia("GACA8200", params); //receiveApiOfApprovalInsert

				if (!"00".equals( org.apache.commons.collections4.MapUtils.getString(response, "resultCode") ) ) {
					throw new GaiaBizException(ErrorType.INTERFACE, org.apache.commons.collections4.MapUtils.getString(response, "resultMsg"));
				}
			}
		} else if ("PGAIA".equals(platformType)) {
			Map<String, Object> params = new HashMap<>();

			params.put("updateApLineList", updateApLineList);
			params.put("updateApDocList", updateApDocList);
			params.put("usrId", UserAuth.get(true).getUsrId());
			params.put("isList", true);

			log.info("send params : {}", params);

			response = invokePgaia2Cairos("GACA8201", params); //receiveApiOfApprovalUpdate

			if (!"00".equals( org.apache.commons.collections4.MapUtils.getString(response, "resultCode") ) ) {
				throw new GaiaBizException(ErrorType.INTERFACE, org.apache.commons.collections4.MapUtils.getString(response, "resultMsg"));
			}
		}
	}


	/**
	 * 상세페이지 - 결재문서 단건 업데이트(결재의견, 결재결과, 공유자)
	 * @param approveOne
	 * @return
	 */
	@Transactional
	public ApDoc updateApprovalOne(ApproveOneInput approveOne, Map<String, Object> reqVoMap) {

		approveOne.setApUsrId(UserAuth.get(true).getUsrId());
		String isApiYn = (String)reqVoMap.get("apiYn");
		String pjtDiv = (String)reqVoMap.get("pjtDiv");
		String platformType = platform.toUpperCase();
		String apStats = approveOne.getApStats();

		// 공유자 변경
		updateShareList(approveOne);

		// 결재상태 변경
		ApLineUpdate resultApLine = updateApLineStatus(approveOne.getApLine(), apStats, UserAuth.get(true).getUsrId());
		String apDocId = resultApLine.getApDocId();

		// 결재가 진행 중인 경우 다음 결재자 알림 생성
		if(resultApLine.getApDocStats() == null || "I".equals(resultApLine.getApDocStats())) {
			eapprovalHelper.createNextAlarms(apDocId);
		}

		// 결재문서(ApDoc) 상태 업데이트&종료 알림&외부문서 상태 업데이트
		// 기성의 경우 유레카 API 통신
		updateApDocStatus(Collections.singletonList(resultApLine), true, approveOne.getApUsrId());

		ApDoc apDoc = approvalService.getApprovalDoc(apDocId);

		String apType = apDoc.getApType();

		// 민간gaia 플랫폼 or cairos플랫폼&반려처리 or api통신제외 or 민간 프로젝트면 return
		if("GAIA".equals(platformType) || ("CAIROS".equals(platformType) && "R".equals(apStats)) || "N".equals(isApiYn) || !"P".equals(pjtDiv)) return apDoc;

		Map<String, Object> params = new HashMap<>();
		params.put("approveOne", approveOne);
		params.put("apStats", apStats);
		params.put("apDocStats", apDoc.getApDocStats());
		params.put("usrId", UserAuth.get(true).getUsrId());
		params.put("reportType", apDoc.getApType());
		params.put("isList", false);
		params.put("cntrctNo", apDoc.getCntrctNo());

		log.info("API params : {}", params);

		Map<String, Object> response = new HashMap<>();

		boolean isSupervisor = approvalService.checkSupervisor(UserAuth.get(true).getPjtNo(), UserAuth.get(true).getUsrId());
		if("CAIROS".equals(platformType) && isSupervisor) {
			// 카이로스 + 관리관인 경우
			response = invokeCairos2Pgaia("GACA8201", params); //receiveApiOfApprovalUpdate

			if (!"00".equals( org.apache.commons.collections4.MapUtils.getString(response, "resultCode") ) ) {
				throw new GaiaBizException(ErrorType.INTERFACE, org.apache.commons.collections4.MapUtils.getString(response, "resultMsg"));
			}

		} else if("CAIROS".equals(platformType) && !"R".equals(apStats)) {
			// 카이로스 + 일반부서인 경우
			// 승인요청 데이터 조회
			if(!EapprovalHelper.APPROVAL_DOC.equals(apType)) {
				Map<String, Object> returnMap = selectApTypeResources(apDocId, apType);
				params.put("report", returnMap.get("report"));
				params.put("resources", returnMap.get("resources"));
			}

			// 다음 결재자 pgaia 유저 체크
			Map<String, Object> checkParams = new HashMap<>();
			checkParams.put("pjtNo", UserAuth.get(true).getPjtNo());
			checkParams.put("apDocId", apDocId);
			checkParams.put("usrId", UserAuth.get(true).getUsrId());
			boolean isPGAIA = approvalService.checkPgaiaApprover(checkParams);

			// 다음 결재자가 pgaia 유저면 전자결재 데이터 전송
			if(isPGAIA) {
				List<ApLine> apLineList = approvalService.getApLineByApDocId(apDocId);
				List<ApShare> apShareList = approvalService.getApShareByApDocId(apDocId);
				List<ApAttachments> apDocFiles = eapprovalHelper.getApAttachmentsByApDocId(apDocId);
				params.put("apDoc", apDoc);
				params.put("apLineList", apLineList);
				params.put("apShareList", apShareList);

				List<Map<String, Object>> apDocFileInfo = Collections.emptyList();
				if(apDocFiles != null && !apDocFiles.isEmpty()) {
					apDocFileInfo = eapprovalHelper.convertToFileInfo(apDocFiles);
				}
				params.put("apDocFileInfo", apDocFileInfo);

				// 카이로스 유저가 결재 승인 시 요청 데이터(+다음 결재자 pgaia 유저이면 결재 데이터) 인서트
				response = invokeCairos2Pgaia("GACA8200", params); //receiveApiOfApprovalInsert

				if (!"00".equals( org.apache.commons.collections4.MapUtils.getString(response, "resultCode") ) ) {
					throw new GaiaBizException(ErrorType.INTERFACE, org.apache.commons.collections4.MapUtils.getString(response, "resultMsg"));
				}
			} else if(!"C".equals(apDoc.getApDocStats())) {
				return apDoc;
			}
		} else if("PGAIA".equals(platformType)) {
			// pgaia 플랫폼에서 승인요청 문서를 승인, 반려처리 한 경우 update api 통신
			response = invokePgaia2Cairos("GACA8201", params); //receiveApiOfApprovalUpdate

			if (!"00".equals( org.apache.commons.collections4.MapUtils.getString(response, "resultCode") ) ) {
				throw new GaiaBizException(ErrorType.INTERFACE, org.apache.commons.collections4.MapUtils.getString(response, "resultMsg"));
			}
		}

		return apDoc;
	}



	/**
	 * 결재선 승인/반려 업데이트
	 * @param target
	 * @param apStats
	 * @param apUsrId
	 * @return
	 */
	private ApLineUpdate updateApLineStatus(ApLineUpdate target, String apStats, String apUsrId) {
		log.info("updateApLine() target : {}" , target);

		target.setApUsrId(apUsrId);
		target.setApStats(apStats);

		// 결재문서별 최대 결재순서
		int maxOrder = approvalService.selectApprovalMaxOrder(target.getApDocId());

		// 로그인 사용자의 실제 결재 순서 조회 (참조 제외)
		Map<String, Object> params = new HashMap<>();
		params.put("apUsrId", apUsrId);
		params.put("apDocId", target.getApDocId());
		int myOrder = approvalService.selectMyApLineOrder(params);

		/////////////////////////// 결재선에 따른 ApDoc 상태 세팅 ///////////////////////////
		if(myOrder == 1) {
			// 첫번째 결재자일 경우
			target.setApDocStats("I");
		}

		if("R".equals(apStats)) {
			// 반려일경우
			target.setApDocStats("R");
		} else if ("A".equals(apStats) && maxOrder == myOrder){
			// 결재완료 및 마지막결재자인 경우
			target.setApDocStats("C");
		}

		// 결재선 상태 변경
		approvalService.updateApLineStatus(target);

		return target;

	}


	/**
	 * 결재문서(ApDoc) 상태 업데이트
	 * @param targetList
	 * @param createAlarm
	 * @param apUsrId
	 */
	private void updateApDocStatus(List<ApLineUpdate> targetList, boolean createAlarm, String apUsrId) {
		log.info("targetList: {}", targetList);

		// 결재문서(ApDoc) 상태 업데이트(I, C, R)
		approvalService.updateApDoc(targetList);

		// 전자결재 승인 요청 문서(보고서)의 상태 변경 및 알림처리
		targetList.forEach(target -> {
			ApDoc findApDoc = approvalService.getApprovalDoc(target.getApDocId());
			log.info("findApDoc: {}", findApDoc);
			String apDocId = findApDoc.getApDocId();
			String apDocStats = findApDoc.getApDocStats();

			Map<String, String> nameList = approvalService.selectUserNameForDocument(apDocId);
			// 문서관리 -> 결재문서 생성
			if("C".equals(findApDoc.getApDocStats()) && "16".equals(findApDoc.getFrmId())) {
				Map<String, Object> requestParams = new HashMap<>();
				requestParams.put("apDoc", findApDoc);
				requestParams.put("apUsrNm", nameList.get("ap_usr_nm"));
				requestParams.put("apCmpltUsrNms", nameList.get("ap_cmplt_usr_nms"));
				requestParams.put("usrId", apUsrId);
				documentServiceClient.createApprovalDocument(requestParams);
			}

			if("C".equals(findApDoc.getApDocStats()) || "R".equals(findApDoc.getApDocStats())) {
				// 1. 완료, 반려 알람생성 -> 마지막결재자 제외, 모든 결재자 및 기안자에 알림
				if(createAlarm) {
					eapprovalHelper.createCompleteAlarm(apDocId, apDocStats);
				}
				// 2. 외부문서 상태 업데이트
				if(!EapprovalHelper.APPROVAL_DOC.equals(findApDoc.getApType())) {
					switch(findApDoc.getApType()) {
						// 기성
						case EapprovalHelper.PAYMENT_DOC:
							// 승인, 반려 시 유레카 연동
							paymentService.updatePaymentByApDocId(apDocId, apUsrId, apDocStats);
							break;
						// 선급금
						case EapprovalHelper.DEPOSIT_DOC:
							depositService.updateDepositByApDocId(apDocId, apUsrId, apDocStats);
							break;
						// 작업일보
						case EapprovalHelper.DAILY_DOC:
							dailyreportService.updateDailyReportByApDocId(apDocId, apUsrId, apDocStats);
							break;
						// 월간보고
						case EapprovalHelper.MONTHLY_DOC:
							monthlyreportService.updateMonthlyReportByApDocId(apDocId, apUsrId, apDocStats);
							break;
						// 품질검측 검측요청
						case EapprovalHelper.QUALITY_ISP_DOC:
							qualityinspectionService.updateQualityInspectionByApDocId(apDocId, apUsrId, apDocStats, target.getApUsrOpnin(), "ISP");
							break;
						// 품질검측 결재요청
						case EapprovalHelper.QUALITY_APP_DOC:
							qualityinspectionService.updateQualityInspectionByApDocId(apDocId, apUsrId, apDocStats, target.getApUsrOpnin(), "APP");
							break;
						// 안전점검 결과작성 요청
						case EapprovalHelper.SAFETY_REP_DOC:
							safetymgmtService.updateSafetyByApDocId(apDocId, apUsrId, apDocStats, target.getApUsrOpnin(), "REP");
							break;
						// 안전점검 승인요청
						case EapprovalHelper.SAFETY_DOC:
							safetymgmtService.updateSafetyByApDocId(apDocId, apUsrId, apDocStats, target.getApUsrOpnin(), "");
							break;
						// 안전지적서 승인요청
						case EapprovalHelper.SADTAG_DOC:
							safetymgmtService.updateSadtagByApDocId(apDocId, apUsrId, apDocStats, target.getApUsrOpnin());
							break;
						// 주간보고 승인요청
						case EapprovalHelper.WEEKLY_DOC:
							weeklyreportService.updateWeeklyreportByApDocId(apDocId, apUsrId, apDocStats);
							break;
						// 감리일지 승인요청
						// case INSPECTION_DOC:
						// 	inspectionreportService.updateInspectionByApDocId(apDocId, apUsrId, apDocStats);
						// 	break;
						// 안전일지
						case EapprovalHelper.SAFETY_DIARY_DOC:
							safetyDiaryIntegrationService.updateSafetDiaryByApDocId(apDocId, apUsrId, apDocStats, target.getApUsrOpnin());
							break;
						// 주요자재 검수요청서
						case EapprovalHelper.MAINMTRL_REQFRM_DOC:
							mainmtrlReqfrmService.updateMainmtrlReqfrmByApDocId(apDocId, apUsrId, apDocStats, target.getApUsrOpnin(), apUsrId);
							break;
						default:
							throw new GaiaBizException(ErrorType.NOT_FOUND, String.format("존재하지 않는 문서 타입입니다: %s", findApDoc.getApType()));
					}
				}
			}
		});
	}

	/**
	 * 각 문서타입별 report 및 연계데이터 조회
	 * @param apDocId
	 * @param apType
	 * @return
	 */
	public Map<String, Object> selectApTypeResources(String apDocId, String apType) {
		switch(apType) {
			// 기성
			case EapprovalHelper.PAYMENT_DOC:
				return paymentService.selectPaymentByApDocId(apDocId);
			// 선급금
			case EapprovalHelper.DEPOSIT_DOC:
				return depositService.selectDepositByApDocId(apDocId);
			// 작업일보
			case EapprovalHelper.DAILY_DOC:
				return dailyreportService.selectDailyReportByApDocId(apDocId);
			// 월간보고
			case EapprovalHelper.MONTHLY_DOC:
				return monthlyreportService.selectMonthlyReportByApDocId(apDocId);
			// 품질검측 검측요청
			case EapprovalHelper.QUALITY_ISP_DOC:
				return qualityinspectionService.selectQualityInspectionByApDocId(apDocId, "ISP");
			// 품질검측 결재요청
			case EapprovalHelper.QUALITY_APP_DOC:
				return qualityinspectionService.selectQualityInspectionByApDocId(apDocId, "APP");
			// 안전점검 결과작성 요청
			case EapprovalHelper.SAFETY_REP_DOC:
				return safetymgmtService.selectSafetyByApDocId(apDocId, "REP");
			// 안전점검 승인요청
			case EapprovalHelper.SAFETY_DOC:
				return safetymgmtService.selectSafetyByApDocId(apDocId, "APP");
			// 안전지적서 승인요청
			case EapprovalHelper.SADTAG_DOC:
				return safetymgmtService.selectSadtagByApDocId(apDocId);
			// 주간보고
			case EapprovalHelper.WEEKLY_DOC:
				return weeklyreportService.selectWeeklyreportByApDocId(apDocId);
			// 감리일지
			// case INSPECTION_DOC:
			// 	return inspectionreportService.selectInspectionByApDocId(apDocId);
			// 안전일지
			case EapprovalHelper.SAFETY_DIARY_DOC:
				return safetyDiaryIntegrationService.selectSafetyDiaryByApDocId(apDocId);
			default:
				throw new BizException("존재하지 않는 문서 타입입니다: " + apType);
		}
	}


	/**
	 * 공유자 추가 / 삭제
	 * @param updateDoc
	 */
	private void updateShareList(ApproveOneInput updateDoc) {
		//공유자 삭제
		if(!updateDoc.getDelShareList().isEmpty()) {
			approvalService.updateDeleteApShareList(updateDoc.getDelShareList());
		}

		//공유자 추가 : 저장할 공유자 리스트 있으면 추가, 없으면 기존 리스트 조회 후 삭제
		if(!updateDoc.getApShareList().isEmpty()) {
			createApShareList(updateDoc.getApShareList(), updateDoc.getPjtNo(), updateDoc.getCntrctNo(), updateDoc.getApUsrId());
		} else {
			updateDeleteApShareList(updateDoc.getApLine().getApDocId(), updateDoc.getApUsrId());
		}
	}

	/**
	 * 공유자 추가
	 * @param apShareList
	 * @param pjtNo
	 * @param cntrctNo
	 */
	public List<ApShare> createApShareList(List<ApShare> apShareList, String pjtNo, String cntrctNo, String apUsrId) {
		List<ApShare> newApShareList = new ArrayList<ApShare>();
		apShareList.forEach(apShare -> {
			apShare.setRgstrId(apUsrId);
			apShare.setRgstDt(LocalDateTime.now());
			// 전체공유면 계약단위로 세팅
			if("01".equals(apShare.getApCnrsDiv())){
				apShare.setApCnrsId(cntrctNo);
			}
			// 기존 공유자 여부 체크
			boolean exists = approvalService.checkApShare(apShare.getApDocId(), apShare.getApCnrsId());
			if(!exists) {
				apShare.setDltYn("N");
				newApShareList.add(apShare);
			}
		});

		if(!newApShareList.isEmpty()) {
			approvalService.createApShareList(newApShareList);
		}
		return apShareList;
	}

	/**
	 * 공유자 삭제
	 * @param apDocId
	 */
	public void updateDeleteApShareList(String apDocId, String usrId) {
		MybatisInput input = MybatisInput.of()
				.add("apDocId", apDocId)
				.add("usrId", usrId);
		approvalService.updateDeleteApShareByApDocId(input);
	}


	/**
	 * 참조자 문서확인 업데이트
	 * @param apLine
	 * @param reqVo
	 */
	@Transactional
	public void updateReferenceDate(ApLine apLine, Map<String, Object> reqVo) {
		approvalService.updateReferenceDate(apLine);

		String isApiYn = (String)reqVo.get("apiYn");
		String pjtDiv = (String)reqVo.get("pjtDiv");

		if("N".equals(isApiYn) || !"P".equals(pjtDiv)) return;

		// 결재선 PGAIA 유저 체크
		Map<String, Object> checkParams = new HashMap<>();
		checkParams.put("pjtNo", UserAuth.get(true).getPjtNo());
		checkParams.put("apDocId", apLine.getApDocId());
		boolean toApi = approvalService.checkPgaiaReference(checkParams);

		if(!toApi) return;

		Map<String, Object> sendParams = new HashMap<>();
		sendParams.put("referenceUpdate", apLine);

		log.info("참조자 상태 업데이트 API Params: {}", sendParams);

		Map<String, Object> response;
		if("CAIROS".equals(platform.toUpperCase())) {
			response = invokeCairos2Pgaia("GACA8202", sendParams);
			if (!"00".equals( org.apache.commons.collections4.MapUtils.getString(response, "resultCode") ) ) {
				throw new GaiaBizException(ErrorType.INTERFACE, org.apache.commons.collections4.MapUtils.getString(response, "resultMsg"));
			}
		} else if("PGAIA".equals(platform.toUpperCase())) {
			response = invokePgaia2Cairos("GACA8202", sendParams);
			if (!"00".equals( org.apache.commons.collections4.MapUtils.getString(response, "resultCode") ) ) {
				throw new GaiaBizException(ErrorType.INTERFACE, org.apache.commons.collections4.MapUtils.getString(response, "resultMsg"));
			}
		}


	}


	/**
	 * 조직도 > 부서조회
	 * @param input
	 * @return
	 */
	public List<MybatisOutput> getApprovalDeptInfo(MybatisInput input) {
		return UserAuth.get(true).isAdmin() ? approvalService.selectAdminDept(input) : approvalService.selectDepartmentInfo(input);
	}


	/**
	 * 부서 소속직원 조회
	 * @param deptId
	 * @return
	 */
	public List selectEmployeeList(String deptId) {
		return approvalService.selectEmployeeList(deptId);
	}



	/**
	 * API receive: GACA8200 승인요청
	 * @param msgId
	 * @param params
	 * @return
	 */
	@Transactional
	public Map<String, Object> receiveApiOfApprovalInsert(String msgId, Map<String, Object> params) {
		Map<String, Object> result = new HashMap<>();

		result.put("resultCode", "00");

		try {
			if("GACA8200".equals(msgId)) {
				log.info("API 연동 params : {}", params);

				String apStats = (String)params.get("apStats");
				String apDocStats = (String)params.get("apDocStats");
				String reportType = (String)params.get("reportType");
				String usrId = (String)params.get("usrId");
				String cntrctNo = (String)params.get("cntrctNo");

				// 결재 완료가 아닐 경우 문서 생성
				if(!"C".equals(apDocStats) || EapprovalHelper.APPROVAL_DOC.equals(reportType)) {
					// 1. 결재문서 생성
					ApDoc savedDoc = approvalService.insertApDocToApi(objectMapper.convertValue(params.get("apDoc"), ApDoc.class));

					// 2. 결재선<->apDocNo 매핑
					Integer apDocNo = savedDoc.getApDocNo();
					List<ApLine> apLineList = objectMapper.convertValue(params.get("apLineList"), new TypeReference<List<ApLine>>() {});
					apLineList.forEach(apLine -> {
						apLine.setApDocNo(apDocNo);
					});

					// 3. 공유자->apDocNo 매핑
					List<ApShare> apShareList = objectMapper.convertValue(params.get("apShareList"), new TypeReference<List<ApShare>>() {});
					if(apShareList != null && !apShareList.isEmpty()) {
						apShareList.forEach(apShare -> {
							apShare.setApDocNo(apDocNo);
						});
					}

					// 4. 결재선 생성
					approvalService.insertApLineToApi(apLineList);

					// 5. 첨부파일 저장
					if(EapprovalHelper.APPROVAL_DOC.equals(reportType)) {
						List<Map<String, Object>> apDocFileInfo = (List<Map<String, Object>>) params.get("apDocFileInfo");
						if (apDocFileInfo != null && !apDocFileInfo.isEmpty()) {
							eapprovalHelper.insertApDocFileInfoToApi(savedDoc, apDocFileInfo);
						}
					}
				}

				if(EapprovalHelper.APPROVAL_DOC.equals(reportType)) return result;

				// 각 메뉴별 연계데이터 꺼내기
				Map<String, Object> resources = (Map<String, Object>) params.get("resources");
				log.info("메뉴별 resources : {}", resources);
				switch(reportType) {
					// 기성
					case EapprovalHelper.PAYMENT_DOC:
						paymentService.insertPgaiaPayment(
								objectMapper.convertValue(params.get("report"), CwPayMng.class),
								objectMapper.convertValue(resources.get("payCostCalculator"), new TypeReference<List<CwPayCostCalculator>>() {}),
								objectMapper.convertValue(resources.get("payDetail"), new TypeReference<List<CwPayDetail>>() {}),
								objectMapper.convertValue(resources.get("activity"),  new TypeReference<List<CwPayActivity>>() {}),
								usrId
						);
						break;
					// 선급금
					case EapprovalHelper.DEPOSIT_DOC:
						depositService.insertPgaiaDeposit(
								objectMapper.convertValue(params.get("report"), CwFrontMoney.class),
								resources
						);
						break;
					// 작업일보
					case EapprovalHelper.DAILY_DOC:
						dailyreportService.insertPgaiaDailyReport(
								objectMapper.convertValue(params.get("report"), CwDailyReport.class),
								objectMapper.convertValue(resources.get("activity"), new TypeReference<List<CwDailyReportActivity>>() {}),
								objectMapper.convertValue(resources.get("qdb"), new TypeReference<List<CwDailyReportQdb>>() {}),
								objectMapper.convertValue(resources.get("photo"), new TypeReference<List<CwDailyReportPhoto>>() {}),
								objectMapper.convertValue(resources.get("dailyReportFileInfo"), new TypeReference<List<Map<String, Object>>>() {}),
								objectMapper.convertValue(resources.get("resource"), new TypeReference<List<CwDailyReportResource>>() {})
						);
						break;
					// 월간보고
//					case EapprovalHelper.MONTHLY_DOC:
//						monthlyreportService.insertResourcesToApi(
//								objectMapper.convertValue(params.get("report"), PrMonthlyReport.class),
//								objectMapper.convertValue(resources.get("activityList"), new TypeReference<List<PrMonthlyReportActivity>>() {}),
//								objectMapper.convertValue(resources.get("progressList"), new TypeReference<List<PrMonthlyReportProgress>>() {})
//						);
//						break;
					// 월간보고
					case EapprovalHelper.MONTHLY_DOC:
						monthlyreportService.insertResourcesToApi(
								cntrctNo,
								objectMapper.convertValue(params.get("report"), PrMonthlyReport.class),
								objectMapper.convertValue(resources.get("activityList"), new TypeReference<List<PrMonthlyReportActivity>>() {}),
								objectMapper.convertValue(resources.get("progressList"), new TypeReference<List<PrMonthlyReportProgress>>() {}),
								objectMapper.convertValue(resources.get("statusList"), new TypeReference<List<PrMonthlyReportStatus>>() {}),
								objectMapper.convertValue(resources.get("photoList"), new TypeReference<List<PrMonthlyReportPhoto>>() {}),
								objectMapper.convertValue(resources.get("prFileInfo"), new TypeReference<List<Map<String, Object>>>() {})
						);
						break;
					// 품질검측 검측요청
					case EapprovalHelper.QUALITY_ISP_DOC:
						qualityinspectionService.insertResourcesToApi(
								objectMapper.convertValue(params.get("report"), CwQualityInspection.class),
								objectMapper.convertValue(params.get("activity"), new TypeReference<List<CwQualityActivity>>() {}),
								objectMapper.convertValue(params.get("checkList"), new TypeReference<List<CwQualityCheckList>>() {}),
								objectMapper.convertValue(params.get("photo"), new TypeReference<List<CwQualityPhoto>>() {}),
								objectMapper.convertValue(params.get("cntqltyLists"), new TypeReference<List<CwCntqltyCheckList>>() {}),
								objectMapper.convertValue(params.get("qualityFileInfo"), new TypeReference<List<Map<String, Object>>>() {}),
								objectMapper.convertValue(params.get("photoFileInfo"), new TypeReference<List<Map<String, Object>>>() {})
						);
						break;
					// 품질검측 결재요청
					case EapprovalHelper.QUALITY_APP_DOC:
						qualityinspectionService.insertResourcesToApi(
								objectMapper.convertValue(params.get("report"), CwQualityInspection.class),
								objectMapper.convertValue(params.get("activity"), new TypeReference<List<CwQualityActivity>>() {}),
								objectMapper.convertValue(params.get("checkList"), new TypeReference<List<CwQualityCheckList>>() {}),
								objectMapper.convertValue(params.get("photo"), new TypeReference<List<CwQualityPhoto>>() {}),
								objectMapper.convertValue(params.get("cntqltyLists"), new TypeReference<List<CwCntqltyCheckList>>() {}),
								objectMapper.convertValue(params.get("qualityFileInfo"), new TypeReference<List<Map<String, Object>>>() {}),
								objectMapper.convertValue(params.get("photoFileInfo"), new TypeReference<List<Map<String, Object>>>() {})
						);
						break;
					// 안전점검 승인요청
					case EapprovalHelper.SAFETY_DOC:
						safetymgmtService.insertSafetyResourcesToApi(
								objectMapper.convertValue(params.get("report"), CwSafetyInspection.class),
								objectMapper.convertValue(params.get("photo"), new TypeReference<List<CwSafetyInspectionPhoto>>() {}),
								objectMapper.convertValue(params.get("inspectionlist"), new TypeReference<List<CwSafetyInspectionList>>() {}),
								objectMapper.convertValue(params.get("standardLists"), new TypeReference<List<CwStandardInspectionList>>() {}),
								objectMapper.convertValue(params.get("safetyFileInfo"), new TypeReference<List<Map<String, Object>>>() {})
						);
						break;
					// 안전점검 점검요청
					case EapprovalHelper.SAFETY_REP_DOC:
						safetymgmtService.insertSafetyResourcesToApi(
								objectMapper.convertValue(params.get("report"), CwSafetyInspection.class),
								objectMapper.convertValue(params.get("photo"), new TypeReference<List<CwSafetyInspectionPhoto>>() {}),
								objectMapper.convertValue(params.get("inspectionlist"), new TypeReference<List<CwSafetyInspectionList>>() {}),
								objectMapper.convertValue(params.get("standardLists"), new TypeReference<List<CwStandardInspectionList>>() {}),
								objectMapper.convertValue(params.get("safetyFileInfo"), new TypeReference<List<Map<String, Object>>>() {})
						);
						break;
					// 안전지적서 승인요청
					case EapprovalHelper.SADTAG_DOC:
						safetymgmtService.insertSadtagResourcesToApi(objectMapper.convertValue(params.get("report"), CwSadtag.class));
						break;
					// 주간보고 승인요청
					case EapprovalHelper.WEEKLY_DOC:
						weeklyreportService.insertResourcesToApi(
								objectMapper.convertValue(params.get("report"), PrWeeklyReport.class),
								objectMapper.convertValue(resources.get("activityList"), new TypeReference<List<PrWeeklyReportActivity>>() {}),
								objectMapper.convertValue(resources.get("progressList"), new TypeReference<List<PrWeeklyReportProgress>>() {})
						);
						break;
					// 감리일지 승인요청
					// case INSPECTION_DOC:
					// 	inspectionreportService.insertResourcesToApi(
					// 			objectMapper.convertValue(params.get("report"), CwInspectionReport.class),
					// 			objectMapper.convertValue(params.get("activity"), new TypeReference<List<CwInspectionReportActivity>>() {}),
					// 			objectMapper.convertValue(params.get("photo"), new TypeReference<List<CwInspectionReportPhoto>>() {}),
					// 			objectMapper.convertValue(params.get("inspectionFileInfo"), new TypeReference<List<Map<String, Object>>>() {})
					// 	);
					// 	break;
					// 안전일지
					case EapprovalHelper.SAFETY_DIARY_DOC:
						safetyDiaryIntegrationService.insertResourcesToApi(
								objectMapper.convertValue(params.get("diary"), Map.class),
								objectMapper.convertValue(resources.get("workList"), new TypeReference<List<Map<String, Object>>>() {}),
								objectMapper.convertValue(resources.get("patrolList"), new TypeReference<List<Map<String, Object>>>() {}),
								objectMapper.convertValue(resources.get("eduList"), new TypeReference<List<Map<String, Object>>>() {}),
								objectMapper.convertValue(resources.get("eduPersonList"), new TypeReference<List<Map<String, Object>>>() {}),
								objectMapper.convertValue(resources.get("disasterList"), new TypeReference<List<Map<String, Object>>>() {}),
								objectMapper.convertValue(resources.get("disasterPersonList"), new TypeReference<List<Map<String, Object>>>() {})
						);
					break;
					default:
						throw new GaiaBizException(ErrorType.NOT_FOUND, String.format("존재하지 않는 report 타입입니다: %s", reportType));
				}

			}
		} catch (GaiaBizException e) {
			log.error("[API통신] >>>>>>>> 결재 문서 인서트 중 오류 발생", e);
			result.put("resultCode", "01");
			result.put("resultMsg", e.getMessage());
		}

		return result;
	}


	/**
	 * API receive: GACA8201 승인/반려
	 * @param msgId
	 * @param params
	 * @return
	 */
	@Transactional
	public Map<String, Object> receiveApiOfApprovalUpdate(String msgId, Map<String, Object> params) {
		Map<String, Object> result = new HashMap<>();

		result.put("resultCode", "00");

		String usrId = (String) params.get("usrId");
		boolean isList = (boolean) params.get("isList");

		try {
			if ("GACA8201".equals(msgId)) {

				if (isList) {
					// 일괄처리
					List<ApLineUpdate> updateApLineList = objectMapper.convertValue(params.get("updateApLineList"), new TypeReference<List<ApLineUpdate>>() {});
					List<ApLineUpdate> updateApDocList = objectMapper.convertValue(params.get("updateApLineList"), new TypeReference<List<ApLineUpdate>>() {});
					log.info("API 연동 updateApLineList : {}", updateApLineList);
					log.info("API 연동 updateApDocList : {}", updateApDocList);

					// 결재라인 update
					updateApLineList.forEach(target -> {
						approvalService.updateApLineStatus(target);
					});

					// 결재문서 update
					updateApDocStatus(updateApDocList, false, usrId);
				} else {
					// 단건처리
					ApproveOneInput approveOne = objectMapper.convertValue(params.get("approveOne"), ApproveOneInput.class);
					log.info("API 연동 approveOne : {}", approveOne);
					// 공유자 변경
					updateShareList(approveOne);
					// 결재선 변경
					ApLineUpdate resultApLine = updateApLineStatus(approveOne.getApLine(), approveOne.getApStats(), usrId);
					// 결재문서 변경
					updateApDocStatus(Collections.singletonList(resultApLine), false, usrId);
				}

			}
		} catch (GaiaBizException e) {
			log.error("결재 상태 업데이트에 실패했습니다: ", e);
			result.put("resultCode", "01");
			result.put("resultMsg", e.getMessage());
		}

		return result;
	}


    /**
     * API receive: GACA8202 참조자 결재상태 변경
     * @param msgId
     * @param params
     * @return
     */
    @Transactional
    public Map<String, Object> receiveApiOfReferenceUpdate(String msgId, Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();

        result.put("resultCode", "00");

        try {
            if ("GACA8202".equals(msgId)) {
                // 참조상태 변경
				approvalService.updateReferenceDate(objectMapper.convertValue(params.get("referenceUpdate"), ApLine.class));
            }
        } catch (GaiaBizException e) {
            log.error("[API통신] >>>>>>>>>>>>>>>>>> 참조 상태 업데이트 중 오류 발생", e);
            result.put("resultCode", "01");
			result.put("resultMsg", e.getMessage());
        }

        return result;
    }

	public Map<String, Object> getApprovalDetails(MybatisInput input) {
		Map<String, Object> returnMap = new HashMap<>();
		ApprovalDetailOutput apDoc = getApDocDetail(input);
		returnMap.put("apDoc", apDoc);
		returnMap.put("apLineList", getApLineDetail(input));
		returnMap.put("myApLine", getMyApLine(input));
		returnMap.put("apShareList", getApShareDetail(apDoc.getApDocId()));
		returnMap.put("apFileList", draftService.selectApFileList(apDoc.getApDocId()));

		DcNavigation dcNavigation = null;
		if(apDoc.getNaviId() != null && !apDoc.getNaviId().isEmpty()) {
			dcNavigation = documentService.getNavigation(apDoc.getNaviId());
		}

		returnMap.put("dcNavigation", dcNavigation);

		return returnMap;
	}
}
