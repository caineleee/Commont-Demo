package com.lee.redis.train.demo.service.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lee.redis.train.demo.constants.RedisConstants;
import com.lee.redis.train.demo.entity.Result;
import com.lee.redis.train.demo.entity.ShopType;
import com.lee.redis.train.demo.mapper.ShopTypeMapper;
import com.lee.redis.train.demo.service.IShopTypeService;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @ClassName ShopTypeServiceImpl
 * @Description 商铺类型服务类
 * @Author lihongliang
 * @Date 2025/12/19 11:37
 * @Version 1.0
 */
@Service
public class ShopTypeServiceImpl extends ServiceImpl<ShopTypeMapper, ShopType> implements IShopTypeService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public Result queryShopTypeList() {
        // 1. 获取商铺类型列表 from redis, 缓存命中则返回数据
        String redisKey = RedisConstants.CACHE_SHOP_TYPE_LIST_KEY;
        List<String> json = stringRedisTemplate.opsForList().range(redisKey, 0, -1);
        if (json != null && !json.isEmpty()) {
            List<ShopType> shopTypeList = json.stream().map(item -> JSONUtil.toBean(item, ShopType.class)).toList();
            return Result.success(shopTypeList);
        }

        // 2. 缓存未命中则查询数据库, 查询数据库没有数据则返回 404
        List<ShopType> shopTypeList = query().orderByAsc("sort").list();
        if (shopTypeList.isEmpty()) {
            return Result.notFount("商铺类型列表不存在");
        }
        // 3. 查询数据库获取数据,转换数据类型并更新缓存
        List<String> list = shopTypeList.stream().map(JSONUtil::toJsonStr).toList();
        stringRedisTemplate.opsForList().rightPushAll(redisKey, list);
        // 4. 返回数据
        return Result.success(shopTypeList);

    }
}
