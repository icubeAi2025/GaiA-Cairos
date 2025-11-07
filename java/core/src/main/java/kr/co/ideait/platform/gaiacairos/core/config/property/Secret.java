package kr.co.ideait.platform.gaiacairos.core.config.property;

import lombok.Data;

@Data
public class Secret {

    String jwt;
    Integer jwtExpiration;
    Integer jwtRefreshExpiration;
    String privateKey;
    String publicKey;
    String jwtKeyPlatform;
    String ssokey;
}
