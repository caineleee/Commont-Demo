package com.lee.redis.train.demo.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @ClassName RedissonConfig
 * @Description Redisson 配置类
 * @Author lihongliang
 * @Date 2025/12/26 20:02
 * @Version 1.0
 */
@Configuration
public class RedissonConfig {

    @Bean
    public RedissonClient redissonClient() {
        // 配置
        Config config = new Config();
        config.useSingleServer().setAddress("redis://localhost:6379").setPassword("root");

        // 创建 redisson 对象
        return Redisson.create(config);
    }

    @Bean
    public RedissonClient redissonClient2() {
        // 配置
        Config config = new Config();
        config.useSingleServer().setAddress("redis://localhost:6380").setPassword("root");

        // 创建 redisson 对象
        return Redisson.create(config);
    }

    @Bean
    public RedissonClient redissonClient3() {
        // 配置
        Config config = new Config();
        config.useSingleServer().setAddress("redis://localhost:6381").setPassword("root");

        // 创建 redisson 对象
        return Redisson.create(config);
    }
}
