package kr.co.ideait.platform.gaiacairos.web.config.request.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.co.ideait.iframework.CommonTraceConstants;
import kr.co.ideait.iframework.ReqUtil;
import kr.co.ideait.iframework.ThreadContext;
import kr.co.ideait.iframework.secu.Crypto;
import kr.co.ideait.iframework.secu.LoginSuccessHandler;
import kr.co.ideait.platform.gaiacairos.comp.auth.AuthComponent;
import kr.co.ideait.platform.gaiacairos.comp.portal.PortalComponent;
import kr.co.ideait.platform.gaiacairos.comp.project.service.InformationService;
import kr.co.ideait.platform.gaiacairos.comp.system.service.DepartmentService;
import kr.co.ideait.platform.gaiacairos.comp.system.service.MenuService;
import kr.co.ideait.platform.gaiacairos.core.components.log.SystemLogComponent;
import kr.co.ideait.platform.gaiacairos.core.config.security.TokenService;
import kr.co.ideait.platform.gaiacairos.core.exception.ErrorType;
import kr.co.ideait.platform.gaiacairos.core.exception.ExceptionAdvice;
import kr.co.ideait.platform.gaiacairos.core.exception.GaiaBizException;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CnProject;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.CommonReqVo;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.cookie.CookieVO;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.security.User;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.security.UserAuth;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.security.ValidTokenDto;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.log.Log;
import kr.co.ideait.platform.gaiacairos.core.type.LogType;
import kr.co.ideait.platform.gaiacairos.core.util.CookieService;
import kr.co.ideait.platform.gaiacairos.core.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.PatternMatchUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.pattern.PathPatternParser;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
@Slf4j
public class AuthFilter extends OncePerRequestFilter {
    @Autowired
    protected SystemLogComponent systemLogComponent;
	
	@Autowired
	AuthComponent authComponent;

    @Autowired
    ThreadContext threadContext;

    @Autowired
    ExceptionAdvice exceptionAdvice;

    @Autowired
    TokenService tokenService;

    @Autowired
    CookieService cookieService;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MenuService menuService;

    @Autowired
    PortalComponent portalComponent;

    @Autowired
    InformationService informationService;

    @Autowired
    DepartmentService departmentService;

    @Autowired
    RedisUtil redisUtil;

    @Value("${spring.profiles.active}")
    String profile;

    @Value("${platform}")
    private String platform;

    @Value("${gaia.secret.iv:}")
    private String secretIv;

    @Value("${gaia.secret.salt:}")
    private String secretSalt;

    @Value("${gaia.secret.keySize:0}")
    private Integer keySize;

    @Value("${gaia.secret.iterationCount:0}")
    private Integer iterationCount;

    private CookieVO cookieVO;

    PathPatternParser pathPatternParser = new PathPatternParser();

    @PostConstruct
    public void init() {
        cookieVO = new CookieVO(platform.toUpperCase());
    }

    //Filter 체크 Pass URI (리소스 URI들)
    private static final String[] WHITELIST_URIS = {
            "/webApi/*",
            "/eurecaWebApi/*",
            "/interface/*",
            "/auth/*",
            "/api/portal/change-lang/*",	//언어변경
            "/loginPage",					//로그인 페이지 이동
            "/login",						//로컬 또는 개발 로그인 처리
            "/linkLogin",					//링크 로그인 처리(OCI)
            "/ncpLogin",					//링크 로그인 처리(NCP)
            "/api/logout",					//로그아웃
            "*.ico",
            "/assets/*",
            "/webjars/*",
            "/upload/*",
            "/error/*",
            "/api/util/kma-weather",
            "/login/dummy",					//NCP 해시체크 더미페이지
            "/api/portal/new-use-request" 	//GaiA / CaiROS 신규 사용신청
    };

    @SuppressWarnings("unchecked")
	@Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        //요청 URI 조회
        String uri = request.getRequestURI();

