package com.lee.redis.train.demo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @ClassName MVCConfig
 * @Description
 * @Author lihongliang
 * @Date 2025/12/19 22:00
 * @Version 1.0
 */
@Configuration
public class MVCConfig implements WebMvcConfigurer {

    /**
     * 添加拦截器
     * 创建后的拦截器(LoginInterceptor) 自身无法生效, 需要添加到拦截器列表中
     * @param registry 拦截器注册器
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginInterceptor())
                // 默认拦截所有请求, 部分请求排除
                .excludePathPatterns(
                        "shop/**",
                        "/shop-type/**",
                        "/upload/**",
                        "/voucher /**",
                        "/user/code",
                        "/user/login",
                        "/blog/hot"
                );
    }
}
