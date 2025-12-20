package com.lee.redis.train.demo.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lee.redis.train.demo.dto.LoginFormDTO;
import com.lee.redis.train.demo.dto.UserDTO;
import com.lee.redis.train.demo.entity.Result;
import com.lee.redis.train.demo.entity.User;
import com.lee.redis.train.demo.mapper.UserMapper;
import com.lee.redis.train.demo.service.IUserService;
import com.lee.redis.train.demo.utils.RegexUtil;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.lee.redis.train.demo.constants.RedisConstants.LOGIN_CODE_KEY;
import static com.lee.redis.train.demo.constants.RedisConstants.LOGIN_CODE_TTL;
import static com.lee.redis.train.demo.constants.RedisConstants.USER_CACHED_KEY;
import static com.lee.redis.train.demo.constants.RedisConstants.USER_CACHED_TTL;
import static com.lee.redis.train.demo.constants.SystemConstants.USER_NICK_NAME_PREFIX;

/**
 * @ClassName UserServiceImpl
 * @Description 用户服务类
 * @Author lihongliang
 * @Date 2025/12/19 17:10
 * @Version 1.0
 */
@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * @Description 发送验证码
     * @param phone 手机号
     * @param session session
     * @return 验证码
     */
    @Override
    public Result sendCode(String phone, HttpSession session) {
        // 校验手机号, 不符合返回异常信息
        if (!RegexUtil.isPhone(phone)) {
            return Result.error("手机号格式错误");
        }
        // 符合，生成验证码
        String code = RandomUtil.randomNumbers(6);
        // 保存验证码到 Redis, 并设置有效期 5 分钟
        stringRedisTemplate.opsForValue().set(LOGIN_CODE_KEY + phone, code, LOGIN_CODE_TTL, TimeUnit.MINUTES);
        // 发送验证码, 实现比较复杂, 先模拟
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
        // 从 Redis 中获取验证码校验
        String cacheCode = stringRedisTemplate.opsForValue().get(LOGIN_CODE_KEY + loginForm.getPhone());
        if (cacheCode == null || !cacheCode.equals(loginForm.getCode())) {
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
        // 保存用户信息到 Redis
        // 1. 生成随机 Token(此处使用 UUID 方便一些) 作为登录令牌
        String uuid = UUID.randomUUID().toString(true);
        String tokenKey = USER_CACHED_KEY + uuid;
        // 2. 将 UserDTO 转为 Hash 存储到 Redis, 设置有效期 30 分钟
        UserDTO userDTO = BeanUtil.copyProperties(user, UserDTO.class);
        Map<String, Object> userMap = BeanUtil.beanToMap(
                // Source Bean 和 target Map
                userDTO, new HashMap<>(),
                // 设置 BeanToMap 转换规则
                CopyOptions.create()
                    // 过滤控制
                    .setIgnoreNullValue(true)
                    // 将非字符串 value 转换为 String, 否则 Redis存入数据会报类型异常
                    .setFieldValueEditor((field, value) -> value.toString()));
        stringRedisTemplate.opsForHash().putAll(tokenKey, userMap);
        stringRedisTemplate.expire(tokenKey, USER_CACHED_TTL, TimeUnit.MINUTES);

        // 3. 将 Token 返回到客户端
        return Result.success("登录成功", uuid);
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
