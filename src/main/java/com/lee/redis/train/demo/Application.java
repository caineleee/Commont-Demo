package com.lee.redis.train.demo;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.lee.redis.train.demo.mapper")
public class Application {

    public static void main(String[] args) {
        // 设置MyBatis相关系统属性以解决版本兼容性问题
        System.setProperty("mybatis.lazy-initialization", "true");
        System.setProperty("spring.mybatis.skip-type-checks", "true");
        SpringApplication.run(Application.class, args);
    }
}