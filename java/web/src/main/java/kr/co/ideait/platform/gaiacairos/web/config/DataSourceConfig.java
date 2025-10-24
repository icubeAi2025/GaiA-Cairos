package kr.co.ideait.platform.gaiacairos.web.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import jakarta.annotation.PostConstruct;
import kr.co.ideait.platform.gaiacairos.core.config.JasyptConfig;
import kr.co.ideait.platform.gaiacairos.core.config.property.Properties;
import lombok.extern.slf4j.Slf4j;
import org.jasypt.encryption.StringEncryptor;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@Slf4j
@Configuration
@EnableTransactionManagement
public class DataSourceConfig {

    @Autowired
    Properties properties;

    StringEncryptor stringEncryptor;

//    @Bean
//    public PlatformTransactionManager transactionManager() throws Exception {
//        return new DataSourceTransactionManager(dataSource());
//    }

    @PostConstruct
    public void init() {
        //encryptorCheck();
        BeanFactory beanFactory = new AnnotationConfigApplicationContext(JasyptConfig.class);
        this.stringEncryptor = beanFactory.getBean("jasyptEncryptorAES", StringEncryptor.class);
    }

    @Bean(name="dataSource")
    @Primary
    public DataSource dataSource() {
        HikariConfig hikariConfig = properties.getGaia().getDatasource1();
    	hikariConfig.setJdbcUrl(stringEncryptor.decrypt(hikariConfig.getJdbcUrl()));
    	hikariConfig.setUsername(stringEncryptor.decrypt(hikariConfig.getUsername()));
    	hikariConfig.setPassword(stringEncryptor.decrypt(hikariConfig.getPassword()));
    	hikariConfig.setSchema(stringEncryptor.decrypt(hikariConfig.getSchema()));
        hikariConfig.setAutoCommit(false);

        log.info("********************************** DB1 GAIA-CMIS ***************************************");
        log.info("url 		: >>>>> {}", hikariConfig.getJdbcUrl());
        log.info("userName : >>>>> {}", hikariConfig.getUsername());
        log.info("password : >>>>> {}", hikariConfig.getPassword());
        log.info("schema 	: >>>>> {}", hikariConfig.getSchema());
        log.info("****************************************************************************************");

        return new HikariDataSource(hikariConfig);
    }

    @Bean(name="icsDataSource")
    public DataSource icsDataSource() {
        HikariConfig hikariConfig = properties.getGaia().getDatasource3();
    	hikariConfig.setJdbcUrl(stringEncryptor.decrypt(hikariConfig.getJdbcUrl()));
    	hikariConfig.setUsername(stringEncryptor.decrypt(hikariConfig.getUsername()));
    	hikariConfig.setPassword(stringEncryptor.decrypt(hikariConfig.getPassword()));
    	hikariConfig.setSchema(stringEncryptor.decrypt(hikariConfig.getSchema()));

    	log.info("********************************** DB3 ICS메신져 ***************************************");
        log.info("url 		: >>>>> {}", hikariConfig.getJdbcUrl());
        log.info("userName : >>>>> {}", hikariConfig.getUsername());
        log.info("password : >>>>> {}", hikariConfig.getPassword());
        log.info("schema 	: >>>>> {}", hikariConfig.getSchema());
    	log.info("****************************************************************************************");

        return new HikariDataSource(hikariConfig);
    }

    @Bean(name="pccsDataSource")
    public DataSource pccsDataSource() {
        HikariConfig hikariConfig = properties.getGaia().getDatasource4();
    	hikariConfig.setJdbcUrl(stringEncryptor.decrypt(hikariConfig.getJdbcUrl()));
    	hikariConfig.setUsername(stringEncryptor.decrypt(hikariConfig.getUsername()));
    	hikariConfig.setPassword(stringEncryptor.decrypt(hikariConfig.getPassword()));
    	hikariConfig.setSchema(stringEncryptor.decrypt(hikariConfig.getSchema()));

    	log.info("********************************** DB4 ORACLE DB ***************************************");
        log.info("url 		: >>>>> {}", hikariConfig.getJdbcUrl());
        log.info("userName : >>>>> {}", hikariConfig.getUsername());
        log.info("password : >>>>> {}", hikariConfig.getPassword());
        log.info("schema 	: >>>>> {}", hikariConfig.getSchema());
    	log.info("****************************************************************************************");

        return new HikariDataSource(hikariConfig);
    }

