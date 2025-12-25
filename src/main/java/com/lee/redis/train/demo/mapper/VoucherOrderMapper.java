package com.lee.redis.train.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lee.redis.train.demo.entity.VoucherOrder;
import org.apache.ibatis.annotations.Mapper;

/**
 * @ClassName VoucherOrderMapper
 * @Description 优惠券订单Mapper
 * @Author lihongliang
 * @Date 2025/12/24 17:20
 * @Version 1.0
 */
@Mapper
public interface VoucherOrderMapper extends BaseMapper<VoucherOrder> {
}
