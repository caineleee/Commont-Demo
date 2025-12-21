package com.lee.redis.train.demo.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lee.redis.train.demo.entity.Result;
import com.lee.redis.train.demo.entity.Shop;
import com.lee.redis.train.demo.mapper.ShopMapper;
import com.lee.redis.train.demo.service.IShopService;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.lee.redis.train.demo.constants.RedisConstants.CACHE_SHOP_KEY;

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

    @Override
    public Result queryShopById(Long id) {
        // 1. 从 Redis 查询缓存
        String redisKey = CACHE_SHOP_KEY + id;
        String json = stringRedisTemplate.opsForValue().get(redisKey);
        // 2. 命中缓存直接返回数据
        if (StrUtil.isNotBlank(json)) {
            Shop bean = JSONUtil.toBean(json, Shop.class);
            return Result.success(List.of(bean));
        }
        // 3. 未命中则读取数据库数据, 查询数据库没有数据则返回 404
        Shop shop = getById(id);
        if (shop == null) {
            return Result.notFount("店铺不存在");
        }
        // 4. 有数据则更新缓存
        stringRedisTemplate.opsForValue().set(redisKey, JSONUtil.toJsonStr(shop));
        // 5. 返回商铺信息
        return Result.success(List.of(shop));
    }
}
