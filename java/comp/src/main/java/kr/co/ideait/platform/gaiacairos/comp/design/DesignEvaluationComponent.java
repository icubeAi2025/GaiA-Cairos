package kr.co.ideait.platform.gaiacairos.comp.design;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.Maps;
import kr.co.ideait.platform.gaiacairos.comp.design.service.*;
import kr.co.ideait.platform.gaiacairos.core.base.AbstractComponent;
import kr.co.ideait.platform.gaiacairos.core.exception.ErrorType;
import kr.co.ideait.platform.gaiacairos.core.exception.GaiaBizException;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.*;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.MybatisOutput;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.CommonReqVo;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.design.tool.evaluation.EvaluationForm;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.design.tool.evaluation.EvaluationMybatisParam;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.security.UserAuth;
import kr.co.ideait.platform.gaiacairos.core.type.PlatformType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class DesignEvaluationComponent extends AbstractComponent {

	@Autowired
	EvaluationForm evaluationForm;

	@Autowired
	EvaluationService evaluationService;

	/**
	 * 설계 평가 관리 목록조회(결함, 답변, 첨부파일, 설계도서)
	 * @param evaluationList
	 * @param langInfo
	 * @return
	 */
	public HashMap<String, Object> getEvaluationListData(EvaluationForm.EvaluationList evaluationList, String langInfo, String usrId) {
		HashMap<String, Object> result = new HashMap<>();

		evaluationList.setLang(langInfo);
		evaluationList.setUsrId(usrId);

		int page = evaluationList.getPage();
		int size = evaluationList.getSize();

		Pageable pageable = PageRequest.of(page - 1, size);
		Page<MybatisOutput> pageData = evaluationService.selectEvaluationList(evaluationForm.toEvaluationListInput(evaluationList), pageable);
		Long totalCount = pageData.getTotalElements();

		result.put("dsgnList", pageData.getContent());
		result.put("totalCount", totalCount);

		return result;
	}

	/**
	 * 평가 의견 상세조회 (등록된 모든 평가의견&첨부파일)
	 * @param evaluationDetail
	 * @return
	 */
	public List<MybatisOutput> getDetailEvaluationData(EvaluationForm.EvaluationDetail evaluationDetail) {
		return evaluationService.selectEvaluationDetail(evaluationForm.toEvaluationDetailInput(evaluationDetail));
	}

	/**
	 * 평가 의견 추가 - 의견, 첨부파일
	 * @param evaluationInsert
	 * @param files
	 * @return
	 */
	@Transactional
	public void registEvaluation(EvaluationForm.EvaluationInsert evaluationInsert, List<MultipartFile> files, CommonReqVo commonReqVo) {
		Map<String, Object> result = evaluationService.insertEvaluation(evaluationForm.toEvaluationInsertInput(evaluationInsert), files);

//		if ( PlatformType.CAIROS.getName().equals(platform) ) {
		if ( PlatformType.CAIROS.getName().equals(platform) && "P".equals(commonReqVo.getPjtDiv()) && "Y".equals(commonReqVo.getApiYn()) ) {
			Map<String, Object> params = new HashMap<>(result);
			params.put("userId", UserAuth.get(true).getUsrId());
			params.put("evaluationInsert", evaluationInsert);

			Map<String, Object> fileMap = new HashMap<>();
			fileMap.put("files", files);

			Map resp = invokeCairos2Pgaia("receiveByRegistEvaluation", params, fileMap);

			if (!"00".equals( MapUtils.getString(resp, "resultCode") ) ) {
				throw new GaiaBizException(ErrorType.INTERFACE, MapUtils.getString(resp, "resultMsg"));
			}
		}
	}
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public Map receiveByRegistEvaluation(String transactionId, Map params) {
		if (!"receiveByRegistEvaluation".equals(transactionId)) {
			throw new GaiaBizException(ErrorType.INTERFACE, "유효하지 않은 거래ID 입니다.");
		}

		EvaluationForm.EvaluationInsert evaluationInsert = objectMapper.convertValue(params.get("evaluationInsert"), EvaluationForm.EvaluationInsert.class);
		List<MultipartFile> files = (List<MultipartFile>)params.get("files");

		evaluationService.insertEvaluation(evaluationForm.toEvaluationInsertInput(evaluationInsert), files, params);

		Map<String, Object> result = Maps.newHashMap();
		result.put("resultCode", "00");

		return result;
	}

	/**
	 * 평가 의견 수정 - 의견, 첨부파일
	 * @param evaluationInsert
	 * @param files
	 * @return
	 */
	@Transactional
	public void modifyEvaluation(EvaluationForm.EvaluationInsert evaluationInsert, List<MultipartFile> files, CommonReqVo commonReqVo) {
		EvaluationMybatisParam.EvaluationInsertInput evaluationInsertInput = evaluationForm.toEvaluationInsertInput(evaluationInsert);

		Map<String, Object> result = evaluationService.updateEvaluation(evaluationInsertInput, files);

//		if ( PlatformType.CAIROS.getName().equals(platform) ) {
		if ( PlatformType.CAIROS.getName().equals(platform) && "P".equals(commonReqVo.getPjtDiv()) && "Y".equals(commonReqVo.getApiYn()) ) {
			Map<String, Object> params = new HashMap<>(result);
			params.put("userId", UserAuth.get(true).getUsrId());
			params.put("evaluationInsertInput", evaluationInsertInput);

			Map<String, Object> fileMap = new HashMap<>();
			fileMap.put("files", files);

			Map resp = invokeCairos2Pgaia("receiveByModifyEvaluation", params, fileMap);

			if (!"00".equals( MapUtils.getString(resp, "resultCode") ) ) {
				throw new GaiaBizException(ErrorType.INTERFACE, MapUtils.getString(resp, "resultMsg"));
			}
		}
	}
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public Map receiveByModifyEvaluation(String transactionId, Map params) {
		if (!"receiveByModifyEvaluation".equals(transactionId)) {
			throw new GaiaBizException(ErrorType.INTERFACE, "유효하지 않은 거래ID 입니다.");
		}

		EvaluationMybatisParam.EvaluationInsertInput evaluationInsertInput = objectMapper.convertValue(params.get("evaluationInsertInput"), EvaluationMybatisParam.EvaluationInsertInput.class);
		List<MultipartFile> files = (List<MultipartFile>)params.get("files");

		evaluationService.updateEvaluation(evaluationInsertInput, files, params);

		Map<String, Object> result = Maps.newHashMap();
		result.put("resultCode", "00");

		return result;
	}

	/**
	 * 평가 의견 삭제 - 의견, 첨부파일
	 * @param evaSeq
	 * @return
	 */
	@Transactional
	public void removeEvaluation(String evaSeq, CommonReqVo commonReqVo) {
		evaluationService.deleteEvaluation(evaSeq);

//		if ( PlatformType.CAIROS.getName().equals(platform) ) {
		if ( PlatformType.CAIROS.getName().equals(platform) && "P".equals(commonReqVo.getPjtDiv()) && "Y".equals(commonReqVo.getApiYn()) ) {
			Map<String, Object> params = new HashMap<>();
			params.put("userId", UserAuth.get(true).getUsrId());
			params.put("evaSeq", evaSeq);

			Map resp = invokeCairos2Pgaia("receiveByRemoveEvaluation", params);

			if (!"00".equals( MapUtils.getString(resp, "resultCode") ) ) {
				throw new GaiaBizException(ErrorType.INTERFACE, MapUtils.getString(resp, "resultMsg"));
			}
		}
	}
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public Map receiveByRemoveEvaluation(String transactionId, Map params) {
		if (!"receiveByRemoveEvaluation".equals(transactionId)) {
			throw new GaiaBizException(ErrorType.INTERFACE, "유효하지 않은 거래ID 입니다.");
		}

		evaluationService.deleteEvaluation((String)params.get("evaSeq"), (String)params.get("userId"));

		Map<String, Object> result = Maps.newHashMap();
		result.put("resultCode", "00");

		return result;
	}

	/**
	 * 평가 의견 일괄 삭제 - 의견, 첨부파일
	 * @param delEvaList
	 * @return
	 */
	@Transactional
	public void removeEvaluationList(List<DmEvaluation> delEvaList, CommonReqVo commonReqVo) {
		evaluationService.deleteAllEvaluation(delEvaList);

//		if ( PlatformType.CAIROS.getName().equals(platform) ) {
		if ( PlatformType.CAIROS.getName().equals(platform) && "P".equals(commonReqVo.getPjtDiv()) && "Y".equals(commonReqVo.getApiYn()) ) {
			Map<String, Object> params = new HashMap<>();
			params.put("userId", UserAuth.get(true).getUsrId());
			params.put("delEvaList", delEvaList);

			Map resp = invokeCairos2Pgaia("receiveByRemoveEvaluationList", params);

			if (!"00".equals( MapUtils.getString(resp, "resultCode") ) ) {
				throw new GaiaBizException(ErrorType.INTERFACE, MapUtils.getString(resp, "resultMsg"));
			}
		}
	}
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public Map receiveByRemoveEvaluationList(String transactionId, Map params) {
		if (!"receiveByRemoveEvaluationList".equals(transactionId)) {
			throw new GaiaBizException(ErrorType.INTERFACE, "유효하지 않은 거래ID 입니다.");
		}

		List<DmEvaluation> delEvaList = objectMapper.convertValue(params.get("delEvaList"), new TypeReference<List<DmEvaluation>>() {});

		evaluationService.deleteAllEvaluation(delEvaList, (String)params.get("userId"));

		Map<String, Object> result = Maps.newHashMap();
		result.put("resultCode", "00");

		return result;
	}

	/**
	 * 평가자 결과 등록 (동의 / 동의안함)
	 * @return
	 */
	@Transactional
	public void modifyApprer(String cntrctNo, String dsgnNo, String apprerCd, CommonReqVo commonReqVo) {
		evaluationService.updateApprer(cntrctNo, dsgnNo, apprerCd);

//		if ( PlatformType.CAIROS.getName().equals(platform) ) {
		if ( PlatformType.CAIROS.getName().equals(platform) && "P".equals(commonReqVo.getPjtDiv()) && "Y".equals(commonReqVo.getApiYn()) ) {
			Map<String, Object> params = new HashMap<>();
			params.put("userId", UserAuth.get(true).getUsrId());
			params.put("cntrctNo", cntrctNo);
			params.put("dsgnNo", dsgnNo);
			params.put("apprerCd", apprerCd);

			Map resp = invokeCairos2Pgaia("receiveByModifyApprer", params);

			if (!"00".equals( MapUtils.getString(resp, "resultCode") ) ) {
				throw new GaiaBizException(ErrorType.INTERFACE, MapUtils.getString(resp, "resultMsg"));
			}
		}
	}
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public Map receiveByModifyApprer(String transactionId, Map params) {
		if (!"receiveByModifyApprer".equals(transactionId)) {
			throw new GaiaBizException(ErrorType.INTERFACE, "유효하지 않은 거래ID 입니다.");
		}

		evaluationService.updateApprer((String)params.get("cntrctNo"), (String)params.get("dsgnNo"), (String)params.get("apprerCd"), (String)params.get("userId"));

		Map<String, Object> result = Maps.newHashMap();
		result.put("resultCode", "00");

		return result;
	}
}
