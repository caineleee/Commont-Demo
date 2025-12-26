package com.lee.redis.train.demo.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lee.redis.train.demo.constants.UserHold;
import com.lee.redis.train.demo.entity.Result;
import com.lee.redis.train.demo.entity.SeckillVoucher;
import com.lee.redis.train.demo.entity.VoucherOrder;
import com.lee.redis.train.demo.lock.SimpleRedisLock;
import com.lee.redis.train.demo.mapper.VoucherOrderMapper;
import com.lee.redis.train.demo.service.ISeckillVoucherService;
import com.lee.redis.train.demo.service.IVoucherOrderService;
import com.lee.redis.train.demo.utils.RedisIdWorker;
import jakarta.annotation.Resource;
import org.springframework.aop.framework.AopContext;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * @ClassName VoucherOrderServiceImpl
 * @Description 优惠券订单服务实现类
 * @Author lihongliang
 * @Date 2025/12/24 17:22
 * @Version 1.0
 */
@Service
public class VoucherOrderServiceImpl extends ServiceImpl<VoucherOrderMapper, VoucherOrder> implements IVoucherOrderService {

    @Resource
    private ISeckillVoucherService seckillVoucherService;

    @Resource
    private RedisIdWorker redisIdWorker;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 下单秒杀券
     * @param voucherId 优惠券id
     * @return 下单结果
     */
    @Override
    public Result seckillVoucher(Long voucherId) {
        // 获取秒杀优惠券信息
        SeckillVoucher seckillVoucher = seckillVoucherService.getById(voucherId);
        // 判断秒杀券是否在秒杀时间段内
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(seckillVoucher.getBeginTime())) {
            return Result.error("秒杀尚未开始");
        } else if (now.isAfter(seckillVoucher.getEndTime())) {
            return Result.error("秒杀已经结束");
        } else if (seckillVoucher.getStock() < 1) {
            return Result.error("库存不足");
        }

        // 一人一单判断
        Long userId = UserHold.getUser().getId();

        // 创建锁对象, 如果需要锁住用户, 这里的参数必须拼接 userId, 否则锁住的就是整个 order 业务.
        SimpleRedisLock lock = new SimpleRedisLock("order:" + userId, stringRedisTemplate);
        // 尝试获取锁, 这里由于要调试, 所以 ttl 设置 为 1200便于debug
        boolean isLock = lock.tryLock(1200);
        if (!isLock) {
            // 获取锁失败, 返回错误或者重试, 这里是秒杀下单场景, 目的是防止恶意刷,所以直接返回错误
            return Result.error("不允许重复下单");
        }
        try {
            // 为了避免事务失败, 所以这里使用代理
            IVoucherOrderService proxy = (IVoucherOrderService) AopContext.currentProxy();
            return proxy.seckillVoucherOrder(voucherId);
        } finally {
            // 释放锁
            lock.unlock();
        }
    }

    @Transactional
    public Result seckillVoucherOrder(Long voucherId) {
        Long userId = UserHold.getUser().getId();
        Long count = query().eq("user_id", userId).eq("voucher_id", voucherId).count();
        if (count > 0) {
            return Result.error("每个用户只能购买一次");
        }

        // 扣减库存
        boolean success = seckillVoucherService.update()
                .setSql("stock = stock - 1")
                .gt("stock", 0)
                .eq("voucher_id", voucherId).update();
        if (!success) {
            return Result.error("库存不足");
        }
        // 创建订单
        VoucherOrder voucherOrder = new VoucherOrder()
                // ID生成器获取订单ID
                .setId(redisIdWorker.nextId("order"))
                .setVoucherId(voucherId)
                // UserHold 获取用户ID
                .setUserId(UserHold.getUser().getId());
        save(voucherOrder);

        return Result.success("秒杀券下单成功", voucherOrder.getId());

    }
}
