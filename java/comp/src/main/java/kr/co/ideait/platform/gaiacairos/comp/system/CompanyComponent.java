package kr.co.ideait.platform.gaiacairos.comp.system;


import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.Maps;
import kr.co.ideait.platform.gaiacairos.comp.system.service.CompanyService;
import kr.co.ideait.platform.gaiacairos.core.base.AbstractComponent;
import kr.co.ideait.platform.gaiacairos.core.exception.ErrorType;
import kr.co.ideait.platform.gaiacairos.core.exception.GaiaBizException;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.*;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.MybatisInput;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.CommonReqVo;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.security.UserAuth;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.board.BoardForm;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.company.CompanyMybatisParam;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.log.Log;
import kr.co.ideait.platform.gaiacairos.core.type.PlatformType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class CompanyComponent extends AbstractComponent {

	@Autowired
	CompanyService companyService;


	public Page<SmCompany> getCompanyListAll(Pageable pageable) {
		return companyService.getCompanyListAll(pageable);
	}

	@Transactional
	public void createCompanyList(List<SmCompany> companyList) {

		companyService.createCompanyList(companyList);
	}

	@Transactional
	public void createCompany(SmCompany company, CommonReqVo commonReqVo) {
		companyService.createCompany(company);

		// API 통신
		if ("Y".equals(commonReqVo.getApiYn())) {
			if ( PlatformType.CAIROS.getName().equals(platform) ) {
				Map<String, Object> invokeParams = Maps.newHashMap();
				invokeParams.put("company", company);
				invokeParams.put("usrId", UserAuth.get(true).getUsrId());

				Map response = invokeCairos2Pgaia("CAGAM07010101", invokeParams);


				if (!"00".equals( MapUtils.getString(response, "resultCode") ) ) {
					throw new GaiaBizException(ErrorType.INTERFACE, MapUtils.getString(response, "resultMsg"));
				}
			}
		}
	}

	public Page<CompanyMybatisParam.IdeaCompanyOutput> getCompanyListByIdea(CompanyMybatisParam.CompanyListInput companyListInput) {
		return companyService.getCompanyListByIdea(companyListInput);
	}

	public Page<CompanyMybatisParam.PcesCompanyOutput> getCompanyListByPces(CompanyMybatisParam.CompanyListInput companyListInput) {
		return companyService.getCompanyListByPces(companyListInput);
	}

	public Page<CompanyMybatisParam.CompanyOutput> getCompanyList(CompanyMybatisParam.CompanyListInput companyListInput) {
		return companyService.getCompanyList(companyListInput);
	}

	public List<CompanyMybatisParam.UserCompanyOutput> getUserCompanyList(CompanyMybatisParam.UserCompanyListInput userCompanyListInput) {
		return companyService.getUserCompanyList(userCompanyListInput);
	}

	public SmCompany getCompany(String corpNo) {
		return companyService.getCompany(corpNo);
	}

	public boolean checkCorpNo(String corpNo) {
		return companyService.checkCorpNo(corpNo);
	}

	@Transactional
	public void deleteCompanyList(List<String> corpNoList, CommonReqVo commonReqVo) {
		companyService.deleteCompanyList(corpNoList);

		// API 통신
		if ("Y".equals(commonReqVo.getApiYn())) {
			if ( PlatformType.CAIROS.getName().equals(platform) ) {
				Map<String, Object> invokeParams = Maps.newHashMap();
				invokeParams.put("corpNoList", corpNoList);
				invokeParams.put("usrId", UserAuth.get(true).getUsrId());

				Map response = invokeCairos2Pgaia("CAGAM07010103", invokeParams);

				if (!"00".equals( MapUtils.getString(response, "resultCode") ) ) {
					throw new GaiaBizException(ErrorType.INTERFACE, MapUtils.getString(response, "resultMsg"));
				}
			}
		}
	}

	@Transactional
	public SmCompany updateCompany(SmCompany smCompany, CommonReqVo commonReqVo) {
		SmCompany returnData = companyService.updateCompany(smCompany);

		// API 통신
		if ("Y".equals(commonReqVo.getApiYn())) {
			if ( PlatformType.CAIROS.getName().equals(platform) ) {
				Map<String, Object> invokeParams = Maps.newHashMap();
				invokeParams.put("smCompany", smCompany);
				invokeParams.put("usrId", UserAuth.get(true).getUsrId());

				Map response = invokeCairos2Pgaia("CAGAM07010102", invokeParams);

				if (!"00".equals( MapUtils.getString(response, "resultCode") ) ) {
					throw new GaiaBizException(ErrorType.INTERFACE, MapUtils.getString(response, "resultMsg"));
				}
			}
		}

		return returnData;
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public Map receiveInterfaceService(String transactionId, Map params) {
		Map<String, Object> result = Maps.newHashMap();
		result.put("resultCode", "00");

		if ("CAGAM07010101".equals(transactionId)) {
			SmCompany smCompany = objectMapper.convertValue(params.get("company"), SmCompany.class);
			String usrId = (String) params.get("usrId");
			companyService.createCompany(smCompany, usrId);

			return result;
		}
		else if ("CAGAM07010102".equals(transactionId)) {
			SmCompany smCompany = objectMapper.convertValue(params.get("smCompany"), SmCompany.class);
			String usrId = (String) params.get("usrId");
			companyService.updateCompany(smCompany, usrId);

			return result;
		}
		else if ("CAGAM07010103".equals(transactionId)) {
			List<String> corpNoList = objectMapper.convertValue(
			params.get("corpNoList"),
			new TypeReference<List<String>>() {
			});
			String usrId = (String) params.get("usrId");

			companyService.deleteCompanyListApi(corpNoList, usrId);

			return result;
		}

		return result;
	}
}
