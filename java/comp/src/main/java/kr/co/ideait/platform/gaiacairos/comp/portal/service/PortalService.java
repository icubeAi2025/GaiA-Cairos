package kr.co.ideait.platform.gaiacairos.comp.portal.service;

import kr.co.ideait.platform.gaiacairos.core.base.AbstractGaiaCairosService;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.MybatisInput;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.portal.PortalMybatisParam.MainComprehensiveProjectInput;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.portal.PortalMybatisParam.SelectBtnAuthorityInput;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.portal.PortalMybatisParam.SelectMenuInput;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.portal.PortalMybatisParam.SelectResourcesAuthorityInput;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.security.UserAuth;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class PortalService extends AbstractGaiaCairosService {

	/**
	 * 로그인 사용자 메뉴 리스트 조회
	 *
	 * @param String (pjtNo)
	 * @param String (cntrctNo)
	 * @return List<Map<String, Object>>
	 * @throws
	 */
	public List<Map<String, Object>> loginUserMenuList(SelectMenuInput selectMenuInput, String cntrctNo) {

		if (UserAuth.get(true).isAdmin()) {
			return mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.portal.selectAdminMenuList", selectMenuInput);
		} else {
			if ("CAIROS".equals(platform.toUpperCase())) {
				selectMenuInput.setCntrctNo(cntrctNo);
			}
			return mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.portal.selectMenuList", selectMenuInput);
		}
	}

	/**
	 * 로그인 사용자 프로젝트&계약 목록 가져오기
	 *
	 * @return List<Map<String, Object>> 
	 * @throws
	 */
	public List<Map<String, Object>> loginUserProjectList(MybatisInput input) {	

		if (UserAuth.get(true).isAdmin()) {
			return mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.portal.selectAdminProjectList",input);
		} else {
			if ("CAIROS".equals(platform.toUpperCase())) {
				return mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.portal.selectCAIROSUserProjectList", input);
			} else {
				return mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.portal.selectGAIAUserProjectList", input);
			}
		}
	}
	
	/**
	 *  쿽메뉴 전자결재 건수 조회
	 *
	 * @return List<Map<String, Object>> 
	 * @throws
	 */
	public Map<String, Object> quickMenuApprovalCount(MybatisInput input) {	

		return mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.portal.selectQuickMenuApprovalCount", input);
	}
	
	/**
	 *  감리여부 조회
	 *
	 * @return List<Map<String, Object>> 
	 * @throws
	 */
	public Map<String, Object> supervisionCheck(MybatisInput input) {	

		return mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.portal.selectSupervisionCheck", input);
	}
	
	/**
	 *  쿽메뉴 검측요청 건수 조회
	 *
	 * @return List<Map<String, Object>> 
	 * @throws
	 */
	public List<Map<String, Object>> quickMenuRequestCount(MybatisInput input) {	

		return mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.portal.selectRequestAppCnt", input);
	}

	/**
	 * 화면 네비게이션 데이터 가져오기
	 *
	 * @param String (menuId)
	 * @return List
	 * @throws
	 */
	public List<Map<String, Object>> makeMenuNav(MybatisInput input) {

		if ("MAIN".equals(input.get("menuId"))) {
			return mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.portal.selectMainNavMenu");
		} else {
			return mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.portal.selectNavMenu", input);
		}
	}

	/**
	 * 메인화면 종합프로젝트 리스트 가져오기
	 *
	 * @param String (login_id) 로그인 아이디
	 * @param String (user_type) 일반사용자/관리자 여부
	 * @param String (usr_id) 유저 아이디
	 * @param String (searchItem) 검색항목
	 * @param String (searchText) 검색어
	 * @param String (favoritesSearch) 즐겨찾기여부
	 * @return List
	 * @throws
	 */
	public List<Map<String, Object>> selectMainComprehensiveProjectList(MainComprehensiveProjectInput param) {

		if (UserAuth.get(true).isAdmin()) {
			return mybatisSession.selectList(
					"kr.co.ideait.platform.gaiacairos.mybatis.mappers.portal.selectAdminMainComprehensiveProjectList", param);
		} else {
			if ("CAIROS".equals(platform.toUpperCase())) {
				return mybatisSession.selectList(
						"kr.co.ideait.platform.gaiacairos.mybatis.mappers.portal.selectCAIROSMainComprehensiveProjectList", param);
			} else {
				return mybatisSession.selectList(
						"kr.co.ideait.platform.gaiacairos.mybatis.mappers.portal.selectGAIAMainComprehensiveProjectList", param);
			}
		}
	}
	
	/**
	 * 메인화면 종합프로젝트 리스트 가져오기 (My Page용)
	 *
	 * @param String (login_id) 로그인 아이디
	 * @param String (user_type) 일반사용자/관리자 여부
	 * @param String (usr_id) 유저 아이디
	 * @param String (searchItem) 검색항목
	 * @param String (searchText) 검색어
	 * @param String (favoritesSearch) 즐겨찾기여부
	 * @return List
	 * @throws
	 */
	public List<Map<String, Object>> selectNormalMainComprehensiveProjectList(MainComprehensiveProjectInput param) {

		if ("CAIROS".equals(platform.toUpperCase())) {
			return mybatisSession.selectList(
					"kr.co.ideait.platform.gaiacairos.mybatis.mappers.portal.selectCAIROSMainComprehensiveProjectList", param);
		} else {
			return mybatisSession.selectList(
					"kr.co.ideait.platform.gaiacairos.mybatis.mappers.portal.selectGAIAMainComprehensiveProjectList", param);
		}
	}

	/**
	 * 메인화면 종합프로젝트화면의 즐겨찾기 셋팅
	 *
	 * @param CnProjectFavorites (즐겨찾기 테이블 객체)
	 * @param String             (입력, 삭제 타입)
	 * @return void
	 * @throws
	 */
	public void setUserFavorites(MybatisInput input, String actionType) {

		if (actionType.equals("icon_btn favorites")) { // 삭제
			mybatisSession.delete("kr.co.ideait.platform.gaiacairos.mybatis.mappers.portal.deletePjtFavorites", input);
		} else { // 등록
			mybatisSession.insert( "kr.co.ideait.platform.gaiacairos.mybatis.mappers.portal.insertPjtFavorites", input);
		}
	}

	/**
	 * 프로젝트&계약 변경시 메뉴 권한 유무 가져오기
	 *
	 * @param MybatisInput input
	 * @return Map<String, Object>
	 * @throws
	 */
	public Map<String, Object> selectpjtChangeMenuAuthority(MybatisInput input) {
		return mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.portal.pjtChangeMenuAuthorityCheck", input);
	}
	
	/**
	 * 리소스 ID에 대한 권한여부 조회하여 Return
	 *
	 * @param SelectResourcesAuthorityInput param
	 * @return List<Map<String, Object>>
	 * @throws
	 */
	public List<Map<String, Object>> selectBtnAuthorityList(SelectResourcesAuthorityInput param) {

		return mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.portal.userResourcesAuthorityCheck", param);
	}

	/**
	 * ===========삭제 예정===========
	 *
	 * @param String   (user_type) ADMIN 여부
	 * @param String   (login_id) 로그인 아이디
	 * @param String   (pjt_type) 프로젝트타입
	 * @param String   (pjt_no) 프로젝트번호
	 * @param String   (cntrct_no) 계약번호
	 * @param String   (menu_cd) 메뉴 아이디
	 * @param String[] (btn_id) 생성할 버튼의 권한 버튼 아이디
	 * @param String[] (btn_class) 생성할 버튼의 클래스명
	 * @param String[] (btn_fun) 생성할 버튼의 추가할 함수
	 * @param String[] (btn_msg) 생성할 버튼의 버튼명
	 * @return String
	 * @throws
	 */
	public String selectBtnAuthorityList(String user_type, String login_id, String pjt_type, String pjt_no,
										 String cntrct_no, String menu_cd, String[] btn_id, String[] btn_class, String[] btn_fun, String[] btn_msg) {

		StringBuilder btn = new StringBuilder();

		if (UserAuth.get(true).isAdmin()) {
			for (int i = 0; i < btn_id.length; i++) {
				btn.append("<button type=\"button\" class=\"" + btn_class[i] + "\" " + btn_fun[i] + ">"
						+ messageSource.getMessage(btn_msg[i], null, LocaleContextHolder.getLocale()) + "</button>");
			}
		} else {
			SelectBtnAuthorityInput param = new SelectBtnAuthorityInput();

			for (int i = 0; i < btn_id.length; i++) {
				param.setLoginId(login_id);
				param.setMenuCd(menu_cd);
				param.setBtnId(btn_id[i]);
				param.setPjtNo(pjt_no);
				param.setCntrctNo(cntrct_no);
				param.setPjtType(platform.toUpperCase());

				List<Map<String, Object>> result = mybatisSession
						.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.portal.userBtnAuthorityList", param);

				if (!(result == null) && !result.isEmpty()) {
					btn.append("<button type=\"button\" class=\"" + btn_class[i] + "\" " + btn_fun[i] + ">"
							+ messageSource.getMessage(btn_msg[i], null, LocaleContextHolder.getLocale())
							+ "</button>");
				}
			}

		}
		String btnHtml = btn.toString();

		return btnHtml;
	}

	/**
	 * ===========삭제 예정===========
	 *
	 * @param String   (user_type) ADMIN 여부
	 * @param String   (login_id) 로그인 아이디
	 * @param String   (pjt_type) 프로젝트타입
	 * @param String   (pjt_no) 프로젝트번호
	 * @param String   (cntrct_no) 계약번호
	 * @param String   (menu_cd) 메뉴 아이디
	 * @param String[] (btn_id) 생성할 버튼의 권한 버튼 아이디
	 * @param String[] (btn_class) 생성할 버튼의 클래스명
	 * @param String[] (btn_fun) 생성할 버튼의 추가할 함수
	 * @param String[] (btn_msg) 생성할 버튼의 버튼명
	 * @param String[] (btn_etc) 추가할 요소 (ID, NAME등)
	 * @return String
	 * @throws
	 */
	public String selectBtnAuthorityList(String user_type, String login_id, String pjt_type, String pjt_no,
										 String cntrct_no, String menu_cd, String[] btn_id, String[] btn_class, String[] btn_fun, String[] btn_msg,
										 String[] btn_etc) {

		StringBuilder btn = new StringBuilder();

		if (UserAuth.get(true).isAdmin()) {
			for (int i = 0; i < btn_id.length; i++) {
				btn.append("<button type=\"button\" class=\"" + btn_class[i] + "\" " + btn_etc[i] + "\" " + btn_fun[i]
						+ ">" + messageSource.getMessage(btn_msg[i], null, LocaleContextHolder.getLocale())
						+ "</button>");
			}
		} else {
			SelectBtnAuthorityInput param = new SelectBtnAuthorityInput();

			for (int i = 0; i < btn_id.length; i++) {
				param.setLoginId(login_id);
				param.setMenuCd(menu_cd);
				param.setBtnId(btn_id[i]);
				param.setPjtNo(pjt_no);
				param.setCntrctNo(cntrct_no);
				param.setPjtType(platform.toUpperCase());

				List<Map<String, Object>> result = mybatisSession
						.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.portal.userBtnAuthorityList", param);

				if (!(result == null) && !result.isEmpty()) {
					btn.append(
							"<button type=\"button\" class=\"" + btn_class[i] + "\" " + btn_etc[i] + "\" " + btn_fun[i]
									+ ">" + messageSource.getMessage(btn_msg[i], null, LocaleContextHolder.getLocale())
									+ "</button>");
				}
			}

		}
		String btnHtml = btn.toString();

		return btnHtml;
	}

	/**
	 * ===========삭제 예정===========
	 *
	 * @param String   (user_type) ADMIN 여부
	 * @param String   (login_id) 로그인 아이디
	 * @param String   (pjt_type) 프로젝트타입
	 * @param String   (pjt_no) 프로젝트번호
	 * @param String   (cntrct_no) 계약번호
	 * @param String   (menu_cd) 메뉴 아이디
	 * @param String[] (btn_id) 생성할 버튼의 권한 버튼 아이디
	 * @param String[] (btn_class) 생성할 버튼의 클래스명
	 * @param String[] (btn_fun) 생성할 버튼의 추가할 함수
	 * @param String[] (btn_msg) 생성할 버튼의 버튼명
	 * @param String[] (btn_etc) 추가할 요소 (ID, NAME등)
	 * @param String[] (btn_icon) 생성할 아이곤
	 * @return String
	 * @throws
	 */
	public String selectBtnAuthorityListWithIcon(String user_type, String login_id, String pjt_type, String pjt_no,
												 String cntrct_no, String menu_cd, String[] btn_id, String[] btn_class, String[] btn_fun, String[] btn_etc,
												 String[] btn_icon, String[] btn_tooltip, String[] btn_blind) {

		StringBuilder btn = new StringBuilder();

		if (UserAuth.get(true).isAdmin()) {
			for (int i = 0; i < btn_id.length; i++) {
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
			SelectBtnAuthorityInput param = new SelectBtnAuthorityInput();

			for (int i = 0; i < btn_id.length; i++) {
				param.setLoginId(login_id);
				param.setMenuCd(menu_cd);
				param.setBtnId(btn_id[i]);
				param.setPjtNo(pjt_no);
				param.setCntrctNo(cntrct_no);
				param.setPjtType(platform.toUpperCase());

				List<Map<String, Object>> result = mybatisSession
						.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.portal.userBtnAuthorityList", param);

				if (!(result == null) && !result.isEmpty()) {
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
		String btnHtml = btn.toString();

		return btnHtml;
	}

	/**
	 * 알림 팝업 리스트 가져오기
	 *
	 * @param String (usrId) 사용자아이디
	 * @return List
	 * @throws
	 */
	public List<Map<String, Object>> selectPopupMsgList(MybatisInput input) {

		return mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.portal.selectPopupMsgList", input);
	}

	/**
	 * 프로젝트 및 계약명 가져오기
	 *
	 * @param String (pjt_no) 프로젝트번호
	 * @param String (cntrct_no) 계약번호
	 * @return List
	 * @throws
	 */
	public Map<String, Object> selectPjtCntrctNm(MybatisInput input) {		

		return mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.portal.selectPjtNmCntrctNm", input);
	}
	
	/**
	 * 계약리스트 가져오기
	 *
	 * @param String (pjt_no) 프로젝트번호
	 * @param String (cntrct_no) 계약번호
	 * @return List
	 * @throws
	 */
	public List<Map<String, Object>> selectContractList(MybatisInput input) {

		return mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.portal.selectContractList", input);
	}

	/**
	 * 계약리스트 가져오기
	 *
	 * @param String (pjt_no) 프로젝트번호
	 * @param String (cntrct_no) 계약번호
	 * @return List
	 * @throws
	 */
	public List<Map<String, Object>> selectContractChangeList(MybatisInput input) {

		return mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.portal.selectContractChangeList", input);
	}
	
	/**
	 * API 사용여부 가져오기
	 *
	 * @param String (url) URL정보
	 * @return Map
	 * @throws
	 */
    public Map<String, Object> getApi(String url) {        
        return mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.portal.selectApiYn", url);
    }

}
