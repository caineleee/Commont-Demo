package com.lee.redis.train.demo.controller;

import cn.hutool.core.bean.BeanUtil;
import com.lee.redis.train.demo.constants.UserHold;
import com.lee.redis.train.demo.dto.LoginFormDTO;
import com.lee.redis.train.demo.dto.UserDTO;
import com.lee.redis.train.demo.entity.Result;
import com.lee.redis.train.demo.entity.User;
import com.lee.redis.train.demo.service.IUserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
        UserDTO user = UserHold.getUser();
        return Result.success(user);
    }

    /**
     * 查询用户信息 (用于查看他人或自己的个人主页)
     * @param id 用户ID
     * @return 用户信息
     */
    @GetMapping("/{id}")
    public Result queryUserById(@PathVariable("id") Long id) {
        User user = userService.getById(id);
        if (user == null) {
            return Result.notFount("用户不存在");
        }
        return Result.success(BeanUtil.copyProperties(user, UserDTO.class));
    }

    @PostMapping("/sign")
    public Result sign() {
        return userService.sign();
    }
}
