package com.lee.redis.train.demo.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lee.redis.train.demo.cache.CacheOperation;
import com.lee.redis.train.demo.constants.SystemConstants;
import com.lee.redis.train.demo.entity.Result;
import com.lee.redis.train.demo.entity.Shop;
import com.lee.redis.train.demo.mapper.ShopMapper;
import com.lee.redis.train.demo.service.IShopService;
import jakarta.annotation.Resource;
import org.apache.tomcat.util.buf.StringUtils;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.GeoResult;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.domain.geo.GeoReference;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.lee.redis.train.demo.constants.RedisConstants.CACHE_SHOP_KEY;
import static com.lee.redis.train.demo.constants.RedisConstants.CACHE_SHOP_TTL;
import static com.lee.redis.train.demo.constants.RedisConstants.POI_GEO_TYPE_KEY;

/**
 * @ClassName ShopServiceImpl
 * @Description
 * @Author lihongliang
 * @Date 2025/12/20 20:59
 * @Version 1.0
 */
@Service
public class ShopServiceImpl extends ServiceImpl<ShopMapper, Shop> implements IShopService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private CacheOperation cacheOperation;

    /**
     * 缓存预加载线程池
     */


    @Override
    public Result queryShopById(Long id) {
        // 缓存穿透
//        Shop shop = queryShopWithPassThrough(id);
//        Shop shop = cacheOperation.queryShopWithPassThrough(
//                CACHE_SHOP_KEY, id, Shop.class, this::getById, CACHE_SHOP_TTL, TimeUnit.MINUTES);
        // 缓存击穿 - 互斥锁
//        Shop shop = queryShopWithMutex(id);
        // 缓存击穿 - 逻辑过期
//        Shop shop = queryShopWithLogicExpire(id);
        Shop shop = cacheOperation.queryShopWithLogicExpire(
                CACHE_SHOP_KEY, id, Shop.class, this::getById, CACHE_SHOP_TTL, TimeUnit.MINUTES);

        if (shop == null) {
            return Result.notFount("商铺不存在");
        }

        return Result.success(List.of(shop));
    }


    /**
     * 更新商铺信息
     *
     * @param shop 商铺信息
     * @return 更新结果
     */
    @Override
    @Transactional
    public Result updateShop(Shop shop) {
        if (shop.getId() == null) {
            return Result.error("店铺ID不能为空");
        }
        // 1. 更新数据库
        updateById(shop);
        // 2. 删除缓存
        stringRedisTemplate.delete(CACHE_SHOP_KEY + shop.getId());
        // 3. 返回更新结果
        return Result.success("修改成功");
    }

    /**
     * 根据商铺类型分页查询商铺信息
     *
     * @param typeId 商铺类型
     * @param current 页码
     * @param x 经度
     * @param y 纬度
     * @return 商铺列表
     */
    @Override
    public Result queryShopByType(Integer typeId, Integer current, Double x, Double y) {
        // 1. 判断是否需要根据坐标查询
        if (x == null || y == null) {
            // 不需要坐标查询, 按照数据库查
            Page<Shop> page = query()
                    .eq("type_id", typeId)
                    .page(new Page<>(current, SystemConstants.DEFAULT_PAGE_SIZE));
            return Result.success(page);
        }

        // 2. 计算分页参数
        int from = (current - 1) * SystemConstants.DEFAULT_PAGE_SIZE;
        int end = current * SystemConstants.DEFAULT_PAGE_SIZE;

        // 3. 查询redis, 按照距离排序, 分页. 结果: shopId / distance
        String key = POI_GEO_TYPE_KEY + typeId;
        // 获取从第一条到指定end 的数据(只能从第一条开始)
        GeoResults<RedisGeoCommands.GeoLocation<String>> redisResults = stringRedisTemplate.opsForGeo()
                .search(key,
                        GeoReference.fromCoordinate(x, y),
                        new Distance(5000), // 搜索范围(半径)这里没有指定单位,默认为米. 如果需要返回其他单位,这里就需要指定单位
                        RedisGeoCommands.GeoSearchCommandArgs.newGeoSearchArgs().includeCoordinates().limit(end) // 距离
                );

        // 4. 解析出id
        if (redisResults == null) {
            return Result.notFount();
        }
        List<GeoResult<RedisGeoCommands.GeoLocation<String>>> content = redisResults.getContent();
        if (content.size() < from) {
            return Result.notFount();
        }
        // 截取从 from 到 end 的集合
        List<Long> ids = new ArrayList<>(content.size());
        Map<String, Distance> distanceMap = new HashMap<>(content.size());
        content.stream().skip(from)
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
        List<Shop> shops = query().in("id", ids).last("ORDER BY FIELD(id, " + idStr + ")").list();
        // 遍历 shops 添加距离字段
        for (Shop shop : shops){
            shop.setDistance(distanceMap.get(shop.getId().toString()).getValue());
        }
        return Result.success(shops);
    }
}
