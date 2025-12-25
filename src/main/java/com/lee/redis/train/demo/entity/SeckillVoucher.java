package com.lee.redis.train.demo.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * @ClassName SeckillVoucher
 * @Description 秒杀券信息实体类
 * @Author lihongliang
 * @Date 2025/12/24 16:11
 * @Version 1.0
 */
@Data
@Accessors(chain = true)
@TableName("tb_seckill_voucher")
public class SeckillVoucher {

    @TableId
    @TableField("voucher_id")
    private Long voucherId;

    private Integer stock;

    @TableField("begin_time")
    private LocalDateTime beginTime;

    @TableField("end_time")
    private LocalDateTime endTime;

    @TableField("create_time")
    private LocalDateTime createTime;

    @TableField("update_time")
    private LocalDateTime updateTime;
}