        //특정 URI 조합 변경
        if("/document".equals(request.getRequestURI())) {
            uri = String.format("%s?div=%s", request.getRequestURI(), request.getParameter("div"));
        }else if("/construction/dailyreport/detail".equals(request.getRequestURI())) {
            uri = String.format("%s?type=%s&sType=%s", request.getRequestURI(), request.getParameter("type"), request.getParameter("sType"));
        }else if("/projectcost/payment/detail".equals(request.getRequestURI())) {
            uri = String.format("%s?type=%s&sType=%s", request.getRequestURI(), request.getParameter("type"), request.getParameter("sType"));
        }

        String accessToken 		= cookieService.getCookie(request, cookieVO.getTokenCookieName());		// 인증 토큰 조회
        String userInfoCookie 	= cookieService.getCookie(request, cookieVO.getPortalCookieName());     // 구분자-portal-auth 쿠키정보 가져오기
        String pjtInfoCookie 	= cookieService.getCookie(request, cookieVO.getSelectCookieName());     // 선택된 프로젝트 및 계약정보 가져오기

        // 토큰이 필요하지 않는 URI 호출 시 : 아래 로직 처리 없이 통과
        if (PatternMatchUtils.simpleMatch(WHITELIST_URIS, uri)) {
//        	if(PatternMatchUtils.simpleMatch("/upload/*", uri) && StringUtils.isEmpty(userInfoCookie)) {
//        		throw new GaiaBizException(ErrorType.UNAUTHORIZED, "정상적인 접근이 아닙니다.(No UserInfo Cookie!!)"); //사용자정보 쿠키가 없으면 첨부파일 접근이 불가
//        	}
            filterChain.doFilter(request, response);
            return;     // 종료
        }

        String commonJson = request.getHeader(CommonTraceConstants.REQID.REQUEST_COMMON_HEADER.name());
        Map<String, String> commonHeaderMap = Maps.newHashMap();

        if (!StringUtils.isEmpty(commonJson)) {
            commonHeaderMap = objectMapper.readValue(commonJson, Map.class);
            log.info("commonHeaderMap: {}", commonHeaderMap);
        }

        MDC.put(CommonTraceConstants.traceKey.TRANSACTIONID.name(), MapUtils.getString(commonHeaderMap, "requestId", UUID.randomUUID().toString()).replaceAll("-", ""));

        String transactionId = MDC.get(CommonTraceConstants.traceKey.TRANSACTIONID.name());

