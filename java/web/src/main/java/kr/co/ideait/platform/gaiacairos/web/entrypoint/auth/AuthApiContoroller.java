package kr.co.ideait.platform.gaiacairos.web.entrypoint.auth;

import io.micrometer.common.util.StringUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import kr.co.ideait.iframework.annotation.Description;
import kr.co.ideait.iframework.helper.CryptoHelper;
import kr.co.ideait.iframework.secu.Crypto;
import kr.co.ideait.platform.gaiacairos.core.base.AbstractController;
import kr.co.ideait.platform.gaiacairos.core.config.property.Properties;
import kr.co.ideait.platform.gaiacairos.core.config.security.annotation.IsUser;
import kr.co.ideait.platform.gaiacairos.core.exception.ErrorType;
import kr.co.ideait.platform.gaiacairos.core.exception.GaiaBizException;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.Result;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.CommonReqVo;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.security.AuthForm;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.security.User;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.security.UserAuth;
import kr.co.ideait.platform.gaiacairos.core.util.RedisUtil;
import kr.co.ideait.platform.gaiacairos.comp.auth.AuthComponent;
import kr.co.ideait.platform.gaiacairos.comp.project.service.ProjectService;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;

@Slf4j
@RestController
public class AuthApiContoroller extends AbstractController {

    @Autowired
	ProjectService projectService;

	@Autowired
	AuthComponent authComponent;
    
    @Autowired
	User user;
    
    @Autowired
	RedisUtil redisUtil;

    @Value("${spring.mail.username}")
    private String from;
    
    /**
     * 
     * 로그아웃 처리
     */
    @GetMapping("/api/logout")
	@Description(name = "로그아웃 처리", description = "로그아웃 처리한다.", type = Description.TYPE.MEHTOD)
    public Result logout(CommonReqVo commonReqVo, HttpServletRequest request, HttpServletResponse response) {
        log.debug("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
        log.debug("=                                      로그 아웃 합니다.                                     =");
        log.debug("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");  

		String currentMode;

		if (activeProfile.contains("prod")) currentMode = "prod";
		else currentMode = "dev";

		response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
		response.setHeader("Pragma", "no-cache");
		response.setDateHeader("Expires", 0);

        cookieService.deleteCookie(response, cookieVO.getTokenCookieName());
        cookieService.deleteCookie(response, cookieVO.getPortalCookieName());
        cookieService.deleteCookie(response, cookieVO.getSelectCookieName());
        
        SecurityContextHolder.clearContext();

        return Result.ok().put("currentMode", currentMode);
    }

	/**
	 *
	 * @return
	 */
	@GetMapping("/api/login/get-login-url")
	@Description(name = "login url 조회", description = "login url 조회", type = Description.TYPE.MEHTOD)
    public Result getLoginUrl(CommonReqVo commonReqVo) {
    	
        return Result.ok().put("loginUrl", properties.getGaia().getUrl().getPortalLogin());
    }
	
	/**
	 *
	 * @return
	 */
	@GetMapping("/api/access-Check")
	@Description(name = "리소스ID 권한 조회", description = "리소스Id로 권한여부를 조회한다.", type = Description.TYPE.MEHTOD)
	public Result getAccessCheck(CommonReqVo commonReqVo, @Valid AuthForm.AccessCheckForm accessCheckForm,
			HttpServletRequest request,
			@CookieValue(name = "lang", required = false) String langInfo) {
		
		return Result.ok().put("authYn", authComponent.getAccessIdCheck(accessCheckForm.getRescId()));
	}


	/**
	 * 20250414 호출부 없음. deprecated 추가
	 * @param userAuth
	 * @return
	 */
	@Deprecated
    @IsUser
    @GetMapping("/api/me")
	@Description(name = "사용자 조회", description = "사용자 정보와 계약정보를 조회한다.", type = Description.TYPE.MEHTOD)
    public Result getMe(CommonReqVo commonReqVo, UserAuth userAuth) throws Exception {

		User.SimpleUser simpleUser = user.toSimpleUser(userAuth);
		simpleUser.setLoginId(userAuth.getLogin_Id());

		String strCrypto = (String)redisUtil.getRedisValue(String.format("CRYPTO_%s", UserAuth.get(true).getUsrId()));

		if (StringUtils.isEmpty(strCrypto)) {
			throw new GaiaBizException(ErrorType.UNAUTHORIZED, "");
		}

		Crypto crypto = objectMapper.readValue(strCrypto, Crypto.class);
//		String encValue = CryptoHelper.encrypt(objectMapper.writeValueAsString(simpleUser), crypto.getSecretKey(), crypto.getSalt(), crypto.getIv(), crypto.getIterationCount(), crypto.getKeySize());
//		String decValue = CryptoHelper.decrypt(encValue, crypto.getSecretKey(), crypto.getSalt(), crypto.getIv(), crypto.getIterationCount(), crypto.getKeySize());
		String encValue = new String( CryptoHelper.encrypt( objectMapper.writeValueAsString(simpleUser).getBytes(StandardCharsets.UTF_8), crypto.getSecretKey().getBytes(StandardCharsets.UTF_8) ), StandardCharsets.UTF_8 );
		String decValue = new String( CryptoHelper.decrypt( encValue.getBytes(StandardCharsets.UTF_8), crypto.getSecretKey().getBytes(StandardCharsets.UTF_8) ), StandardCharsets.UTF_8 );
		log.info("encValue 	: {}", encValue);
		log.info("decValue 	: {}", decValue);

        return Result.ok().put("me", encValue );
    }
}
