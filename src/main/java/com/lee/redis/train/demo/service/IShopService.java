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

    /**
     * 更新商铺信息
     * @param shop 商铺信息
     * @return 更新结果
     */
    Result updateShop(Shop shop);

    /**
     * 根据商铺类型分页查询商铺信息
     * @param typeId 商铺类型
     * @param current 页码
     * @param x 经度
     * @param y 纬度
     * @return 商铺列表
     */
    Result queryShopByType(Integer typeId, Integer current, Double x, Double y);
}
