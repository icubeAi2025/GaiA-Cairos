package kr.co.ideait.platform.gaiacairos.web.entrypoint.auth;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.collect.Maps;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import kr.co.ideait.platform.gaiacairos.comp.auth.AuthComponent;
import kr.co.ideait.platform.gaiacairos.comp.portal.service.PortalService;
import kr.co.ideait.platform.gaiacairos.core.base.AbstractController;
import kr.co.ideait.platform.gaiacairos.core.config.security.SSOLoignTokenService;
import kr.co.ideait.platform.gaiacairos.core.config.security.TokenService;
import kr.co.ideait.platform.gaiacairos.core.exception.ErrorType;
import kr.co.ideait.platform.gaiacairos.core.exception.GaiaBizException;
import kr.co.ideait.platform.gaiacairos.core.persistence.Device;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.SmUserInfo;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.Result;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.CommonReqVo;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.security.User;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.security.UserAuth;
import kr.co.ideait.platform.gaiacairos.core.type.PlatformType;
import kr.co.ideait.platform.gaiacairos.core.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.RequestContext;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
public class AuthPageController extends AbstractController {

	private HttpSessionSecurityContextRepository securityContextRepository = new HttpSessionSecurityContextRepository();

	@Autowired
	TokenService tokenService;

	@Autowired
	SSOLoignTokenService ssoLoignTokenService;

	@Autowired
	AuthComponent authComponent;

	@Autowired
	PortalService portalService;

	@Autowired
	RedisUtil redisUtil;

	@Autowired
	User user;

	@Value("${gaia.url.portal-login}")
	private String PORTAL_LOGIN;

	@Value("${link.domain.url}")
	private String DOMAIN_URL;

	@Value("${gaia.backoffice.auth-url}")
	private String BO_AUTH_URL;

