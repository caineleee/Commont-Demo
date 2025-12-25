package com.lee.redis.train.demo.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lee.redis.train.demo.entity.SeckillVoucher;
import com.lee.redis.train.demo.mapper.SeckillVoucherMapper;
import com.lee.redis.train.demo.service.ISeckillVoucherService;
import org.springframework.stereotype.Service;

/**
 * @ClassName SeckillVoucherServiceImpl
 * @Description 秒杀券服务类
 * @Author lihongliang
 * @Date 2025/12/24 16:25
 * @Version 1.0
 */
@Service
public class SeckillVoucherServiceImpl
        extends ServiceImpl<SeckillVoucherMapper, SeckillVoucher> implements ISeckillVoucherService {
}
