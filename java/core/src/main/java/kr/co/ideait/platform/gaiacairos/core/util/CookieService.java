package kr.co.ideait.platform.gaiacairos.core.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.co.ideait.platform.gaiacairos.core.config.property.Properties;

@Service
public class CookieService {

    @Autowired
    Properties properties;

    public void setHttpOnlyCookie(HttpServletResponse response, String key, String value, int sec) {
        response.addCookie(genCookie(key, value, true, sec));
    }

    public void setCookie(HttpServletResponse response, String key, String value, boolean httpOnly, int sec) {
        response.addCookie(genCookie(key, value, httpOnly, sec));
    }
    
    public void setHttpOnlyCookie(HttpServletResponse response, String key, String value) {
        response.addCookie(genCookie(key, value, true));
    }

    public void setCookie(HttpServletResponse response, String key, String value, boolean httpOnly) {
        response.addCookie(genCookie(key, value, httpOnly));
    }

    public Cookie genCookie(String key, String value, boolean httpOnly, int sec) {
        Cookie cookie = new Cookie(key, value);
        cookie.setHttpOnly(httpOnly);
        cookie.setSecure(properties.getGaia().isHttps());
        cookie.setPath("/");
        cookie.setMaxAge(sec);
        return cookie;
    }
    
    public Cookie genCookie(String key, String value, boolean httpOnly) {
        Cookie cookie = new Cookie(key, value);
        cookie.setHttpOnly(httpOnly);
        cookie.setSecure(properties.getGaia().isHttps());
        cookie.setPath("/");
        return cookie;
    }

    public void deleteCookie(HttpServletResponse response, String key) {
        deleteCookie(response, key, true);
    }

    /**
     * 브라우저는 쿠키를 삭제할 때,
     * 기존에 설정된 쿠키와 Path, HttpOnly, Secure 값이 동일한 경우에만 덮어씁니다.
     *
     * @param response
     * @param key
     * @param httpOnly
     */
    public void deleteCookie(HttpServletResponse response, String key, boolean httpOnly) {
        Cookie cookie = new Cookie(key, null);

        if (httpOnly) { cookie.setHttpOnly(true); }

        cookie.setSecure(properties.getGaia().isHttps());
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }
    
    public ResponseEntity<String> deleteCookieWithCacheHeaders(HttpServletResponse response, String key) {
        Cookie cookie = new Cookie(key, null);
        cookie.setMaxAge(0); // 쿠키 만료
        cookie.setPath("/"); // 경로 설정
        response.addCookie(cookie);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
        headers.add("Pragma", "no-cache");
        headers.add("Expires", "0");

        return new ResponseEntity<>("Cookie deleted and cache disabled", headers, HttpStatus.OK);
    }
    
    /**
     * 1. cookieNam쿠키의 쿠키값 가져옵니다.
     * 2. 쿠키값이 없으면 헤더 cookieNam 값 가져옵니다.
     */
	public String getCookie(HttpServletRequest request, String cookieName) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookieName.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        String header = request.getHeader(cookieName);
        return header != null ? header : null;
    }
}
