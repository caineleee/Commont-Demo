package com.lee.redis.train.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lee.redis.train.demo.entity.SeckillVoucher;
import org.apache.ibatis.annotations.Mapper;

/**
 * @ClassName SeckillVoucherMapper
 * @Description 秒杀券 Mapper
 * @Author lihongliang
 * @Date 2025/12/24 16:23
 * @Version 1.0
 */
@Mapper
public interface SeckillVoucherMapper extends BaseMapper<SeckillVoucher> {
}