        CommonReqVo commonReqVo = new CommonReqVo();
        commonReqVo.setPlatform(platform);
        commonReqVo.setEnvMode(profile);
        commonReqVo.setTrnId(transactionId);
        commonReqVo.setUserIp(ReqUtil.getClientIP(request));
        commonReqVo.setReferer(request.getHeader("referer"));
        commonReqVo.setUserAgent(request.getHeader("user-agent"));
//        commonReqVo.setReqHeader( ReqUtil.mapToQueryString( ReqUtil.makeHeaderMap(request) ) );
        commonReqVo.setReqHeader( objectMapper.writeValueAsString( ReqUtil.makeHeaderMap(request) ) );
        commonReqVo.setReqData( objectMapper.writeValueAsString( request.getParameterMap() ) );
        commonReqVo.setSrvMode(profile);
        commonReqVo.setReqDt(LocalDateTime.now(ZoneId.of("Asia/Seoul")).format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));

        String[] pjtInfo = pjtInfoCookie != null ? pjtInfoCookie.split(":") : new String[]{"", ""};

        String pjtNo 	= StringUtils.isEmpty(request.getParameter("pjtNo")) ? pjtInfo[0] : request.getParameter("pjtNo");
        String cntrctNo = StringUtils.isEmpty(request.getParameter("cntrctNo")) ? pjtInfo[1] : request.getParameter("cntrctNo");

        pjtNo 		= StringUtils.isEmpty(pjtNo) ? commonHeaderMap.get("pjtNo") : pjtNo;
        cntrctNo 	= StringUtils.isEmpty(cntrctNo) ? commonHeaderMap.get("cntrctNo") : cntrctNo;

        pjtInfoCookie = "CAIROS".equals(platform.toUpperCase()) ? String.format("%s:%s", pjtNo, cntrctNo) : String.format("%s:%s", pjtNo, pjtNo);

        try {
            try {
                log.info("#########################################################################################");
                log.info("filter Check 현재 요청 URL은 {} 입니다.  !!", uri);
                log.info("#########################################################################################");
                if(StringUtils.isEmpty(userInfoCookie)) {
                    throw new GaiaBizException(ErrorType.UNAUTHORIZED, "정상적인 접근이 아닙니다.(No UserInfo Cookie!!)");
                }
                
                String[] param = userInfoCookie != null ? userInfoCookie.split(":") : new String[]{"", "", "", "", ""};
                
                String redisKey = String.format("%s_%s_%s", platform.toUpperCase(), param[3], request.getRemoteAddr());
                
                log.info("####################################################################");
    			log.info("Redis refreshToken 값 확인 {}입니다.", redisUtil.getRedisValue(redisKey));
    			log.info("Redis refreshToken 만료시간은 {}입니다.", redisUtil.getExpire(redisKey));
    			log.info("####################################################################");

                if (!StringUtils.isEmpty(accessToken)) {

                    commonReqVo.setUserParam(userInfoCookie != null ? userInfoCookie.split(":") : new String[]{"", "", "", "", ""});
                    commonReqVo.setPjtParam(pjtInfo);
                    
                    ValidTokenDto accTokenValidDto = setAuthentication(accessToken, userInfoCookie, pjtInfoCookie, "accessToken");

                    if (accTokenValidDto.isValid()) {

                        // 사용자 정보 셋
                        setCrypto(String.format("CRYPTO_%s", UserAuth.get(true).getUsrId()));
                        UserAuth user = accTokenValidDto.getUserAuth();

                        if (StringUtils.isNotBlank(user.getUsrId())) {
                            // 로그인 성공 후처리.
//                            loginSuccessHandler.successHandle(user);
                            if (user.getDepartments() == null || user.getDepartments().isEmpty()) {
                                user.setDepartments(departmentService.getDepartmentListByUserId(user.getUsrId()));
                            }

                            if (!StringUtils.isEmpty(pjtNo)) {
                                commonReqVo.setPjtNo(pjtNo);
                                commonReqVo.setCntrctNo(cntrctNo);

                                //Map<String, Object> cnProject = informationService.getProject(pjtNo, url);

                                CnProject cnProject = informationService.getProject(pjtNo);
                                if (cnProject != null) {
                                    commonReqVo.setPjtDiv(cnProject.getPjtDiv());
                                    commonReqVo.setProject(cnProject);
                                }
                            }

                            commonReqVo.setUserId( user.getUsrId() );
                            commonReqVo.setLoginId( user.getLogin_Id() );
                            commonReqVo.setUserName( URLDecoder.decode( StringUtils.isEmpty(user.getName()) ? param[4] : user.getName(), StandardCharsets.UTF_8 ) );
                            commonReqVo.setAdmin( user.isAdmin() );
                            commonReqVo.setUserAuth( user );

                            request.setAttribute(CommonTraceConstants.REQID.REQUEST_COMMON_HEADER.name(), commonReqVo);
                            threadContext.set(commonReqVo.toSmUserLogDto());

                            setLog("userInfo");
                            // 접근 URI에 대한 허용 여부 체크 false 이면 허용 안됨으로 오류 발생
                            if(!"ADMIN".equals(param[1]) && !"/".equals(uri) && !checkMenuPath(uri)) {
                                log.info("{} 시스템의 프로젝트코드 {} 와 계약코드 {} 에 대한 {} URI 접근이 허용되지 않습니다.", platform.toUpperCase(), pjtNo, cntrctNo, uri);
                                throw new GaiaBizException(ErrorType.ACCESS_DENIED, "접근 권한이 없습니다.");
                            }
                            
                            //API 사용여부 셋팅
                            Map<String, Object> apiSend = portalComponent.getApi(uri);
                            if (apiSend != null && !apiSend.isEmpty()) {
                                commonReqVo.setApiYn((String) apiSend.get("menu_api"));
                            } else {
                                commonReqVo.setApiYn("N");
                            }

                            filterChain.doFilter(request, response);
                        } else {
                            throw new GaiaBizException(ErrorType.NOT_FOUND, "인증토큰내 아이디가 존재하지 않습니다");
                        }
                    } else {
                        log.info("accessToken 오류 명 {} 입니다.", accTokenValidDto.getErrorName());
                        if ("TOKEN_EXPIRED".equals(accTokenValidDto.getErrorName())) {

                            String refreshToken = (String)redisUtil.getRedisValue(redisKey);

                            if(refreshToken != null) {

                                ValidTokenDto refreshTokenValidDto = setAuthentication(refreshToken, userInfoCookie, pjtInfoCookie, "refreshToken");

                                if (refreshTokenValidDto.isValid()) {
                                    log.info("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
                                    log.info("@@                           인증 토큰  JWT 만료!! refreshToken으로 재생성                         @@");
                                    log.info("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");

                                    Map<String, Object> userInfo = authComponent.loginUserInfo(param[3]);

                                    if (MapUtils.isEmpty(userInfo)) {
                                        throw new GaiaBizException(ErrorType.NOT_FOUND, "사용자정보가 존재하지 않습니다.");
                                    }

                                    String usrId 			= MapUtils.getString(userInfo, "usr_id");
                                    String loginId 			= MapUtils.getString(userInfo, "login_id");

                                    User.AccessAuthority authories = new User.AccessAuthority();

                                    authories.setUserType((String) userInfo.get("user_type"));

                                    List<User.AccessAuthority> accessAuthories = Arrays.asList(authories);

                                    //새로운 토큰 생성
                                    String reAccessToken = tokenService.generate(usrId, loginId, accessAuthories);

                                    // 사용자 정보 셋
                                    UserAuth user = setAuthentication(reAccessToken, userInfoCookie, pjtInfoCookie);

                                    // 토큰을 쿠키에 저장
                                    cookieService.setHttpOnlyCookie(response, cookieVO.getTokenCookieName(), reAccessToken, 60 * 60); //1시간

                                    commonReqVo.setUserId( user.getUsrId() );
                                    commonReqVo.setLoginId( user.getLogin_Id() );
                                    commonReqVo.setUserName( URLDecoder.decode( StringUtils.isEmpty(user.getName()) ? param[4] : user.getName(), StandardCharsets.UTF_8 ) );
                                    commonReqVo.setAdmin( user.isAdmin() );
                                    commonReqVo.setUserAuth( user );

                                    request.setAttribute(CommonTraceConstants.REQID.REQUEST_COMMON_HEADER.name(), commonReqVo);
                                    threadContext.set(commonReqVo.toSmUserLogDto());

                                    setLog("userInfo");
                                    // 접근 URI에 대한 허용 여부 체크 false 이면 허용 안됨으로 오류 발생
                                    if(!"ADMIN".equals(param[1]) && !"/".equals(uri) && !checkMenuPath(uri)) {
                                        log.info("{} 시스템의 프로젝트코드 {} 와 계약코드 {} 에 대한 {} URI 접근이 허용되지 않습니다.", platform.toUpperCase(), pjtNo, cntrctNo, uri);
                                        throw new GaiaBizException(ErrorType.ACCESS_DENIED, "접근 권한이 없습니다.");
                                    }
                                    
                                    //API 사용여부 셋팅
                                    Map<String, Object> apiSend = portalComponent.getApi(uri);
                                    if (apiSend != null && !apiSend.isEmpty()) {
                                        commonReqVo.setApiYn((String) apiSend.get("menu_api"));
                                    } else {
                                        commonReqVo.setApiYn("N");
                                    }

                                    filterChain.doFilter(request, response);                              	// 리소스로 접근을 허용합니다.
                                } else {
                                    throw new GaiaBizException(ErrorType.UNAUTHORIZED, "재 로그인이 필요합니다."); //accessToken 토큰이 TOKEN_EXPIRED 이고 refreshToken도 TOKEN_EXPIRED 인 경우
                                }
                            }else {
                                throw new GaiaBizException(ErrorType.UNAUTHORIZED, "인증토큰 정보가 없습니다. (No refreshToken!!)"); //accessToken(5분) 토큰이 TOKEN_EXPIRED 이고 refreshToken의 redis정보도 삭제되었을 경우(24시간)
                            }
                        }else {
                            throw new GaiaBizException(ErrorType.UNAUTHORIZED, "인증토큰이 정상이 아닙니다."); // accessToken이 isValid체크가 False이고 사유가 TOKEN_EXPIRED 아닌경우
                        }
                    }
                } else {
                    log.info("쿠기가 만료되어 accessToken이 존재하지 않습니다!! accessToken : {}", accessToken);
                    throw new GaiaBizException(ErrorType.UNAUTHORIZED, "인증토큰 정보가 없습니다. (No Token Cookie!!)"); //토큰이 존재하지 않는 경우(쿠키 만료시간 초과로 로그인 페이지로 이동!!!!)
                }
            } catch (ExpiredJwtException | SignatureException e) {
                Log.SmUserLogDto logDto = commonReqVo.toSmUserLogDto();
                logDto.setLogType(LogType.FUNCTION.name());
                logDto.setExecType("로그인 체크");

                threadContext.set(logDto);

                exceptionAdvice.handleAuthentication(request, response, GaiaBizException.of(e));
            } catch (GaiaBizException e) {

                if(e.getErrorType().equals(ErrorType.UNAUTHORIZED)) {
                    cookieService.deleteCookieWithCacheHeaders(response, cookieVO.getTokenCookieName());
                    cookieService.deleteCookieWithCacheHeaders(response, cookieVO.getPortalCookieName());
                    cookieService.deleteCookieWithCacheHeaders(response, cookieVO.getSelectCookieName());
                    
                    SecurityContextHolder.clearContext();
                }


                Log.SmUserLogDto logDto = commonReqVo.toSmUserLogDto();
                logDto.setLogType(LogType.FUNCTION.name());
                logDto.setExecType("로그인 체크");

                threadContext.set(logDto);

                exceptionAdvice.handleAuthentication(request, response, e);
            }  catch (AccessDeniedException e) {
                Log.SmUserLogDto logDto = commonReqVo.toSmUserLogDto();
                logDto.setLogType(LogType.FUNCTION.name());
                logDto.setExecType("로그인 체크");

                threadContext.set(logDto);

                exceptionAdvice.handleAuthentication(request, response, e);
            } catch (Exception e) {
                Log.SmUserLogDto logDto = commonReqVo.toSmUserLogDto();
                logDto.setLogType(LogType.FUNCTION.name());
                logDto.setExecType("로그인 체크");

                threadContext.set(logDto);

                exceptionAdvice.handleAuthentication(request, response, GaiaBizException.of(e));
            }
        } finally {
            Log.SmUserLogDto logDto = (Log.SmUserLogDto)threadContext.get();

            systemLogComponent.insertUserLog(logDto);

            MDC.clear();
            threadContext.clear();
        }
    }

    /**
     * 1. 토큰값을 검증합니다.
     * 2. 정상적인 토큰일 경우 SecurityContextHolder로 인증정보를 저장합니다.
     */
    private ValidTokenDto setAuthentication(String token, String userInfo, String pjtInfoCookie, String checkType) {
        ValidTokenDto validTokenDto = null;

        try {
            UserAuth userAuthentication = tokenService.parse(token);

            if("accessToken".equals(checkType)) {
                if (userInfo != null) {
                    String[] userParam = userInfo.split(":");
                    userAuthentication.setName(userParam[4]);
                    userAuthentication.setSelectedProjectAndContract(pjtInfoCookie);
                }

                SecurityContextHolder.getContext().setAuthentication(userAuthentication);
            }

            validTokenDto = ValidTokenDto.builder().isValid(true).errorName(null).userAuth(userAuthentication).build();
        } catch (ExpiredJwtException exception) {
            log.error("Token Expired message: {}", exception.getMessage());
            validTokenDto = ValidTokenDto.builder().isValid(false).errorName("TOKEN_EXPIRED").build();
        } catch (JwtException exception) {
            log.error("Token Tampered message: {}", exception.getMessage());
            validTokenDto = ValidTokenDto.builder().isValid(false).errorName("TOKEN_INVALID").build();
//        } catch (NullPointerException exception) {
//            log.error("Token is null message: {}", exception.getMessage());
//            validTokenDto = ValidTokenDto.builder().isValid(false).errorName("TOKEN_NULL").build();
        }

        return validTokenDto;
    }

    /**
     * 1. 토큰값을 검증합니다.
     * 2. 정상적인 토큰일 경우 SecurityContextHolder로 인증정보를 저장합니다.
     */
    private UserAuth setAuthentication(String token, String userInfo, String pjtInfoCookie) {
        UserAuth userAuthentication = tokenService.parse(token);
        String[] userParam = userInfo.split(":");
        userAuthentication.setName(userParam[4]);
        userAuthentication.setSelectedProjectAndContract(pjtInfoCookie);
        SecurityContextHolder.getContext().setAuthentication(userAuthentication);
        return userAuthentication;
    }

    /**
     * 접근 URI이 대한 권한 체크
     * @return
     */
    private boolean checkMenuPath(String uri) {
    	List<String> accessUri = authComponent.getAccessUriList();
    	
    	// ArrayList를 배열로 변환        
    	int arrListSize = accessUri.size();        
    	String checkList[] = accessUri.toArray(new String[arrListSize]);

        log.info("accessUri: {} match: {}", accessUri, PatternMatchUtils.simpleMatch(checkList, uri));

    	if(!CollectionUtils.isEmpty(accessUri)) {
			if(PatternMatchUtils.simpleMatch(checkList, uri)) {
				log.info("'{}' URI에 대한 접근이 허용됩니다.", uri);
				return true;
			}
    	}
        return false;
    }


    private void setCrypto(String key) throws JsonProcessingException {
        String strCrypto = (String) redisUtil.getRedisValue(key);

        if (StringUtils.isEmpty(strCrypto)) {
            Crypto crypto = new Crypto();
            crypto.setIv(secretIv);
            crypto.setSalt(secretSalt);
            crypto.setSecretKey(UUID.randomUUID().toString().replaceAll("-", ""));
            crypto.setKeySize(keySize);
            crypto.setIterationCount(iterationCount);

            redisUtil.saveRedis(key, objectMapper.writeValueAsString(crypto));
        }
    }

    private void setLog(String logKey) {
        if("userInfo".equals(logKey)) {
            log.info("===================================================");
            log.info("어드민 여부는 {}입니다.", UserAuth.get(true).isAdmin());
            log.info("아이디는 {}입니다.", UserAuth.get(true).getUsrId());
            log.info("로그인아이디는 {}입니다.", UserAuth.get(true).getLogin_Id());
            log.info("프로젝트번호는 {}입니다.", UserAuth.get(true).getPjtNo());
            log.info("계약번호는 {}입니다.", UserAuth.get(true).getCntrctNo());
            log.info("===================================================");
        }
    }
}