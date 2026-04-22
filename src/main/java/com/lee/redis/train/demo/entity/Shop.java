package com.lee.redis.train.demo.entity;

import cn.hutool.core.date.DateTime;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;

import java.time.LocalDateTime;

/**
 * @ClassName Shop
 * @Description
 * @Author lihongliang
 * @Date 2025/12/20 20:58
 * @Version 1.0
 */
@Data
@TableName("tb_shop")
public class Shop {

    @Id
    private Long id;
    private Long typeId;
    private String name;
    private String x;
    private String y;
    private String area;
    private String address;
    private String openHours;
    private Integer score;
    private Integer avgPrice;
    private Integer comments;
    private Integer sold;
    private String images;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    @Transient // 映射不存在的数据库字段(Canal)
    @TableField(exist = false)
    private Double distance;

}
