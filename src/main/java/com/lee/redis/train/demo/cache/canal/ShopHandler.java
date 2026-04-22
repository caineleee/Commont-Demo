package com.lee.redis.train.demo.cache.canal;

import com.github.benmanes.caffeine.cache.Cache;
import com.lee.redis.train.demo.cache.CacheOperation;
import com.lee.redis.train.demo.entity.Shop;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import top.javatool.canal.client.annotation.CanalTable;
import top.javatool.canal.client.handler.EntryHandler;

/**
 * @ClassName ShopHandler
 * @Description Canal 监听 Shop Class 的配置类,
 * 用于数据库变更后,JVM进程缓存 和Redis缓存数据同步
 * @Author lihongliang
 * @Date 2026/4/20 11:31
 * @Version 1.0
 */
@Slf4j
@CanalTable("tb_shop")
@Component
public class ShopHandler implements EntryHandler<Shop> {

    @Resource
    private CacheOperation cacheOperation;

    @Resource
    private Cache<Long, Shop> cache;

    /** 监听 tb_shop 的数据新增的逻辑
     * @param shop
     */
    @Override
    public void insert(Shop shop) {
        log.info("Canal 监听到 Shop 新增: id={}, name={}", shop.getId(), shop.getName());
        // 写数据到 JVM 本地(进程)缓存
        cache.put(shop.getId(), shop);
        // 写数据到 redis
        cacheOperation.saveShopToRedis(shop);
        log.info("Shop 数据已同步到 Redis: id={}", shop.getId());
    }

    /** 监听 tb_shop 的数据更新的逻辑
     * @param before 更新前的数据
     * @param after 更新后的数据
     */
    @Override
    public void update(Shop before, Shop after) {
        log.info("Canal 监听到 Shop 更新: id={}, name={}", after.getId(), after.getName());
        // 写数据到 JVM 本地(进程)缓存
        cache.put(after.getId(), after);
        // 写数据到 redis
        cacheOperation.saveShopToRedis(after);
        log.info("Shop 数据已同步到 Redis: id={}", after.getId());
    }

    /** 监听 tb_shop 的数据删除的逻辑
     * @param shop
     */
    @Override
    public void delete(Shop shop) {
        log.info("Canal 监听到 Shop 删除: id={}, name={}", shop.getId(), shop.getName());
        // 删除 JVM 本地(进程)缓存数据
        cache.invalidate(shop.getId());
        // 删除 redis 数据
        cacheOperation.deleteShopToRedis(shop);
        log.info("Shop 数据已从 Redis 删除: id={}", shop.getId());

    }
}
