package com.lee.redis.train.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lee.redis.train.demo.entity.Shop;
import org.apache.ibatis.annotations.Mapper;

/**
 * @ClassName ShopMapper
 * @Description
 * @Author lihongliang
 * @Date 2025/12/20 21:00
 * @Version 1.0
 */
@Mapper
public interface ShopMapper extends BaseMapper<Shop> {
}
