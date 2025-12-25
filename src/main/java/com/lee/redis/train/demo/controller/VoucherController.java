package com.lee.redis.train.demo.controller;

import com.lee.redis.train.demo.entity.Result;
import com.lee.redis.train.demo.entity.Voucher;
import com.lee.redis.train.demo.service.IVoucherService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName VoucherController
 * @Description
 * @Author lihongliang
 * @Date 2025/12/24 12:30
 * @Version 1.0
 */
@RestController
@RequestMapping("/voucher")
public class VoucherController {

    @Resource
    private IVoucherService voucherService;

    /**
     * 新增秒杀券
     * @param voucher 秒杀券信息
     * @return 秒杀券id
     */
    @PostMapping("/seckill")
    public Result addSeckillVoucher(@RequestBody Voucher voucher) {
        voucherService.addSeckillVoucher(voucher);
        return Result.success("添加成功", voucher.getId());
    }
}
