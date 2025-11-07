package kr.co.ideait.platform.gaiacairos.core.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import jakarta.persistence.EntityManagerFactory;

@Configuration
@EntityScan("kr.co.ideait.platform.gaiacairos.core.persistence.entity")
@EnableJpaRepositories(basePackages = "kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories", entityManagerFactoryRef = "entityManagerFactory", transactionManagerRef = "jpaTransactionManager")
public class JpaConfig {

    @Autowired
    DataSource dataSource;

    @Bean
    public PlatformTransactionManager jpaTransactionManager(EntityManagerFactory entityManagerFactory) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory);
        return transactionManager;
    }
}
