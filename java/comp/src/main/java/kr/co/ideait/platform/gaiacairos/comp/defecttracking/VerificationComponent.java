package kr.co.ideait.platform.gaiacairos.comp.defecttracking;

import com.fasterxml.jackson.core.type.TypeReference;
import kr.co.ideait.platform.gaiacairos.comp.defecttracking.helper.DefectTrackingHelper;
import kr.co.ideait.platform.gaiacairos.comp.defecttracking.service.DefectTrackingService;
import kr.co.ideait.platform.gaiacairos.comp.defecttracking.service.VerificationService;
import kr.co.ideait.platform.gaiacairos.core.base.AbstractComponent;
import kr.co.ideait.platform.gaiacairos.core.exception.ErrorType;
import kr.co.ideait.platform.gaiacairos.core.exception.GaiaBizException;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.DtAttachments;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.DtDeficiency;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.DtDeficiencyConfirm;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.DtDeficiencyConfirmHistory;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.MybatisInput;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.MybatisOutput;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.defecttracking.tool.verification.VerificationMybatisParam.ConfirmHistoryInput;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.defecttracking.tool.verification.VerificationMybatisParam.DfccyConfirmDetailInput;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.defecttracking.tool.verification.VerificationMybatisParam.DfccyConfirmInsertInput;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.defecttracking.tool.verification.VerificationMybatisParam.DfccyConfirmListInput;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.security.UserAuth;
import kr.co.ideait.platform.gaiacairos.core.type.FileUploadType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class VerificationComponent extends AbstractComponent {

    @Autowired
    DefectTrackingService defectTrackingService;

    @Autowired
    VerificationService verificationService;

    @Autowired
    DefectTrackingHelper defectTrackingHelper;


	/**
	 * 결함 확인 관리 목록조회 (결함, 답변, 첨부파일)
	 * @param dfccyConfirmListInput
	 * @param pageable
	 * @return
	 */
    public Page<MybatisOutput> selectDfccyConfirmList(DfccyConfirmListInput dfccyConfirmListInput, Pageable pageable) {
        dfccyConfirmListInput.setPageable(pageable);
        List<MybatisOutput> output = verificationService.selectDfccyConfirmList(dfccyConfirmListInput);
        Long totalCount = verificationService.selectDfccyConfirmCount(dfccyConfirmListInput);
        return new PageImpl<>(output, dfccyConfirmListInput.getPageable(), totalCount);
    }


	/**
	 * 검색 셀렉트 옵션 - 결함분류 조회
	 * @return
	 */
    public List<MybatisOutput> selectDfccyCd() {
        return verificationService.selectDfccyCd();
    }


	/**
	 * 결함 확인 상세조회 (등록된 모든 확인의견&첨부파일)
	 * @param dfccyConfirmDetailInput
	 * @return
	 */
    public List<MybatisOutput> selectDfccyConfirmDetail(DfccyConfirmDetailInput dfccyConfirmDetailInput) {
        return verificationService.selectDfccyConfirmDetail(dfccyConfirmDetailInput);
    }


	/**
	 * QA / 관리관 확인 이력 조회
	 * @param confirmHistoryInput
	 * @return
	 */
    public List<MybatisOutput> selectConfirmHistoryList(ConfirmHistoryInput confirmHistoryInput) {
        return verificationService.selectConfirmHistoryList(confirmHistoryInput);
    }


	/**
	 * 확인 의견 추가 - 의견, 첨부파일
	 * @param dtDeficiencyConfirm
	 * @param files
	 * @param reqVoMap
	 */
    @Transactional
    public void insertDfccyConfirm(DtDeficiencyConfirm dtDeficiencyConfirm, List<MultipartFile> files, Map<String, Object> reqVoMap) {
	    // 1. 파일저장
        Integer fileNo = null;
	    if(files != null && !files.isEmpty()) {
            String uploadPath = getUploadPathByWorkType(FileUploadType.DEFICIENCY, dtDeficiencyConfirm.getCntrctNo());
            fileNo = defectTrackingHelper.convertToDtAttachments(files, uploadPath, null, UserAuth.get(true).getUsrId());
	    }

	    // 2. 확인 의견 저장
	    Short maxRgstOrder = verificationService.getConfirmMaxRgstOrder(dtDeficiencyConfirm.getDfccyNo());
		dtDeficiencyConfirm.setDfccySeq(UUID.randomUUID().toString());
		dtDeficiencyConfirm.setRgstOrdr(++maxRgstOrder);
		dtDeficiencyConfirm.setDltYn("N");
		dtDeficiencyConfirm.setAtchFileNo(fileNo);
		dtDeficiencyConfirm.setRgstrId(UserAuth.get(true).getUsrId());
		dtDeficiencyConfirm.setChgId(UserAuth.get(true).getUsrId());
        DtDeficiencyConfirm savedConfirm = verificationService.saveDtDeficiencyConfirm(dtDeficiencyConfirm);

		if(!"P".equals(reqVoMap.get("pjtDiv")) || !"Y".equals(reqVoMap.get("apiYn"))) return;

		Map<String, Object> sendParams = new HashMap<>();
		sendParams.put("confirm", savedConfirm);
		sendParams.put("delFileList", null);
		sendParams.put("pjtNo", UserAuth.get(true).getPjtNo());

		log.info("결함추적 > 확인관리 > 의견 추가 API 통신 - transactionId: GACA7300, sendParams: {},", sendParams);

		sendToApi("GACA7300", sendParams, files);

    }


	/**
	 * 확인 의견 수정 - 의견, 첨부파일
	 * @param dfccyConfirmInsertInput
	 * @param files
	 * @param reqVoMap
	 */
    @Transactional
    public void updateDfccyConfirm(DfccyConfirmInsertInput dfccyConfirmInsertInput, List<MultipartFile> files, Map<String, Object> reqVoMap) {
		DtDeficiencyConfirm dtDeficiencyConfirm =  dfccyConfirmInsertInput.getDtDeficiencyConfirm();
		List<DtAttachments> delFileList = dfccyConfirmInsertInput.getDelFileList();

		// 1. 확인 의견 수정
	    DtDeficiencyConfirm findConfirm = verificationService.selectDfccyConfirm(dtDeficiencyConfirm.getDfccySeq());
		if (findConfirm == null) {
            throw new GaiaBizException(ErrorType.NOT_FOUND, "수정할 확인 의견을 찾을 수 없습니다.");
        }
		findConfirm.setCnfrmOpnin(dtDeficiencyConfirm.getCnfrmOpnin());

	    // 2. 새 파일저장
	    Integer existingFileNo = findConfirm.getAtchFileNo();
		Integer fileNo = null;
	    if(files != null && !files.isEmpty()) {
            String uploadPath = getUploadPathByWorkType(FileUploadType.DEFICIENCY, dtDeficiencyConfirm.getCntrctNo());
            fileNo = defectTrackingHelper.convertToDtAttachments(files, uploadPath, existingFileNo, UserAuth.get(true).getUsrId());
            findConfirm.setAtchFileNo(fileNo);
	    }

        findConfirm = verificationService.saveDtDeficiencyConfirm(findConfirm);

	    // 3. 기존 파일삭제
	    if(delFileList != null && !delFileList.isEmpty()) {
            defectTrackingHelper.deleteAttachmentList(delFileList, UserAuth.get(true).getUsrId());
	    }

		if(!"P".equals(reqVoMap.get("pjtDiv")) || !"Y".equals(reqVoMap.get("apiYn"))) return;

		Map<String, Object> sendParams = new HashMap<>();
		sendParams.put("confirm", findConfirm);
		sendParams.put("delFileList", delFileList);
		sendParams.put("pjtNo", UserAuth.get(true).getPjtNo());

		log.info("결함추적 > 확인관리 > 의견 수정 API 통신 - transactionId: GACA7300, sendParams: {},", sendParams);

		sendToApi("GACA7300", sendParams, files);

    }


	/**
	 * 확인 의견 삭제 - 의견, 첨부파일
	 * @param dfccySeq
	 * @param toApi
	 * @param dltId
	 * @param pjtNo
	 * @param reqVoMap
	 */
    @Transactional
	public void deleteVerification(String dfccySeq, boolean toApi, String dltId, String pjtNo, Map<String, Object> reqVoMap) {
		DtDeficiencyConfirm findConfirm = verificationService.selectDfccyConfirm(dfccySeq);
		if(findConfirm != null) {
			if(findConfirm.getAtchFileNo() != null) {
                defectTrackingHelper.deleteAttachmentList(defectTrackingHelper.getFileList(findConfirm.getAtchFileNo()), dltId);
			}
            verificationService.deleteDfccyConfirm(findConfirm, dltId);
		}

		if(!"P".equals(reqVoMap.get("pjtDiv")) || !toApi || !"Y".equals(reqVoMap.get("apiYn"))) return;

		Map<String, Object> sendParams = new HashMap<>();
		sendParams.put("dfccySeq", dfccySeq);
		sendParams.put("dltId", dltId);
		sendParams.put("pjtNo", pjtNo);
		sendParams.put("reqVoMap", reqVoMap);

		log.info("결함추적 > 확인관리 > 의견 삭제 API 통신 - transactionId: GACA7302, sendParams: {},", sendParams);

		sendToApi("GACA7302", sendParams);

	}


    /**
	 * 확인 의견 전체 삭제 - 의견, 첨부파일
	 * @param delDfccyList
	 */
	@Transactional
	public void deleteAllVerification(List<DtDeficiencyConfirm> delDfccyList, boolean toApi, String dltId, String pjtNo, Map<String, Object> reqVoMap) {
		delDfccyList.forEach(dfccy -> {
			List<DtDeficiencyConfirm> findConfirm = verificationService.selectDfccyConfirmByUsrId(dfccy.getDfccyNo(), dltId);
			if(findConfirm != null && !findConfirm.isEmpty()) {
				findConfirm.forEach(confirm -> {
					if(confirm.getAtchFileNo() != null) {
						MybatisInput input = MybatisInput.of()
								.add("fileNo", confirm.getAtchFileNo())
								.add("usrId", dltId);
						defectTrackingHelper.deleteAttachmentList(input);
					}
					verificationService.deleteDfccyConfirm(confirm, dltId);
				});
			}
		});

		if(!"P".equals(reqVoMap.get("pjtDiv")) || !toApi || !"Y".equals(reqVoMap.get("apiYn"))) return;

		Map<String, Object> sendParams = new HashMap<>();
		sendParams.put("delDfccyList", delDfccyList);
		sendParams.put("dltId", dltId);
		sendParams.put("pjtNo", pjtNo);
		sendParams.put("reqVoMap", reqVoMap);

		log.info("결함추적 > 확인관리 > 전체 의견 삭제 API 통신 - transactionId: GACA7303, sendParams: {},", sendParams);

		sendToApi("GACA7303", sendParams);
	}


	/**
	 * 확인 결과 일괄 종료(관리관/QA)
	 * @param finishList
	 * @param reqVoMap
	 */
    @Transactional
    public void finishList(List<DtDeficiencyConfirmHistory> finishList, Map<String, Object> reqVoMap) {
        finishList.forEach(item -> {
			insertDfccyConfirmHistory(item, reqVoMap);
		});
    }


    /**
	 * 확인 결과 이력 추가 (QA확인/관리관확인 - 미결, 보류, 종료)
	 * @param dtDeficiencyConfirmHistory
	 * @param reqVoMap
	 */
	@Transactional
	public void insertDfccyConfirmHistory(DtDeficiencyConfirmHistory dtDeficiencyConfirmHistory, Map<String, Object> reqVoMap) {
		Short maxRgstOrder = verificationService.getConfirmHistoryMaxRgstOrder(dtDeficiencyConfirmHistory.getDfccyNo(), dtDeficiencyConfirmHistory.getCnfrmDiv());

		dtDeficiencyConfirmHistory.setHistorySeq(UUID.randomUUID().toString());
		dtDeficiencyConfirmHistory.setRgstOrdr(++maxRgstOrder);
		dtDeficiencyConfirmHistory.setDltYn("N");
		dtDeficiencyConfirmHistory.setRgstrId(UserAuth.get(true).getUsrId());
		dtDeficiencyConfirmHistory.setChgId(UserAuth.get(true).getUsrId());
        verificationService.saveDtDeficiencyConfirmHistory(dtDeficiencyConfirmHistory);

		// 결함 결과 업데이트
		DtDeficiency dtDeficiency =  defectTrackingService.getDeficiency(dtDeficiencyConfirmHistory.getDfccyNo());
		if(dtDeficiency != null) {
			if(dtDeficiencyConfirmHistory.getCnfrmDiv().equals("01")) {
				dtDeficiency.setQaCd(dtDeficiencyConfirmHistory.getCnfrmCd());
				dtDeficiency.setQaRgstDt(LocalDateTime.now());
				dtDeficiency.setQaRgstrId(UserAuth.get(true).getUsrId());
			} else {
				dtDeficiency.setSpvsCd(dtDeficiencyConfirmHistory.getCnfrmCd());
				dtDeficiency.setSpvsRgstDt(LocalDateTime.now());
				dtDeficiency.setSpvsRgstrId(UserAuth.get(true).getUsrId());
			}
			dtDeficiency.setChgId(UserAuth.get(true).getUsrId());
            dtDeficiency = defectTrackingService.saveDeficiency(dtDeficiency);
		}

		if(!"P".equals(reqVoMap.get("pjtDiv")) || !"Y".equals(reqVoMap.get("apiYn"))) return;

		Map<String, Object> sendParams = new HashMap<>();
		sendParams.put("confirmHistory", dtDeficiencyConfirmHistory);
		sendParams.put("dtDeficiency", dtDeficiency);

		log.info("결함추적 > 확인관리 > 확인 결과 API 통신 - transactionId: GACA7301, sendParams: {},", sendParams);

		sendToApi("GACA7301", sendParams);

	}


	/**
	 * API send
	 * @param transactionId
	 * @param sendParams
	 */
	private void sendToApi(String transactionId, Map<String, Object> sendParams) {
		Map<String, Object> response = new HashMap<>();
		if("cairos".equals(platform)) {
			response = invokeCairos2Pgaia(transactionId, sendParams);
		} else if("pgaia".equals(platform)) {
			response = invokePgaia2Cairos(transactionId, sendParams);
		}
		if (!"00".equals( org.apache.commons.collections4.MapUtils.getString(response, "resultCode") ) ) {
			throw new GaiaBizException(ErrorType.INTERFACE, org.apache.commons.collections4.MapUtils.getString(response, "resultMsg"));
		}
    }

	private void sendToApi(String transactionId, Map<String, Object> sendParams, List<MultipartFile> files) {
		Map<String, Object> response = new HashMap<>();
		if("cairos".equals(platform)) {
			response = invokeCairos2Pgaia(transactionId, sendParams, files);
		} else if("pgaia".equals(platform)) {
			response = invokePgaia2Cairos(transactionId, sendParams, files);
		}
		if (!"00".equals( org.apache.commons.collections4.MapUtils.getString(response, "resultCode") ) ) {
			throw new GaiaBizException(ErrorType.INTERFACE, org.apache.commons.collections4.MapUtils.getString(response, "resultMsg"));
		}
    }


    /**
	 * API receive
	 * @param msgId
	 * @param params
	 * @return
	 */
	@Transactional
	public Map<String, Object> receiveApiOfVerification(String msgId, Map<String, Object> params){
		Map<String, Object> result = new HashMap<>();
		result.put("resultCode", "00");

		try {
			log.info("결함추적 > 확인관리 > receive msgId: {}, 연동 params : {}", msgId, params);

			// 확인 의견 추가
			if("GACA7300".equals(msgId)) {

				// 확인의견
				DtDeficiencyConfirm confirm = verificationService.saveDtDeficiencyConfirm(objectMapper.convertValue(params.get("confirm"), DtDeficiencyConfirm.class));

				// 확인 첨부파일
				List<MultipartFile> files = (List<MultipartFile>)params.get("files");
				if(files != null && !files.isEmpty()) {
                    String uploadPath = getUploadPathByWorkType(FileUploadType.DEFICIENCY, confirm.getCntrctNo());
                    defectTrackingHelper.convertToDtAttachments(files, uploadPath, confirm.getAtchFileNo(), confirm.getRgstrId());
				}

				// 첨부파일 삭제
				List<DtAttachments> delFileList = objectMapper.convertValue(params.get("delFileList"), new TypeReference<List<DtAttachments>>() {});
				if(delFileList != null && !delFileList.isEmpty()) {
                    defectTrackingHelper.deleteAttachmentList(delFileList, confirm.getRgstrId());
				}

			}
			// 확인 이력 추가
			if("GACA7301".equals(msgId)) {
				verificationService.saveDtDeficiencyConfirmHistory(objectMapper.convertValue(params.get("confirmHistory"), DtDeficiencyConfirmHistory.class));
				defectTrackingService.saveDeficiency(objectMapper.convertValue(params.get("dtDeficiency"), DtDeficiency.class));
			}
			// 확인 의견 삭제
			if("GACA7302".equals(msgId)) {
				deleteVerification(
						(String)params.get("dfccySeq"),
						false,
						(String)params.get("dltId"),
						(String)params.get("pjtNo"),
						(Map<String, Object>) params.get("reqVoMap")
				);
			}
			// 확인 의견 일괄 삭제
			if("GACA7303".equals(msgId)) {
				deleteAllVerification(
						objectMapper.convertValue(params.get("delDfccyList"), new TypeReference<List<DtDeficiencyConfirm>>() {}),
						false,
						(String)params.get("dltId"),
						(String)params.get("pjtNo"),
						(Map<String, Object>) params.get("reqVoMap")
				);
			}

		} catch (GaiaBizException e) {
			log.error("[결함 추적 - 확인관리] API receive 중 오류 발생: ", e);
			result.put("resultCode", "01");
			result.put("resultMsg", e.getMessage());
		}

		return result;
	}


}
