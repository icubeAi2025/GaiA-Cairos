package kr.co.ideait.platform.gaiacairos.core.config.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import kr.co.ideait.platform.gaiacairos.core.config.property.Properties;
import kr.co.ideait.platform.gaiacairos.core.constant.KeyConstants;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.security.User;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.security.User.TokenUser;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.security.UserAuth;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.*;


@Slf4j
@Service
//public class TokenService implements InitializingBean {
public class TokenService {
    @Autowired
    kr.co.ideait.platform.gaiacairos.core.config.property.Properties properties;

    String issuer;
    private static final PrivateKey privateKey;
    private static final PublicKey publicKey;
    private static final String privateKeyFile;
    private static final String publicKeyFile;
    private static final long jwtExpiration;
    private static final long jwtRefreshExpiration;

    static {
        //파일로 읽어와서 암복호화 key 생성하는 방법
        String privateKeyContent = null;

        try {
            String env = System.getProperty("spring.profiles.active");
            String platform = System.getProperty("platform");

            java.util.Properties prop = Properties.getProp(String.format("application-%s-%s.properties", env, platform));

            String propPrivateKey = "gaia.secret.privateKey";
            String propPublicKey = "gaia.secret.publicKey";
            String propJwtExpiration = "gaia.secret.jwt-expiration";
            String propJwtRefreshExpiration = "gaia.secret.jwt-refresh-expiration";
            privateKeyFile = StringUtils.defaultString(System.getProperty(propPrivateKey), prop.getProperty(propPrivateKey));
            publicKeyFile = StringUtils.defaultString(System.getProperty(propPublicKey), prop.getProperty(propPublicKey));
            jwtExpiration = NumberUtils.toLong( StringUtils.defaultString(System.getProperty(propJwtExpiration), prop.getProperty(propJwtExpiration)) );
            jwtRefreshExpiration = NumberUtils.toLong( StringUtils.defaultString(System.getProperty(propJwtRefreshExpiration), prop.getProperty(propJwtRefreshExpiration)) );

            //            privateKeyContent = new String(Files.readAllBytes(Paths.get(new FileSystemResource(properties.getGaia().getSecret().getPrivateKey()).getURI())), Charset.defaultCharset());
//            String publicKeyContent = new String(Files.readAllBytes(Paths.get(new FileSystemResource(properties.getGaia().getSecret().getPublicKey()).getURI())), Charset.defaultCharset());
            privateKeyContent = new String(Files.readAllBytes(Paths.get(new FileSystemResource(privateKeyFile).getURI())), Charset.defaultCharset());
            String publicKeyContent = new String(Files.readAllBytes(Paths.get(new FileSystemResource(publicKeyFile).getURI())), Charset.defaultCharset());

            privateKeyContent = privateKeyContent.replaceAll("\\n", "").replace("-----BEGIN PRIVATE KEY-----", "").replace("-----END PRIVATE KEY-----", "");
            publicKeyContent = publicKeyContent.replaceAll("\\n", "").replace("-----BEGIN PUBLIC KEY-----", "").replace("-----END PUBLIC KEY-----", "");;

            KeyFactory kf = KeyFactory.getInstance("RSA");

            PKCS8EncodedKeySpec keySpecPKCS8 = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(privateKeyContent));
            privateKey = kf.generatePrivate(keySpecPKCS8);

            X509EncodedKeySpec keySpecX509 = new X509EncodedKeySpec(Base64.getDecoder().decode(publicKeyContent));
            publicKey = kf.generatePublic(keySpecX509);
        } catch (IOException | InvalidKeySpecException | NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

    }

    public String generate(String usrId, String loginId, List<User.AccessAuthority> authorities) {
    	toKey();
    	Map<String, Object> claims = Map.of(KeyConstants.TOKEN_ROLES, authorities.stream()
                .map(authority -> KeyConstants.ADMIN_CONTRACT_NO_HARDCODE.equals(authority.getUserType()) ? "2" : "1")
                .distinct().toList(),
                "id", usrId,
                "login_id", loginId);
        Date now = new Date();
//        Date exp = new Date(now.getTime() + properties.getGaia().getSecret().getJwtExpiration());
        Date exp = new Date(now.getTime() + jwtExpiration);

        return Jwts.builder()
                .claims(claims)
                .issuer(issuer)
                .issuedAt(now)
                .expiration(exp)
                .signWith(privateKey)
                .compact();
    }
    
    public String apiGenerate(String usrId, String loginId) {
    	toKey();
    	Map<String, Object> claims = Map.of(KeyConstants.TOKEN_ROLES, "1", "id", usrId, "login_id", loginId);
        Date now = new Date();
//        Date exp = new Date(now.getTime() + properties.getGaia().getSecret().getJwtExpiration());
        Date exp = new Date(now.getTime() + jwtExpiration);

        return Jwts.builder()
                .claims(claims)
                .issuer(issuer)
                .issuedAt(now)
                .expiration(exp)
                .signWith(privateKey)
                .compact();
    }
    
    public String refreshGenerate(String usrId, String loginId) {
    	toKey();
    	Map<String, Object> claims = Map.of("id", usrId, "login_id", loginId);
        Date now = new Date();
//        Date exp = new Date(now.getTime() + properties.getGaia().getSecret().getJwtRefreshExpiration());
        Date exp = new Date(now.getTime() + jwtRefreshExpiration);

        return Jwts.builder()
                .claims(claims)
                .issuer(issuer)
                .issuedAt(now)
                .expiration(exp)
                .signWith(privateKey)
                .compact();
    }

    public UserAuth parse(String token) {
    	toKey();
        Map<String, Object> claims = Jwts.parser()
                .requireIssuer(issuer)
                .verifyWith(publicKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return UserAuth.of(TokenUser.of(claims));
    }
    
    /**
     * 'JWT' 내에서 'Claims' 정보를 반환하는 메서드
     *
     * @param token : 토큰
     * @return Claims : Claims
     */

    public Claims getTokenToClaims(String token) {
    	//toKey();
        return Jwts.parser()
                .requireIssuer(issuer)
                .verifyWith(publicKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public void toKey() {
    	log.debug("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
    	log.debug("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
    	log.debug("privateKey : >>>>>>>>>> 	" + privateKey);
    	log.debug("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
    	log.debug("publicKey : >>>>>>>>>> 	" + publicKey);
    	log.debug("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
    	log.debug("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
    }
}
