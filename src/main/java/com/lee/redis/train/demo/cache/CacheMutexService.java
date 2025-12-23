package com.lee.redis.train.demo.cache;

import cn.hutool.core.util.BooleanUtil;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * @ClassName CacheMutexService
 * @Description 缓存互斥锁类
 * @Author lihongliang
 * @Date 2025/12/22 21:27
 * @Version 1.0
 */
@Component
public class CacheMutexService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 尝试获取锁
     * @param key 锁的 key
     * @return 是否获取成功
     */
    public boolean tryLock(String key) {
        Boolean flag = stringRedisTemplate.opsForValue().setIfAbsent(key, "1", 10L, TimeUnit.SECONDS);
        // return flag; 不能直接返回 flag, 因为我们需要把封装类 Boolean 转成基本类型 boolean 返回
        // 直接返回 flag 会导致返回过程中需要拆包, 可能出现空指针问题.
        // 所以这里使用工具类 BooleanUtil
        return BooleanUtil.isTrue(flag);
    }

    /**
     * 释放锁
     * @param key 锁的 key
     */
    public void unlock(String key) {
        stringRedisTemplate.delete(key);
    }
}
