package kr.co.ideait.platform.gaiacairos.web.config.request.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.co.ideait.iframework.CommonTraceConstants;
import kr.co.ideait.iframework.helper.CryptoHelper;
import kr.co.ideait.iframework.secu.Crypto;
import kr.co.ideait.platform.gaiacairos.comp.portal.PortalComponent;
import kr.co.ideait.platform.gaiacairos.core.config.property.EurecaProp;
import kr.co.ideait.platform.gaiacairos.core.constant.PlatformConstants;
import kr.co.ideait.platform.gaiacairos.core.exception.ErrorType;
import kr.co.ideait.platform.gaiacairos.core.exception.GaiaBizException;
import kr.co.ideait.platform.gaiacairos.core.persistence.Device;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.CommonReqVo;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.cookie.CookieVO;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.security.User;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.security.UserAuth;
import kr.co.ideait.platform.gaiacairos.core.util.CookieService;
import kr.co.ideait.platform.gaiacairos.core.util.RedisUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.basjes.parse.useragent.UserAgent;
import nl.basjes.parse.useragent.UserAgentAnalyzer;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;

@Slf4j
@RequiredArgsConstructor
public class PlatformCommonInterceptor implements HandlerInterceptor {

    @Autowired
    CookieService cookieService;

    @Autowired
    PortalComponent portalComponent;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    RedisUtil redisUtil;

    @Autowired
    EurecaProp eurecaProp;

    @Autowired
    User user;

    @Value("${spring.profiles.active}")
    String profile;

    @Value("${platform}")
    String platform;

    @Value("${report.viewerUrl}")
    String reportViewerUrl;

    @Value("${report.exportUrl}")
    String reportExportUrl;

    CookieVO cookieVO;

    UserAgentAnalyzer uaa = UserAgentAnalyzer
            .newBuilder()
            .hideMatcherLoadStats()
            .withCache(10000)
            .build();

    @PostConstruct
    public void init(){
        cookieVO = new CookieVO(platform.toUpperCase());
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String url = StringUtils.defaultString(request.getRequestURI(), "");
        String domain = request.getServerName();

        if ("dev".equals(profile) && NumberUtils.isDigits(domain.replaceAll("\\D", ""))) {
//        if (NumberUtils.isDigits(domain.replaceAll("\\D", ""))) {
            throw new GaiaBizException(ErrorType.ACCESS_DENIED, "domain으로 접속하세요.");
        }

        UserAgent userAgent = uaa.parse(request.getHeader("User-Agent"));
        Device device = Device.build(userAgent.toMap(), objectMapper);

//        log.info("current device: {}", device);

        CommonReqVo commonReqVo = (CommonReqVo)request.getAttribute(CommonTraceConstants.REQID.REQUEST_COMMON_HEADER.name());

        if (commonReqVo != null) {
            commonReqVo.setDevice(device);
        }

        boolean isAjax = request.getHeader("X-Requested-With") != null;
        if (!isAjax && !"/".equals(url) && Device.DEVICE_MOBILE.equals(device.getOperatingSystemClass()) && request.getAttribute(PlatformConstants.IS_MO_REDIRECT) == null) {
//            request.setAttribute(PlatformConstants.IS_MO_REDIRECT, Boolean.TRUE);
//            response.sendRedirect("/");
//            return true;
        }

        return HandlerInterceptor.super.preHandle(request, response, handler);
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        String url = request.getRequestURI();

        if ( !(url.contains("/linkLogin") || url.contains("/ncpLogin")) ) {
            if (modelAndView != null) {
                modelAndView.getModelMap().addAttribute("title", StringUtils.upperCase("pgaia".equals(platform) ? "GAIA" : platform) );
                modelAndView.getModelMap().addAttribute("envMode", profile);
                modelAndView.getModelMap().addAttribute("platform", "pgaia".equals(platform) ? "gaia" : platform);
                modelAndView.getModelMap().addAttribute("gaia", "GAIA");
                modelAndView.getModelMap().addAttribute("cairos", "CAIROS");  // TODO 영향성 분석 후, CAIROS 변경 필요 .
                modelAndView.getModelMap().addAttribute("REPORT_VIEWER_URL", reportViewerUrl);
                modelAndView.getModelMap().addAttribute("REPORT_EXPORT_URL", reportExportUrl);

                if (
                    url.contains("/project/contractstatus/bid") // 사업관리 > 계약관리 > 계약 현황 > 계약내역서 보기
                    || url.contains("/projectcost/contract") // 사업비관리 > 공사비 관리
                    || url.contains("/projectcost/payment/history") // 사업비관리 > 기성관리 > 기성내역서
                ) {
                    modelAndView.getModelMap().addAttribute("eurecaDomain", eurecaProp.getDomain());
                    modelAndView.getModelMap().addAttribute("eurecaPaymentListView", eurecaProp.getPaymentListView());
                    modelAndView.getModelMap().addAttribute("eurecaContractListView", eurecaProp.getContractListView());
                }

                String strCrypto = (String)redisUtil.getRedisValue(String.format("CRYPTO_%s", UserAuth.get(true).getUsrId()));

                if (StringUtils.isNotBlank(strCrypto)) {
                    Crypto crypto = objectMapper.readValue(strCrypto, Crypto.class);
                    modelAndView.getModelMap().addAttribute("crypto", crypto);
                }

                UserAuth userAuth = UserAuth.get(true);

                if (userAuth != null) {
                    String cookieVal = cookieService.getCookie(request, cookieVO.getPortalCookieName());
                    String[] portalCookie = StringUtils.defaultString(cookieVal, "").split(":");

                    User.SimpleUser simpleUser = user.toSimpleUser(userAuth);

                    log.info("portalCookie: {} {}", portalCookie, portalCookie.length);

                    if (portalCookie.length == 4 && StringUtils.isEmpty(simpleUser.getName())) {
                        simpleUser.setName(portalCookie[4]);
                    }

                    simpleUser.setLoginId(userAuth.getLogin_Id());
                    simpleUser.setProjects( portalComponent.loginUserProjectList() );

                    Crypto crypto = objectMapper.readValue(strCrypto, Crypto.class);
        //		String encValue = CryptoHelper.encrypt(objectMapper.writeValueAsString(simpleUser), crypto.getSecretKey(), crypto.getSalt(), crypto.getIv(), crypto.getIterationCount(), crypto.getKeySize());
        //		String decValue = CryptoHelper.decrypt(encValue, crypto.getSecretKey(), crypto.getSalt(), crypto.getIv(), crypto.getIterationCount(), crypto.getKeySize());
                    String encValue = new String( CryptoHelper.encrypt( objectMapper.writeValueAsString(simpleUser).getBytes(StandardCharsets.UTF_8), crypto.getSecretKey().getBytes(StandardCharsets.UTF_8) ), StandardCharsets.UTF_8 );
    //                String decValue = new String( CryptoHelper.decrypt( encValue.getBytes(), crypto.getSecretKey().getBytes() ) );
    //                log.info("encValue 	: {}", encValue);
    //                log.info("decValue 	: {}", decValue);

                    modelAndView.getModelMap().addAttribute("me", encValue);
                }
            }
        }

        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }
}
