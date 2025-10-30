package kr.co.ideait.platform.gaiacairos.core.persistence.vo.security;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ValidTokenDto {
	
	private boolean isValid = false;
    private String errorName = "";
    private UserAuth userAuth;

    @Builder(toBuilder = true)
    public ValidTokenDto(boolean isValid, String errorName, UserAuth userAuth) {
        this.isValid = isValid;
        this.errorName = errorName;
        this.userAuth = userAuth;
    }

}
