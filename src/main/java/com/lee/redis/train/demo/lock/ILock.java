package com.lee.redis.train.demo.lock;

/**
 * @ClassName ILock
 * @Description 锁接口
 * @Author lihongliang
 * @Date 2025/12/26 10:36
 * @Version 1.0
 */
public interface ILock {

    /**
     * 尝试获取锁, 非阻塞机制
     * @param timeoutSec TTL 超时时间
     * @return true: 获取锁成功, false: 获取锁失败
     */
    boolean tryLock(long timeoutSec);

    /**
     * 释放锁
     */
    void unlock();
}
