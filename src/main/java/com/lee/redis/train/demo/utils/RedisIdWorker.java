package com.lee.redis.train.demo.utils;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

/**
 * @ClassName RedisIdWorker
 * @Description Redis 全局唯一ID 生成器
 * @Author lihongliang
 * @Date 2025/12/24 09:41
 * @Version 1.0
 */
@Component
public class RedisIdWorker {

    /**
     * 时间戳起始时间, 2020-01-01 00:00:00
     * 这是偏移时间, 减少时间戳占用的 bit 位, 因为如果使用 unit 1970-01-01 00:00:00 的时间戳, 时间更长久, bit 占用会更长
     */
    private static final long BEGIN_TIMESTAMP = 1577836800L;

    /**
     * 序列号占用的位数
     */
    private static final long COUNT_BITS = 32;

    private final StringRedisTemplate stringRedisTemplate;

    public RedisIdWorker(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    /**
     * 获取下一个ID
     * @param keyPrefix 业务键前缀
     * @return 下一个ID号
     */
    public long nextId(String keyPrefix) {
        // 1. 生成时间戳(秒) - 符号位不需要管, 只要是正数就可以
        LocalDateTime now = LocalDateTime.now();
        long nowSecond = now.toEpochSecond(ZoneOffset.UTC);
        long timestamp = nowSecond - BEGIN_TIMESTAMP; // 获取从2020-01-01 00:00:00 开始的秒数(时间戳)

        // 2. 生成序列号并拼接时间戳

        // 这里必须让 Redis key 拼接一个时间, 否则同一个业务会一直用同一个 Key, 这样就算是 1^{32} 也可能会用尽bit位
        // 另外这里拼接一个时间, 也可以看做一个天然的过滤, 也便于统计一天内的数据新增量
        String today = now.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        long count = stringRedisTemplate.opsForValue().increment("incr:" + keyPrefix + ":" + today);

        // 这里 long 拼接将 timestamp 左移 32 位, 然后与 count 进行或运算,
        // timestamp 左移 32 位, 那么右边 32位为自动填充 0
        // count 正好32位, 这里进行或运算, count 为0时, 0 | 0 运算=0, 而 count 为1时, 0 | 1 运算=1
        // 经过或运算正好将 count 填充进 timestamp 32 低 bit 位中
        return timestamp << COUNT_BITS | count;
    }

}
