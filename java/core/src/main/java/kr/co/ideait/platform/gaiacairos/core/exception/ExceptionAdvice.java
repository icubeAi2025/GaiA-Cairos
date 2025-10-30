package kr.co.ideait.platform.gaiacairos.core.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import io.pebbletemplates.spring.servlet.PebbleViewResolver;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import kr.co.ideait.iframework.ThreadContext;
import kr.co.ideait.platform.gaiacairos.core.base.AbstractBase;
import kr.co.ideait.platform.gaiacairos.core.config.property.Properties;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.log.Log;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Map;


@ControllerAdvice
@Slf4j
public class ExceptionAdvice extends AbstractBase {

    @Autowired
    ThreadContext threadContext;

    @Autowired
    Properties properties;

    @Autowired
    PebbleViewResolver pebbleViewResolver;

    final static String API_PREFIX = "/api";
    final static String WEB_API_PREFIX = "/webApi";
    final static String[] ALLOWED_REDIRECT_URL = {"/ociLogin", "/ncpLogin"};

    @ExceptionHandler(Exception.class)
    public final Object handleException(Exception ex, WebRequest request) {
        log.error("handleException: {}", ex);

        Log.SmUserLogDto logDto = (Log.SmUserLogDto)threadContext.get();
//        log.info("logDto: {}", logDto);

        if (logDto != null && !StringUtils.isEmpty(logDto.getPlatform())) {
            logDto.setResult("FAIL");
            logDto.setErrorReason(ex.getMessage());
        }

        if (ex instanceof GaiaBizException gaiaCmisBizException) {
            return getSimpleResponse(gaiaCmisBizException, request);
        } else if (ex instanceof NoResourceFoundException noResourceFoundException) {
            // FIXME: 로그가 많이 나와, 개발이 힘들어 임시로 로그없게 합니다. (나중에 없는 리소스를 확인해야 할수 있음)
            return getSimpleResponse(GaiaBizException.of(noResourceFoundException), request);
        } else {
//            log.error(ex.getMessage(), ex);
            return getSimpleResponse(GaiaBizException.of(ex), request);
        }
    }

    private boolean isSafe(String value) {
        if (StringUtils.isEmpty(value)) {
            return true;
        }

        return true;
    }

