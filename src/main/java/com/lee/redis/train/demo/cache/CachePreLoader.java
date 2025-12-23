package com.lee.redis.train.demo.cache;

import cn.hutool.json.JSONUtil;
import com.lee.redis.train.demo.entity.Shop;
import com.lee.redis.train.demo.mapper.ShopMapper;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

import static com.lee.redis.train.demo.constants.RedisConstants.CACHE_SHOP_KEY;

/**
 * @ClassName CachePreLoader
 * @Description 缓存数据预加载类
 * @Author lihongliang
 * @Date 2025/12/23 10:27
 * @Version 1.0
 */
@Component
public class CachePreLoader {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private ShopMapper shopMapper;

    /**
     * 将商铺信息 + 逻辑过期时间添加到 Redis
     * @param id 商铺ID
     * @param expireSeconds 逻辑过期时间(秒)
     */
    public void saveShopToRedis(Long id, Long expireSeconds) {
        // 这里实现的是自己查询数据库, 其实直接传入 bean 对象比较好
        // 自己查询可能会存在数据不一致问题, 因为绕过了 ServiceImpl 中所有处理逻辑.
        // 目前只是简单处理, 如果数据有额外的处理, 就需要替换为直接传入 bean 对象, 只负责存不符合查相关的操作.
        Shop shop = shopMapper.selectById(id);
        String redisKey = CACHE_SHOP_KEY + id;
        RedisData<Shop> redisData = new RedisData<>();
        redisData.setData(shop);
        redisData.setExpireTime(LocalDateTime.now().plusSeconds(expireSeconds));
        // 写入缓存
        stringRedisTemplate.opsForValue().set(redisKey, JSONUtil.toJsonStr(redisData));
    }
}
