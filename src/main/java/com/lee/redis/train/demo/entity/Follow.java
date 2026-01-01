package com.lee.redis.train.demo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * @ClassName Follow
 * @Description
 * @Author lihongliang
 * @Date 2025/12/31 21:07
 * @Version 1.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("tb_follow")
public class Follow {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private Long followId;

    private LocalDateTime createTime;
}
