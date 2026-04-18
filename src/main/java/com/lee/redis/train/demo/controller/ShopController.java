package com.lee.redis.train.demo.controller;

import com.github.benmanes.caffeine.cache.Cache;
import com.lee.redis.train.demo.entity.Result;
import com.lee.redis.train.demo.entity.Shop;
import com.lee.redis.train.demo.service.IShopService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


/**
 * @ClassName ShopController
 * @Description
 * @Author lihongliang
 * @Date 2025/12/20 20:56
 * @Version 1.0
 */
@RestController
@RequestMapping("/shop")
public class ShopController {

    @Resource
    private IShopService shopService;

    @Resource
    private Cache<Long, Shop> shopCache;


    /**
     * 新增 JVM Caffeine缓存
     * @param id 商铺id
     * @return 商铺信息
     */
    @GetMapping("/{id}")
    public Result queryShopByIdWithCaffeine(@PathVariable("id") Long id) {
        // 先获取本地缓存数据, 未命中则从数据库查询, 命中则返回数据
        Shop shop = shopCache.get(id, key -> shopService.getById(id));
        return Result.success(shop);
    }

    @GetMapping("/of/type")
    public Result queryShopByType(@RequestParam("typeId") Integer typeId,
                                  @RequestParam(value = "current", defaultValue = "1") Integer current,
                                  @RequestParam(value = "x", required = false) Double x,
                                  @RequestParam(value = "y", required = false) Double y) {
        return shopService.queryShopByType(typeId, current, x, y);
    }

    @PutMapping("/")
    public Result updateShop(@RequestBody Shop shop) {
        return shopService.updateShop(shop);
    }

}
