package kr.co.ideait.platform.gaiacairos.comp.portal;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.Maps;

import kr.co.ideait.platform.gaiacairos.comp.auth.AuthComponent;
import kr.co.ideait.platform.gaiacairos.comp.mail.MailComponent;
import kr.co.ideait.platform.gaiacairos.comp.portal.service.PortalService;
import kr.co.ideait.platform.gaiacairos.core.base.AbstractComponent;
import kr.co.ideait.platform.gaiacairos.core.constant.CommonCodeConstants;
import kr.co.ideait.platform.gaiacairos.core.exception.GaiaBizException;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.*;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.MybatisInput;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.portal.PortalMybatisParam.MainComprehensiveProjectInput;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.portal.PortalMybatisParam.SelectMenuInput;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.portal.PortalMybatisParam.SelectResourcesAuthorityInput;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.security.UserAuth;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.board.BoardForm;
import kr.co.ideait.platform.gaiacairos.core.util.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class PortalComponent extends AbstractComponent {
	
	@Autowired
	AuthComponent authComponent;

	@Autowired
	PortalService portalService;
	
	@Autowired
	FileService fileService;
	
	@Autowired
	MailComponent mailComponent;
	
	/**
	 * 로그인 사용자 정보 조회
	 *
	 * @param String (loginID)
	 * @return List
	 * @throws
	 */
	public Map<String, Object> loginUserInfo() {

		return authComponent.loginUserInfo(UserAuth.get(true).getUsrId());
	}

	/**
	 * 로그인 사용자 메뉴 리스트 조회
	 *
	 * @throws
	 */
	public List<Map<String, Object>> loginUserMenuList(String pjtNo, String cntrctNo) {
		
		SelectMenuInput selectMenuInput = new SelectMenuInput();
		selectMenuInput.setUsrId(UserAuth.get(true).getUsrId());
		selectMenuInput.setPjtType(pjtType);
		selectMenuInput.setPjtNo(pjtNo);
		
		return portalService.loginUserMenuList(selectMenuInput, cntrctNo);
	}

	/**
	 * 로그인 사용자 프로젝트&계약 목록 가져오기
	 *
	 * @throws
	 */
	public List<Map<String, Object>> loginUserProjectList() {
		
		MybatisInput input = MybatisInput.of().add("usrId", UserAuth.get(true).getUsrId()).add("pjtType", pjtType)
				.add("cmnGrpCd", CommonCodeConstants.WORKTYPE_CODE_GROUP_CODE).add("pjtDiv",platform.toUpperCase().substring(0, 1));
		
		return portalService.loginUserProjectList(input);
	}
	
	/**
	 * 쿽메뉴 카운트 가져오기
	 *
	 * @throws
	 */
	public Map<String, Object> selectQuickMenuCount(String pjtNo, String cntrctNo) {
		Map<String, Object> resultMap = new HashMap<>();
		
		String userType = UserAuth.get(true).isAdmin() ? "ADMIN" : "NORMAL";
		
		MybatisInput input = MybatisInput.of().add("usrId", UserAuth.get(true).getUsrId()).add("pjtNo", pjtNo)
				.add("cntrctNo", cntrctNo).add("pjtType",platform.toUpperCase()).add("userType", userType).add("cmnGrpCd", CommonCodeConstants.REQUEST_CODE_GROUP_CODE);
		
		Map<String, Object> approvalCountList = portalService.quickMenuApprovalCount(input);		
		resultMap.put("approval", approvalCountList);
		
		if(!UserAuth.get(true).isAdmin()) {
			Map<String, Object> supervisionCheckYn = portalService.supervisionCheck(input);
			resultMap.put("supervision", supervisionCheckYn);
			
			if("Y".equals(supervisionCheckYn.get("checkyn"))) {
				List<Map<String, Object>> requestCountList = portalService.quickMenuRequestCount(input);
				
				resultMap.put("request", requestCountList);
			}
		}
		
		return resultMap;
	}

	/**
	 * 화면 네비게이션 데이터 가져오기
	 *
	 * @throws
	 */
	public List<Map<String, Object>> makeMenuNav(String menuId) {
		MybatisInput input = MybatisInput.of().add("menuId", menuId)
				.add("pjtType", pjtType);
		
		return portalService.makeMenuNav(input);
	}

	/**
	 * 메인화면 종합프로젝트 리스트 가져오기
	 *
	 * @throws
	 */
	public List<Map<String, Object>> selectMainComprehensiveProjectList() {
		
		MainComprehensiveProjectInput param = new MainComprehensiveProjectInput();
		param.setLoginId(UserAuth.get(true).getLogin_Id());
		param.setUsrId(UserAuth.get(true).getUsrId());
		param.setConPstatsCd(CommonCodeConstants.CON_PSTATS_CODE_GROUP_CODE);
		param.setMajorCnsttyCd(CommonCodeConstants.WORKTYPE_CODE_GROUP_CODE);
		param.setPjtType(pjtType);
		param.setPjtDiv(pjtType.substring(0, 1));
		
		return portalService.selectMainComprehensiveProjectList(param);
	}
	
	/**
	 * 메인화면 종합프로젝트 리스트 가져오기
	 *
	 * @throws
	 */
	public List<Map<String, Object>> selectNormalMainComprehensiveProjectList() {
		
		MainComprehensiveProjectInput param = new MainComprehensiveProjectInput();
		param.setLoginId(UserAuth.get(true).getLogin_Id());
		param.setUsrId(UserAuth.get(true).getUsrId());
		param.setConPstatsCd(CommonCodeConstants.CON_PSTATS_CODE_GROUP_CODE);
		param.setMajorCnsttyCd(CommonCodeConstants.WORKTYPE_CODE_GROUP_CODE);
		param.setPjtType(pjtType);
		param.setPjtDiv(pjtType.substring(0, 1));
		
		return portalService.selectNormalMainComprehensiveProjectList(param);
	}

	/**
	 * 검색된 메인화면 종합프로젝트 리스트 가져오기
	 *
	 * @throws
	 */
	public List<Map<String, Object>> selectMainComprehensiveProjectList(String searchItem, String searchText, String favoritesSearch) {
		
		MainComprehensiveProjectInput param = new MainComprehensiveProjectInput();
		param.setLoginId(UserAuth.get(true).getLogin_Id());
		param.setUsrId(UserAuth.get(true).getUsrId());
		param.setConPstatsCd(CommonCodeConstants.CON_PSTATS_CODE_GROUP_CODE);
		param.setMajorCnsttyCd(CommonCodeConstants.WORKTYPE_CODE_GROUP_CODE);
		param.setSearchItem(searchItem);
		param.setSearchText(searchText);
		param.setFavoritesSearch(favoritesSearch);
		param.setPjtType(pjtType);
		
		return portalService.selectMainComprehensiveProjectList(param);
	}

	/**
	 * 메인화면 종합프로젝트화면의 즐겨찾기 셋팅
	 *
	 * @throws
	 */
	public String setUserFavorites(CnProjectFavorites cnProjectFavorites, String actionType) {
		
		MybatisInput input = MybatisInput.of().add("pjtNo", cnProjectFavorites.getPjtNo())
				.add("cntrctNo", cnProjectFavorites.getCntrctNo())
				.add("loginId", cnProjectFavorites.getLoginId())
				.add("pjtType", pjtType)
				.add("usrId", cnProjectFavorites.getRgstrId());
		
		portalService.setUserFavorites(input, actionType);
		
		String sendMsg = "";
		if (("icon_btn favorites").equals(actionType)) {
			sendMsg = messageSource.getMessage("msg.014", null, LocaleContextHolder.getLocale());
		} else {
			sendMsg = messageSource.getMessage("msg.013", null, LocaleContextHolder.getLocale());
		}
		
		return sendMsg;
	}

	/**
	 * 프로젝트&계약 변경시 메뉴 권한 유무 가져오기
	 *
	 * @throws
	 */
	public String selectpjtChangeMenuAuthority(String pjtNo, String cntrctNo, String menuCd) {
		
		MybatisInput input = MybatisInput.of().add("pjtNo", pjtNo).add("cntrctNo", cntrctNo).add("pjtType", pjtType)
				.add("usrId", UserAuth.get(true).getUsrId()).add("menuCd", menuCd);

		Map<String, Object> result = portalService.selectpjtChangeMenuAuthority(input);
		
		String authority_yn = (String) result.get("authority_yn");
		
		return authority_yn;
	}

	/**
	 * 권한 있는 버튼 만들어 Return
	 *
	 * @throws
	 */
	public String selectBtnAuthorityList(String[] resc_id, String[] btn_class, String[] btn_fun, String[] btn_msg) {
		
		StringBuilder btn = new StringBuilder();

		if (UserAuth.get(true).isAdmin()) {
			for (int i = 0; i < resc_id.length; i++) {
				btn.append("<button type=\"button\" class=\"" + btn_class[i] + "\" " + btn_fun[i] + ">"
						+ messageSource.getMessage(btn_msg[i], null, LocaleContextHolder.getLocale()) + "</button>");
			}
		} else {
			SelectResourcesAuthorityInput param = new SelectResourcesAuthorityInput();
			
			param.setUsrId(UserAuth.get(true).getUsrId());
			param.setPjtNo(UserAuth.get(true).getPjtNo());
			param.setCntrctNo(UserAuth.get(true).getCntrctNo());
			param.setPjtType(pjtType);
			
			ArrayList<String> rescIdList = new ArrayList<>();
			
			for(String rescId : resc_id) {
				rescIdList.add(rescId);
			}
			
			param.setRescIdList(rescIdList);
			
			List<Map<String, Object>> result = portalService.selectBtnAuthorityList(param);
			
			for(int i = 0; i < resc_id.length; i++) {
				for(Map<String, Object> resultItem : result) {
					if(resultItem.get("resc_id").equals(resc_id[i])) {
						if ("Y".equals(resultItem.get("use_yn"))) {
							btn.append("<button type=\"button\" class=\"" + btn_class[i] + "\" " + btn_fun[i] + ">"
									+ messageSource.getMessage(btn_msg[i], null, LocaleContextHolder.getLocale())
									+ "</button>");					
						}
					}
				}
			}
		}
		
		String btnHtml = btn.toString();

		return btnHtml;
	}

	/**
	 * 권한 있는 버튼 만들어 Return
	 *
	 * @throws
	 */
	public String selectBtnAuthorityList(String[] resc_id, String[] btn_class, String[] btn_fun, String[] btn_msg,
										 String[] btn_etc) {
		
		StringBuilder btn = new StringBuilder();

		if (UserAuth.get(true).isAdmin()) {
			for (int i = 0; i < resc_id.length; i++) {
				btn.append("<button type=\"button\" class=\"" + btn_class[i] + "\" " + btn_etc[i] + "\" " + btn_fun[i]
						+ ">" + messageSource.getMessage(btn_msg[i], null, LocaleContextHolder.getLocale())
						+ "</button>");
			}
		} else {
			
			SelectResourcesAuthorityInput param = new SelectResourcesAuthorityInput();

			param.setUsrId(UserAuth.get(true).getUsrId());
			param.setPjtNo(UserAuth.get(true).getPjtNo());
			param.setCntrctNo(UserAuth.get(true).getCntrctNo());
			param.setPjtType(pjtType);
			
			ArrayList<String> rescIdList = new ArrayList<>();
			
			for(String rescId : resc_id) {
				rescIdList.add(rescId);
			}
			
			param.setRescIdList(rescIdList);
			
			List<Map<String, Object>> result = portalService.selectBtnAuthorityList(param);
			
			for(int i = 0; i < resc_id.length; i++) {
				for(Map<String, Object> resultItem : result) {
					if(resultItem.get("resc_id").equals(resc_id[i])) {
						if ("Y".equals(resultItem.get("use_yn"))) {
							btn.append("<button type=\"button\" class=\"" + btn_class[i] + "\" " + btn_etc[i] + "\" " + btn_fun[i]
									+ ">" + messageSource.getMessage(btn_msg[i], null, LocaleContextHolder.getLocale())
									+ "</button>");				
						}
					}
				}
			}
		}
		String btnHtml = btn.toString();

		return btnHtml;
	}

	/**
	 * 권한 있는 버튼 만들어 Return
	 *
	 * @throws
	 */
	public String selectBtnAuthorityListWithIcon(String[] resc_id, String[] btn_class, String[] btn_fun, String[] btn_etc,
												 String[] btn_icon, String[] btn_tooltip, String[] btn_blind) {
		StringBuilder btn = new StringBuilder();

		if (UserAuth.get(true).isAdmin()) {
			for (int i = 0; i < resc_id.length; i++) {
				btn.append("<button type=\"button\" class=\"" + btn_class[i] + "\" " + btn_etc[i] + " " + btn_fun[i]
						+ "><i class='" + btn_icon[i] + "'></i>");
				if (btn_tooltip[i] != null && !btn_tooltip[i].isEmpty()) {
					btn.append("<span class='tooltip'>"
							+ messageSource.getMessage(btn_tooltip[i], null, LocaleContextHolder.getLocale())
							+ "</span>");
				}
				if (btn_blind[i] != null && !btn_blind[i].isEmpty()) {
					btn.append("<span class='blind'>"
							+ messageSource.getMessage(btn_blind[i], null, LocaleContextHolder.getLocale())
							+ "</span>");
				}
				btn.append("</button>");
			}
		} else {
			SelectResourcesAuthorityInput param = new SelectResourcesAuthorityInput();

			param.setUsrId(UserAuth.get(true).getUsrId());
			param.setPjtNo(UserAuth.get(true).getPjtNo());
			param.setCntrctNo(UserAuth.get(true).getCntrctNo());
			param.setPjtType(pjtType);
			
			ArrayList<String> rescIdList = new ArrayList<>();
			
			for(String rescId : resc_id) {
				rescIdList.add(rescId);
			}
			
			param.setRescIdList(rescIdList);
			
			List<Map<String, Object>> result = portalService.selectBtnAuthorityList(param);
			
			for(int i = 0; i < resc_id.length; i++) {
				for(Map<String, Object> resultItem : result) {
					if(resultItem.get("resc_id").equals(resc_id[i])) {
						if ("Y".equals(resultItem.get("use_yn"))) {
							btn.append("<button type=\"button\" class=\"" + btn_class[i] + "\" " + btn_etc[i] + " " + btn_fun[i]
									+ "><i class='" + btn_icon[i] + "'></i>");
							if (btn_tooltip[i] != null && !btn_tooltip[i].isEmpty()) {
								btn.append("<span class='tooltip'>"
										+ messageSource.getMessage(btn_tooltip[i], null, LocaleContextHolder.getLocale())
										+ "</span>");
							}
							if (btn_blind[i] != null && !btn_blind[i].isEmpty()) {
								btn.append("<span class='blind'>"
										+ messageSource.getMessage(btn_blind[i], null, LocaleContextHolder.getLocale())
										+ "</span>");
							}
							btn.append("</button>");				
						}
					}
				}
			}
		}
		String btnHtml = btn.toString();

		return btnHtml;
	}

	/**
	 * 알림 팝업 리스트 가져오기
	 *
	 * @throws
	 */
	public List<Map<String, Object>> selectPopupMsgList() {

		MybatisInput input = MybatisInput.of().add("usrId", UserAuth.get(true).getUsrId())
				.add("pjtType", platform.toUpperCase());
		
		return portalService.selectPopupMsgList(input);
	}

	/**
	 * 프로젝트 및 계약명 가져오기
	 *
	 * @throws
	 */
	public Map<String, Object> selectPjtCntrctNm(String pjt_no, String cntrct_no) {
		
		String sarchType = pjt_no.equals(cntrct_no) ? "GAIA" : "CAIROS";

		MybatisInput input = MybatisInput.of().add("pjt_no", pjt_no)
				.add("cntrct_no", cntrct_no)
				.add("sarchType", sarchType);
		
		return portalService.selectPjtCntrctNm(input);
	}

	/**
	 * 계약리스트 가져오기
	 *
	 * @throws
	 */
	public List<Map<String, Object>> selectContractList(String pjt_no) {
		
		MybatisInput input = MybatisInput.of().add("pjtNo", pjt_no)
				.add("cmnGrpCd", CommonCodeConstants.WORKTYPE_CODE_GROUP_CODE);
		
		return portalService.selectContractList(input);
	}

	/**
	 * 계약차수리스트 가져오기
	 *
	 * @throws
	 */
	public List<Map<String, Object>> selectContractChangeList(String cntrct_no) {

		MybatisInput input = MybatisInput.of().add("cntrctNo", cntrct_no);
		
		return portalService.selectContractChangeList(input);
	}
	
	/**
	 * API 사용여부 가져오기
	 *
	 * @throws
	 */
	public Map<String, Object> getApi(String url) {
		
		return portalService.getApi(url);
	}
	
	/**
	 * 시스템 신규 사용여부 신청하기
	 * @throws Exception 
	 *
	 * @throws
	 */
	public void newUseReuestSendMail(String pjtNm, String jobNm, String usrId) throws Exception {
		
		mailComponent.sendNewUseRequest(pjtNm, jobNm, usrId);
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public Map receiveInterfaceService(String transactionId, Map params) {
		Map<String, Object> result = Maps.newHashMap();
		result.put("resultCode", "00");

		try {
			if ("CAGA9001".equals(transactionId)) {
//				BoardForm.Board board = objectMapper.convertValue(params.get("board"), BoardForm.Board.class);
//				List<MultipartFile> files = (List<MultipartFile>)params.get("files");
//				String[] userParam = objectMapper.convertValue(params.get("userParam"), String[].class);

//				CommonReqVo commonReqVo = new CommonReqVo();
//				commonReqVo.setUserParam(userParam);

//				boardService.createBoard(board, files, commonReqVo);

				return result;
			}
			else if ("CAGA9002".equals(transactionId)) {
//				SmBoard board = objectMapper.convertValue(params.get("board"), SmBoard.class);
//				List<SmBoardReception> smBoardReceptionList = objectMapper.convertValue(params.get("smBoardReceptionList"), new TypeReference<List<SmBoardReception>>() {});
//				List<SmPopupMsg> smPopupMsgList = objectMapper.convertValue(params.get("smPopupMsgList"), new TypeReference<List<SmPopupMsg>>() {});
//				String preShareYn = objectMapper.convertValue(params.get("preShareYn"), String.class);
//				List<MultipartFile> files = (List<MultipartFile>)params.get("files");

//				boardService.updateBoard(board, smBoardReceptionList, smPopupMsgList, files, preShareYn);

				return result;
			}
			else if ("CAGA9003".equals(transactionId)) {
//				List<String> boards = objectMapper.convertValue(params.get("boards"), new TypeReference<List<String>>() {});
//				String dltId = objectMapper.convertValue(params.get("dltId"), String.class);

//				boardService.deleteBoard(boards, dltId);

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
