package kr.co.ideait.platform.gaiacairos.comp.system;


import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.Maps;
import kr.co.ideait.platform.gaiacairos.comp.system.service.CommonCodeService;
import kr.co.ideait.platform.gaiacairos.core.base.AbstractComponent;
import kr.co.ideait.platform.gaiacairos.core.exception.ErrorType;
import kr.co.ideait.platform.gaiacairos.core.exception.GaiaBizException;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.SmComCode;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.SmComCodeGroup;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.SmEcoMaterial;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.CommonReqVo;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.security.UserAuth;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.commoncode.CommonCodeDto;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.commoncode.CommonCodeMybatisParam;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.ecomaterial.EcoMaterialForm;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.ecomaterial.EcoMaterialMybatisParam;
import kr.co.ideait.platform.gaiacairos.core.type.PlatformType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class CommonCodeComponent extends AbstractComponent {

	@Autowired
	CommonCodeService commonCodeService;

	// 그룹코드
	// -----------------------------------------------------------------------------------------------------

	public List<CommonCodeDto.SmComCodeGroup> getCommonCodeGroupList() {
		return commonCodeService.getCommonCodeGroupList();
	}

	public SmComCodeGroup getCommonCodeGroup(int commonCodeGroupNo) {
		return commonCodeService.getCommonCodeGroup(commonCodeGroupNo);
	}

	public boolean existCommonCodeGroup(Integer upCmnGrpNo, String cmnCd) {
		return commonCodeService.existCommonCodeGroup(upCmnGrpNo, cmnCd);
	}

	@Transactional
	public SmComCodeGroup createCommonCodeGroup(SmComCodeGroup smComCodeGroup, CommonReqVo commonReqVo) {
		UserAuth userAuth = UserAuth.get(true);
		if(userAuth == null){
			throw new GaiaBizException(ErrorType.UNAUTHORIZED,"인증이 만료되었습니다.");
		}
		String userId = userAuth.getUsrId();
		SmComCodeGroup returnData = commonCodeService.createCommonCodeGroup(smComCodeGroup,userId);

		// API 통신
		if ("Y".equals(commonReqVo.getApiYn())) {
			if ( PlatformType.CAIROS.getName().equals(platform) ) {
				Map<String, Object> invokeParams = Maps.newHashMap();
				invokeParams.put("smComCodeGroup", returnData);
				invokeParams.put("userId",userId);

				Map response = invokeCairos2Pgaia("CAGA9007", invokeParams);

				if (!"00".equals( MapUtils.getString(response, "resultCode") ) ) {
					throw new GaiaBizException(ErrorType.INTERFACE, MapUtils.getString(response, "resultMsg"));
				}
			}
		}

		return returnData;
	}

	@Transactional
	public CommonCodeDto.SmComCodeGroup updateCommonCodeGroup(CommonCodeDto.SmComCodeGroup smComCodeGroup, CommonReqVo commonReqVo) {
		UserAuth userAuth = UserAuth.get(true);
		if(userAuth == null){
			throw new GaiaBizException(ErrorType.UNAUTHORIZED,"인증이 만료되었습니다.");
		}
		String userId = userAuth.getUsrId();
		CommonCodeDto.SmComCodeGroup returnData = commonCodeService.updateCommonCodeGroup(smComCodeGroup,userId);

		// API 통신
		if ("Y".equals(commonReqVo.getApiYn())) {
			if ( PlatformType.CAIROS.getName().equals(platform) ) {
				Map<String, Object> invokeParams = Maps.newHashMap();
				invokeParams.put("smComCodeGroup", returnData);
				invokeParams.put("userId", userId);

				Map response = invokeCairos2Pgaia("CAGA9008", invokeParams);

				if (!"00".equals( MapUtils.getString(response, "resultCode") ) ) {
					throw new GaiaBizException(ErrorType.INTERFACE, MapUtils.getString(response, "resultMsg"));
				}
			}
		}

		return returnData;
	}

	@Transactional
	public Map deleteCommonCodeGroup(List<String> groupCodeCdList, CommonReqVo commonReqVo) {
		UserAuth userAuth = UserAuth.get(true);
		if(userAuth == null){
			throw new GaiaBizException(ErrorType.UNAUTHORIZED,"인증이 만료되었습니다.");
		}
		String userId = userAuth.getUsrId();

		Map response = Maps.newHashMap();
		response.put("returnCode", "01");

		commonCodeService.deleteCommonCodeGroup(groupCodeCdList,userId);

		// API 통신
		if ("Y".equals(commonReqVo.getApiYn())) {
			if ( PlatformType.CAIROS.getName().equals(platform) ) {
				Map<String, Object> invokeParams = Maps.newHashMap();
				invokeParams.put("groupCodeNoList", groupCodeCdList);
				invokeParams.put("userId",userId);

				response = invokeCairos2Pgaia("CAGA9009", invokeParams);

				if (!"00".equals( MapUtils.getString(response, "resultCode") ) ) {
					throw new GaiaBizException(ErrorType.INTERFACE, MapUtils.getString(response, "resultMsg"));
				}
			}
		}

		return response;
	}

	@Transactional
	public boolean upGroup(CommonCodeDto.SmComCodeGroup smComCodeGroup, CommonReqVo commonReqVo) {
		UserAuth userAuth = UserAuth.get(true);
		if(userAuth == null){
			throw new GaiaBizException(ErrorType.UNAUTHORIZED,"인증이 만료되었습니다.");
		}
		String userId = userAuth.getUsrId();

		boolean result = commonCodeService.upGroup(smComCodeGroup,userId);

		// API 통신
		if ("Y".equals(commonReqVo.getApiYn())) {
			if ( result && PlatformType.CAIROS.getName().equals(platform) ) {
				Map<String, Object> invokeParams = Maps.newHashMap();
				invokeParams.put("smComCodeGroup", smComCodeGroup);
				invokeParams.put("userId", userId);

				Map response = invokeCairos2Pgaia("CAGA9010", invokeParams);

				if (!"00".equals( MapUtils.getString(response, "resultCode") ) ) {
					throw new GaiaBizException(ErrorType.INTERFACE, MapUtils.getString(response, "resultMsg"));
				}
			}
		}

		return true;
	}

	@Transactional
	public boolean downGroup(CommonCodeDto.SmComCodeGroup smComCodeGroup, CommonReqVo commonReqVo) {
		UserAuth userAuth = UserAuth.get(true);
		if(userAuth == null){
			throw new GaiaBizException(ErrorType.UNAUTHORIZED,"인증이 만료되었습니다.");
		}
		String userId = userAuth.getUsrId();

		boolean result = commonCodeService.downGroup(smComCodeGroup,userId);

		// API 통신
		if (result && "Y".equals(commonReqVo.getApiYn())) {
			if ( PlatformType.CAIROS.getName().equals(platform) ) {
				Map<String, Object> invokeParams = Maps.newHashMap();
				invokeParams.put("smComCodeGroup", smComCodeGroup);
				invokeParams.put("userId", userId);

				Map response = invokeCairos2Pgaia("CAGA9011", invokeParams);

				if (!"00".equals( MapUtils.getString(response, "resultCode") ) ) {
					throw new GaiaBizException(ErrorType.INTERFACE, MapUtils.getString(response, "resultMsg"));
				}
			}
		}

		return true;
	}

	// 코드-----------------------------------------------------------------------------------------------------

	@SuppressWarnings("null")
	public Page<CommonCodeDto.SmComCode> getCommonCodeListGrid(CommonCodeMybatisParam.CommonCodeListInput commonCodeListInput) {
		return commonCodeService.getCommonCodeListGrid(commonCodeListInput);
	}

	public List<Map<String, ?>> getCommonCodeList(CommonCodeMybatisParam.CommonCodeListInput commonCodeListInput) {
		return commonCodeService.getCommonCodeList(commonCodeListInput);
	}

	public List<SmComCode> getCommonCodeListByGroupCode(String groupCode) {
		return commonCodeService.getCommonCodeListByGroupCode(groupCode);
	}

	public CommonCodeDto.SmComCode getCommonCodeLoadData(String cmnCdNo) {
		return commonCodeService.getCommonCodeLoadData(cmnCdNo);
	}

	public SmComCode getCommonCode(String cmnCdNo, String cmnCd) {
		return commonCodeService.getCommonCode(cmnCdNo, cmnCd);
	}

	public boolean existCommonCode(String groupCodeCd, String code) {
		return commonCodeService.existCommonCode(groupCodeCd, code);
	}

	@Transactional
	public CommonCodeDto.SmComCode createCommonCode(CommonCodeDto.SmComCode smComCode, CommonReqVo commonReqVo) {
		UserAuth userAuth = UserAuth.get(true);
		if(userAuth == null){
			throw new GaiaBizException(ErrorType.UNAUTHORIZED,"인증이 만료되었습니다.");
		}
		String userId = userAuth.getUsrId();

		CommonCodeDto.SmComCode returnData = commonCodeService.createCommonCode(smComCode,userId);
		if(returnData == null){
			log.error("서비스 수행 실패");
		}
		else {
			// API 통신
			if ("Y".equals(commonReqVo.getApiYn())) {
				if (PlatformType.CAIROS.getName().equals(platform)) {
					Map<String, Object> invokeParams = Maps.newHashMap();
					invokeParams.put("smComCode", returnData);
					invokeParams.put("userId", userId);

					Map response = invokeCairos2Pgaia("CAGA9012", invokeParams);

					if (!"00".equals(MapUtils.getString(response, "resultCode"))) {
						throw new GaiaBizException(ErrorType.INTERFACE, MapUtils.getString(response, "resultMsg"));
					}
				}
			}
		}
		return returnData;
	}

	@Transactional
	public CommonCodeDto.SmComCode updateCommonCode(CommonCodeDto.SmComCode smComCode, CommonReqVo commonReqVo) {
		UserAuth userAuth = UserAuth.get(true);
		if(userAuth == null){
			throw new GaiaBizException(ErrorType.UNAUTHORIZED,"인증이 만료되었습니다.");
		}
		String userId = userAuth.getUsrId();


		CommonCodeDto.SmComCode returnData = commonCodeService.updateCommonCode(smComCode,userId);

		if(returnData == null){
			throw new GaiaBizException(ErrorType.ETC,"기초 코드 수정 Data Logic 실패");
		}
		// API 통신
		if ("Y".equals(commonReqVo.getApiYn())) {
			if ( PlatformType.CAIROS.getName().equals(platform) ) {
				Map<String, Object> invokeParams = Maps.newHashMap();
				invokeParams.put("smComCode", returnData);
				invokeParams.put("userId",userId);

				Map response = invokeCairos2Pgaia("CAGA9013", invokeParams);

				if (!"00".equals( MapUtils.getString(response, "resultCode") ) ) {
					throw new GaiaBizException(ErrorType.INTERFACE, MapUtils.getString(response, "resultMsg"));
				}
			}
		}

		return returnData;
	}

	@Transactional
	public void updateCommonCodeOrder(List<SmComCode> codeList, CommonReqVo commonReqVo) {
		UserAuth userAuth = UserAuth.get(true);
		if(userAuth == null){
			throw new GaiaBizException(ErrorType.UNAUTHORIZED,"인증이 만료되었습니다.");
		}
		String userId = userAuth.getUsrId();
		commonCodeService.updateCommonCodeOrder(codeList,userId);

		// API 통신
		if ("Y".equals(commonReqVo.getApiYn())) {
			if ( PlatformType.CAIROS.getName().equals(platform) ) {
				Map<String, Object> invokeParams = Maps.newHashMap();
				invokeParams.put("codeList", codeList);
				invokeParams.put("userId", userId);

				Map response = invokeCairos2Pgaia("CAGA9014", invokeParams);

				if (!"00".equals( MapUtils.getString(response, "resultCode") ) ) {
					throw new GaiaBizException(ErrorType.INTERFACE, MapUtils.getString(response, "resultMsg"));
				}
			}
		}
	}

	@Transactional
	public void deleteCommonCode(List<SmComCode> codeList, CommonReqVo commonReqVo) {
		UserAuth userAuth = UserAuth.get(true);
		if(userAuth == null){
			throw new GaiaBizException(ErrorType.UNAUTHORIZED,"인증이 만료되었습니다.");
		}
		String userId = userAuth.getUsrId();

		commonCodeService.deleteCommonCode(codeList,userId);

		// API 통신
		if ("Y".equals(commonReqVo.getApiYn())) {
			if ( PlatformType.CAIROS.getName().equals(platform) ) {
				Map<String, Object> invokeParams = Maps.newHashMap();
				invokeParams.put("codeList", codeList);
				invokeParams.put("userId", userId);

				Map response = invokeCairos2Pgaia("CAGA9015", invokeParams);

				if (!"00".equals( MapUtils.getString(response, "resultCode") ) ) {
					throw new GaiaBizException(ErrorType.INTERFACE, MapUtils.getString(response, "resultMsg"));
				}
			}
		}
	}

	/**
	 * 코드 콤보 여러 개 조회
	 *
	 * @param cmnGrpCdList
	 * @param langInfo
	 * @return
	 */
	public Map<String, List<Map<String, Object>>> getCommonCodeListByGroupCode(List<String> cmnGrpCdList, String langInfo) {
		return commonCodeService.getCommonCodeListByGroupCode(cmnGrpCdList, langInfo);
	}

	/**
	 * 문서관리 구분 데이터 조회
	 * @param naviDiv
	 * @return
	 */
	public SmComCode getDocumentTypeInfo(String naviDiv) {
		return commonCodeService.getDocumentTypeInfo(naviDiv);
	}

	// 문서관리 > 속성 정의 추가 - 정보문서관리 하위 노드 리스트 조회
	public List<Map<String, Object>> getAttrbtTypeSelOptions(String langInfo) {
		return commonCodeService.getAttrbtTypeSelOptions(langInfo);
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public Map receiveInterfaceService(String transactionId, Map params) {
		Map<String, Object> result = Maps.newHashMap();
		result.put("resultCode", "00");

		try {
			//
			if ("CAGA9007".equals(transactionId)) {
				SmComCodeGroup smComCodeGroup = objectMapper.convertValue(params.get("smComCodeGroup"), SmComCodeGroup.class);
				String userId = MapUtils.getString(params, "userId");

				commonCodeService.createCommonCodeGroup(smComCodeGroup, userId);
			}
			//
			else if ("CAGA9008".equals(transactionId)) {
				CommonCodeDto.SmComCodeGroup smComCodeGroup = objectMapper.convertValue(params.get("smComCodeGroup"), CommonCodeDto.SmComCodeGroup.class);
				String userId = MapUtils.getString(params, "userId");

				commonCodeService.updateCommonCodeGroup(smComCodeGroup, userId);
			}
			//
			else if ("CAGA9009".equals(transactionId)) {
				List<String> groupCodeCdList = objectMapper.convertValue(params.get("groupCodeNoList"), new TypeReference<List<String>>() {});
				String userId = MapUtils.getString(params, "userId");

				commonCodeService.deleteCommonCodeGroup(groupCodeCdList, userId);
			}
			//
			else if ("CAGA9010".equals(transactionId)) {
				CommonCodeDto.SmComCodeGroup smComCodeGroup = objectMapper.convertValue(params.get("smComCodeGroup"), CommonCodeDto.SmComCodeGroup.class);
				String userId = MapUtils.getString(params, "userId");

				commonCodeService.upGroup(smComCodeGroup, userId);
			}
			//
			else if ("CAGA9011".equals(transactionId)) {
				CommonCodeDto.SmComCodeGroup smComCodeGroup = objectMapper.convertValue(params.get("smComCodeGroup"), CommonCodeDto.SmComCodeGroup.class);
				String userId = MapUtils.getString(params, "userId");

				commonCodeService.downGroup(smComCodeGroup, userId);
			}
			//
			else if ("CAGA9012".equals(transactionId)) {
				CommonCodeDto.SmComCode smComCode = objectMapper.convertValue(params.get("smComCode"), CommonCodeDto.SmComCode.class);
				String userId = MapUtils.getString(params, "userId");

				commonCodeService.createCommonCode(smComCode, userId);
			}
			//
			else if ("CAGA9013".equals(transactionId)) {
				CommonCodeDto.SmComCode smComCode = objectMapper.convertValue(params.get("smComCode"), CommonCodeDto.SmComCode.class);
				String userId = MapUtils.getString(params, "userId");

				commonCodeService.updateCommonCode(smComCode, userId);
			}
			// TODO
			else if ("CAGA9014".equals(transactionId)) {
				List<SmComCode> codeList = objectMapper.convertValue(params.get("codeList"), new TypeReference<List<SmComCode>>() {});
				String userId = MapUtils.getString(params, "userId");

				commonCodeService.updateCommonCodeOrder(codeList, userId);
			}
			//
			else if ("CAGA9015".equals(transactionId)) {
				List<SmComCode> codeList = objectMapper.convertValue(params.get("codeList"), new TypeReference<List<SmComCode>>() {});
				String userId = MapUtils.getString(params, "userId");

				commonCodeService.deleteCommonCode(codeList, userId);
			}
		} catch (GaiaBizException e) {
			log.error(e.getMessage(), e);
			result.put("resultCode", "01");
			result.put("resultMsg", e.getMessage());
		}

		return result;
	}
}
