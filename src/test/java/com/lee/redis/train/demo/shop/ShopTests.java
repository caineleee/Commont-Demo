package com.lee.redis.train.demo.shop;

import cn.hutool.core.util.StrUtil;
import com.lee.redis.train.demo.cache.CacheOperation;
import com.lee.redis.train.demo.constants.SystemConstants;
import com.lee.redis.train.demo.entity.Result;
import com.lee.redis.train.demo.entity.Shop;
import com.lee.redis.train.demo.mapper.ShopMapper;
import com.lee.redis.train.demo.service.impl.ShopServiceImpl;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.GeoResult;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.domain.geo.GeoReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.lee.redis.train.demo.constants.RedisConstants.CACHE_SHOP_KEY;
import static com.lee.redis.train.demo.constants.RedisConstants.POI_GEO_TYPE_KEY;

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

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private ShopServiceImpl shopService;


    @Test
    void testSaveShop() {
        // 预缓存数据
        Shop shop = shopMapper.selectById(1L);
        caseOperation.cachePreLoader(CACHE_SHOP_KEY, 1L, shop,10L, TimeUnit.SECONDS);
    }

    @Test
    public void importShopDataToRedis() {
        List<Shop> shops = shopService.list();
        // 将所有数据按照 TypeID 分组
        Map<Long, List<Shop>> collect = shops.stream().collect(Collectors.groupingBy(Shop::getTypeId));

        for (Map.Entry<Long, List<Shop>> entry : collect.entrySet()) {
            Long typeId = entry.getKey();
            String redisKey = POI_GEO_TYPE_KEY + typeId;
            List<Shop> value = entry.getValue();
            List<RedisGeoCommands.GeoLocation<String>> locations = new ArrayList<>(value.size());
            // 将所有店铺信息转换为 RedisGeoCommands.GeoLocation
            for (Shop shop : value) {
                locations.add(new RedisGeoCommands.GeoLocation<>(
                        shop.getId().toString(),
                        new Point(Double.parseDouble(shop.getX()), Double.parseDouble(shop.getY()))
                ));
            }
            // 分类批量插入缓存
            stringRedisTemplate.opsForGeo().add(redisKey, locations);
        }
    }

    @Test
    public void test() {
        Double x = 121.42;
        Double y = 31.22;
        int from = (1 - 1) * SystemConstants.DEFAULT_PAGE_SIZE;
        int end = 1 * SystemConstants.DEFAULT_PAGE_SIZE;
        String redisKey = POI_GEO_TYPE_KEY + 1;
        GeoResults<RedisGeoCommands.GeoLocation<String>> redisResults = stringRedisTemplate.opsForGeo()
                .search(redisKey,
                        GeoReference.fromCoordinate(x, y),
                        new Distance(5000),
                        RedisGeoCommands.GeoSearchCommandArgs.newGeoSearchArgs().includeCoordinates().limit(end) // 距离
                );
        // 4. 解析出id
        if (redisResults == null) {
            System.out.println(" ============= null ==================");
        }
        List<GeoResult<RedisGeoCommands.GeoLocation<String>>> content = redisResults.getContent();
        // 截取从 from 到 end 的集合
        List<Long> ids = new ArrayList<>(content.size());
        Map<String, Distance> distanceMap = new HashMap<>(content.size());
        content.stream().skip(1)
                .forEach(item -> {
                            // 获取店铺id (这里的name 存放的是店铺id字符串)
                            String shopId = item.getContent().getName();
                            ids.add(Long.valueOf(shopId));

                            // 获取距离
                            Distance distance = item.getDistance();
                            distanceMap.put(shopId, distance);
                        }
                );
        // 5. 根据id查询店铺
        String idStr = StrUtil.join(",", ids);
        System.out.println("id string ============>: " + idStr);
    }

}
