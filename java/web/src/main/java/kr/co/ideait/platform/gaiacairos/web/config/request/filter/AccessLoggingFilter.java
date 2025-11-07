package kr.co.ideait.platform.gaiacairos.web.config.request.filter;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

@Slf4j
public class AccessLoggingFilter extends OncePerRequestFilter {
    private StringBuffer stringBuffer = new StringBuffer();
    private List<String> headers = new ArrayList();
    private List<String> parameters = new ArrayList();
    private List<String> cookies = new ArrayList();

    private void logRequestInfo(HttpServletRequest request) {
        headers.clear();
        parameters.clear();
        cookies.clear();

        for (Enumeration<String> enumeration = request.getHeaderNames(); enumeration.hasMoreElements(); ) {
            String key = enumeration.nextElement();
            headers.add(String.format("%s=%s", key, request.getHeader(key)));
        }
        for (Enumeration<String> enumeration = request.getParameterNames(); enumeration.hasMoreElements(); ) {
            String key = enumeration.nextElement();
            parameters.add(String.format("%s=%s", key, request.getParameter(key)));
        }
//        for (Enumeration<String> enumeration = request.getHeaderNames(); enumeration.hasMoreElements(); ) {
//            String key = enumeration.nextElement();
//            cookies.add(String.format("%s=%s", key, request.getHeader(key)));
//        }


//            Parser uaParser = new Parser();
//            Client client = uaParser.parse(request.getHeader("User-Agent"));

        String ip = request.getHeader("X-FORWARDED-FOR");

        if (ip == null) {
            ip = request.getRemoteAddr();
        }

        stringBuffer.setLength(0);
        stringBuffer.append(String.format("%nRequestURI: %s ip:%s client:", request.getRequestURI(), ip));
        stringBuffer.append(String.format("%nHeaders: %s", String.join("&", headers)));
        stringBuffer.append(String.format("%nParameters: %s", String.join("&", parameters)));
        stringBuffer.append(String.format("%nCookies: %s", String.join("&", cookies)));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        logRequestInfo(request);
        filterChain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }
}
