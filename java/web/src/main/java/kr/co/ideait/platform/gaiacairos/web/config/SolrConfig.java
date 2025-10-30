package kr.co.ideait.platform.gaiacairos.web.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.repository.config.EnableSolrRepositories;

@Configuration
@ConfigurationProperties(prefix="spring.data.solr")
@EnableSolrRepositories(basePackages = "kr.co.ideait.platform.gaiacairos.comp.document")
@Slf4j
public class SolrConfig {
    @Value("${spring.data.solr.host}")
    private String solrHost;

    @Bean
    public SolrClient solrClient() {
        log.info("solrHostURL ={}", solrHost);
        return new HttpSolrClient.Builder(solrHost).build();
    }

    @Bean
    public SolrTemplate solrTemplate(SolrClient client) throws Exception {
        return new SolrTemplate(client);
    }

}