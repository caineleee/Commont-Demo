package com.lee.redis.train.demo.config.interceptor;

import jakarta.annotation.Resource;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
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

    @Resource
    private StringRedisTemplate stringRedisTemplate;

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
                        "/voucher/**",
                        "/user/code",
                        "/user/login",
                        "/blog/hot"
                ).order(1);
        // 注册 RefreshTokenInterceptor 拦截器,
        // 使用 Order 方法保证两个拦截器执行顺序, 数字越小越先执行.
        registry.addInterceptor(new ReFreshTokenInterceptor(stringRedisTemplate)).order(0);
    }
}
