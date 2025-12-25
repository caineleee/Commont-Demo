package com.lee.redis.train.demo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lee.redis.train.demo.entity.Voucher;

/**
 * @ClassName IVoucherService
 * @Description 优惠券服务接口
 * @Author lihongliang
 * @Date 2025/12/24 12:35
 * @Version 1.0
 */
public interface IVoucherService extends IService<Voucher> {

    /**
     * 新增秒杀券
     * @param voucher 秒杀券信息
     */
    void addSeckillVoucher(Voucher voucher);
}
