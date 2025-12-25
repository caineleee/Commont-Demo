package com.lee.redis.train.demo.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * @ClassName VoucherOrder
 * @Description 优惠券订单实体类
 * @Author lihongliang
 * @Date 2025/12/24 16:49
 * @Version 1.0
 */
@Data
@Accessors(chain = true)
@TableName("tb_voucher_order")
public class VoucherOrder {

    @TableId
    private Long id;

    @TableField("user_id")
    private Long userId;

    @TableField("voucher_id")
    private Long voucherId;

    @TableField("pay_type")
    private Integer payType;

    @TableField("pay_time")
    private LocalDateTime payTime;

    @TableField("use_time")
    private LocalDateTime useTime;

    @TableField("refund_time")
    private LocalDateTime refundTime;

    @TableField("status")
    private Integer status;

    @TableField("create_time")
    private LocalDateTime createTime;

    @TableField("update_time")
    private LocalDateTime updateTime;


}
