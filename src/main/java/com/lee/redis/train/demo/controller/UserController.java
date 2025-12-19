package com.lee.redis.train.demo.controller;

import com.lee.redis.train.demo.constants.UserHold;
import com.lee.redis.train.demo.dto.LoginFormDTO;
import com.lee.redis.train.demo.entity.Result;
import com.lee.redis.train.demo.entity.User;
import com.lee.redis.train.demo.service.IUserService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName UserController
 * @Description 用户控制器
 * @Author lihongliang
 * @Date 2025/12/19 17:02
 * @Version 1.0
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private IUserService userService;

    @PostMapping("/code")
    public Result sendCode(@RequestParam String phone, HttpSession session) {
        return userService.sendCode(phone, session);
    }

    @PostMapping("/login")
    public Result login(@RequestBody LoginFormDTO loginForm, HttpSession session) {
        return userService.login(loginForm, session);
    }

    @GetMapping("/me")
    public Result me() {
        User user = UserHold.getUser();
        return Result.success(user);
    }
}
