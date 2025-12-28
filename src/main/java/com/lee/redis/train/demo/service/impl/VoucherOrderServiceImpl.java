package com.lee.redis.train.demo.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lee.redis.train.demo.constants.UserHold;
import com.lee.redis.train.demo.entity.Result;
import com.lee.redis.train.demo.entity.VoucherOrder;
import com.lee.redis.train.demo.mapper.VoucherOrderMapper;
import com.lee.redis.train.demo.service.ISeckillVoucherService;
import com.lee.redis.train.demo.service.IVoucherOrderService;
import com.lee.redis.train.demo.utils.RedisIdWorker;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.aop.framework.AopContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @ClassName VoucherOrderServiceImpl
 * @Description 优惠券订单服务实现类
 * @Author lihongliang
 * @Date 2025/12/24 17:22
 * @Version 1.0
 */
@Slf4j
@Service
public class VoucherOrderServiceImpl extends ServiceImpl<VoucherOrderMapper, VoucherOrder> implements IVoucherOrderService {

    @Resource
    private ISeckillVoucherService seckillVoucherService;

    /**
     * ID 生成器
     */
    @Resource
    private RedisIdWorker redisIdWorker;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private RedissonClient redissonClient;

    private IVoucherOrderService proxy;

    /**
     * 阻塞队列
     */
    private final BlockingQueue<VoucherOrder> orderTasks = new ArrayBlockingQueue<>(1024 * 1024);

    /**
     * 线程池 (研发环境不需要太多线程, 这里给1个用于测试功能)
     */
    private static final ExecutorService SECKILL_ORDER_EXECUTOR = Executors.newFixedThreadPool(1);

    /**
     * Lua 脚本对象
     */
    private static final DefaultRedisScript<Long> SECKILL_SCRIPT;

    /*
      初始化静态 Lua 脚本对象
     */
    static {
        SECKILL_SCRIPT = new DefaultRedisScript<>();
        SECKILL_SCRIPT.setLocation(new ClassPathResource("lua/Seckill.lua"));
        SECKILL_SCRIPT.setResultType(Long.class);
    }

    /**
     * 线程池执行执行阻塞队列任务
     * 设置 @PostConstruct 指定在 VoucherOrderServiceImpl service 初始化后执行
     */
    @PostConstruct
    private void init() {
        SECKILL_ORDER_EXECUTOR.submit(new VoucherOrderHandler());
    }

    /**
     * 下单任务
     * 这个任务需要监听阻塞队列, 所以需要用户请求之前就启动,
     * 所以设置为 VoucherOrderServiceImpl 初始化之后就启动
     */
    private class VoucherOrderHandler implements Runnable {
        /**
         * Runs this operation.
         */
        @Override
        public void run() {
            while (true) {
                try {
                    // 获取订单中的信息 take 是阻塞方法, 会一直等待队列中有数据可用时才会使用. 不会对 CPU 造成巨大消耗
                    VoucherOrder order = orderTasks.take();
                    // 创建订单
                    createVoucherOrder(order);
                } catch (Exception e) {
                    log.error("处理订单异常" + e);
                }
            }
        }
    }

    /**
     * 异步创建订单
     * @param order 订单信息
     */
    private void createVoucherOrder(VoucherOrder order) {
        // 获取用户
        Long userId = order.getUserId();
        // 创建锁对象, 如果需要锁住用户, 这里的参数必须拼接 userId, 否则锁住的就是整个 order 业务.
        RLock lock = redissonClient.getLock("lock:order:" + userId);
        // 尝试获取锁, Redisson 这里可以设置重试等待时间.
        // 在异步创建订单之前,  Redis 已经做过判断, 这里不需要加锁. 但是以防万一加锁做兜底.
        boolean isLock = lock.tryLock();
        if (!isLock) {
            // 获取锁失败, 返回错误或者重试, 这里是秒杀下单场景, 目的是防止恶意刷,所以直接返回错误
            log.error("不允许重复下单");
        }
        try {

            proxy.seckillVoucherOrder(order);
        } finally {
            // 释放锁
            lock.unlock();
        }
    }

    /**
     * Mysql 扣减库存+下单
     * @param voucherOrder 优惠券
     */
    @Transactional
    public void seckillVoucherOrder(VoucherOrder voucherOrder) {
        Long userId = voucherOrder.getUserId();
        Long count = query().eq("user_id", userId).eq("voucher_id", voucherOrder.getVoucherId()).count();
        if (count > 0) {
            log.error("每个用户只能购买一次");
            return;
        }

        // 扣减库存
        boolean success = seckillVoucherService.update()
                .setSql("stock = stock - 1")
                .gt("stock", 0)
                .eq("voucher_id", voucherOrder.getVoucherId()).update();
        if (!success) {
            log.error("库存不足");
            return;
        }
        // 创建订单 现在直接传入了 VoucherOrder 对象, 不需要再创建, 直接save 即可
        save(voucherOrder);
    }



    /**
     * 下单秒杀券
     * @param voucherId 优惠券id
     * @return 下单结果
     */
    @Override
    public Result seckillVoucher(Long voucherId) {
        // 执行 Lua 脚本, 判断返回是否为 0 (用户是否可以下单), 如果 != 0 返回错误
        Long userId = UserHold.getUser().getId();
        Long result = stringRedisTemplate.execute(
                SECKILL_SCRIPT,
                Collections.emptyList(), // Key 列表这里由于这个 Lua 脚本不需要 key, 所以传空集合
                voucherId.toString(), userId.toString()
        );
        int resultIntValue = result.intValue();
        if (resultIntValue != 0) {
            return Result.error(resultIntValue == 1 ? "库存不足" : "不能重复下单");
        }
        // 如果返回0 则把下单信息放入阻塞队列, 并返回订单ID 等信息

        // 生成订单id
        long orderId = redisIdWorker.nextId("order");

        // 封装订单信息
        VoucherOrder order = new VoucherOrder();
        order.setId(orderId);
        order.setUserId(userId);
        order.setVoucherId(voucherId);
        // 将订单信息放入阻塞队列
        orderTasks.add(order);

        // 为了避免事务失败, 所以这里使用代理对象.
        // 代理对象之所以从这里获取, 是因为如果在异步线程中获取到的只是异步线程(子线程) 的信息.
        // 我们需要获取的是主线程(父线程) 的信息, 所以只能在这里获取, 然后初始化给属性, 被子线程的逻辑获取.
        proxy = (IVoucherOrderService) AopContext.currentProxy();

        return Result.success(orderId);
    }


}
