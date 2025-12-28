package com.lee.redis.train.demo.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lee.redis.train.demo.entity.SeckillVoucher;
import com.lee.redis.train.demo.entity.Voucher;
import com.lee.redis.train.demo.mapper.VoucherMapper;
import com.lee.redis.train.demo.service.ISeckillVoucherService;
import com.lee.redis.train.demo.service.IVoucherService;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.lee.redis.train.demo.constants.RedisConstants.SECKILL_STOCK_KEY;

/**
 * @ClassName VoucherServiceImpl
 * @Description 优惠券服务类
 * @Author lihongliang
 * @Date 2025/12/24 12:37
 * @Version 1.0
 */
@Service
public class VoucherServiceImpl extends ServiceImpl<VoucherMapper, Voucher> implements IVoucherService {

    @Resource
    private ISeckillVoucherService seckillVoucherService;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 新增秒杀券
     * @param voucher 秒杀券信息
     */
    @Override
    @Transactional
    public void addSeckillVoucher(Voucher voucher) {
        // 保存优惠券
        save(voucher);

        // 保存秒杀信息
        SeckillVoucher seckillVoucher = new SeckillVoucher()
                .setVoucherId(voucher.getId())
                .setStock(voucher.getStock())
                .setBeginTime(voucher.getBeginTime())
                .setEndTime(voucher.getEndTime());
        seckillVoucherService.save(seckillVoucher);

        // 将优惠券信息保存在 Redis 当中
        stringRedisTemplate.opsForValue().set(SECKILL_STOCK_KEY + voucher.getId(), voucher.getStock().toString());

    }
}
