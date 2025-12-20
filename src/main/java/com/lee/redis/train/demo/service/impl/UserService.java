package com.lee.redis.train.demo.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lee.redis.train.demo.dto.LoginFormDTO;
import com.lee.redis.train.demo.dto.UserDTO;
import com.lee.redis.train.demo.entity.Result;
import com.lee.redis.train.demo.entity.User;
import com.lee.redis.train.demo.exception.WebExceptionAdvice;
import com.lee.redis.train.demo.mapper.UserMapper;
import com.lee.redis.train.demo.service.IUserService;
import com.lee.redis.train.demo.utils.RegexUtil;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static com.lee.redis.train.demo.constants.SystemConstants.USER_NICK_NAME_PREFIX;

/**
 * @ClassName UserService
 * @Description 用户服务类
 * @Author lihongliang
 * @Date 2025/12/19 17:10
 * @Version 1.0
 */
@Slf4j
@Service
public class UserService extends ServiceImpl<UserMapper, User> implements IUserService {

    /**
     * @Description 发送验证码
     * @param phone 手机号
     * @param session session
     * @return 验证码
     */
    @Override
    public Result sendCode(String phone, HttpSession session) {
        // 1. 校验手机号
        // 2. 不符合返回异常信息
        if (!RegexUtil.isPhone(phone)) {
            return Result.error("手机号格式错误");
        }
        // 3. 符合，生成验证码
        String code = RandomUtil.randomNumbers(6);
        // 4. 保存验证码到 session
        session.setAttribute("code", code);
        // 5. 发送验证码, 实现比较复杂, 先模拟
        log.info("发送验证码成功, 验证码: {}", code);
        // 返回发送结果, 这里返回 code 为了测试方便
        return Result.success("发送成功", code);
    }

    /**
     * @Description 登录功能
     * @param loginForm 登录信息
     * @param session session
     * @return 登录结果
     */
    @Override
    public Result login(LoginFormDTO loginForm, HttpSession session) {
        // 校验手机号
        if (!RegexUtil.isPhone(loginForm.getPhone())) {
            return Result.error("手机号格式错误");
        }
        // 校验验证码
        if (!RegexUtil.isCode(loginForm.getCode())) {
            return Result.error("验证码格式错误");
        }
        if (!loginForm.getCode().equals(session.getAttribute("code"))) {
            return Result.error("验证码错误");
        }
        // 根据手机号查询数据库用户信息
        User user = query().eq("phone", loginForm.getPhone()).one();
        // 判断用户是否存在, 不存在，注册用户
        if (user == null) {
            user = createUserWithPhone(loginForm.getPhone());
            if (user == null) {
                log.error("创建用户失败 Phone:{}", loginForm.getPhone());
                return Result.error("创建用户失败");
            }
        }
        // 10. 保存用户信息到 session
        session.setAttribute("user", BeanUtil.copyProperties(user, UserDTO.class));
        // 11. 返回登录结果
        return Result.success("登录成功");
    }

    /**
     * @Description 创建用户
     * @param phone 手机号
     * @return 用户
     */
    private User createUserWithPhone(String phone) {
        User user = new User()
                .setPhone(phone)
                .setNickName(USER_NICK_NAME_PREFIX + RandomUtil.randomString(10))
                .setCreateTime(LocalDateTime.now())
                .setUpdateTime(LocalDateTime.now())
                .setEnabled(1);
        try {
            save(user);
        } catch (DuplicateKeyException e) {
            // 处理唯一约束冲突（如手机号已存在）演示用, 此处只简单处理，实际项目需处理更复杂的逻辑
            log.warn("手机号或用户名已存在, 注册失败", e);
        }
        return user;
    }
}
