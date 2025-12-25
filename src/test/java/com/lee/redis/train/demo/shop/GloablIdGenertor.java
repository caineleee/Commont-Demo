package com.lee.redis.train.demo.shop;

import com.lee.redis.train.demo.utils.RedisIdWorker;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @ClassName GloablIdGenertor
 * @Description
 * @Author lihongliang
 * @Date 2025/12/24 11:13
 * @Version 1.0
 */
@SpringBootTest
public class GloablIdGenertor {

    @Resource
    private RedisIdWorker redisIdWorker;


    @Test
    public void test() {
        long id = redisIdWorker.nextId("order");
        System.out.println(id);
    }

}
