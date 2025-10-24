package kr.co.ideait.platform.gaiacairos.core.config.security;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import kr.co.ideait.platform.gaiacairos.core.exception.GaiaBizException;
import kr.co.ideait.platform.gaiacairos.core.util.CookieService;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import kr.co.ideait.platform.gaiacairos.core.config.property.Properties;
import kr.co.ideait.platform.gaiacairos.core.constant.KeyConstants;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * Modification Information
 * 2024 11 21
 */
@Slf4j
@Service
public class SSOLoignTokenService {
	
	@Autowired
    Properties properties;

	private SecretKey secretKey = null;
	private final static long LIMIT_MILLIS 	= 1000 * 60 * 60 * 9;// 로그인 9 시간 이후 다시 로그인 하도록
	private byte[] ssoKeyBytes 			= null;

	@Autowired
	CookieService cookieService;
	/**
     * 1. 쿠키값 "X-AC-PLATFORM"을 가져옵니다.
     * 2. 쿠키값이 없으면 헤더값 "X-AC-PLATFORM"을 가져옵니다.
     */
	public String getNCPToken(HttpServletRequest request) {
//        Cookie[] cookies = request.getCookies();
//        if (cookies != null) {
//            for (Cookie cookie : cookies) {
//                if (KeyConstants.AUTH_KEY_NCP.equals(cookie.getName())) {
//					return cookie.getValue();
//                }
//            }
//        }
//		return request.getHeader(KeyConstants.AUTH_KEY_NCP);
		return cookieService.getCookie(request,KeyConstants.AUTH_KEY_NCP);
    }
	
	/**
     * 1. 쿠키값 "X-AC-PLATFORM"을 가져옵니다.
     * 2. 쿠키값이 없으면 헤더값 "X-AC-PLATFORM"을 가져옵니다.
     */
	public String getNewToken(HttpServletRequest request) {
//        Cookie[] cookies = request.getCookies();
//        if (cookies != null) {
//            for (Cookie cookie : cookies) {
//                if (KeyConstants.AUTH_KEY_NEW.equals(cookie.getName())) {
//					return cookie.getValue();
//                }
//            }
//        }
//		return request.getHeader(KeyConstants.AUTH_KEY_NEW);
		return cookieService.getCookie(request,KeyConstants.AUTH_KEY_NEW);
    }
	
	/**
     * 1. 쿠키값 "IDPLSSOID"을 가져옵니다.
     * 2. 쿠키값이 없으면 헤더값 "IDPLSSOID"을 가져옵니다.
     */
	public String getOldToken(HttpServletRequest request) {
//        Cookie[] cookies = request.getCookies();
//        if (cookies != null) {
//            for (Cookie cookie : cookies) {
//                if (KeyConstants.AUTH_KEY_OLD.equals(cookie.getName())) {
//					return cookie.getValue();
//                }
//            }
//        }
//		return request.getHeader(KeyConstants.AUTH_KEY_OLD);
		return cookieService.getCookie(request,KeyConstants.AUTH_KEY_OLD);
    }

	/////////////// tobe 로그인 부분
	
	/**
	 * 로그인 후 쿠키에 넣을 토큰 만들기. tobe 버전
	 */
	public String makeLoginTokenNew(Duration durExp, String userId) {
		Map payload = Map.of("uid", userId, "type", "login");
		Date now = new Date();
		JwtBuilder jwtBuilder = Jwts.builder();
		jwtBuilder.claims(payload);// 위치 중요
		if(true) {
			jwtBuilder.issuedAt(now);
		}
		jwtBuilder.expiration(new Date(now.getTime() + durExp.toMillis()));
		jwtBuilder.signWith(getSecretKey(), Jwts.SIG.HS256);
		return jwtBuilder.compact();
	}

	public String getUserIdNew(String token) {
		String result = null;

		Jws<Claims> jws = Jwts.parser()
				.verifyWith(getSecretKey())
				.build()
				.parseSignedClaims(token);
		Map payload = jws.getPayload();

		if(payload != null) {
			log.debug("payload 		: >>>>>>> {}", payload);
			log.debug("payload.type : >>>>>>> {}", payload.get("type"));
			log.debug("payload.uid 	: >>>>>>> {}",  payload.get("uid"));
			log.debug("payload.iat 	: >>>>>>> {}", payload.get("iat"));
			log.debug("payload.exp 	: >>>>>>> {}", payload.get("exp"));
			// payload 검증
			if (!"login".equals(payload.get("type")))return null;
			if(ObjectUtils.isEmpty(payload.get("uid")))return null;
			if(ObjectUtils.isEmpty(payload.get("iat")))return null;
			if(ObjectUtils.isEmpty(payload.get("exp")))return null;

			result = (String)payload.get("uid");
		}
		return result;
	}

	private SecretKey getSecretKey(){
		String jwtKeyPlatform = properties.getGaia().getSecret().getJwtKeyPlatform();
		
		log.debug("jwtKeyPlatform : >>>>>>> {}", jwtKeyPlatform);

		if (secretKey == null) {
			try {
				this.secretKey = readSecretKeyFromFile(new File(jwtKeyPlatform));
			} catch (IOException e) {
				throw new GaiaBizException(e);
			}
		}

		log.debug("secretKey : >>>>>>> {}", secretKey);
		return secretKey;
	}

