package kr.co.ideait.platform.gaiacairos.core.config;

import lombok.extern.slf4j.Slf4j;
import org.jasypt.encryption.StringEncryptor;
import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;


@Slf4j
@Configuration
@EnableEncryptableProperties
public class JasyptConfig {
	
	@Value("${jasypt.encryptor.password}")
    private String jasyptPassword;
    
    @Bean("jasyptEncryptorAES")
	public StringEncryptor stringEncryptor() {
		SimpleStringPBEConfig config = new SimpleStringPBEConfig();
		config.setPassword(jasyptPassword); // 암호화키
		config.setAlgorithm("PBEWITHHMACSHA512ANDAES_256"); // 알고리즘
		config.setKeyObtentionIterations("1000"); // 반복할 해싱 회수
		config.setPoolSize("1"); // 인스턴스 pool
		config.setProviderName("SunJCE");
		config.setSaltGeneratorClassName("org.jasypt.salt.RandomSaltGenerator"); // salt 생성 클래스
		config.setIvGeneratorClassName("org.jasypt.iv.RandomIvGenerator");
        config.setStringOutputType("base64"); //인코딩 방식

		PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
		encryptor.setConfig(config);

		return encryptor;
	}
}
