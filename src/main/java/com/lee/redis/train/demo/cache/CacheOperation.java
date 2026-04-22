package com.lee.redis.train.demo.cache;

import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.lee.redis.train.demo.entity.Shop;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import static com.lee.redis.train.demo.constants.RedisConstants.CACHE_SHOP_KEY;
import static com.lee.redis.train.demo.constants.RedisConstants.GENERIC_LOCK_TTL;


/**
 * @ClassName CacheOperation
 * @Description
 * @Author lihongliang
 * @Date 2025/12/23 16:20
 * @Version 1.0
 */
@Slf4j
@Component
public class CacheOperation {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    private static final ExecutorService CACHE_BUILD_EXECUTOR = Executors.newFixedThreadPool(10);

    /**
     * 尝试获取锁
     * @param key 锁的 key
     * @return 是否获取成功
     */
    public boolean tryLock(String key, long ttl, TimeUnit unit) {
        Boolean flag = stringRedisTemplate.opsForValue().setIfAbsent(key, "1", ttl, unit);
        // return flag; 不能直接返回 flag, 因为我们需要把封装类 Boolean 转成基本类型 boolean 返回
        // 直接返回 flag 会导致返回过程中需要拆包, 可能出现空指针问题.
        // 所以这里使用工具类 BooleanUtil
        return BooleanUtil.isTrue(flag);
    }

    /**
     * 释放锁
     * @param key 锁的 key
     */
    public void unlock(String key) {
        stringRedisTemplate.delete(key);
    }

    /**
     * 预加载缓存数据
     * @param keyPrefix 缓存 key 前缀
     * @param id 缓存数据 ID
     * @param data 预加载的数据
     * @param expireTime 逻辑过期时间(秒)
     * @param unit 逻辑过期时间(秒)
     * @param <ID> 数据 ID 的类型
     * @param <R> <R> 缓存数据类型
     */
    public <ID, R> void cachePreLoader(String keyPrefix, ID id, R data, Long expireTime, TimeUnit unit) {
        // 这里实现的是自己查询数据库, 其实直接传入 bean 对象比较好
        // 自己查询可能会存在数据不一致问题, 因为绕过了 ServiceImpl 中所有处理逻辑.
        // 目前只是简单处理, 如果数据有额外的处理, 就需要替换为直接传入 bean 对象, 只负责存不符合查相关的操作.
        String redisKey = keyPrefix + id;
        RedisData<R> redisData = new RedisData<>();
        redisData.setData(data);
        redisData.setExpireTime(LocalDateTime.now().plusSeconds(unit.toSeconds(expireTime)));
        // 写入缓存
        stringRedisTemplate.opsForValue().set(redisKey, JSONUtil.toJsonStr(redisData));
    }

    /**
     * 将任意类型的 Bean 转换为 Json 字符串存入 Redis - TTL物理过期
     * @param key 缓存key
     * @param value 缓存值(Bean)
     * @param ttl 缓存时间 Long 类型
     * @param unit 时间单位
     * @param <T> 泛型(传入需要缓存数据的类型)
     */
    public <T> void setWithTTL(String key, T value, Long ttl, TimeUnit unit) {
        String json = JSONUtil.toJsonStr(value);
        stringRedisTemplate.opsForValue().set(key, json, ttl, unit);
    }

