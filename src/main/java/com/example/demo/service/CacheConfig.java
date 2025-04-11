package com.example.demo.service;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for caching Gemini API responses.
 * Implementation Complexity: 3/10
 * 
 * This configuration is relatively simple:
 * - Basic Spring cache configuration
 * - No complex cache invalidation logic
 * - Standard Spring Boot setup
 */
@Configuration
@EnableCaching
public class CacheConfig {
    
    /**
     * Creates a cache manager for storing API responses.
     * 
     * @return CacheManager instance
     */
    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager("contextAnalysis", "competitorAnalysis", "trendAnalysis");
    }
}
