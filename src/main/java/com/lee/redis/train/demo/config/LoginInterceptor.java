package com.lee.redis.train.demo.config;

import com.lee.redis.train.demo.constants.UserHold;
import com.lee.redis.train.demo.dto.UserDTO;
import com.lee.redis.train.demo.entity.User;
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
        // 1. 获取 session 中的用户信息
        UserDTO user = (UserDTO) request.getSession().getAttribute("user");
        // 2. 判断用户是否存在, 不存在就拦截
        if (user == null) {
            // 拦截
            response.setStatus(401);
            return false;
        }
        // 3. 存在将用户信息保存到 ThreadLocal
        UserHold.setUser(user);
        // 4. 放行
        return true; // 修复：应该返回true而不是false
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