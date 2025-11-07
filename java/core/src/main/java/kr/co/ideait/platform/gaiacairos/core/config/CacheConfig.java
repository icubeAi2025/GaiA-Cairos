package kr.co.ideait.platform.gaiacairos.core.config;

import java.time.Duration;

import javax.cache.CacheManager;
import javax.cache.Caching;

import org.ehcache.config.CacheConfiguration;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.ExpiryPolicyBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.config.units.MemoryUnit;
import org.ehcache.jsr107.Eh107Configuration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.jcache.JCacheCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class CacheConfig {

    // EhCache Configuration
    CacheConfiguration<String, Object> KEY_STRING_MIN_10 = CacheConfigurationBuilder.newCacheConfigurationBuilder(
            String.class, Object.class,
            ResourcePoolsBuilder.heap(100).offheap(10, MemoryUnit.MB))
            .withExpiry(ExpiryPolicyBuilder.timeToLiveExpiration(Duration.ofSeconds(600)))
            .build();

    @Bean
    public JCacheCacheManager jCacheCacheManager() { // jCacheCacheManager
        CacheManager cacheManager = Caching.getCachingProvider().getCacheManager();

        // MutableConfiguration<Object, Object> configuration = new
        // MutableConfiguration<>();
        // configuration.setExpiryPolicyFactory(CreatedExpiryPolicy.factoryOf(Duration.ONE_HOUR));

        cacheManager.createCache("menu", Eh107Configuration.fromEhcacheCacheConfiguration(KEY_STRING_MIN_10));
        cacheManager.createCache("menuPath", Eh107Configuration.fromEhcacheCacheConfiguration(KEY_STRING_MIN_10));
        return new JCacheCacheManager(cacheManager);
    }
}
