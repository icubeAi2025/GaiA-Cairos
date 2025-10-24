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
public class DesignSettingComponent extends AbstractComponent {

	@Autowired
	FileService fileService;

	@Autowired
	DesignResponsesForm responsesForm;

	@Autowired
	DesignSettingForm designSettingForm;

	@Autowired
	DesignSettingService settingService;

	@Autowired
	DesignHelper designHelper;


	public List getDesignSettingListData(String cntrctNo) {
		return settingService.selectDesignPhaseList(cntrctNo);
	}

	public List getDetailSettingData(DesignSettingForm.DesignPhaseDetail designPhaseDetail) {
		DesignSettingMybatisParam.DesignPhaseDetailInput designPhaseDetailInput = designSettingForm.toDesignPhaseDetailInput(designPhaseDetail);
		return settingService.selectDesignPhase(designPhaseDetailInput);
	}

	@Transactional
	public void registDesignPhase(DmDesignPhase dmDesignPhase, List<DmDesignSchedule> scheduleArr, CommonReqVo commonReqVo) {
		settingService.insertDesignPhase(dmDesignPhase, scheduleArr);

//		if ( PlatformType.CAIROS.getName().equals(platform) ) {
		if ( PlatformType.CAIROS.getName().equals(platform) && "P".equals(commonReqVo.getPjtDiv()) && "Y".equals(commonReqVo.getApiYn()) ) {
			Map<String, Object> invokeParams = new HashMap<>();
			invokeParams.put("dmDesignPhase", dmDesignPhase);
			invokeParams.put("scheduleArr", scheduleArr);
			invokeParams.put("userId", UserAuth.get(true).getUsrId());

			Map response = invokeCairos2Pgaia("receiveByRegistDesignPhase", invokeParams);

			if (!"00".equals( MapUtils.getString(response, "resultCode") ) ) {
				throw new GaiaBizException(ErrorType.INTERFACE, MapUtils.getString(response, "resultMsg"));
			}
		}
	}
	//설계관리 > 설계 검토 툴 > 설계 검토 단계 설정 > 추가
	@Transactional
	public Map receiveByRegistDesignPhase(String transactionId, Map params) {
		if (!"receiveByRegistDesignPhase".equals(transactionId)) {
			throw new GaiaBizException(ErrorType.INTERFACE, "유효하지 않은 거래ID 입니다.");
		}

		DmDesignPhase dmDesignPhase = objectMapper.convertValue(params.get("dmDesignPhase"),DmDesignPhase.class);
		List<DmDesignSchedule> scheduleArr = objectMapper.convertValue(params.get("scheduleArr"), new TypeReference<List<DmDesignSchedule>>() {});

		settingService.insertDesignPhase(dmDesignPhase, scheduleArr, (String)params.get("userId"));

		Map result = new HashMap();
		result.put("resultCode", "00");

		return result;
	}

	@Transactional
	public void modifyDesignPhase(DmDesignPhase dmDesignPhase, List<DmDesignSchedule> scheduleArr, CommonReqVo commonReqVo) {
		settingService.updateDesignPhase(dmDesignPhase, scheduleArr);

//		if ( PlatformType.CAIROS.getName().equals(platform) ) {
		if ( PlatformType.CAIROS.getName().equals(platform) && "P".equals(commonReqVo.getPjtDiv()) && "Y".equals(commonReqVo.getApiYn()) ) {
			Map<String, Object> invokeParams = new HashMap<>();
			invokeParams.put("userId", UserAuth.get(true).getUsrId());
			invokeParams.put("dmDesignPhase", dmDesignPhase);
			invokeParams.put("scheduleArr", scheduleArr);
			invokeParams.put("userId", UserAuth.get(true).getUsrId());

			Map response = invokeCairos2Pgaia("receiveByModifyDesignPhase", invokeParams);

			if (!"00".equals( MapUtils.getString(response, "resultCode") ) ) {
				throw new GaiaBizException(ErrorType.INTERFACE, MapUtils.getString(response, "resultMsg"));
			}
		}
	}
	//설계관리 > 설계 검토 툴 > 설계 검토 단계 설정 > 수정
	@Transactional
	public Map receiveByModifyDesignPhase(String transactionId, Map params) {
		if (!"receiveByModifyDesignPhase".equals(transactionId)) {
			throw new GaiaBizException(ErrorType.INTERFACE, "유효하지 않은 거래ID 입니다.");
		}

		DmDesignPhase dmDesignPhase = objectMapper.convertValue(params.get("dmDesignPhase"),DmDesignPhase.class);
		List<DmDesignSchedule> scheduleArr = objectMapper.convertValue(params.get("scheduleArr"), new TypeReference<List<DmDesignSchedule>>() {});

		settingService.updateDesignPhase(dmDesignPhase, scheduleArr, (String)params.get("userId"));

		Map result = new HashMap();
		result.put("resultCode", "00");

		return result;
	}


