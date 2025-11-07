package kr.co.ideait.platform.gaiacairos.comp.design;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.Maps;
import jakarta.validation.Valid;
import kr.co.ideait.platform.gaiacairos.comp.design.helper.DesignHelper;
import kr.co.ideait.platform.gaiacairos.comp.design.service.*;
import kr.co.ideait.platform.gaiacairos.comp.project.service.InformationService;
import kr.co.ideait.platform.gaiacairos.core.base.AbstractComponent;
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

import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class DesignBackCheckComponent extends AbstractComponent {

	@Autowired
	FileService fileService;

	@Autowired
	DesignResponsesForm responsesForm;

	@Autowired
	BackCheckService backCheckService;

	@Autowired
	DesignHelper designHelper;


	/**
	 * 백체크 목록조회 (결함, 답변, 평가, 첨부파일, 설계도서)
	 * @return
	 */
	public Page<MybatisOutput> getBackCheckListData(BackCheckMybatisParam.BackCheckListInput backCheckListInput, Pageable pageable) {
		return backCheckService.selectBackCheckList(backCheckListInput, pageable);
	}

	/**
	 * 백체크 상세조회 (등록된 모든 백체크 의견&첨부파일)
	 * @return
	 */
	public List<MybatisOutput> getBackCheckData(String dsgnNo, String dsgnPhaseNo) {
		return backCheckService.selectBackCheckDetail(dsgnNo, dsgnPhaseNo);
	}

	/**
	 * 백체크 추가 - 의견, 첨부파일
	 * @param backCheckInsert
	 * @param files
	 * @return
	 */
	@Transactional
	public void registBackCheck(BackCheckForm.BackCheckInsert backCheckInsert, List<MultipartFile> files, CommonReqVo commonReqVo) {
		Map<String, Object> result = backCheckService.insertBackCheck(backCheckInsert, files);

//		if ( PlatformType.CAIROS.getName().equals(platform) ) {
		if ( PlatformType.CAIROS.getName().equals(platform) && "P".equals(commonReqVo.getPjtDiv()) && "Y".equals(commonReqVo.getApiYn()) ) {
			Map<String, Object> invokeParams = Maps.newHashMap(result);
			invokeParams.put("backCheckInsert", backCheckInsert);
			invokeParams.put("userId", UserAuth.get(true).getUsrId());

			Map<String, Object> invokeFileParams = Maps.newHashMap();
			invokeFileParams.put("files", files);

			Map response = invokeCairos2Pgaia("receiveByRegistBackCheckApi", invokeParams, invokeFileParams);

			if (!"00".equals( MapUtils.getString(response, "resultCode") ) ) {
				throw new GaiaBizException(ErrorType.INTERFACE, MapUtils.getString(response, "resultMsg"));
			}
		}
	}
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public Map receiveByRegistBackCheckApi(String transactionId, Map params) {
		if (!"receiveByRegistBackCheckApi".equals(transactionId)) {
			throw new GaiaBizException(ErrorType.INTERFACE, "유효하지 않은 거래ID 입니다.");
		}

		BackCheckForm.BackCheckInsert backCheckInsert = objectMapper.convertValue(params.get("backCheckInsert"), BackCheckForm.BackCheckInsert.class);
		List<MultipartFile> files = (List<MultipartFile>)params.get("files");

		backCheckService.insertBackCheck(backCheckInsert, files, params);

		Map<String, Object> result = Maps.newHashMap();
		result.put("resultCode", "00");

		return result;
	}

	/**
	 * 백체크 수정 - 의견, 첨부파일
	 * @param backCheckInsert
	 * @param files
	 * @return
	 */
	@Transactional
	public void modifyBackCheck(BackCheckForm.BackCheckInsert backCheckInsert, List<MultipartFile> files, CommonReqVo commonReqVo) {
		Map<String, Object> result = backCheckService.updateBackCheck(backCheckInsert, files);

//		if ( PlatformType.CAIROS.getName().equals(platform) ) {
		if ( PlatformType.CAIROS.getName().equals(platform) && "P".equals(commonReqVo.getPjtDiv()) && "Y".equals(commonReqVo.getApiYn()) ) {
			Map<String, Object> invokeParams = Maps.newHashMap(result);
			invokeParams.put("backCheckInsert", backCheckInsert);
			invokeParams.put("userId", UserAuth.get(true).getUsrId());

			Map<String, Object> invokeFileParams = Maps.newHashMap();
			invokeFileParams.put("files", files);

			Map response = invokeCairos2Pgaia("receiveByModifyBackCheck", invokeParams, invokeFileParams);

			if (!"00".equals( MapUtils.getString(response, "resultCode") ) ) {
				throw new GaiaBizException(ErrorType.INTERFACE, MapUtils.getString(response, "resultMsg"));
			}
		}
	}
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public Map receiveByModifyBackCheck(String transactionId, Map params) {
		if (!"receiveByModifyBackCheck".equals(transactionId)) {
			throw new GaiaBizException(ErrorType.INTERFACE, "유효하지 않은 거래ID 입니다.");
		}

		BackCheckForm.BackCheckInsert backCheckInsert = objectMapper.convertValue(params.get("backCheckInsert"), BackCheckForm.BackCheckInsert.class);
		List<MultipartFile> files = (List<MultipartFile>)params.get("files");

		backCheckService.updateBackCheck(backCheckInsert, files, params);

		Map<String, Object> result = Maps.newHashMap();
		result.put("resultCode", "00");

		return result;
	}

	/**
	 * 백체크 결과 등록 (미결 / 종결)
	 * @return
	 */
	@Transactional
	public void modifyBackCheckResult(String cntrctNo, String dsgnNo, String backchkCd, CommonReqVo commonReqVo) {
		backCheckService.updateBackchkCd(cntrctNo, dsgnNo, backchkCd);

//		if ( PlatformType.CAIROS.getName().equals(platform) ) {
		if ( PlatformType.CAIROS.getName().equals(platform) && "P".equals(commonReqVo.getPjtDiv()) && "Y".equals(commonReqVo.getApiYn()) ) {
			Map<String, Object> invokeParams = Maps.newHashMap();
			invokeParams.put("cntrctNo", cntrctNo);
			invokeParams.put("dsgnNo", dsgnNo);
			invokeParams.put("backchkCd", backchkCd);
			invokeParams.put("userId", UserAuth.get(true).getUsrId());

			Map response = invokeCairos2Pgaia("receiveByModifyBackCheckResult", invokeParams);

			if (!"00".equals( MapUtils.getString(response, "resultCode") ) ) {
				throw new GaiaBizException(ErrorType.INTERFACE, MapUtils.getString(response, "resultMsg"));
			}
		}
	}
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public Map receiveByModifyBackCheckResult(String transactionId, Map params) {
		if (!"receiveByModifyBackCheckResult".equals(transactionId)) {
			throw new GaiaBizException(ErrorType.INTERFACE, "유효하지 않은 거래ID 입니다.");
		}

		backCheckService.updateBackchkCd((String)params.get("cntrctNo"), (String)params.get("dsgnNo"), (String)params.get("backchkCd"), (String)params.get("userId"));

		Map<String, Object> result = Maps.newHashMap();
		result.put("resultCode", "00");

		return result;
	}

	/**
	 * 백체크 삭제 - 의견, 첨부파일
	 * @param backSeq
	 * @return
	 */
	@Transactional
	public void removeBackCheck(String backSeq, CommonReqVo commonReqVo) {
		backCheckService.deleteBackCheck(backSeq);

//		if ( PlatformType.CAIROS.getName().equals(platform) ) {
		if ( PlatformType.CAIROS.getName().equals(platform) && "P".equals(commonReqVo.getPjtDiv()) && "Y".equals(commonReqVo.getApiYn()) ) {
			Map<String, Object> invokeParams = Maps.newHashMap();
			invokeParams.put("backSeq", backSeq);
			invokeParams.put("userId", UserAuth.get(true).getUsrId());

			Map response = invokeCairos2Pgaia("receiveByRemoveBackCheck", invokeParams);

			if (!"00".equals( MapUtils.getString(response, "resultCode") ) ) {
				throw new GaiaBizException(ErrorType.INTERFACE, MapUtils.getString(response, "resultMsg"));
			}
		}
	}
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public Map receiveByRemoveBackCheck(String transactionId, Map params) {
		if (!"receiveByRemoveBackCheck".equals(transactionId)) {
			throw new GaiaBizException(ErrorType.INTERFACE, "유효하지 않은 거래ID 입니다.");
		}

		backCheckService.deleteBackCheck((String)params.get("backSeq"), (String)params.get("userId"));

		Map<String, Object> result = Maps.newHashMap();
		result.put("resultCode", "00");

		return result;
	}

	/**
	 * 백체크 의견 일괄 삭제 - 의견, 첨부파일
	 * @param delList
	 * @return
	 */
	@Transactional
	public void removeAllBackCheck(List<DmBackcheck> delList, CommonReqVo commonReqVo) {
		backCheckService.deleteAllBackCheck(delList);

//		if ( PlatformType.CAIROS.getName().equals(platform) ) {
		if ( PlatformType.CAIROS.getName().equals(platform) && "P".equals(commonReqVo.getPjtDiv()) && "Y".equals(commonReqVo.getApiYn()) ) {
			Map<String, Object> invokeParams = Maps.newHashMap();
			invokeParams.put("delList", delList);
			invokeParams.put("userId", UserAuth.get(true).getUsrId());

			Map response = invokeCairos2Pgaia("receiveByRemoveAllBackCheck", invokeParams);

			if (!"00".equals( MapUtils.getString(response, "resultCode") ) ) {
				throw new GaiaBizException(ErrorType.INTERFACE, MapUtils.getString(response, "resultMsg"));
			}
		}
	}
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public Map receiveByRemoveAllBackCheck(String transactionId, Map params) {
		if (!"receiveByRemoveAllBackCheck".equals(transactionId)) {
			throw new GaiaBizException(ErrorType.INTERFACE, "유효하지 않은 거래ID 입니다.");
		}

		List<DmBackcheck> delList = objectMapper.convertValue(params.get("delList"), new TypeReference<List<DmBackcheck>>() {});

		backCheckService.deleteAllBackCheck(delList, (String)params.get("userId"));

		Map<String, Object> result = Maps.newHashMap();
		result.put("resultCode", "00");

		return result;
	}
}
