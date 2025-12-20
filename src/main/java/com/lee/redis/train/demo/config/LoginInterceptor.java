package com.lee.redis.train.demo.config;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.lee.redis.train.demo.constants.UserHold;
import com.lee.redis.train.demo.dto.UserDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.lee.redis.train.demo.constants.RedisConstants.USER_CACHED_KEY;
import static com.lee.redis.train.demo.constants.RedisConstants.USER_CACHED_TTL;

/**
 * @ClassName LoginInterceptor
 * @Description 登录拦截器, 必须继承 HandlerInterceptor
 * @Author lihongliang
 * @Date 2025/12/19 21:09
 * @Version 1.0
 */
public class LoginInterceptor implements HandlerInterceptor {

    private final StringRedisTemplate stringRedisTemplate;

    /**
     * 构造函数注入 StringRedisTemplate 对象
     * Note: 由于这里不是 Spring Bean, 需要手动注入, 哪里调用拦截器，那里就手动注入
     * @param stringRedisTemplate StringRedisTemplate 对象
     */
    public LoginInterceptor(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    /**
     * 登录拦截 (Controller 执行之前的前置拦截)
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 获取 redis 中的用户信息
        String tokenKey = request.getHeader("Authorization");
        String redisTokenKey = USER_CACHED_KEY + tokenKey;
        if (StrUtil.isBlank(tokenKey)) {
            // 拦截
            response.setStatus(401);
            return false;
        }
        Map<Object, Object> userMap = stringRedisTemplate.opsForHash().entries(redisTokenKey);

        // 判断用户是否存在, 不存在就拦截
        if (userMap.isEmpty()) {
            // 拦截
            response.setStatus(401);
            return false;
        }
        // 存在将用户信息保存到 ThreadLocal
        UserDTO userDTO = BeanUtil.fillBeanWithMap(userMap, new UserDTO(), false);
        UserHold.setUser(userDTO);
        // 将 User 信息延期 30分钟
        stringRedisTemplate.expire(redisTokenKey, USER_CACHED_TTL, TimeUnit.MINUTES);
        // 4. 放行
        return true;
    }

    /**
     * 销毁用户信息,避免内存泄漏 (Controller 执行之后，返回视图之前)
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 销毁用户信息
        UserHold.removeUser();
    }
}