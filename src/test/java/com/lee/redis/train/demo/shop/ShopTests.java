package com.lee.redis.train.demo.shop;

import com.lee.redis.train.demo.cache.CachePreLoader;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @ClassName ShopTests
 * @Description
 * @Author lihongliang
 * @Date 2025/12/23 11:07
 * @Version 1.0
 */
@SpringBootTest
public class ShopTests {

    @Resource
    private CachePreLoader cachePreLoader;


    @Test
    void testSaveShop() {
        cachePreLoader.saveShopToRedis(1L, 10L);
    }

}
