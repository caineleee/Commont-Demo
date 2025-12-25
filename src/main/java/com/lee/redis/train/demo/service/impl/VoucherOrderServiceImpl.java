package com.lee.redis.train.demo.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lee.redis.train.demo.constants.UserHold;
import com.lee.redis.train.demo.entity.Result;
import com.lee.redis.train.demo.entity.SeckillVoucher;
import com.lee.redis.train.demo.entity.VoucherOrder;
import com.lee.redis.train.demo.mapper.VoucherOrderMapper;
import com.lee.redis.train.demo.service.ISeckillVoucherService;
import com.lee.redis.train.demo.service.IVoucherOrderService;
import com.lee.redis.train.demo.utils.RedisIdWorker;
import jakarta.annotation.Resource;
import org.springframework.aop.framework.AopContext;
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

        // synchronized userId(Long) 会锁住 long 对象, 而不是 UserId 的值.
        // 这样每次调用对象都会变化, 无法做到锁住 UserId value 代表的用户
        // 所以需要将 userId.toString(), 但是发现锁住 UserId 的字符串值,
        // 底层也是只能锁住一个每次调用都会变化的 String 对象, 而不是对应的值
        // 所以再给 userId.toString() 加一个 intern(), 这样就是每次调用方法, 都会去常量池中寻找 value 一致的地址
        synchronized (userId.toString().intern()) {
            // 为了避免事务失效, 这里需要使用代理对象
            IVoucherOrderService proxy = (IVoucherOrderService) AopContext.currentProxy();

            // 之所以在调用的时候才加锁, 是因为, 锁的粒度越小, 锁的冲突就越小
            // 如果锁住整个方法, 所有进程就会阻塞等待一个用户执行, 相当于单核执行, 效率极差
            // 只锁住用户, 就可以做到避免用户重复下单

            // 如果只在 seckillVoucherOrder 方法内部加锁, 可能会导致有的线程修改了数据, 但还没来得及全部执行完毕所有流程
            // 而 seckillVoucherOrder 加了 Transaction 注解, 遇到这样的情况就导致还没来得及提交, 其他线程就进到这个方法里.
            // 这也是数据冲突的原因, 为了避免这个问题, 所以直接在方法调用的外层加锁.
            return proxy.seckillVoucherOrder(voucherId);
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
