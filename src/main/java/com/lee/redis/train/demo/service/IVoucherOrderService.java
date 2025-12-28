package com.lee.redis.train.demo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lee.redis.train.demo.entity.Result;
import com.lee.redis.train.demo.entity.VoucherOrder;

/**
 * @ClassName IVoucherOrderService
 * @Description 优惠券订单接口
 * @Author lihongliang
 * @Date 2025/12/24 17:22
 * @Version 1.0
 */
public interface IVoucherOrderService extends IService<VoucherOrder> {

    /**
     * 下单秒杀券
     * @param voucherId 优惠券id
     * @return 下单结果
     */
    Result seckillVoucher(Long voucherId);

    /**
     * 下单秒杀券(不能直接调用这个方法, 是用于seckillVoucher调用的, 为了避免事务失效)
     * @param voucherOrder 优惠券
     * @return 下单结果 // 移除 return, 因为异步下单不需要返回信息.
     */
    void seckillVoucherOrder(VoucherOrder voucherOrder);

}
