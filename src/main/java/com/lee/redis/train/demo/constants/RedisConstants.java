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
     * 缓存空数据 TTL
     */
    public static final Long CACHE_NULL_TTL = 2L;

    /**
     * 商铺类型缓存 Key 前缀
     */
    public static final String CACHE_SHOP_TYPE_LIST_KEY = "cache:shop_type:list";

    /**
     * 通用 Redis 互斥锁的过期时间
     */
    public static final Long GENERIC_LOCK_TTL = 10L;

    /**
     * POI 类型缓存 Key 前缀
     */
    public static final String POI_GEO_TYPE_KEY = "poi:geo:typeid:";

    /**
     * 秒杀优惠券库存 Key 前缀
     */
    public static final String SECKILL_STOCK_KEY = "seckill:stock:";

    /**
     * 笔记点赞 Key 前缀
     */
    public static final String BLOB_LIKE_KEY = "blog:liked:";

    /**
     * 关注 Key 前缀
     */
    public static final String FOLLOW_KEY = "follow:";

    /**
     * 笔记收件箱 Key 前缀
     */
    public static final String FEED_KEY = "feed:";

}
