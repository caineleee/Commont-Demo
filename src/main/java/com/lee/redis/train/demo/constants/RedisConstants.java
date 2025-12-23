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
     * 登录验证码 Key TTL
     */
    public static final Long LOGIN_CODE_TTL = 5L;

    /**
     * 用户数据缓存 Key 前缀
     */
    public static final String USER_CACHED_KEY = "user:token:";

    /**
     * 用户数据缓存 Key TTL
     */
    public static final Long USER_CACHED_TTL = 30L;

    /**
     * 商铺缓存 Key 前缀
     */
    public static final String CACHE_SHOP_KEY = "cache:shop:";

    /**
     * 商铺缓存 Key TTL
     */
    public static final Long CACHE_SHOP_TTL = 30L;

    /**
     * 缓存空数据 Key TTL
     */
    public static final Long CACHE_NULL_TTL = 2L;

    /**
     * 互斥锁 Key 前缀 (互斥锁方案 | 逻辑过期方案) - SHOP
     */
    public static final String LOCK_SHOP_KEY = "lock:shop:";

    /**
     * 互斥锁 Key TTL
     */
    public static final Long LOCK_SHOP_TTL = 10L;

    /**
     * 商铺类型缓存 Key 前缀
     */
    public static final String CACHE_SHOP_TYPE_LIST_KEY = "cache:shop_type:list";

}
