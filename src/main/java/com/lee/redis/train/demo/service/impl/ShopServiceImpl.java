package com.lee.redis.train.demo.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lee.redis.train.demo.cache.CacheOperation;
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

import static com.lee.redis.train.demo.constants.RedisConstants.CACHE_SHOP_KEY;
import static com.lee.redis.train.demo.constants.RedisConstants.CACHE_SHOP_TTL;

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
    private CacheOperation cacheOperation;

    /**
     * 缓存预加载线程池
     */


    @Override
    public Result queryShopById(Long id) {
        // 缓存穿透
//        Shop shop = queryShopWithPassThrough(id);
//        Shop shop = cacheOperation.queryShopWithPassThrough(
//                CACHE_SHOP_KEY, id, Shop.class, this::getById, CACHE_SHOP_TTL, TimeUnit.MINUTES);
        // 缓存击穿 - 互斥锁
//        Shop shop = queryShopWithMutex(id);
        // 缓存击穿 - 逻辑过期
//        Shop shop = queryShopWithLogicExpire(id);
        Shop shop = cacheOperation.queryShopWithLogicExpire(
                CACHE_SHOP_KEY, id, Shop.class, this::getById, CACHE_SHOP_TTL, TimeUnit.MINUTES);

        if (shop == null) {
            return Result.notFount("商铺不存在");
        }

        return Result.success(List.of(shop));
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
