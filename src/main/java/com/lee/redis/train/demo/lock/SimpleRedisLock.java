package com.lee.redis.train.demo.lock;

import cn.hutool.core.lang.UUID;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName SimpleRedisLock
 * @Description Redis 分布式锁
 * @Author lihongliang
 * @Date 2025/12/26 10:41
 * @Version 1.0
 */
public class SimpleRedisLock implements ILock {

    /**
     * 业务名称(Redis Key)
     */
    private String name;

    /**
     * Redis Key 前缀
     */
    private static final String KEY_PREFIX = "lock:";

    /**
     * 锁 value 前缀, 定义为 static 就可以代表一个服务器本身的标识
     */
    private static final String ID_PREFIX = UUID.randomUUID().toString(true) + "-";

    /**
     * Lua 脚本对象
     */
    private static final DefaultRedisScript<Long> UNLOCK_SCRIPT;

    /*
     * 初始化静态 Lua 脚本对象
     */
    static {
        UNLOCK_SCRIPT = new DefaultRedisScript<>();
        UNLOCK_SCRIPT.setLocation(new ClassPathResource("lua/simpleRedisLockUnlock.lua"));
        UNLOCK_SCRIPT.setResultType(Long.class);
    }

    private StringRedisTemplate stringRedisTemplate;

    public SimpleRedisLock(String name, StringRedisTemplate stringRedisTemplate) {
        this.name = name;
        this.stringRedisTemplate = stringRedisTemplate;
    }

    /**
     * 尝试获取锁, 非阻塞机制
     *
     * @param timeoutSec TTL 超时时间
     * @return true: 获取锁成功, false: 获取锁失败
     */
    @Override
    public boolean tryLock(long timeoutSec) {
        // 获取线程 ID
        String threadId = ID_PREFIX + Thread.currentThread().threadId();
        Boolean bool = stringRedisTemplate.opsForValue()
                .setIfAbsent(KEY_PREFIX + name, threadId, timeoutSec, TimeUnit.SECONDS);
        // 直接返回封装的 Long 可能产生空指针异常
        return Boolean.TRUE.equals(bool);
    }

    /**
     * 释放锁
     */
    @Override
    public void unlock() {
        // 调用 Lua 脚本
        stringRedisTemplate.execute(
                UNLOCK_SCRIPT,
                List.of(KEY_PREFIX + name),
                ID_PREFIX + Thread.currentThread().threadId()
        );

//        // 获取线程标识
//        String threadId = ID_PREFIX + Thread.currentThread().threadId();
//        // 获取锁里的标识
//        String id = stringRedisTemplate.opsForValue().get(KEY_PREFIX + name);
//        if (threadId.equals(id)) {
//            stringRedisTemplate.delete(KEY_PREFIX + name);
//        }
//
//        stringRedisTemplate.delete(KEY_PREFIX + name);
    }
}