    // 암호화 확인
    public void encryptorCheck() {
    	String userName = "gaia";
    	String password = "gaia0001!1";
    	String jdbcUrl  = "jdbc:log4jdbc:postgresql://193.123.250.99:5432/gaia_cmis";
    	String schema  	= "gaia_cmis";

    	String userName2 = "gaia";
    	String password2 = "gaia0001!1";
    	String jdbcUrl2  = "jdbc:postgresql://10.0.0.175:5432/gaia_cmis";
    	String schema2 	 = "wbs_gen";

    	String userName3 = "icsadm";
    	String password3 = "icsadm_20240717";
    	String jdbcUrl3  = "jdbc:postgresql://10.0.0.175:5432/icsdb";
    	String schema3   = "icsmsg";

    	String userName4 = "PCCS_RO";
    	String password4 = "LSTJ9W9IFL_20241230";
    	String jdbcUrl4  = "jdbc:oracle:thin:@10.0.0.41:1521/ideadb3_pdb1.sub06030034550.vcn01.oraclevcn.com";
    	String schema4   = "PCCS";

    	String userName5 = "gaia";
    	String password5 = "gaia_0001!1";
    	String jdbcUrl5  = "jdbc:postgresql://146.56.191.67:5432/gaia_cmis";
    	String schema5   = "gaia_cmis";

        String encUserName 	= stringEncryptor.encrypt(userName);
        String encPassword 	= stringEncryptor.encrypt(password);
        String encJdbcUrl 	= stringEncryptor.encrypt(jdbcUrl);
        String encSchema 	= stringEncryptor.encrypt(schema);

        String encUserName2 = stringEncryptor.encrypt(userName2);
        String encPassword2 = stringEncryptor.encrypt(password2);
        String encJdbcUrl2 	= stringEncryptor.encrypt(jdbcUrl2);
        String encSchema2 	= stringEncryptor.encrypt(schema2);

        String encUserName3	= stringEncryptor.encrypt(userName3);
        String encPassword3	= stringEncryptor.encrypt(password3);
        String encJdbcUrl3 	= stringEncryptor.encrypt(jdbcUrl3);
        String encSchema3 	= stringEncryptor.encrypt(schema3);

        String encUserName4	= stringEncryptor.encrypt(userName4);
        String encPassword4	= stringEncryptor.encrypt(password4);
        String encJdbcUrl4 	= stringEncryptor.encrypt(jdbcUrl4);
        String encSchema4 	= stringEncryptor.encrypt(schema4);

        String encUserName5	= stringEncryptor.encrypt(userName5);
        String encPassword5	= stringEncryptor.encrypt(password5);
        String encJdbcUrl5 	= stringEncryptor.encrypt(jdbcUrl5);
        String encSchema5 	= stringEncryptor.encrypt(schema5);

        log.debug("****************************************************************************************");
        log.debug("encUserName : >>>>> " + encUserName);
        log.debug("encPassword : >>>>> " + encPassword);
        log.debug("encJdbcUrl : >>>>> " + encJdbcUrl);
        log.debug("encSchema : >>>>> " + encSchema);
        log.debug("****************************************************************************************");
        log.debug("****************************************************************************************");
        log.debug("encUserName2 : >>>>> " + encUserName2);
        log.debug("encPassword2 : >>>>> " + encPassword2);
        log.debug("encJdbcUrl2 : >>>>> " + encJdbcUrl2);
        log.debug("encSchema2 : >>>>> " + encSchema2);
        log.debug("****************************************************************************************");
        log.debug("****************************************************************************************");
        log.debug("encUserName3 : >>>>> " + encUserName3);
        log.debug("encPassword3 : >>>>> " + encPassword3);
        log.debug("encJdbcUrl3 : >>>>> " + encJdbcUrl3);
        log.debug("encSchema3 : >>>>> " + encSchema3);
        log.debug("****************************************************************************************");
        log.debug("****************************************************************************************");
        log.debug("encUserName4 : >>>>> " + encUserName4);
        log.debug("encPassword4 : >>>>> " + encPassword4);
        log.debug("encJdbcUrl4 : >>>>> " + encJdbcUrl4);
        log.debug("encSchema4 : >>>>> " + encSchema4);
        log.debug("****************************************************************************************");
        log.debug("****************************************************************************************");
        log.debug("encUserName5 : >>>>> " + encUserName5);
        log.debug("encPassword5 : >>>>> " + encPassword5);
        log.debug("encJdbcUrl5 : >>>>> " + encJdbcUrl5);
        log.debug("encSchema5 : >>>>> " + encSchema5);
        log.debug("****************************************************************************************");
    }

}
