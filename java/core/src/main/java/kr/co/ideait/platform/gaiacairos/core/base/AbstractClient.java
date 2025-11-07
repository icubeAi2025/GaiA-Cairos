package kr.co.ideait.platform.gaiacairos.core.base;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import kr.co.ideait.platform.gaiacairos.core.config.security.SSOLoignTokenService;
import kr.co.ideait.platform.gaiacairos.core.config.security.TokenService;
import kr.co.ideait.platform.gaiacairos.core.exception.ErrorType;
import kr.co.ideait.platform.gaiacairos.core.exception.GaiaBizException;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.cookie.CookieVO;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.security.User;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.security.UserAuth;
import kr.co.ideait.platform.gaiacairos.core.type.FileUploadType;
import kr.co.ideait.platform.gaiacairos.core.util.CookieService;
import kr.co.ideait.platform.gaiacairos.core.util.ThreadContextHelper;
import kr.co.ideait.platform.gaiacairos.core.util.restclient.RestClientUtil;
import org.apache.commons.collections.MapUtils;
import org.apache.tika.utils.StringUtils;
import org.jasypt.encryption.StringEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Component
public class AbstractClient {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    ThreadContextHelper threadContextHelper;

    @Autowired
    private StringEncryptor jasyptEncryptorAES;

    @Autowired
    private SSOLoignTokenService ssoLoignTokenService;

    @Autowired
    private TokenService tokenService;

    @Autowired
    protected CookieService cookieService;

    @Autowired
    protected RestClientUtil restClientUtil;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected CookieVO cookieVO;

    @Value("${spring.profiles.active}")
    protected String activeProfile;

    @Value("${platform}")
    protected String platform;

    private AbstractBase abstractBase;

    @PostConstruct
    public void init() {
        abstractBase = (AbstractBase) applicationContext.getBean("abstractBase");
    }

    public String generateSSOToken() {
        String accessToken = "";

        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if (servletRequestAttributes != null) {
            HttpServletRequest request = servletRequestAttributes.getRequest();

            Map<String, Object> userInfo = (Map<String, Object>) threadContextHelper.get("userInfo");

            if (userInfo == null) {
                throw new GaiaBizException(ErrorType.NO_DATA, "사용자 정보가 없습니다");
            }

            String usrId = MapUtils.getString(userInfo, "usr_id");
            String loginId = MapUtils.getString(userInfo, "login_id");

//            String token = ssoLoignTokenService.getNewToken(request);
//
//            if (StringUtils.isEmpty(token)) {
//                token = ssoLoignTokenService.getOldToken(request);
//
//                if (StringUtils.isEmpty(token)) {
//                    try {
//                        token = ssoLoignTokenService.makeLoginTokenOld(usrId);
//                    } catch (IOException e) {
//                        throw new GaiaBizException(e);
//                    } catch (InvalidAlgorithmParameterException e) {
//                        throw new GaiaBizException(e);
//                    } catch (NoSuchPaddingException e) {
//                        throw new GaiaBizException(e);
//                    } catch (IllegalBlockSizeException e) {
//                        throw new GaiaBizException(e);
//                    } catch (NoSuchAlgorithmException e) {
//                        throw new GaiaBizException(e);
//                    } catch (BadPaddingException e) {
//                        throw new GaiaBizException(e);
//                    } catch (InvalidKeyException e) {
//                        throw new GaiaBizException(e);
//                    }
//                }

                User.AccessAuthority authories = new User.AccessAuthority();
                authories.setUserType((String) userInfo.get("user_type"));
                List<User.AccessAuthority> accessAuthories = Arrays.asList(authories);
                accessToken = tokenService.generate(usrId, loginId, accessAuthories); //30분
//            }
        }

        return accessToken;
    }

    public String getUploadPathByWorkType(FileUploadType fileUploadType) {
        return abstractBase.getUploadPathByWorkType(fileUploadType);
    }
}
