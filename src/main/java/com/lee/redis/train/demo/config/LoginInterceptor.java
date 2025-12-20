package com.lee.redis.train.demo.config;

import com.lee.redis.train.demo.constants.UserHold;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * @ClassName LoginInterceptor
 * @Description 登录拦截器, 必须继承 HandlerInterceptor
 * @Author lihongliang
 * @Date 2025/12/19 21:09
 * @Version 1.0
 */
public class LoginInterceptor implements HandlerInterceptor {

    /**
     * 登录拦截 (Controller 执行之前的前置拦截)
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 获取 ThreadLocal 中的用户信息, 没有就拦截
        return UserHold.getUser() != null;
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