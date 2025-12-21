package com.lee.redis.train.demo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lee.redis.train.demo.entity.Result;
import com.lee.redis.train.demo.entity.Shop;

/**
 * @ClassName IShopService
 * @Description
 * @Author lihongliang
 * @Date 2025/12/20 20:57
 * @Version 1.0
 */
public interface IShopService extends IService<Shop> {

    /**
     * 通过 id 获取商铺
     * @param id 商铺 ID
     * @return 商铺信息
     */
    Result queryShopById(Long id);
}
