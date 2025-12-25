package com.lee.redis.train.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lee.redis.train.demo.entity.Voucher;
import org.apache.ibatis.annotations.Mapper;

/**
 * @ClassName VoucherMapper
 * @Description VoucherMapper 接口
 * @Author lihongliang
 * @Date 2025/12/24 12:37
 * @Version 1.0
 */
@Mapper
public interface VoucherMapper extends BaseMapper<Voucher> {
}
