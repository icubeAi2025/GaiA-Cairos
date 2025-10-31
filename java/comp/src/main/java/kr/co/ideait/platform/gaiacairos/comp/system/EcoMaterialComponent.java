package kr.co.ideait.platform.gaiacairos.comp.system;


import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import jakarta.persistence.EntityNotFoundException;
import kr.co.ideait.platform.gaiacairos.comp.system.service.EcoMaterialService;
import kr.co.ideait.platform.gaiacairos.core.base.AbstractComponent;
import kr.co.ideait.platform.gaiacairos.core.exception.ErrorType;
import kr.co.ideait.platform.gaiacairos.core.exception.GaiaBizException;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.SmBoard;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.SmBoardReception;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.SmEcoMaterial;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.SmPopupMsg;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.CommonReqVo;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.security.UserAuth;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.board.BoardDto;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.board.BoardForm;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.ecomaterial.EcoMaterialForm;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.ecomaterial.EcoMaterialMybatisParam;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.log.Log;
import kr.co.ideait.platform.gaiacairos.core.type.PlatformType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class EcoMaterialComponent extends AbstractComponent {

	@Autowired
	EcoMaterialService ecoMaterialService;



	// 친환경 자재 목록 조회
	public List<EcoMaterialMybatisParam.EcoMaterialListOutput> getEcoMaterialList(EcoMaterialMybatisParam.EcoMaterialListInput ecomaterialListInput) {

		return ecoMaterialService.getEcoMaterialList(ecomaterialListInput);
	}

	// 자재 목록 조회
	public List<EcoMaterialMybatisParam.EcoMaterialListOutput> getMaterialList(EcoMaterialMybatisParam.EcoMaterialListInput ecomaterialListInput) {

		return ecoMaterialService.getMaterialList(ecomaterialListInput);
	}

	// 친환경 자재 조회
	public SmEcoMaterial getEcoMaterial(String ecoId) {
		return ecoMaterialService.getEcoMaterial(ecoId);
	}

	// 친환경 자재 생성
	@Transactional
	public void ecoMaterialCreate(EcoMaterialForm.CreateEcoMaterial ecoMaterial, CommonReqVo commonReqVo) {
		EcoMaterialForm.CreateEcoMaterial returnData = ecoMaterialService.ecoMaterialCreate(ecoMaterial);

		// API 통신
		if ("Y".equals(commonReqVo.getApiYn())) {
			if ( PlatformType.CAIROS.getName().equals(platform) ) {
				Map<String, Object> invokeParams = Maps.newHashMap();
				invokeParams.put("ecoMaterial", returnData);
				invokeParams.put("userId", UserAuth.get(true).getUsrId());

				Map response = invokeCairos2Pgaia("CAGA9004", invokeParams);

				if (!"00".equals( MapUtils.getString(response, "resultCode") ) ) {
					throw new GaiaBizException(ErrorType.INTERFACE, MapUtils.getString(response, "resultMsg"));
				}
			}
		}
	}

	// 친환경 자재 수정
	@Transactional
	public void ecoMaterialUpdate(EcoMaterialForm.UpdateEcoMaterial form, CommonReqVo commonReqVo) {
		EcoMaterialForm.UpdateEcoMaterial returnData = ecoMaterialService.ecoMaterialUpdate(form);

		// API 통신
		if ("Y".equals(commonReqVo.getApiYn())) {
			if ( PlatformType.CAIROS.getName().equals(platform) ) {
				Map<String, Object> invokeParams = Maps.newHashMap();
				invokeParams.put("ecoMaterial", returnData);
				invokeParams.put("userId", UserAuth.get(true).getUsrId());

				Map response = invokeCairos2Pgaia("CAGA9005", invokeParams);

				if (!"00".equals( MapUtils.getString(response, "resultCode") ) ) {
					throw new GaiaBizException(ErrorType.INTERFACE, MapUtils.getString(response, "resultMsg"));
				}
			}
		}
	}


	// 친환경 자재 삭제
	@Transactional
	public void ecoMaterialDelete(List<SmEcoMaterial> ecoMaterialList, CommonReqVo commonReqVo) {
		ecoMaterialService.ecoMaterialDelete(ecoMaterialList);

		// API 통신
		if ("Y".equals(commonReqVo.getApiYn())) {
			if ( PlatformType.CAIROS.getName().equals(platform) ) {
				Map<String, Object> invokeParams = Maps.newHashMap();
				invokeParams.put("ecoMaterialList", ecoMaterialList);

				Map response = invokeCairos2Pgaia("CAGA9006", invokeParams);

				if (!"00".equals( MapUtils.getString(response, "resultCode") ) ) {
					throw new GaiaBizException(ErrorType.INTERFACE, MapUtils.getString(response, "resultMsg"));
				}
			}
		}
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public Map receiveInterfaceService(String transactionId, Map params) {
		Map<String, Object> result = Maps.newHashMap();
		result.put("resultCode", "00");

		try {
			if ("CAGA9004".equals(transactionId)) {
				EcoMaterialForm.CreateEcoMaterial ecoMaterial = objectMapper.convertValue(params.get("ecoMaterial"), EcoMaterialForm.CreateEcoMaterial.class);
				String userId = MapUtils.getString(params, "userId");

				ecoMaterialService.ecoMaterialCreate(ecoMaterial, userId);
			}
			else if ("CAGA9005".equals(transactionId)) {
				EcoMaterialForm.UpdateEcoMaterial ecoMaterial = objectMapper.convertValue(params.get("ecoMaterial"), EcoMaterialForm.UpdateEcoMaterial.class);
				String userId = MapUtils.getString(params, "userId");

				ecoMaterialService.ecoMaterialUpdate(ecoMaterial, userId);
			}
			else if ("CAGA9006".equals(transactionId)) {
				List<SmEcoMaterial> ecoMaterialList = objectMapper.convertValue(params.get("ecoMaterialList"), new TypeReference<List<SmEcoMaterial>>() {});
				String userId = MapUtils.getString(params, "userId");

				ecoMaterialService.ecoMaterialDelete(ecoMaterialList, userId);
			}
		} catch (GaiaBizException e) {
			log.error(e.getMessage(), e);
			result.put("resultCode", "01");
			result.put("resultMsg", e.getMessage());
		}

		return result;
	}
}
