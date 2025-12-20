package com.lee.redis.train.demo.constants;

/**
 * @ClassName RedisConstants
 * @Description Redis相关常量
 * @Author lihongliang
 * @Date 2025/12/20 11:50
 * @Version 1.0
 */
public class RedisConstants {
    /**
     * 登录验证码 Key 前缀
     */
    public static final String LOGIN_CODE_KEY = "login:code:";

    /**
     * 登录验证码 Key 过期时间
     */
    public static final Long LOGIN_CODE_TTL = 5L;

    /**
     * 用户数据缓存 Key 前缀
     */
    public static final String USER_CACHED_KEY = "user:token:";

    /**
     * 用户数据缓存 Key 过期时间
     */
    public static final Long USER_CACHED_TTL = 30L;
}
