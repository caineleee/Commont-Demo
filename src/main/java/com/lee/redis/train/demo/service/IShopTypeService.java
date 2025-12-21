package com.lee.redis.train.demo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lee.redis.train.demo.entity.Result;
import com.lee.redis.train.demo.entity.ShopType;

/**
 * @ClassName IShopTypeService
 * @Description 商铺类型服务接口
 * @Author lihongliang
 * @Date 2025/12/19 11:36
 * @Version 1.0
 */
public interface IShopTypeService extends IService<ShopType> {

    /**
     * 查询商铺类型列表
     *
     * @return 商铺类型列表
     */
    Result queryShopTypeList();
}
