package kr.co.ideait.platform.gaiacairos.comp.system;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.Maps;

import kr.co.ideait.platform.gaiacairos.comp.system.service.MenuService;
import kr.co.ideait.platform.gaiacairos.core.base.AbstractComponent;
import kr.co.ideait.platform.gaiacairos.core.constant.CommonCodeConstants;
import kr.co.ideait.platform.gaiacairos.core.exception.ErrorType;
import kr.co.ideait.platform.gaiacairos.core.exception.GaiaBizException;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.SmBilling;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.SmBoard;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.SmBoardReception;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.SmButtonAuthority;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.SmMenu;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.SmPopupMsg;
import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories.SmButtonAuthorityRepository;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.MybatisInput;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.MybatisOutput;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.CommonReqVo;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.security.UserAuth;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.MenuMybatisParam;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.board.BoardForm;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.log.Log;
import kr.co.ideait.platform.gaiacairos.core.type.PlatformType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class MenuComponent extends AbstractComponent {

	@Autowired
	MenuService menuService;

	@Autowired
	SmButtonAuthorityRepository smButtonAuthorityRepository;

	public List<SmMenu> getMenuList() {
		return menuService.getMenuList();
	}

	/*
	 * 내 메뉴
	 */
	@Cacheable(value = "menu", key = "#p0")
	public List<Map<String, ?>> getMenuList(String contractNo, String userId) {
		return menuService.getMenuList(contractNo, userId);
	}

	/**
	 * 내가 권한이 있는 메뉴 경로
	 */
	public Set<String> getMyMenuPath(String contractNo, String userId) {
		return menuService.getMyMenuPath(contractNo, userId);
	}

	public SmMenu getMenu(String menuCd) {
		return menuService.getMenu(menuCd);
	}

	@Transactional
	public SmMenu createMenu(SmMenu smMenu, CommonReqVo commonReqVo) {
		SmMenu returnData = menuService.createMenu(smMenu);

		// API 통신
		if ("Y".equals(commonReqVo.getApiYn())) {
			if (PlatformType.CAIROS.getName().equals(platform)) {
				Map<String, Object> invokeParams = Maps.newHashMap();
				invokeParams.put("smMenu", smMenu);
				invokeParams.put("usrId", UserAuth.get(true).getUsrId());

				Map response = invokeCairos2Pgaia("CAGAM070301", invokeParams);

				if (!"00".equals(MapUtils.getString(response, "resultCode"))) {
					throw new GaiaBizException(ErrorType.INTERFACE, MapUtils.getString(response, "resultMsg"));
				}
			}
		}

		return returnData;
	}

	@Transactional
	public SmMenu updateMenu(SmMenu smMenu, CommonReqVo commonReqVo) {
		SmMenu returnData = menuService.updateMenu(smMenu);

		// API 통신
		if ("Y".equals(commonReqVo.getApiYn())) {
			if (PlatformType.CAIROS.getName().equals(platform)) {
				Map<String, Object> invokeParams = Maps.newHashMap();
				invokeParams.put("smMenu", smMenu);
				invokeParams.put("usrId", UserAuth.get(true).getUsrId());

				Map response = invokeCairos2Pgaia("CAGAM070302", invokeParams);

				if (!"00".equals(MapUtils.getString(response, "resultCode"))) {
					throw new GaiaBizException(ErrorType.INTERFACE, MapUtils.getString(response, "resultMsg"));
				}
			}
		}

		return returnData;
	}

	@Transactional
	public boolean upMenu(SmMenu smMenu, CommonReqVo commonReqVo) {
		boolean returnData = menuService.upMenu(smMenu);

		// API 통신
		if ("Y".equals(commonReqVo.getApiYn())) {
			if (PlatformType.CAIROS.getName().equals(platform)) {
				Map<String, Object> invokeParams = Maps.newHashMap();
				invokeParams.put("smMenu", smMenu);

				Map response = invokeCairos2Pgaia("CAGAM070304", invokeParams);

				if (!"00".equals(MapUtils.getString(response, "resultCode"))) {
					throw new GaiaBizException(ErrorType.INTERFACE, MapUtils.getString(response, "resultMsg"));
				}
			}
		}

		return returnData;
	}

	@Transactional
	public boolean downMenu(SmMenu smMenu, CommonReqVo commonReqVo) {
		boolean returnData = menuService.downMenu(smMenu);

		// API 통신
		if ("Y".equals(commonReqVo.getApiYn())) {
			if (PlatformType.CAIROS.getName().equals(platform)) {
				Map<String, Object> invokeParams = Maps.newHashMap();
				invokeParams.put("smMenu", smMenu);

				Map response = invokeCairos2Pgaia("CAGAM070305", invokeParams);

				if (!"00".equals(MapUtils.getString(response, "resultCode"))) {
					throw new GaiaBizException(ErrorType.INTERFACE, MapUtils.getString(response, "resultMsg"));
				}
			}
		}

		return returnData;
	}

	public boolean existMenuCode(String menuCd) {
		return menuService.existMenuCode(menuCd);
	}

	@Transactional
	public void deleteMenuList(List<String> menuCdList, CommonReqVo commonReqVo) {
		menuService.deleteMenuList(menuCdList);

		// API 통신
		if ("Y".equals(commonReqVo.getApiYn())) {
			if (PlatformType.CAIROS.getName().equals(platform)) {
				Map<String, Object> invokeParams = Maps.newHashMap();
				invokeParams.put("menuCdList", menuCdList);
				invokeParams.put("usrId", UserAuth.get(true).getUsrId());

				Map response = invokeCairos2Pgaia("CAGAM070303", invokeParams);

				if (!"00".equals(MapUtils.getString(response, "resultCode"))) {
					throw new GaiaBizException(ErrorType.INTERFACE, MapUtils.getString(response, "resultMsg"));
				}
			}
		}
	}

	public List<Map<String, ?>> getBillingList(int menuNo) {
		MenuMybatisParam.MenuBillingListInput input = new MenuMybatisParam.MenuBillingListInput();
		input.setMenuNo(menuNo);
		input.setCmnGrpCd(CommonCodeConstants.PAID_FUNCTION_CODE_GROUP_CODE);

		return menuService.getBillingList(menuNo);
	}

	@Transactional
	public SmBilling createBilling(SmBilling billing, CommonReqVo commonReqVo) {
		SmBilling returnData = menuService.createBilling(billing);

		// API 통신
		if ("Y".equals(commonReqVo.getApiYn())) {
			if (PlatformType.CAIROS.getName().equals(platform)) {
				Map<String, Object> invokeParams = Maps.newHashMap();
				invokeParams.put("billing", billing);

				Map response = invokeCairos2Pgaia("CAGAM070309", invokeParams);

				if (!"00".equals(MapUtils.getString(response, "resultCode"))) {
					throw new GaiaBizException(ErrorType.INTERFACE, MapUtils.getString(response, "resultMsg"));
				}
			}
		}

		return returnData;
	}

	@Transactional
	public void deleteBilling(List<Integer> billingNoList, CommonReqVo commonReqVo) {
		menuService.deleteBilling(billingNoList);

		// API 통신
		if ("Y".equals(commonReqVo.getApiYn())) {
			if (PlatformType.CAIROS.getName().equals(platform)) {
				Map<String, Object> invokeParams = Maps.newHashMap();
				invokeParams.put("billingNoList", billingNoList);
				invokeParams.put("usrId", UserAuth.get(true).getUsrId());

				Map response = invokeCairos2Pgaia("CAGAM070310", invokeParams);

				if (!"00".equals(MapUtils.getString(response, "resultCode"))) {
					throw new GaiaBizException(ErrorType.INTERFACE, MapUtils.getString(response, "resultMsg"));
				}
			}
		}
	}

	public boolean existBilling(SmBilling smBilling) {
		return menuService.existBilling(smBilling);
	}

	/**
	 * 메뉴권한 추가 > 선택한 메뉴 계층리스트 조회
	 * 
	 * @param menuCd
	 * @return
	 */
	public List<Map<String, ?>> getMenuBreadcrumb(String menuCd, String platform) {
		return menuService.getMenuBreadcrumb(menuCd, platform);
	}

	/**
	 * 통합 연동 API 수신 처리 메서드(메뉴, 게시판)
	 * CAGAM070301 - 메뉴 등록
	 * CAGAM070302 - 메뉴 수정
	 * CAGAM070303 - 메뉴 삭제
	 * CAGAM070304 - 메뉴 위로 이동
	 * CAGAM070305 - 메뉴 아래로 이동
	 * CAGAM070306 - 메뉴 버튼 권한 등록
	 * CAGAM070307 - 메뉴 버튼 권한 수정
	 * CAGAM070308 - 메뉴 버튼 권한 삭제
	 * CAGAM070309 - 유료 기능 등록
	 * CAGAM070310 - 유료 기능 삭제
	 */
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public Map receiveInterfaceService(String transactionId, Map params) {
		Map<String, Object> result = Maps.newHashMap();
		result.put("resultCode", "00");

		try {
			if ("CAGAM070301".equals(transactionId)) {
				SmMenu smMenu = objectMapper.convertValue(params.get("smMenu"), SmMenu.class);
				String usrId = (String) params.get("usrId");
				menuService.createMenu(smMenu, usrId);

				return result;
			} else if ("CAGAM070302".equals(transactionId)) {
				SmMenu smMenu = objectMapper.convertValue(params.get("smMenu"), SmMenu.class);
				String usrId = (String) params.get("usrId");
				menuService.updateMenuApi(smMenu, usrId);

				return result;
			} else if ("CAGAM070303".equals(transactionId)) {
				List<String> menuCdList = objectMapper.convertValue(
						params.get("menuCdList"),
						new TypeReference<List<String>>() {
						});
				String usrId = (String) params.get("usrId");

				menuService.deleteMenuList(menuCdList, usrId);

				return result;
			} else if ("CAGAM070304".equals(transactionId)) {
				SmMenu smMenu = objectMapper.convertValue(params.get("smMenu"), SmMenu.class);
				menuService.upMenuApi(smMenu);

				return result;
			} else if ("CAGAM070305".equals(transactionId)) {
				SmMenu smMenu = objectMapper.convertValue(params.get("smMenu"), SmMenu.class);
				menuService.downMenuApi(smMenu);

				return result;
			} else if ("CAGAM070309".equals(transactionId)) {
				SmBilling billing = objectMapper.convertValue(params.get("billing"), SmBilling.class);
				menuService.createBillingApi(billing);

				return result;
			} else if ("CAGAM070310".equals(transactionId)) {
				List<Integer> billingNoList = objectMapper.convertValue(
						params.get("billingNoList"),
						new TypeReference<List<Integer>>() {
						});
				String usrId = (String) params.get("usrId");
				menuService.deleteBillingApi(billingNoList, usrId);

				return result;
			}
		} catch (GaiaBizException e) {
			log.error(e.getMessage(), e);
			result.put("resultCode", "01");
			result.put("resultMsg", e.getMessage());
		}

		return result;
	}
}
