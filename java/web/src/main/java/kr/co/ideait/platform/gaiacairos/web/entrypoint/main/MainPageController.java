package kr.co.ideait.platform.gaiacairos.web.entrypoint.main;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.co.ideait.platform.gaiacairos.core.base.AbstractController;
import kr.co.ideait.platform.gaiacairos.core.config.security.SSOLoignTokenService;
import kr.co.ideait.platform.gaiacairos.core.util.CookieService;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.CommonReqVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.LocalTime;

@Slf4j
@Controller
public class MainPageController extends AbstractController {
	
	@Autowired
	SSOLoignTokenService loginToken;
	
    @GetMapping("/test")
    public String ssoLogin(CommonReqVo commonReqVo, HttpServletResponse response, HttpServletRequest request) throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, IOException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
    	
    	LocalTime time1 = LocalTime.of(0, 0, 0);
    	LocalTime time2 = LocalTime.of(9, 0, 0); // 12시간 23분 56초
    			
		//String token = loginToken.makeLoginTokenNew(exTime, "U202411001C07");
		
		String token = loginToken.makeLoginTokenOld("U202411001C07");
		
		log.debug("token : >>>>>>>>>>>>>>>>>>>>>> {}", token);
		
		//String cookieString = "X-AC-PLATFORM="+token+";max-age=2592000;path=/;SameSite=None;secure=true";
		
		//cookieService.setHttpOnlyCookie(response,"X-AC-PLATFORM", token, 60 * 30); //30분
		
		cookieService.setHttpOnlyCookie(response,"IDPLSSOID", token, 60 * 30); //30분
		
		//response.setHeader("Set-Cookie", cookieString);
    	
        return "temp/test";
    }

}
