package com.lee.redis.train.demo.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lee.redis.train.demo.constants.UserHold;
import com.lee.redis.train.demo.entity.Result;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
        if (UserHold.getUser() == null) {
            Result result = Result.unAuthorized("未登录,请重新登录");
            ObjectMapper objectMapper = new ObjectMapper();
            String json = objectMapper.writeValueAsString(result);

            // 设置响应头和状态码
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding("UTF-8");

            // 写入响应体
            response.getWriter().write(json);
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return false;
        }
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