package kr.co.ideait.platform.gaiacairos.core.persistence.vo.security;

import org.mapstruct.Mapper;

import jakarta.validation.constraints.NotBlank;
import kr.co.ideait.platform.gaiacairos.core.config.GlobalMapperConfig;
import lombok.Data;

@Mapper(config = GlobalMapperConfig.class)
public interface AuthForm {
	
	@Data
    class LoginCheck {
		String encId;
        String encPw;
        String loginType;
    }
	
	@Data
    class SendKey {
        String id;
        String loginType;
    }

    @Data
    class Login {
        String loginId;
        String loginPw;
        String key;
        String exKey;
        String loginType;
        String lastPage;
    }
    
    @Data
    class PortalLoginGet {
        String redirectUrl;
        String lastPage;
    }

    @Data
    class PortalLoginPost {
        @NotBlank
        String id;
        String loginType;
    }
    
	@Data
    class AccessCheckForm {
        String rescId;
    }
}
