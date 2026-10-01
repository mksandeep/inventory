package org.sam.inventory.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.support.NoOpCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Cache configuration for the application.
 * Uses NoOp cache manager (no caching) for development.
 * Redis can be enabled in production by changing spring.cache.type in application.properties
 */
@Configuration
@EnableCaching
@Slf4j
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        log.info("Initializing NoOp CacheManager (no caching) for development");
        return new NoOpCacheManager();
    }
}


