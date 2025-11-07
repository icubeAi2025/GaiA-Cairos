package kr.co.ideait.platform.gaiacairos.web.config.request.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import kr.co.ideait.platform.gaiacairos.web.config.request.XssFilterRequestBodyWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.NonNull;
import org.springframework.util.PatternMatchUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Slf4j
@WebFilter(urlPatterns = {"/*"}, description = "wrapping request")
public class XssFilter extends OncePerRequestFilter {

    private final static String[] acceptUrls = {
        "/assets"
        , "/webApi/retrieveApi"
        , "/api/util/*"
        , "/api/portal/nav-menu"
        , "/api/eapproval/approval/approval-details"
        , "/api/eapproval/draft/select-draft"
        , "/api/construction/dailyreport/make-commonBox"
        , "/api/projectcost/get/payprceTmnum"
        , "/api/document/gdoc/request"
        , "/api/document/add-item/html/create"
        , "/api/system/document/html/*"
    };

    public static boolean excludeUrl(ServletRequest request) {
        String uri = ((HttpServletRequest) request).getRequestURI().trim();
        return PatternMatchUtils.simpleMatch(acceptUrls, uri);
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain chain) throws ServletException, IOException {
        String contentType = request.getContentType();
        if (excludeUrl(request)) {
            chain.doFilter(request, response);
        } else if (StringUtils.contains(contentType, "application/json") || StringUtils.contains(contentType, "multipart/form-data")) {
            chain.doFilter(new XssFilterRequestBodyWrapper(request), response);
//            chain.doFilter(request, response);
        } else {
            chain.doFilter(new XssServletWrapper(request), response);
        }
    }

    @Override
    public void destroy() {
    }

    public class XssServletWrapper extends HttpServletRequestWrapper {

        private byte[] rawData;

        public XssServletWrapper(HttpServletRequest request) {
            super(request);
            try {
                //reqeust를 inputStream으로 바이트 변환하여 XSS 필터링
                if("post".equalsIgnoreCase(request.getMethod()) && ("application/json".equals(request.getContentType()) || "multipart/form-data".equals(request.getContentType()))) {
                    InputStream is = request.getInputStream();
                    this.rawData = replaceXSS(IOUtils.toByteArray(is));
                }
            } catch (IOException e) {
                log.error("XssServletWrapper error : {}", e.getMessage());
            }
        }

        //바이트 단위 script 태그 치환
        private byte[] replaceXSS(byte[] data) {
            String strData = new String(data, StandardCharsets.UTF_8);
            strData = strData.replaceAll("<", "&lt;")
                    .replaceAll(">", "&gt;")
                    .replaceAll("\\(", "&#40;")
                    .replaceAll("\\)", "&#41;");

            return strData.getBytes(StandardCharsets.UTF_8);
        }

        //문자열 단위 script 태그 치환
        private String replaceXSS(String value) {
            if(value != null) {
                value = value.replaceAll("<", "&lt;")
                        .replaceAll(">", "&gt;")
                        .replaceAll("\\(", "&#40;")
                        .replaceAll("\\)", "&#41;");
            }
            return value;
        }


        @Override
        public ServletInputStream getInputStream() throws IOException {
            if(this.rawData == null) {
                return super.getInputStream();
            }
            final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(this.rawData);

            return new ServletInputStream() {

                @Override
                public int read() throws IOException {
                    // TODO Auto-generated method stub
                    return byteArrayInputStream.read();
                }

                @Override
                public void setReadListener(ReadListener readListener) {
                    // TODO Auto-generated method stub
                }

                @Override
                public boolean isReady() {
                    // TODO Auto-generated method stub
                    return false;
                }

                @Override
                public boolean isFinished() {
                    // TODO Auto-generated method stub
                    return false;
                }
            };
        }

        @Override
        public String getQueryString() {
            return replaceXSS(super.getQueryString());
        }

        @Override
        public String getParameter(String name) {
            return replaceXSS(super.getParameter(name));
        }

        @Override
        public Map<String, String[]> getParameterMap() {
            Map<String, String[]> params = super.getParameterMap();
            if(params != null) {
                params.forEach((key, value) -> {
                    for(int i=0; i<value.length; i++) {
                        value[i] = replaceXSS(value[i]);
                    }
                });
            }
            return params;
        }

        @Override
        public String[] getParameterValues(String name) {
            String[] params = super.getParameterValues(name);
            if(params != null) {
                for(int i=0; i<params.length; i++) {
                    params[i] = replaceXSS(params[i]);
                }
            }
            return params;
        }
    }
}