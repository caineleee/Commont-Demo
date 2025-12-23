package com.lee.redis.train.demo.cache;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * @ClassName RedisData
 * @Description Redis 逻辑过期封装类
 * @Author lihongliang
 * @Date 2025/12/23 10:11
 * @Version 1.0
 */
@Data
public class RedisData<T> {
    /**
     * 逻辑过期时间
     */
    private LocalDateTime expireTime;

    /**
     * 缓存数据
     */
    private T data;
}
