package com.lee.redis.train.demo.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lee.redis.train.demo.cache.CacheMutexService;
import com.lee.redis.train.demo.entity.Result;
import com.lee.redis.train.demo.entity.Shop;
import com.lee.redis.train.demo.mapper.ShopMapper;
import com.lee.redis.train.demo.service.IShopService;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.lee.redis.train.demo.constants.RedisConstants.CACHE_NULL_TTL;
import static com.lee.redis.train.demo.constants.RedisConstants.CACHE_SHOP_KEY;
import static com.lee.redis.train.demo.constants.RedisConstants.CACHE_SHOP_TTL;
import static com.lee.redis.train.demo.constants.RedisConstants.LOCK_MUTEX_KEY;

/**
 * @ClassName ShopServiceImpl
 * @Description
 * @Author lihongliang
 * @Date 2025/12/20 20:59
 * @Version 1.0
 */
@Service
public class ShopServiceImpl extends ServiceImpl<ShopMapper, Shop> implements IShopService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private CacheMutexService cacheMutexService;

    @Override
    public Result queryShopById(Long id) {
        // 缓存穿透
//        Shop shop = queryShopWithPassThrough(id);
        // 缓存击穿 - 互斥锁
        Shop shop = queryShopWithMutex(id);
        if (shop == null) {
            return Result.notFount("商铺不存在");
        }

        return Result.success(List.of(shop));
    }

    /**
     * 缓存击穿 - 互斥锁
     * @param id 商铺id
     * @return 商铺信息
     */
    public Shop queryShopWithMutex(Long id) {
        Shop shop;
        // Redis 查询缓存
        String redisKey = CACHE_SHOP_KEY + id;
        String json = stringRedisTemplate.opsForValue().get(redisKey);
        // 2. 命中缓存直接返回数据
        if (StrUtil.isNotBlank(json)) {
            return JSONUtil.toBean(json, Shop.class);
        }
        // 经过 isNotBlank 校验到这里的只有 null 和 空字符串两种情况
        if (json != null) {
            return null;
        }

        String lockKey = LOCK_MUTEX_KEY + id;

        try{
            // 获取互斥锁, 获取失败则休眠并尝试重新读取缓存
            boolean flag = cacheMutexService.tryLock(lockKey);
            if (!flag) {
                // 这里的休眠时长需要基于缓存重建的时间设定, 具体休眠多久需要多次重试.
                Thread.sleep(50);
                // 递归尝试重新读取缓存
                return queryShopWithMutex(id);
            }
            // 获取互斥锁成功则重建缓存: 查询数据库
            shop = getById(id);
            if (shop == null) {
                // 将null object 写入 Redis, 防止缓存穿透,
                // 设置 null object 过期时间 2 分钟, 防止内存占用过多.
                stringRedisTemplate.opsForValue().set(redisKey, "", CACHE_NULL_TTL, TimeUnit.MINUTES);
                return null;
            }
            // 有数据则更新缓存
            stringRedisTemplate.opsForValue().set(redisKey, JSONUtil.toJsonStr(shop), CACHE_SHOP_TTL, TimeUnit.MINUTES);
        } catch (Exception e){
            throw new RuntimeException(e);
        }finally {
            // 释放互斥锁
            cacheMutexService.unlock(lockKey);
        }

        // 返回商铺信息
        return shop;
    }


    /**
     * 获取商铺信息 - 缓存穿透
     * @param id 商铺ID
     * @return 商铺信息
     */
    public Shop queryShopWithPassThrough(Long id) {
        // Redis 查询缓存
        String redisKey = CACHE_SHOP_KEY + id;
        String json = stringRedisTemplate.opsForValue().get(redisKey);
        // 命中缓存直接返回数据
        if (StrUtil.isNotBlank(json)) {
            return JSONUtil.toBean(json, Shop.class);
        }
        // 经过 isNotBlank 校验到这里的只有 null 和 空字符串两种情况
        if (json != null) {
            return null;
        }

        // 获取成功则查询数据库, 未命中则读取数据库数据, 查询数据库没有数据则返回 404
        Shop shop = getById(id);
        if (shop == null) {
            // 将null object 写入 Redis, 防止缓存穿透,
            // 设置 null object 过期时间 2 分钟, 防止内存占用过多.
            stringRedisTemplate.opsForValue().set(redisKey, "", CACHE_NULL_TTL, TimeUnit.MINUTES);
            return null;
        }
        // 有数据则更新缓存
        stringRedisTemplate.opsForValue().set(redisKey, JSONUtil.toJsonStr(shop), CACHE_SHOP_TTL, TimeUnit.MINUTES);
        // 返回商铺信息
        return shop;
    }

    /**
     * 更新商铺信息
     *
     * @param shop 商铺信息
     * @return 更新结果
     */
    @Override
    @Transactional
    public Result updateShop(Shop shop) {
        if (shop.getId() == null) {
            return Result.error("店铺ID不能为空");
        }
        // 1. 更新数据库
        updateById(shop);
        // 2. 删除缓存
        stringRedisTemplate.delete(CACHE_SHOP_KEY + shop.getId());
        // 3. 返回更新结果
        return Result.success("修改成功");
    }
}