	/**
	 * 설계검토단계 삭제(결함 단계 및 일정) -> 삭제 이후 순서 재정렬
	 * @param delPhaseList
	 * @param cntrctNo
	 * @param commonReqVo
	 * @return
	 */
	@Transactional
	public void removeDesignPhase(List<String> delPhaseList, String cntrctNo, CommonReqVo commonReqVo) {
		settingService.deleteDesignPhase(delPhaseList, cntrctNo, commonReqVo.getUserId());

//		if ( PlatformType.CAIROS.getName().equals(platform) ) {
		if ( PlatformType.CAIROS.getName().equals(platform) && "P".equals(commonReqVo.getPjtDiv()) && "Y".equals(commonReqVo.getApiYn()) ) {
			Map<String, Object> params = new HashMap<>();
			params.put("userId", UserAuth.get(true).getUsrId());
			params.put("delPhaseList", delPhaseList);
			params.put("cntrctNo", cntrctNo);

			Map response = invokeCairos2Pgaia("receiveByRemoveDesignPhase", params);

			if (!"00".equals( MapUtils.getString(response, "resultCode") ) ) {
				throw new GaiaBizException(ErrorType.INTERFACE, MapUtils.getString(response, "resultMsg"));
			}
		}
	}
	//설계관리 > 설계 검토 툴 > 설계 검토 단계 설정 > 삭제
	@Transactional
	public Map receiveByRemoveDesignPhase(String transactionId, Map params) {
		if (!"receiveByRemoveDesignPhase".equals(transactionId)) {
			throw new GaiaBizException(ErrorType.INTERFACE, "유효하지 않은 거래ID 입니다.");
		}

		List<String> delPhaseList = objectMapper.convertValue(params.get("delPhaseList"), new TypeReference<List<String>>() {});
		String cntrctNo = objectMapper.convertValue(params.get("cntrctNo"),String.class);

		settingService.deleteDesignPhase(delPhaseList, cntrctNo, (String)params.get("userId"));

		Map result = new HashMap();
		result.put("resultCode", "00");

		return result;
	}


	@Transactional
	public void modifyDesignPhaseDisplayOrder(List<DesignSettingForm.DesignDisplayOrderMove> designDisplayOrderMove, CommonReqVo commonReqVo) {
		List<DesignSettingMybatisParam.DesignDisplayOrderMoveInput> designDisplayOrderMoveInput = designSettingForm.toDesignDisplayOrderMoveInput(designDisplayOrderMove);
		settingService.updateDesignDisplayOrder(designDisplayOrderMoveInput);

//		if ( PlatformType.CAIROS.getName().equals(platform) ) {
		if ( PlatformType.CAIROS.getName().equals(platform) && "P".equals(commonReqVo.getPjtDiv()) && "Y".equals(commonReqVo.getApiYn()) ) {
			Map<String, Object> params = new HashMap<>();
			params.put("userId", UserAuth.get(true).getUsrId());
			params.put("designDisplayOrderMoveInput", designDisplayOrderMoveInput);

			Map response = invokeCairos2Pgaia("receiveByModifyDesignPhaseDisplayOrder", params);

			if (!"00".equals( MapUtils.getString(response, "resultCode") ) ) {
				throw new GaiaBizException(ErrorType.INTERFACE, MapUtils.getString(response, "resultMsg"));
			}
		}
	}
	@Transactional
	public Map receiveByModifyDesignPhaseDisplayOrder(String transactionId, Map params) {
		if (!"receiveByModifyDesignPhaseDisplayOrder".equals(transactionId)) {
			throw new GaiaBizException(ErrorType.INTERFACE, "유효하지 않은 거래ID 입니다.");
		}

		List<DesignSettingMybatisParam.DesignDisplayOrderMoveInput> designDisplayOrderMoveInput = objectMapper.convertValue(params.get("designDisplayOrderMoveInput"), new TypeReference<List<DesignSettingMybatisParam.DesignDisplayOrderMoveInput>>() {});

		settingService.updateDesignDisplayOrder(designDisplayOrderMoveInput, (String)params.get("userId"));

		Map result = new HashMap();
		result.put("resultCode", "00");

		return result;
	}
}
