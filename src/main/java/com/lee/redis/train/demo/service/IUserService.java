package com.lee.redis.train.demo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lee.redis.train.demo.dto.LoginFormDTO;
import com.lee.redis.train.demo.entity.Result;
import com.lee.redis.train.demo.entity.User;
import jakarta.servlet.http.HttpSession;


/**
 * @ClassName IUserService
 * @Description 用户服务接口
 * @Author lihongliang
 * @Date 2025/12/19 17:06
 * @Version 1.0
 */
public interface IUserService extends IService<User> {

    /**
     * @Description 发送验证码
     * @param phone 手机号
     * @param session session
     * @return 验证码
     */
    Result sendCode(String phone, HttpSession session);

    /**
     * @Description 登录
     * @param loginForm 登录表单
     * @param session session
     * @return 登录结果
     */
    Result login(LoginFormDTO loginForm, HttpSession session);
}
