package com.lee.redis.train.demo.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lee.redis.train.demo.entity.ShopType;
import com.lee.redis.train.demo.mapper.ShopTypeMapper;
import com.lee.redis.train.demo.service.IShopTypeService;
import org.springframework.stereotype.Service;

/**
 * @ClassName ShopTypeServiceImpl
 * @Description 商铺类型服务类
 * @Author lihongliang
 * @Date 2025/12/19 11:37
 * @Version 1.0
 */
@Service
public class ShopTypeServiceImpl extends ServiceImpl<ShopTypeMapper, ShopType> implements IShopTypeService {

}
