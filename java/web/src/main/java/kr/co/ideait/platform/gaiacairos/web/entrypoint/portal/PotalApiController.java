package kr.co.ideait.platform.gaiacairos.web.entrypoint.portal;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import kr.co.ideait.iframework.annotation.Description;
import kr.co.ideait.platform.gaiacairos.comp.common.service.MainBoardService;
import kr.co.ideait.platform.gaiacairos.comp.portal.PortalComponent;
import kr.co.ideait.platform.gaiacairos.comp.system.service.CommonCodeService;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.board.BoardMybatisParam.BoardListInput;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.portal.PortalForm;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.security.UserAuth;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.commoncode.CommonCodeDto;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.log.Log;
import kr.co.ideait.platform.gaiacairos.core.type.LogType;
import kr.co.ideait.platform.gaiacairos.core.base.AbstractController;
import kr.co.ideait.platform.gaiacairos.core.constant.CommonCodeConstants;
import kr.co.ideait.platform.gaiacairos.core.exception.ErrorType;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.Result;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.CommonReqVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.RequestContext;

import java.util.Locale;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/portal")
public class PotalApiController extends AbstractController {

	@Autowired
	PortalForm portalForm;

	@Autowired
	MainBoardService mainBoardService;

	@Autowired
	CommonCodeService commonCodeService;

	@Autowired
	CommonCodeDto commonCodeDto;

	@Autowired
	PortalComponent portalComponent;

	/**
	 * 최초 메인화면 종합 프로젝트 LIst 가져오기
	 */
	@PostMapping("/main-home")
	@Description(name = "메인 종합화면 조회", description = "메인 종합화면을 조회한다.", type = Description.TYPE.MEHTOD)
	public Result getmainComprehensiveProjectList(CommonReqVo commonReqVo, @CookieValue(name = "lang", required = false, defaultValue = "ko") String langInfo, HttpServletRequest request) {
        
		// 공통로그
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("메인 종합화면 조회");     

		Map<String, Object> user = portalComponent.loginUserInfo();

		BoardListInput input = new BoardListInput();
		input.setBoardType("1");
		input.setUserId(UserAuth.get(true).getUsrId());
		input.setUserType(UserAuth.get(true).isAdmin() ? "ADMIN" : "NORMAL");
		input.setSystemType(platform.toUpperCase());

		return Result.ok()
				.put("projectList", portalComponent.selectMainComprehensiveProjectList())
				.put("popupMsgList", portalComponent.selectPopupMsgList())
				.put("userInfo", cookieService.getCookie(request, cookieVO.getPortalCookieName()))
				.put("pjt_add", user.get("project_add"))
				.put("faqCategory",
						commonCodeService.getCommonCodeListByGroupCode(CommonCodeConstants.FAQ_CODE_GROUP_CODE).stream().map(smComCode -> {
							CommonCodeDto.CommonCodeCombo codeCombo = commonCodeDto.fromSmComCodeToCombo(smComCode);
							codeCombo.setCmnCdNm(
									langInfo.equals("en") ? smComCode.getCmnCdNmEng() : smComCode.getCmnCdNmKrn());
							return codeCombo;
						}))
				.put("noticeList", mainBoardService.getMainBoardList(input));
	}

	/**
	 * 최초 메인화면 종합 프로젝트 검색하기
	 */
	@PostMapping("/main-home-search")
	@Description(name = "메인 종합화면 검색", description = "메인 종합화면에서 검색으로 조회한다.", type = Description.TYPE.MEHTOD)
	public Result searchMainComprehensiveProjectList(
			CommonReqVo commonReqVo,
			@Valid @RequestBody PortalForm.MainPortalTotalSearchPjtParam mainPortalTotalSearchPjtParam,
			HttpServletRequest request) {
        
		// 공통로그
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("메인 종합화면 검색"); 

		return Result.ok()
				.put("projectList",
						portalComponent.selectMainComprehensiveProjectList(mainPortalTotalSearchPjtParam.getSearchItem(),
								mainPortalTotalSearchPjtParam.getSearchText(), mainPortalTotalSearchPjtParam.getFavoritesSearch().equals("icon_btn _outline") ? "icon_btn favorites" : ""))
				.put("userInfo", cookieService.getCookie(request, cookieVO.getPortalCookieName()));
	}

	/**
	 * 메인 좌측 메뉴 및 상단 셀렉트 박스 프로젝트 List 가져오기
	 */
	@PostMapping("/left-Menu-userPjt")
	@Description(name = "메뉴정보 조회", description = "메뉴 및 상단프로젝트정보, 퀴메뉴건수를 조회한다.", type = Description.TYPE.MEHTOD)
	public Result getuserAllMenuList(CommonReqVo commonReqVo, @Valid @RequestBody PortalForm.MenuListParam menuListParam,
			HttpServletRequest request, HttpServletResponse response) {
		
		// 공통로그
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("메뉴정보 조회");  
        
        //현재 선택된 프로젝트번호 와 계약번호 쿠키 셋팅
        cookieService.setHttpOnlyCookie(response, cookieVO.getSelectCookieName(), menuListParam.getPjtNo() + ":" + menuListParam.getCntrctNo(), 60 * 60 * 24);
		
		return Result.ok()
				.put("menuList", portalComponent.loginUserMenuList(menuListParam.getPjtNo(), menuListParam.getCntrctNo()))
				.put("projectList", portalComponent.loginUserProjectList())
				.put("quickMenuList", portalComponent.selectQuickMenuCount(menuListParam.getPjtNo(), menuListParam.getCntrctNo()))
				.put("userInfo", cookieService.getCookie(request, cookieVO.getPortalCookieName()));
	}

