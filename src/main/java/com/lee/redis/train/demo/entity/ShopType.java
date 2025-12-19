package com.lee.redis.train.demo.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @ClassName ShopType
 * @Description 商铺类型
 * @Author lihongliang
 * @Date 2025/12/19 11:13
 * @Version 1.0
 */
@Data
@TableName("tb_shoptype")
public class ShopType {

    private int id;
    private int sort;
    private String name;
    private String icon;
}
