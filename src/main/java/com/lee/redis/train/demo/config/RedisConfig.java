package com.lee.redis.train.demo.config;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lee.redis.train.demo.entity.Shop;
import com.lee.redis.train.demo.service.IShopService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.lee.redis.train.demo.constants.RedisConstants.CACHE_SHOP_KEY;

/**
 * @ClassName RedisConfig
 * @Description 用于缓存预热功能
 * @Author lihongliang
 * @Date 2026/4/18 17:20
 * @Version 1.0
 */
@Component
public class RedisConfig implements InitializingBean {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private IShopService shopService;

    /**
     * Invoked by the containing {@code BeanFactory} after it has set all bean properties
     * and satisfied {@link BeanFactoryAware}, {@code ApplicationContextAware} etc.
     * <p>This method allows the bean instance to perform validation of its overall
     * configuration and final initialization when all bean properties have been set.
     *
     * @throws Exception in the event of misconfiguration (such as failure to set an
     *                   essential property) or if initialization fails for any other reason
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        // 初始化缓存
        // 查询需要预热的热点数据, 做个简单查询示例
        List<Shop> shops = shopService.list(new QueryWrapper<>(Shop.class).eq("id", 1L));
        // 将数据缓存入 Redis
        for (Shop shop : shops) {
            stringRedisTemplate.opsForValue().set(CACHE_SHOP_KEY + shop.getId(), JSONUtil.toJsonStr(shop));
        }
    }
}
