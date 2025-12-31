package com.lee.redis.train.demo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @ClassName Blog
 * @Description 博客实体类
 * @Author lihongliang
 * @Date 2025/12/30 10:20
 * @Version 1.0
 */
@Data
@TableName("tb_blog")
public class Blog {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField(value = "shop_id")
    private Long shopId;

    @TableField(value = "user_id")
    private Long userId;

    private String title;

    private String images;

    private String content;

    private Integer liked;

    private Integer comments;

    @TableField(value = "create_time")
    private LocalDateTime createTime;

    @TableField(value = "update_time")
    private LocalDateTime updateTime;

    /**
     * 用户图标
     */
    @TableField(exist = false)
    private String icon;

    /**
     * 用户名称
     */
    @TableField(exist = false)
    private String name;

    /**
     * 是否点过赞
     */
    @TableField(exist = false)
    private Boolean isLike;

    /**
     * 店铺名称
     */
    @TableField(exist = false)
    private String summary;

}
