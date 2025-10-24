package kr.co.ideait.platform.gaiacairos.core.config.security;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.crypto.SecretKey;

import jakarta.annotation.PostConstruct;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.security.User;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.security.UserAuth;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.security.User.TokenUser;
import kr.co.ideait.platform.gaiacairos.core.config.property.Properties;
import kr.co.ideait.platform.gaiacairos.core.constant.KeyConstants;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Service
//public class TokenService implements InitializingBean {
public class TokenService {

    @Autowired
    Properties properties;

    String issuer;
    private PrivateKey privateKey;
    private PublicKey publicKey;


    @PostConstruct
    public void init() throws Exception {
        //파일로 읽어와서 암복호화 key 생성하는 방법
        String privateKeyContent = new String(Files.readAllBytes(Paths.get(new FileSystemResource(properties.getGaia().getSecret().getPrivateKey()).getURI())), Charset.defaultCharset());
        String publicKeyContent = new String(Files.readAllBytes(Paths.get(new FileSystemResource(properties.getGaia().getSecret().getPublicKey()).getURI())), Charset.defaultCharset());

//        log.debug(privateKeyContent);
//        log.debug(publicKeyContent);

        privateKeyContent = privateKeyContent.replaceAll("\\n", "").replace("-----BEGIN PRIVATE KEY-----", "").replace("-----END PRIVATE KEY-----", "");
        publicKeyContent = publicKeyContent.replaceAll("\\n", "").replace("-----BEGIN PUBLIC KEY-----", "").replace("-----END PUBLIC KEY-----", "");;

        KeyFactory kf = KeyFactory.getInstance("RSA");

        PKCS8EncodedKeySpec keySpecPKCS8 = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(privateKeyContent));
        privateKey = kf.generatePrivate(keySpecPKCS8);

        X509EncodedKeySpec keySpecX509 = new X509EncodedKeySpec(Base64.getDecoder().decode(publicKeyContent));
        publicKey = kf.generatePublic(keySpecX509);
    }

    public String generate(String usrId, String loginId, List<User.AccessAuthority> authorities) {
    	toKey();
    	Map<String, Object> claims = Map.of(KeyConstants.TOKEN_ROLES, authorities.stream()
                .map(authority -> KeyConstants.ADMIN_CONTRACT_NO_HARDCODE.equals(authority.getUserType()) ? "2" : "1")
                .distinct().toList(),
                "id", usrId,
                "login_id", loginId);
        Date now = new Date();
        Date exp = new Date(now.getTime() + properties.getGaia().getSecret().getJwtExpiration());

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
        Date exp = new Date(now.getTime() + properties.getGaia().getSecret().getJwtExpiration());

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
        Date exp = new Date(now.getTime() + properties.getGaia().getSecret().getJwtRefreshExpiration());

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

//    @Override
//    public void afterPropertiesSet() throws Exception {
////        issuer = properties.getApp().getName().toString();
////        key = Jwts.SIG.HS256.key().build();
////
////        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
////        generator.initialize(2048);
////        KeyPair pair = generator.generateKeyPair();
//
//        //파일로 읽어와서 암복호화 key 생성하는 방법
//        String privateKeyContent = new String(Files.readAllBytes(Paths.get(new FileSystemResource(properties.getGaia().getSecret().getPrivateKey()).getURI())), Charset.defaultCharset());
//        String publicKeyContent = new String(Files.readAllBytes(Paths.get(new FileSystemResource(properties.getGaia().getSecret().getPublicKey()).getURI())), Charset.defaultCharset());
//
//        log.debug(privateKeyContent);
//        log.debug(publicKeyContent);
//
//        privateKeyContent = privateKeyContent.replaceAll("\\n", "").replace("-----BEGIN PRIVATE KEY-----", "").replace("-----END PRIVATE KEY-----", "");
//        publicKeyContent = publicKeyContent.replaceAll("\\n", "").replace("-----BEGIN PUBLIC KEY-----", "").replace("-----END PUBLIC KEY-----", "");;
//
//        KeyFactory kf = KeyFactory.getInstance("RSA");
//
//        PKCS8EncodedKeySpec keySpecPKCS8 = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(privateKeyContent));
//        privateKey = kf.generatePrivate(keySpecPKCS8);
//
//        X509EncodedKeySpec keySpecX509 = new X509EncodedKeySpec(Base64.getDecoder().decode(publicKeyContent));
//        publicKey = (RSAPublicKey) kf.generatePublic(keySpecX509);
//
//
////        privateKey = pair.getPrivate();
////        publicKey = pair.getPublic();
//        // key = Keys.hmacShaKeyFor(gaiaProp.getSecret().getJwt().getBytes()); --장프로 소스
//    }
    
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