    /**
     * 将任意类型的 Bean 转换为 Json 字符串存入 Redis - 逻辑过期
     * @param key 缓存键
     * @param value 缓存值(Bean)
     * @param expireTime 过期时间 Long 类型
     * @param unit 时间单位
     * @param <T> 泛型(传入需要缓存数据的类型)
     */
    public <T> void setWithLogicalExpire(String key, T value, Long expireTime, TimeUnit unit) {
        RedisData<T> redisData = new RedisData<>();
        redisData.setData(value);
        redisData.setExpireTime(LocalDateTime.now().plusSeconds(unit.toSeconds(expireTime)));
        stringRedisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(redisData));
    }

    /**
     * 缓存数据查询 - 缓存穿透: 缓存空数据方案
     * @param keyPrefix Redis key 前缀
     * @param id 查询的数据 ID
     * @param type 查询的数据类型
     * @param dbFallback 数据库查询回调函数
     * @param ttl 过期时间
     * @param unit 过期时间单位
     * @param <R> 返回数据类型
     * @param <ID> ID 的数据类型
     * @return 缓存数据
     */
    public <R, ID> R queryShopWithPassThrough(
            String keyPrefix ,ID id, Class<R> type, Function<ID, R> dbFallback, Long ttl, TimeUnit unit) {
        // Redis 查询缓存
        String redisKey = keyPrefix + id;
        String json = stringRedisTemplate.opsForValue().get(redisKey);
        // 命中缓存直接返回数据
        if (StrUtil.isNotBlank(json)) {
            return JSONUtil.toBean(json, type);
        }
        // 经过 isNotBlank 校验到这里的只有 null 和 空字符串两种情况
        if (json != null) {
            return null;
        }

        // 获取成功则查询数据库, 未命中则读取数据库数据
        R r = dbFallback.apply(id);
        if (r == null) {
            // 将null object 写入 Redis, 防止缓存穿透,
            // 设置 null object 过期时间 2 分钟, 防止内存占用过多.
            stringRedisTemplate.opsForValue().set(redisKey, "", ttl, unit);
            return null;
        }
        // 有数据则更新缓存
        stringRedisTemplate.opsForValue().set(redisKey, JSONUtil.toJsonStr(r), ttl, unit);
        this.setWithTTL(redisKey, r, ttl, unit);
        // 返回信息
        return r;
    }

    /**
     * 缓存数据查询 - 缓存击穿: 逻辑过期方案
     * @param keyPrefix 缓存 Key 前缀
     * @param id 数据 ID
     * @param type 指定数据类型
     * @param dbFallback 数据库查询方法
     * @param expireTime 逻辑过期时间
     * @param unit 过期时间单位
     * @return 指定类型的数据
     * @param <ID> 数据 ID的类型
     * @param <R> <R> 返回数据类的类型
     */
    public <ID, R> R queryShopWithLogicExpire(
            String keyPrefix,ID id, Class<R> type, Function<ID, R> dbFallback, Long expireTime, TimeUnit unit) {
        // Redis 查询缓存
        String redisKey = keyPrefix + id;
        String json = stringRedisTemplate.opsForValue().get(redisKey);
        // 未命中缓存直接返回数据
        if (StrUtil.isBlank(json)) {
            return null;
        }
        // 命中缓存则反序列化缓存数据,并判断逻辑过期时间, 如果未过期则直接返回缓存数据
        RedisData redisData = JSONUtil.toBean(json, RedisData.class);
        R r = JSONUtil.toBean((JSONObject) redisData.getData(), type);
        if (redisData.getExpireTime().isAfter(LocalDateTime.now())) {
            // 逻辑超时未过期, 直接返回返序列化之后的数据
            return r;
        }
        // 如果逻辑过期时间已过期,则尝试获取互斥锁
        String lockKey = "lock:" + keyPrefix + id;
        boolean isLock = tryLock(lockKey, GENERIC_LOCK_TTL, TimeUnit.SECONDS);
        // 如果获取成功开启独立线程重构缓存
        if (isLock) {
            CACHE_BUILD_EXECUTOR.submit(() -> {
                try {
                    // 查询数据库
                    R r1 = dbFallback.apply(id);
                    // 重新写入缓存
                    setWithLogicalExpire(redisKey, r1, expireTime, unit);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                } finally {
                    // 释放锁
                    unlock(lockKey);
                }
            });

        }
        // 获取锁失败则则直接返回数据
        return r;

    }

    /**
     * 保存商铺信息到 Redis (目前只适用于 Canal 同步数据)
     * @param shop 商铺信息
     */
    public void saveShopToRedis(Shop shop) {
        try {
            String json = JSONUtil.toJsonStr(shop);
            stringRedisTemplate.opsForValue().set(CACHE_SHOP_KEY + shop.getId(), json);
        } catch (Exception e) {
            log.error("保存商铺信息到 Redis 失败: " + e.getMessage());
        }
    }

    /**
     * 删除 Redis 商铺信息 (目前只适用于 Canal 同步数据)
     * @param shop 商铺信息
     */
    public void deleteShopToRedis(Shop shop) {
        try {
            String json = JSONUtil.toJsonStr(shop);
            stringRedisTemplate.delete(CACHE_SHOP_KEY + shop.getId());
        } catch (Exception e) {
            log.error("删除 Redis 商铺信息失败: " + e.getMessage());
        }
    }


}
