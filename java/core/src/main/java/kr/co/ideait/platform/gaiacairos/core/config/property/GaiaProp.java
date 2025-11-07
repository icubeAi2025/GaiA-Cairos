package kr.co.ideait.platform.gaiacairos.core.config.property;

import org.springframework.boot.context.properties.ConfigurationProperties;

import com.zaxxer.hikari.HikariConfig;

import lombok.Data;

@Data
@ConfigurationProperties(prefix = "gaia")
public class GaiaProp {

    boolean https;
    HikariConfig datasource1;
    HikariConfig datasource2;
    HikariConfig datasource3;
    HikariConfig datasource4;
    Secret secret;
    Url url;
    Path path;
}
