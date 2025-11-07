package kr.co.ideait.platform.gaiacairos.web.config.request.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.co.ideait.iframework.CommonTraceConstants;
import kr.co.ideait.iframework.ReqUtil;
import kr.co.ideait.iframework.ThreadContext;
import kr.co.ideait.platform.gaiacairos.core.config.security.TokenService;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.CommonReqVo;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.cookie.CookieVO;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.security.UserAuth;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.log.Log;
import kr.co.ideait.platform.gaiacairos.core.util.CookieService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Component;
import org.springframework.util.PatternMatchUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.pattern.PathPatternParser;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Slf4j
@Component
@WebFilter(urlPatterns = {"/interface/*"}, description = "wrapping request")
public class InterfaceAuthFilter extends OncePerRequestFilter {

    private HttpSessionSecurityContextRepository securityContextRepository = new HttpSessionSecurityContextRepository();

    @Autowired
    ThreadContext threadContext;

    @Autowired
    CookieService cookieService;

    @Autowired
    TokenService tokenService;

    private CookieVO cookieVO;

    PathPatternParser pathPatternParser = new PathPatternParser();

    private static final String[] BLACKLIST_URIS = {
        "/interface/*",
//        "/webApi/*",
    };

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain chain) throws ServletException, IOException {
        String uri = request.getRequestURI();

        if (!PatternMatchUtils.simpleMatch(BLACKLIST_URIS, uri)) {
            chain.doFilter(request, response);
            return;     // 종료
        }

//        String contentType = request.getContentType();
        String accessToken = StringUtils.defaultString(request.getHeader("x-auth"), cookieService.getCookie(request, "x-auth"));

        if (StringUtils.isEmpty(accessToken)) {
            accessToken = request.getParameter("x-auth");
        }

        UserAuth userAuthentication = tokenService.parse(accessToken);

        SecurityContextHolder.getContext().setAuthentication(userAuthentication);

        SecurityContext context = SecurityContextHolder.getContext();
        securityContextRepository.saveContext(context, request, response);

        CommonReqVo commonReqVo = new CommonReqVo();
//        commonReqVo.setPlatform(platform);
//        commonReqVo.setTrnId(transactionId);
        commonReqVo.setUserIp(ReqUtil.getClientIP(request));
        commonReqVo.setReferer(request.getHeader("referer"));
        commonReqVo.setUserAgent(request.getHeader("user-agent"));
//        commonReqVo.setReqHeader( ReqUtil.mapToQueryString( ReqUtil.makeHeaderMap(request) ) );
//        commonReqVo.setReqHeader( objectMapper.writeValueAsString( ReqUtil.makeHeaderMap(request) ) );
//        commonReqVo.setReqData( objectMapper.writeValueAsString( request.getParameterMap() ) );
//        commonReqVo.setSrvMode(profile);
        commonReqVo.setReqDt(LocalDateTime.now(ZoneId.of("Asia/Seoul")).format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));

        request.setAttribute(CommonTraceConstants.REQID.REQUEST_COMMON_HEADER.name(), commonReqVo);
        threadContext.set(commonReqVo.toSmUserLogDto());

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }
}
