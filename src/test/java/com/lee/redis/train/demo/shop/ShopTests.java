package com.lee.redis.train.demo.shop;

import com.lee.redis.train.demo.cache.CacheOperation;
import com.lee.redis.train.demo.entity.Shop;
import com.lee.redis.train.demo.mapper.ShopMapper;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.TimeUnit;

import static com.lee.redis.train.demo.constants.RedisConstants.CACHE_SHOP_KEY;

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
    private CacheOperation caseOperation;

    @Resource
    private ShopMapper shopMapper;


    @Test
    void testSaveShop() {
        // 预缓存数据
        Shop shop = shopMapper.selectById(1L);
        caseOperation.cachePreLoader(CACHE_SHOP_KEY, 1L, shop,10L, TimeUnit.SECONDS);
    }

}
