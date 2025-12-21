package com.lee.redis.train.demo.controller;

import com.lee.redis.train.demo.entity.Result;
import com.lee.redis.train.demo.service.IShopTypeService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName ShopTypeController
 * @Description 商铺类型控制器
 * @Author lihongliang
 * @Date 2025/12/19 11:52
 * @Version 1.0
 */
@RestController
@RequestMapping("/shop-type")
public class ShopTypeController {

    @Resource
    private IShopTypeService shopTypeService;

    @GetMapping("/list")
    public Result list() {
        return shopTypeService.queryShopTypeList();
    }

}