    private Object getSimpleResponse(GaiaBizException gaiaBizException, WebRequest request) {
        if (gaiaBizException == null) {
            return ResponseEntity.status(HttpStatusCode.valueOf(500));
        }
        if (request == null) {
            return ResponseEntity.status(HttpStatusCode.valueOf(500)).build();
        }

        HttpServletRequest req = ((ServletWebRequest) request).getRequest();

        final String url = req.getRequestURI();

        log.info("\n>>>> exception\nurl: {}\nmessage: {}\ndetail\n{}", url, gaiaBizException.getMessage(), gaiaBizException.getDetailMessage());

        if (gaiaBizException.getThrowable() != null) {
            log.error("throwable: {}", gaiaBizException.getThrowable());
        }

        String returnUrl = StringUtils.defaultString(getLoginPage(req));
        String apiLoginPageInfo = StringUtils.defaultString(getApiLoginPage(req));

        if (url.startsWith(API_PREFIX)) {
        	log.info("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
            log.info("/api 오류가 발생했습니다.");
            log.info("이동한 페이지는 {}입니다.", returnUrl);
            log.info("이동한 페이지는 {}입니다.", apiLoginPageInfo);
            log.info("오류 타입은 {} 입니다!!", gaiaBizException.getErrorType());
            log.info("오류 메시지(은)는 {} 입니다!!", gaiaBizException.getMessage());
            log.info("오류 코드(은)는 {} 입니다!!", gaiaBizException.getNokResult().getCode());
            log.info("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
        	Map<String, String> result = Maps.newHashMap();
            result.put("reTurnUrl", isSafe(returnUrl) ?  returnUrl : "");
            result.put("apiLoginPageInfo", isSafe(apiLoginPageInfo) ?  apiLoginPageInfo : "");
            result.put("resultMsg", URLEncoder.encode(gaiaBizException.getMessage(), StandardCharsets.UTF_8));
            result.put("errorCode", Integer.toString(gaiaBizException.getNokResult().getCode()));
            return new ResponseEntity(result, gaiaBizException.getStatus());
        } else if (url.startsWith(WEB_API_PREFIX)) {
            Map result = Maps.newHashMap();
            result.put("resultCode", "01");
            result.put("resultMsg", gaiaBizException.getMessage());
            return new ResponseEntity(result, HttpStatusCode.valueOf(500));
//            throw gaiaBizException;
        } else {
            HttpSession session = req.getSession();
            session.setAttribute("loginPageInfo", isSafe(returnUrl) ?  returnUrl : "");
            session.setAttribute("apiLoginPageInfo", isSafe(apiLoginPageInfo) ?  apiLoginPageInfo : "");
            session.setAttribute("errorMsg", gaiaBizException.getMessage());
            session.setAttribute("detailMsg", gaiaBizException.getDetailMessage());
            
            log.info("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
            log.info("오류가 발생했습니다. 커스텀 오류로! 지정한 오류 페이지로 이동합니다.");
            log.info("이동한 페이지는 {}입니다.", returnUrl);
            log.info("이동한 페이지는 {}입니다.", apiLoginPageInfo);
            log.info("오류 타입은 {} 입니다!!", gaiaBizException.getErrorType());
            log.info("오류 메시지(은)는 {} 입니다!!", gaiaBizException.getMessage());
            log.info("오류 상세메시지(은)는 {} 입니다!!", gaiaBizException.getDetailMessage());
            log.info("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
            return switch (gaiaBizException.getErrorType()) {
                case BAD_REQUEST 	-> "error/400";
                case UNAUTHORIZED 	-> "error/401";
                case FORBIDDEN 		-> "error/403";
                case ACCESS_DENIED 	-> "error/403";
                case NOT_FOUND 		-> "error/404";
                case NO_USER_DATA 	-> "request/use_request";
                case LOGIN_ERROR 	-> "redirect:" + returnUrl;
                default -> "error/error";
//                case BAD_REQUEST -> "redirect:/error/400";
//                case UNAUTHORIZED -> "redirect:/error/401";
//                case FORBIDDEN -> "redirect:/error/403";
//                case ACCESS_DENIED -> "redirect:/error/403";
//                case NOT_FOUND -> "redirect:/error/404";
//                case LOGIN_ERROR -> "redirect:" + loginPageInfo;
//                default -> "redirect:/error/error";
            };
        }
    }

    public String getLoginPage(HttpServletRequest request) {
        String redirectPath = "";
        // 현재 path를 가지고 로그인페이지로
        if (request.getQueryString() != null) {
            redirectPath = String.format("%s?%s", URLEncoder.encode(request.getRequestURI(), StandardCharsets.UTF_8), URLEncoder.encode(request.getQueryString(), StandardCharsets.UTF_8));
        }else {
            redirectPath = URLEncoder.encode(request.getRequestURI(), StandardCharsets.UTF_8);
        }

        if ("prod".equals(activeProfile)) {
            String returnUrl = "";
            redirectPath = String.format("?redirectPath=%s", redirectPath);

            // 250411 redirectPath가 mainPage 면 제외처리.
            if (redirectPath.contains("mainPage")) { redirectPath = ""; }

            log.debug("운영모드 O: redirectPath:: {}", redirectPath);

            // 250411 Platform - WBSGEN OR GAIA/CAIROS 분기처리
            String prodBranch = "PGAIA".equals(platform.toUpperCase()) ? "/ncpLogin" : "/linkLogin";

            returnUrl = properties.getGaia().getUrl().getPortalLogin() + prodBranch + redirectPath;

            if ("PGAIA".equals(platform.toUpperCase())) {
                returnUrl = String.format("https://www.pces.co.kr/pcespg-portal/main/login/doLogout.do?returnUrl=%s", returnUrl);
            }

            return returnUrl;
        } else {
            log.debug("운영모드 X redirectPath:: {}", redirectPath);
            return properties.getGaia().getUrl().getPortalLogin() + redirectPath;
        }
    }
    
    public String getApiLoginPage(HttpServletRequest request) {
        String currentPage = !StringUtils.isEmpty(request.getHeader("X-Current-Page")) ? request.getHeader("X-Current-Page") : "";
        if (activeProfile.contains("prod")) {
            String returnUrl = "";
            currentPage = !StringUtils.isEmpty(currentPage) ? String.format("?redirectPath=%s", currentPage) : "";
            // 250411 Platform - WBSGEN OR GAIA/CAIROS 분기처리
            String prodBranch = "PGAIA".equals(platform.toUpperCase()) ? "/ncpLogin" : "/linkLogin";

            returnUrl = properties.getGaia().getUrl().getPortalLogin() + prodBranch + currentPage;

            if ("PGAIA".equals(platform.toUpperCase())) {
                returnUrl = String.format("https://www.pces.co.kr/pcespg-portal/main/login/doLogout.do?returnUrl=%s", returnUrl);
            }

            return returnUrl;
        } else {
            return properties.getGaia().getUrl().getPortalLogin()+currentPage;
        }
    }


    public void handleAuthentication(HttpServletRequest request, HttpServletResponse response, Exception authException) {
        Object exResult = handleException(authException, new ServletWebRequest(request, response));

        try {
            if (exResult instanceof String string) {
                string = string.replaceAll("[\\r\\n]","");

                if (string.startsWith("redirect:")) {
                    for (String url : ALLOWED_REDIRECT_URL) {
                        String u = string.substring(9);

                        if (u.contains(url)) {
                            response.sendRedirect(u);
                            break;
                        }
                    }
                } else {
                    View view = pebbleViewResolver.resolveViewName(string, Locale.getDefault());
                    if (view != null) {
                        view.render(null, request, response);
                    }
                }
                return;
            } else if (exResult instanceof ResponseEntity entity) {
                response.setStatus(entity.getStatusCode().value());
                Object body = entity.getBody();
                if (body != null) {
                    ObjectMapper mapper = new ObjectMapper();
                    response.getWriter().write(mapper.writeValueAsString(body));
                }
                return;
            }

            response.setStatus(500);
            response.getWriter().write("{\"result\":\"nok\"}");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
