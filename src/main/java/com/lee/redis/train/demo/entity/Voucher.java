package com.lee.redis.train.demo.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @ClassName Voucher
 * @Description 优惠券实体类
 * @Author lihongliang
 * @Date 2025/12/24 12:31
 * @Version 1.0
 */
@Data
@TableName("tb_voucher")
public class Voucher {

    private Long id;

    @TableField("shop_id")
    private Long shopId;

    private String title;

    @TableField("sub_title")
    private String subTitle;

    private String rules;

    @TableField("pay_value")
    private Long payValue;

    @TableField("actual_value")
    private Long actualValue;

    private Integer type;

    @TableField("status")
    private Integer status;

    @TableField("create_time")
    private LocalDateTime createTime;

    @TableField("update_time")
    private LocalDateTime updateTime;

    @TableField(exist = false)
    private Integer stock;

    @TableField(exist = false)
    private LocalDateTime beginTime;

    @TableField(exist = false)
    private LocalDateTime endTime;
}