	/**
	 * 메인 상단 네비게이션 만들어 가져오기
	 */
	@PostMapping("/nav-menu")
	@Description(name = "네비게이션정보 조회", description = "네비게이션정보를 조회한다.", type = Description.TYPE.MEHTOD)
	public Result getuserNavList(CommonReqVo commonReqVo, @Valid @RequestBody PortalForm.NavMenuInput navMenuInput) {
		
		// 공통로그
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("네비게이션정보 조회");   
        
		return Result.ok().put("navMenu", portalComponent.makeMenuNav(navMenuInput.getMenu_id()));
	}

	/**
	 * 최초 메인 종합 프로젝트 화면에서 사용자별 즐겨찾기 셋팅
	 */
	@PostMapping("/set-favorites")
	@Description(name = "프로젝트 즐겨찾기 설정", description = "프로젝트 즐겨찾기를 설정한다.", type = Description.TYPE.MEHTOD)
	public Result setUserFavorites(CommonReqVo commonReqVo, @Valid @RequestBody PortalForm.SetFavoritesParam param) {
		
		// 공통로그
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("프로젝트 즐겨찾기 설정");   

		return Result.ok().put("msg", portalComponent.setUserFavorites(portalForm.toCnProjectFavorites(param), param.getFavoritesYN()));
	}

	/**
	 * 프로젝트 변경 시 변경 프로젝트의 현재 메뉴 권한 유무 가져오기
	 */
	@GetMapping("/change-pjt/check-authority/{pjtNo}/{menuCd}/{cntrctNo}")
	@ResponseBody
	@Description(name = "메뉴권한 확인", description = "선택한 프로젝트의 메뉴권한을 조회한다.", type = Description.TYPE.MEHTOD)
	public Result getpjtChangeMenuAuthority(CommonReqVo commonReqVo, @PathVariable("pjtNo") String pjtNo, @PathVariable("menuCd") String menuCd,
			@PathVariable("cntrctNo") String cntrctNo, HttpServletRequest request) {
		
		// 공통로그
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("메뉴권한 확인"); 

		return Result.ok().put("checkAuthority", portalComponent.selectpjtChangeMenuAuthority(pjtNo, cntrctNo, menuCd));
	}

	/**
	 * 다국어 언어 변경
	 */
	@GetMapping("/change-lang/{lang}")
	@ResponseBody
	@Description(name = "언어변경", description = "선택한 언어로 변경한다.", type = Description.TYPE.MEHTOD)
	public Result changeLang(@PathVariable("lang") String lang, HttpServletResponse response,
			HttpServletRequest request) {
		// 필터 패스 URI로 로그 남기기 삭제

		if (lang != null) {
			Locale locale = Locale.of(lang);

			RequestContext requestContext = new RequestContext(request, response);
			requestContext.changeLocale(locale);

			return Result.ok().put("lang", lang);
		} else {
			return Result.nok(ErrorType.BAD_REQUEST);
		}
	}
	
	/**
	 * 프로젝트 및 계약명 가져오기
	 */
	@ResponseBody
	@GetMapping("/select-pjtNm-cntrctNm/{pjtNo}/{cntrctNo}")
	@Description(name = "프로젝트 및 계약명 조회", description = "프로젝트 및 계약명 정보를 조회한다.", type = Description.TYPE.MEHTOD)
	public Result selectPjtCntrctNm(CommonReqVo commonReqVo, @PathVariable("pjtNo") String pjtNo, @PathVariable("cntrctNo") String cntrctNo) {
		
		// 공통로그
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("프로젝트 및 계약명 조회");

		pjtNo = StringUtils.defaultIfEmpty(pjtNo, "");
		cntrctNo = StringUtils.defaultIfEmpty(cntrctNo, "");

		return Result.ok().put("pjtNmCntrctNm", portalComponent.selectPjtCntrctNm(pjtNo, cntrctNo));

	}
	
	/**
	 * 계약리스트 가져오기
	 */
	@PostMapping("/select-cntrctList")
	@ResponseBody
	@Description(name = "계약리스트 조회", description = "계약 셀릭트 박스 생성을 위해 프로젝트의 전체 계약리스트 조회", type = Description.TYPE.MEHTOD)
	public Result selectContractList(CommonReqVo commonReqVo, @RequestBody Map<String, String> params) {
		
		// 공통로그
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("계약리스트 조회"); 

		return Result.ok().put("contractList", portalComponent.selectContractList(params.get("pjtNo")));

	}

	/**
	 * 계약차수리스트 가져오기
	 */
	@PostMapping("/select-cntrctchgList")
	@ResponseBody
	@Description(name = "계약 차수 리스트 조회", description = "계약 변경 차수 셀렉트 박스 생성을 위해 계약의 계약 변경 차수리스트 조회", type = Description.TYPE.MEHTOD)
	public Result selectContractChangeList(CommonReqVo commonReqVo, @RequestBody Map<String, String> params) {
		
		// 공통로그
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("계약 차수 리스트 조회"); 

		return Result.ok().put("contractChangeList", portalComponent.selectContractChangeList(params.get("cntrctNo")));

	}
	
	/**
	 * GaiA / CaiROS 신규 사용신청
	 * @throws Exception 
	 */
	@ResponseBody
	@GetMapping("/new-use-request")
	@Description(name = "GaiA / CaiROS 신규 사용신청", description = "이데아플랫폼 가입자가 GaiA / CaiROS 신규 사용신청", type = Description.TYPE.MEHTOD)
	public Result newSystemUseRequest(CommonReqVo commonReqVo, @Valid PortalForm.NewUseReuestParam params) throws Exception {
        
        log.info("입력 데이터는 {} 입니다.", params.toString());
        
        portalComponent.newUseReuestSendMail(params.getPjtNm(), params.getPjtNm(), params.getUsrId());

		return Result.ok();
	}
}
