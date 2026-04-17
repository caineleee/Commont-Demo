package com.lee.redis.train.demo.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.lee.redis.train.demo.entity.Shop;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @ClassName CaffeineConfig
 * @Description JVM本地缓存 Caffeine 配置类
 * @Author lihongliang
 * @Date 2026/4/16 16:38
 * @Version 1.0
 */
@Configuration
public class CaffeineConfig {

    /**
     * 创建 Shop Caffeine本地缓存对象
     * @return Caffeine 缓存对象
     */
    @Bean
    public Cache<Long, Shop> shopCache() {
        return com.github.benmanes.caffeine.cache.Caffeine.newBuilder()
                .initialCapacity(100)
                .maximumSize(10000)
                .build();
    }
}