	/**
     * 로그인 시 토큰 생성 및 프로젝트 권한 확인등 공통 처리용
     */
	private Map<String, Object> executeLogin(SmUserInfo selectUser, HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {
		Map<String, Object> result = new HashMap<>();
		result.put("result", "00");

		try {
			if (selectUser.getMngDiv().isEmpty()) {
				result.put("result", "fail");
				result.put("message", "관리자 승인이 필요합니다.");
				return result;
			}

			Map<String, Object> userInfo = authComponent.loginUserInfo(selectUser.getUsrId());

			// mngDiv. A: 시스템관리자, C: CS, M: 관리관
			if ( !( "A".equals(selectUser.getMngDiv()) || "C".equals(selectUser.getMngDiv() ) || "M".equals(selectUser.getMngDiv() ) ) ) {
				if (
					MapUtils.getIntValue(userInfo, "project_join_count") > 0 &&
					( MapUtils.getIntValue(userInfo, "correct_status_count") == 0 && MapUtils.getIntValue(userInfo, "correct_period_count") == 0 )
				)
				{
					result.put("result", "fail");
					result.put("message", "이용불가 사용자입니다.\n불가사유: 퇴직자 또는 접속기간 만료");
					return result;
				}
			}
			
			User.AccessAuthority authories = new User.AccessAuthority();
			authories.setUserType((String) userInfo.get("user_type"));
            
            List<User.AccessAuthority> accessAuthories = Arrays.asList(authories);

			// 1. 포탈 사용자 정보로 토큰을 생성한다.
			String accessToken 	= tokenService.generate(selectUser.getUsrId(), selectUser.getLoginId(), accessAuthories); //30분
			String refreshToken = tokenService.refreshGenerate(selectUser.getUsrId(), selectUser.getLoginId()); //24시간

			String redisKey = String.format("%s_%s_%s", platform.toUpperCase(), selectUser.getUsrId(), request.getRemoteAddr());
			
			//기존 Redis 값 삭제 후 재생성
			redisUtil.deleteRedis(redisKey);

			redisUtil.saveRedis(redisKey, refreshToken);

			// 2. 토큰을 쿠키에 저장 및 Authentication 저장
			UserAuth userAuthentication = tokenService.parse(accessToken);
			SecurityContext context = SecurityContextHolder.getContext();
			context.setAuthentication(userAuthentication);
			securityContextRepository.saveContext(context, request, response);
			cookieService.setHttpOnlyCookie(response, cookieVO.getTokenCookieName(), accessToken, 60 * 60); //1시간

			RequestContext requestContext = new RequestContext(request, response);
			requestContext.changeLocale(request.getLocale());

			final String value = String.format("%s:%s:%s:%s:%s"
					, selectUser.getLoginId()
					, URLEncoder.encode((String) userInfo.get("user_type"), StandardCharsets.UTF_8)
					, platform.toUpperCase()
					, URLEncoder.encode(selectUser.getUsrId(), StandardCharsets.UTF_8)
					, URLEncoder.encode(selectUser.getUsrNm(), StandardCharsets.UTF_8)
			);

			// 3. 로그인 사용자 정보를 쿠키에 저장
//			Cookie cookie = new Cookie(cookieVO.getPortalCookieName(), value);	// #795 워크샵 피드백 - 포털 상단에 메일이 아닌 이름 노출로 변경 활용위해 추가
//			cookie.setHttpOnly(true);
//
//			if ("prod".equals(activeProfile)) {
//				cookie.setSecure(true); //SSL 통신채널 연결 시에만 쿠키를 전송하도록 설정
//			}
//
//			cookie.setPath("/");
////			cookie.setMaxAge(60 * 60 * 24); // 24시간
//			cookie.setMaxAge(3600); // 24시간
//			response.addCookie(cookie);
			cookieService.setCookie(response,cookieVO.getPortalCookieName(),value,!"prod".equals(activeProfile),3600);


			HttpSession session = request.getSession(true);
			session.setAttribute("userInfo", userInfo);
		} catch (GaiaBizException e) {
			log.info("doLogin.Exception : {}", e.getMessage());

			result.put("result", "01");
			result.put("message", "로그인에 실패하였습니다.");
		}

		return result;
	}	
	
	/**
     * 운영 공통 로그인처리
     */
	private String doSSOLogin(String redirectPath, HttpServletRequest request, HttpServletResponse response) throws Throwable {
		String usrId 	= "";
		String ssoToken = "";
		
		if (PlatformType.PGAIA.getName().equals(platform)) {
			ssoToken = ssoLoignTokenService.getNCPToken(request);
		}else {
			ssoToken = ssoLoignTokenService.getNewToken(request);
		}
		
		if (!StringUtils.isEmpty(ssoToken)) {
			usrId = ssoLoignTokenService.getUserIdNew(ssoToken);
		} else {
			ssoToken = ssoLoignTokenService.getOldToken(request);

			if (!StringUtils.isEmpty(ssoToken)) {
				usrId = ssoLoignTokenService.getUserIdOld(ssoToken);
			} else {
				String redirectUrl = "";
				
				if (PlatformType.PGAIA.getName().equals(platform)) {
					redirectUrl = String.format("%s/ncpLogin", PORTAL_LOGIN);
				}else {
					redirectUrl = String.format("%s/linkLogin", PORTAL_LOGIN);
				}

				return "redirect:" + redirectUrl;
			}
		}
		
		if (!StringUtils.isEmpty(usrId)) {
			Map<String, String> sqlParams = Maps.newHashMap();
			
			if (PlatformType.PGAIA.getName().equals(platform)) {
				sqlParams.put("ncpUsrId", usrId);
			}else {
				sqlParams.put("ociUsrId", usrId);
			}			

			// 아이디로 사용자 정보 확인
			SmUserInfo selectUser = authComponent.selectUser(sqlParams);

			if (selectUser == null) {
				throw new GaiaBizException(ErrorType.NO_USER_DATA, "사용자 정보가 존재하지 않습니다.");
			}

			Map<String, Object> result = executeLogin(selectUser, request, response);

			if ("00".equals(result.get("result"))) {
				/**
				 * TODO: last page가 권한이 있는지 확인해서 권한이 있으면 리다이렉트해야 루프에 빠지지 않는다.
				 * 또는 추가로 파라미터를 전달하여 해당 파라미터를 받은 페이지에서는 로그인페이지로 보내지 않는다.
				 */

				if (request.getQueryString() != null) {

					// 25-06-11 @RequestParam -> request.getQueryString()에서 파싱하는 방식으로 변경.
					// query string에서 redirectPath 값만 추출
					String query = request.getQueryString();
					String redirectPathRaw = null;

					int startIdx = query.indexOf("redirectPath=");
					if (startIdx != -1) {
						redirectPathRaw = query.substring(startIdx + "redirectPath=".length());
					}

					String decodedRedirectPath = URLDecoder.decode(redirectPath, StandardCharsets.UTF_8);

					if(redirectPathRaw != null) {
						decodedRedirectPath = URLDecoder.decode(redirectPathRaw, StandardCharsets.UTF_8);
					}
					
					String redirectUrl = "";
					if (PlatformType.PGAIA.getName().equals(platform)) {
						redirectUrl = String.format("%s%s", DOMAIN_URL, redirectPath);
					}else {
						// 2025-01-16 명시하지 않으면 Default (80 Port) 로 Redirect
						redirectUrl = String.format("%s%s", DOMAIN_URL, decodedRedirectPath);
					}	
					
					if ("page/main".equals(redirectPath)) {
						return redirectUrl;
					} else {
						return "redirect:" + redirectUrl;
					}
				}

				String scheme = request.getScheme();
				String domain = request.getServerName();
				String contextPath = request.getContextPath();
				
				String redirectUrl = "";
					
				if (PlatformType.PGAIA.getName().equals(platform)) {
					redirectUrl = String.format( "redirect:%s://%s%s", scheme, domain, StringUtils.isEmpty(contextPath) ? "" : "/" + contextPath );
				}else {
					int port = 8101; //gaia, pgaia

					if (PlatformType.CAIROS.getName().equals(platform)) {
						port = 8102;
					} else if (PlatformType.WBSGEN.getName().equals(platform)) {
						port = 8103;
					}
					redirectUrl = String.format( "redirect:%s://%s%s%s", scheme, domain, port != 80 ? ":" + port : "", StringUtils.isEmpty(contextPath) ? "" : "/" + contextPath );
				}			

				return redirectUrl;
			} else {
				throw new GaiaBizException(ErrorType.LOGIN_ERROR);
			}
		}
		
		String redirectUrl = String.format("%s/login", PORTAL_LOGIN);

		return "redirect:" + redirectUrl;
		
	}
	
	/**
     * 운영 공통 로그인 URI
	 * @throws Throwable 
     */
	@GetMapping("/login")
	public String loginPage(@RequestParam(value = "redirectPath", defaultValue = "") String redirectPath, @RequestParam(value = "isBackdoor", defaultValue = "false") boolean isBackdoor, HttpServletResponse response, HttpServletRequest request) throws Throwable {
		if ("prod".equals(activeProfile) && !isBackdoor ) {
			return doSSOLogin(redirectPath, request, response);
		}

		return "page/portal/login";
	}

	/**
     * 운영 OCI용 삭제 예정
     */
	private String doOciLogin(String redirectPath, HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {
		String usrId = "";
		String ssoToken = ssoLoignTokenService.getNewToken(request);

		if (!StringUtils.isEmpty(ssoToken)) {
			usrId = ssoLoignTokenService.getUserIdNew(ssoToken);
		} else {
			ssoToken = ssoLoignTokenService.getOldToken(request);

			if (!StringUtils.isEmpty(ssoToken)) {
				usrId = ssoLoignTokenService.getUserIdOld(ssoToken);
			} else {
				// 분기 1: GAIA/CMIS 내부 Redirect 정보가 없을 때
				String redirectUrl = String.format("%s/linkLogin", PORTAL_LOGIN);

				return "redirect:" + redirectUrl;
			}
		}

		if (!StringUtils.isEmpty(usrId)) {
			// TODO WBS-GEN,  GAIA/CMIS 분기처리

			Map<String, String> sqlParams = Maps.newHashMap();
			sqlParams.put("ociUsrId", usrId);

			// 아이디로 사용자 정보 확인
			SmUserInfo selectUser = authComponent.selectUser(sqlParams);

			if (selectUser == null) {
				return "error/login_error";
			}

			Map<String, Object> result = executeLogin(selectUser, request, response);

			if ("00".equals(result.get("result"))) {
				/**
				 * TODO: last page가 권한이 있는지 확인해서 권한이 있으면 리다이렉트해야 루프에 빠지지 않는다.
				 * 또는 추가로 파라미터를 전달하여 해당 파라미터를 받은 페이지에서는 로그인페이지로 보내지 않는다.
				 */

				if (request.getQueryString() != null) {

					// 25-06-11 @RequestParam -> request.getQueryString()에서 파싱하는 방식으로 변경.
					// query string에서 redirectPath 값만 추출
					String query = request.getQueryString();
					String redirectPathRaw = null;

					int startIdx = query.indexOf("redirectPath=");
					if (startIdx != -1) {
						redirectPathRaw = query.substring(startIdx + "redirectPath=".length());
					}

					String decodedRedirectPath = URLDecoder.decode(redirectPath, StandardCharsets.UTF_8);

					if(redirectPathRaw != null) {
						decodedRedirectPath = URLDecoder.decode(redirectPathRaw, StandardCharsets.UTF_8);
					}

					// 2025-01-16 명시하지 않으면 Default (80 Port) 로 Redirect
					String redirectUrl = String.format("%s%s", DOMAIN_URL, decodedRedirectPath);
//					String redirectUrl = String.format("%s%s", DOMAIN_URL, redirectPath);

					if ("page/main".equals(redirectPath)) {
						return redirectUrl;
					} else {
						return "redirect:" + redirectUrl;
					}
				}

				String scheme = request.getScheme();
				String domain = request.getServerName();
				String contextPath = request.getContextPath();

				int port = 8101; //gaia, pgaia

				if (PlatformType.CAIROS.getName().equals(platform)) {
					port = 8102;
				} else if (PlatformType.WBSGEN.getName().equals(platform)) {
					port = 8103;
				}

				String redirectUrl = String.format( "redirect:%s://%s%s%s", scheme, domain, port != 80 ? ":" + port : "", StringUtils.isEmpty(contextPath) ? "" : "/" + contextPath );

				return redirectUrl;
			} else {
				throw new GaiaBizException(ErrorType.LOGIN_ERROR);
			}
		}

		String redirectUrl = String.format("%s/linkLogin", PORTAL_LOGIN);

		return "redirect:" + redirectUrl;
	}

	/**
     * 운영 NCP용 삭제 예정
     */
	private String doNcpLogin(String redirectPath, HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {
		String usrId	= "";
		String ssoToken = ssoLoignTokenService.getNCPToken(request);

		if (!StringUtils.isEmpty(ssoToken)) {
			usrId = ssoLoignTokenService.getUserIdNew(ssoToken);
		} else {
			// 분기 1: GAIA/CMIS 내부 Redirect 정보가 없을 때
			String redirectUrl = String.format("%s/ncpLogin", PORTAL_LOGIN);

			return "redirect:" + redirectUrl;
		}

		if (!StringUtils.isEmpty(usrId)) {
			// TODO WBS-GEN,  GAIA/CMIS 분기처리
			// 아이디로 사용자 정보 확인
			Map<String, String> sqlParams = Maps.newHashMap();
			sqlParams.put("ncpUsrId", usrId);

			SmUserInfo selectUser = authComponent.selectUser(sqlParams);

			if (selectUser == null) {
				return "error/login_error";
			}

			Map<String, Object> result = executeLogin(selectUser, request, response);

			if ("00".equals(result.get("result"))) {
				/**
				 * TODO: last page가 권한이 있는지 확인해서 권한이 있으면 리다이렉트해야 루프에 빠지지 않는다.
				 * 또는 추가로 파라미터를 전달하여 해당 파라미터를 받은 페이지에서는 로그인페이지로 보내지 않는다.
				 */

				if (request.getQueryString() != null) {

					// 25-06-11 @RequestParam -> request.getQueryString()에서 파싱하는 방식으로 변경.
					// query string에서 redirectPath 값만 추출
//					String query = request.getQueryString();
//					String redirectPathRaw = null;

//					int startIdx = query.indexOf("redirectPath=");
//					if (startIdx != -1) {
//						redirectPathRaw = query.substring(startIdx + "redirectPath=".length());
//					}

//					String decodedRedirectPath = URLDecoder.decode(redirectPath, StandardCharsets.UTF_8);
//
//					if(redirectPathRaw != null) {
//						decodedRedirectPath = URLDecoder.decode(redirectPathRaw, StandardCharsets.UTF_8);
//					}

					// 2025-01-16 명시하지 않으면 Default (80 Port) 로 Redirect
					String redirectUrl = String.format("%s%s", DOMAIN_URL, redirectPath);

					if ("page/main".equals(redirectPath)) {
						return redirectUrl;
					} else {
						return "redirect:" + redirectUrl;
					}
				}

				String scheme = request.getScheme();
				String domain = request.getServerName();
				String contextPath = request.getContextPath();

				String redirectUrl = String.format( "redirect:%s://%s%s", scheme, domain, StringUtils.isEmpty(contextPath) ? "" : "/" + contextPath );

				return redirectUrl;
			} else {
				throw new GaiaBizException(ErrorType.LOGIN_ERROR);
			}
		}


		String redirectUrl = String.format("%s/ncpLogin", PORTAL_LOGIN);

		return "redirect:" + redirectUrl;
	}

	/**
     * 최초 로그인처리 후 메인페이지 이동용
     */
	@GetMapping("/")
	public String main(CommonReqVo commonReqVo, HttpServletRequest request) {
		if ("cairos".equals(platform) && !Device.DEVICE_DESKTOP.equals(commonReqVo.getDevice().getOperatingSystemClass())) {
			return "page/mo/index";
		}

		String tokenCook = cookieService.getCookie(request, cookieVO.getTokenCookieName());

		if(!StringUtils.isEmpty(tokenCook)) {

			String redirectUrl = request.getParameter("lastPage");
			if(redirectUrl != null) {
				return "redirect:" + redirectUrl;
			} else {
				return "page/main";
			}
		} else {
			return "redirect:/login";
		}
	}


	
	/**
     * 로컬&개발&스테이징 로그인 처리용
     */
	private Result doLogin(Map<String, String> param, HttpServletRequest request, HttpServletResponse response) throws JsonProcessingException, UnsupportedEncodingException {
		Result result = Result.ok();

		String loginId = MapUtils.getString(param, "loginId");

		// 아이디로 사용자 정보 확인
		Map<String, String> sqlParams = Maps.newHashMap();
		sqlParams.put("loginId", loginId);

		SmUserInfo selectUser = authComponent.selectUser(sqlParams);

		if (selectUser == null) {
			throw new GaiaBizException(ErrorType.NO_USER_DATA, "사용자 정보가 존재하지 않습니다.", loginId);
		}

		Map<String, Object> resp = executeLogin(selectUser, request, response);

		if ("00".equals(resp.get("result"))) {
			/**
			 * TODO: last page가 권한이 있는지 확인해서 권한이 있으면 리다이렉트해야 루프에 빠지지 않는다.
			 * 또는 추가로 파라미터를 전달하여 해당 파라미터를 받은 페이지에서는 로그인페이지로 보내지 않는다.
			 */
			String lastPage = MapUtils.getString(param, "lastPage");
			if ( !StringUtils.isEmpty(lastPage) ) {
				result.put("url", lastPage);
				return result;
			}

			result.put("url", "/");

			return result;
		} else {
			//return Result.nok(ErrorType.NO_DATA, "사용자 정보가 존재하지 않습니다.");
			//throw new GaiaBizException(ErrorType.LOGIN_ERROR);
			result.put("url", "/login");

			return result;
		}
	}

	/**
	 * 로컬&개발&스테이징용 로그인 페이지에서 접근 시 (운영에서는 추후 사용 안되도록 수정)
	 * portal => (login) => gaia (set-cookie) => page
	 * 포탈 redirect로 나중에 규약을 맞추어야 함.
	 */
	@PostMapping("/login")
	public String login(@RequestParam Map<String, String> param, HttpServletResponse response, HttpServletRequest request, ModelMap modelMap) throws IOException {
		modelMap.addAttribute(doLogin(param, request, response));

		return "jsonView";
	}

	/**
	 *
	 * OCI에서 SSO-Login 링크로 넘어와서 로그인 처리
	 */
	@GetMapping("/linkLogin")
	public String newSsoLogin(@RequestParam(value = "redirectPath", defaultValue = "") String redirectPath, HttpServletResponse response, HttpServletRequest request) throws UnsupportedEncodingException {
		String usrId = "";
		String ssoToken = ssoLoignTokenService.getNewToken(request);

		if (!StringUtils.isEmpty(ssoToken)) {
			usrId = ssoLoignTokenService.getUserIdNew(ssoToken);
		} else {
			ssoToken = ssoLoignTokenService.getOldToken(request);

			if (!StringUtils.isEmpty(ssoToken)) {
				usrId = ssoLoignTokenService.getUserIdOld(ssoToken);
			} else {
				// 분기 1: GAIA/CMIS 내부 Redirect 정보가 없을 때
				String redirectUrl = String.format("%s/linkLogin", PORTAL_LOGIN);

				return "redirect:" + redirectUrl;
			}
		}

		if (!StringUtils.isEmpty(usrId)) {
			// TODO WBS-GEN,  GAIA/CMIS 분기처리

			Map<String, String> sqlParams = Maps.newHashMap();
			sqlParams.put("ociUsrId", usrId);

			// 아이디로 사용자 정보 확인
			SmUserInfo selectUser = authComponent.selectUser(sqlParams);

			if (selectUser == null) {
				throw new GaiaBizException(ErrorType.NO_USER_DATA, "사용자 정보가 존재하지 않습니다.", usrId);
			}

			Map<String, Object> result = executeLogin(selectUser, request, response);

			if ("00".equals(result.get("result"))) {
				/**
				 * TODO: last page가 권한이 있는지 확인해서 권한이 있으면 리다이렉트해야 루프에 빠지지 않는다.
				 * 또는 추가로 파라미터를 전달하여 해당 파라미터를 받은 페이지에서는 로그인페이지로 보내지 않는다.
				 */

				if (request.getQueryString() != null) {

					// 25-06-11 @RequestParam -> request.getQueryString()에서 파싱하는 방식으로 변경.
					// query string에서 redirectPath 값만 추출
					String query = request.getQueryString();
					String redirectPathRaw = null;

					int startIdx = query.indexOf("redirectPath=");
					if (startIdx != -1) {
						redirectPathRaw = query.substring(startIdx + "redirectPath=".length());
					}

					String decodedRedirectPath = URLDecoder.decode(redirectPath, StandardCharsets.UTF_8);

					if(redirectPathRaw != null) {
						decodedRedirectPath = URLDecoder.decode(redirectPathRaw, StandardCharsets.UTF_8);
					}

					// 2025-01-16 명시하지 않으면 Default (80 Port) 로 Redirect
					String redirectUrl = String.format("%s%s", DOMAIN_URL, decodedRedirectPath);
//					String redirectUrl = String.format("%s%s", DOMAIN_URL, redirectPath);

					if ("page/main".equals(redirectPath)) {
						return redirectUrl;
					} else {
						return "redirect:" + redirectUrl;
					}
				}

				String scheme = request.getScheme();
				String domain = request.getServerName();
				String contextPath = request.getContextPath();

				int port = "prod".equals(activeProfile) ? 8101 : 8089; //gaia, pgaia

				if (PlatformType.CAIROS.getName().equals(platform)) {
					port = "prod".equals(activeProfile) ? 8102 : 8091;
				} else if (PlatformType.WBSGEN.getName().equals(platform)) {
					port = "prod".equals(activeProfile) ? 8103 : 8088;
				}

				String redirectUrl = String.format( "redirect:%s://%s%s%s", scheme, domain, port != 80 ? ":" + port : "", StringUtils.isEmpty(contextPath) ? "" : "/" + contextPath );
				
				return redirectUrl;
			} else {
				throw new GaiaBizException(ErrorType.LOGIN_ERROR);
			}
		}
		
		String redirectUrl = String.format("%s/linkLogin", PORTAL_LOGIN);

		return "redirect:" + redirectUrl;
	}
	
	/**
	 *
	 * NCP에서 SSO-Login 링크로 넘어와서 로그인 처리
	 */
	@GetMapping("/ncpLogin")
	public String ncpSsoLogin(@RequestParam(value = "redirectPath", defaultValue = "") String redirectPath, HttpServletResponse response, HttpServletRequest request) throws UnsupportedEncodingException {
		String usrId	= "";
		String ssoToken = ssoLoignTokenService.getNCPToken(request);

		if (!StringUtils.isEmpty(ssoToken)) {
			usrId = ssoLoignTokenService.getUserIdNew(ssoToken);
		} else {
			// 분기 1: GAIA/CMIS 내부 Redirect 정보가 없을 때
			String redirectUrl = String.format("%s/ncpLogin", PORTAL_LOGIN);

			return "redirect:" + redirectUrl;
		}

		if (!StringUtils.isEmpty(usrId)) {
			// TODO WBS-GEN,  GAIA/CMIS 분기처리
			// 아이디로 사용자 정보 확인
			Map<String, String> sqlParams = Maps.newHashMap();
			sqlParams.put("ncpUsrId", usrId);

			SmUserInfo selectUser = authComponent.selectUser(sqlParams);

			if (selectUser == null) {
				throw new GaiaBizException(ErrorType.NO_USER_DATA, "사용자 정보가 존재하지 않습니다.", usrId);
			}

			Map<String, Object> result = executeLogin(selectUser, request, response);

			if ("00".equals(result.get("result"))) {
				/**
				 * TODO: last page가 권한이 있는지 확인해서 권한이 있으면 리다이렉트해야 루프에 빠지지 않는다.
				 * 또는 추가로 파라미터를 전달하여 해당 파라미터를 받은 페이지에서는 로그인페이지로 보내지 않는다.
				 */

				if (request.getQueryString() != null) {
					
					// 25-06-11 @RequestParam -> request.getQueryString()에서 파싱하는 방식으로 변경.
					// query string에서 redirectPath 값만 추출
//					String query = request.getQueryString();
//					String redirectPathRaw = null;

//					int startIdx = query.indexOf("redirectPath=");
//					if (startIdx != -1) {
//						redirectPathRaw = query.substring(startIdx + "redirectPath=".length());
//					}

//					String decodedRedirectPath = URLDecoder.decode(redirectPath, StandardCharsets.UTF_8);
//
//					if(redirectPathRaw != null) {
//						decodedRedirectPath = URLDecoder.decode(redirectPathRaw, StandardCharsets.UTF_8);
//					}

					// 2025-01-16 명시하지 않으면 Default (80 Port) 로 Redirect
					String redirectUrl = String.format("%s%s", DOMAIN_URL, redirectPath);

					if ("page/main".equals(redirectPath)) {
						return redirectUrl;
					} else {
						return "redirect:" + redirectUrl;
					}
				}

				String scheme = request.getScheme();
				String domain = request.getServerName();
				String contextPath = request.getContextPath();

				String redirectUrl = String.format( "redirect:%s://%s%s", scheme, domain, StringUtils.isEmpty(contextPath) ? "" : "/" + contextPath );
				
				return redirectUrl;
			} else {
				throw new GaiaBizException(ErrorType.LOGIN_ERROR);
			}
		}


		String redirectUrl = String.format("%s/ncpLogin", PORTAL_LOGIN);

		return "redirect:" + redirectUrl;
	}

	@GetMapping("/error/{code}")
	public String error(@PathVariable("code") String code, HttpServletResponse response, HttpServletRequest request, Model model) {		
		model.addAttribute("loginPageInfo", request.getSession().getAttribute("loginPageInfo"));
		model.addAttribute("errorMsg", request.getSession().getAttribute("errorMsg"));
		
		return switch (code) {
			case "400" -> "error/400";
			case "401" -> "error/401";
			case "402" -> "error/402";
			case "403" -> "error/403";
			case "404" -> "error/404";
			case "500" -> "error/500";
			case "1000" -> "error/login_error";
			default -> "error/error";
		};
	}

	@GetMapping("/auth/bo")
	public String authBo(HttpServletRequest request, HttpServletResponse response, Model model) {
		String authToken = cookieService.getCookie(request, cookieVO.getTokenCookieName());
		response.setHeader("x-auth", authToken);

		return String.format("redirect:%s?x-auth=%s", BO_AUTH_URL, authToken);
	}

}