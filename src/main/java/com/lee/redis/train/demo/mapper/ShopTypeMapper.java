package com.lee.redis.train.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lee.redis.train.demo.entity.ShopType;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @ClassName ShopTypeMapper
 * @Description
 * @Author lihongliang
 * @Date 2025/12/19 11:41
 * @Version 1.0
 */
@Mapper
public interface ShopTypeMapper extends BaseMapper<ShopType> {
}
