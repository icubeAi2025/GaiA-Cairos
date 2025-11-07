package kr.co.ideait.platform.gaiacairos.web.config.request;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.io.IOException;

@Slf4j
@WebFilter(urlPatterns = {"/*"}, description = "wrapping request")
public class ContentCachingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
//        String uri = ((HttpServletRequest) request).getRequestURI().trim();

//        filterChain.doFilter(request, response);

//        if (isMultipartRequest(request)) {
//            CachingBodyRequestWrapper requestWrapper = new CachingBodyRequestWrapper(request);
//            filterChain.doFilter(requestWrapper, response);
//        } else {
            ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request);
            filterChain.doFilter(requestWrapper, response);
//        }
    }

    private boolean isMultipartRequest(HttpServletRequest request) {
        return request.getContentType() != null && request.getContentType().startsWith("multipart/");
    }
}