	private SecretKey readSecretKeyFromFile(File secKeyFile) throws IOException {
		String base64 = FileUtils.readFileToString(secKeyFile, StandardCharsets.UTF_8);
		byte[] byteArr = Base64.getDecoder().decode(base64);
		return Keys.hmacShaKeyFor(byteArr);
	}

	////////////////// 기존 로그인 부분
	
	/**
	 * 로그인 후 쿠키에 넣을 토큰 만들기. old 버전
	 */
	public String makeLoginTokenOld(String userID) throws IOException, InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
		if(StringUtils.isBlank(userID)){
			throw new IllegalArgumentException("ID is black");
		}
		long currmillis = System.currentTimeMillis();

		String originalText = currmillis+"<>"+userID;
		byte[] originalBytes = originalText.getBytes("utf-8");
		byte[] encBytes = encryptAES_CBC_PKCS5Padding(getKeyBytes(), originalBytes);
		String ssoToken = Hex.encodeHexString(encBytes);
		return ssoToken;
	}

	public String getUserIdOld(String loginToken) {
		String result = null;

		try{
			if (StringUtils.isEmpty(loginToken)) {
				loginToken = "";
			}

			byte[] encBytes = Hex.decodeHex(loginToken.toCharArray());
			byte[] decBytes = decryptAES_CBC_PKCS5Padding(getKeyBytes(), encBytes);
			String decrypted = new String(decBytes, "UTF-8");
			String[] ssoValues = decrypted.split("<>");
			if(ssoValues == null || ssoValues.length < 2)return null;
			String loginMillis = ssoValues[0];
			String ssoUserID = ssoValues[1];
			long elapsed = System.currentTimeMillis() - Long.parseLong(loginMillis);
			if(elapsed > LIMIT_MILLIS) return null;
			result = ssoUserID;
		}catch(GaiaBizException | IOException | DecoderException | NoSuchPaddingException | NoSuchAlgorithmException |
               InvalidAlgorithmParameterException | InvalidKeyException | IllegalBlockSizeException |
               BadPaddingException e){
			log.error("Exception", e);
			result = null;
        }

        return result;
	}

	private byte[] getKeyBytes() throws IOException{
		String ssokey = properties.getGaia().getSecret().getSsokey();
		
		if(ssoKeyBytes == null) {
			String ssoValue = FileUtils.readFileToString(new File(ssokey), Charset.defaultCharset());
			ssoKeyBytes = StringUtils.defaultString(ssoValue).getBytes(Charset.defaultCharset());
		}
		return ssoKeyBytes;
	}

	private static strictfp byte[] decryptAES_CBC_PKCS5Padding(byte[] keyBytes, byte[] encryptedBytes) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		keyBytes = Arrays.copyOf(keyBytes, 16);//키 사이즈 맞춤 128/8
		SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");
		IvParameterSpec ivSpec = new IvParameterSpec(Arrays.copyOf(keyBytes, 128/8));//16
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");//
		cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
		return cipher.doFinal(encryptedBytes);
	}

	private static byte[] encryptAES_CBC_PKCS5Padding(byte[] keyBytes, byte[] originalBytes) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		keyBytes = Arrays.copyOf(keyBytes, 16);//키 사이즈 맞춤 128/8
		SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");
		IvParameterSpec ivSpec = new IvParameterSpec(Arrays.copyOf(keyBytes, 16));//16
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");//
		cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
		return cipher.doFinal(originalBytes);
	}

	/////////////// 유틸


//	private void removeCookie(HttpServletRequest request, HttpServletResponse response, String cookieName) {
//		String topDomain = extractTopDomain(request.getServerName());
//		// 쿠키값 없을 때는 도메인 쿠키랑 안도메인 쿠키랑 모두 삭제
//		Cookie noDomainCookie = new Cookie(cookieName, null);
//		noDomainCookie.setPath("/");
//		noDomainCookie.setHttpOnly(true);
//		noDomainCookie.setMaxAge(0);// 쿠키 삭제
//		//noDomainCookie.setSecure(secureFlag);
//		response.addCookie(noDomainCookie);
//		if (StringUtils.isNotEmpty(topDomain)) {
//			Cookie domainCookie = new Cookie(cookieName, null);
//			domainCookie.setDomain(topDomain);
//			domainCookie.setPath("/");
//			domainCookie.setHttpOnly(true);
//			domainCookie.setMaxAge(0);// 쿠키 삭제
//			//domainCookie.setSecure(secureFlag);
//			response.addCookie(domainCookie);
//		}
//	}

//	private final Set<String> TOP_LEVEL_DOMAINS = new HashSet<>(Arrays.asList(
//			"com", "co",
//			"org", "or",
//			"gov", "go",
//			"net", "edu", "ai",
//			"kr", "jp", "uk"));

	/**
	 * 메인 도메인 추출하기
	 */
//	private String extractTopDomain(String domain) {
//		String[] parts = domain.split("\\.");
//		int length = parts.length;
//		if (length < 2) {
//			return domain; // 도메인 부분이 너무 짧은 경우 그대로 반환
//		}
//		if (length > 2 && TOP_LEVEL_DOMAINS.contains(parts[length - 2])) {
//			return parts[length - 3] + "." + parts[length - 2] + "." + parts[length - 1];
//		}
//		return parts[length - 2] + "." + parts[length - 1];
//	}

}
