package com.lee.redis.train.demo.utils;

/**
 * @ClassName RegexPatterns
 * @Description 正则表达式工具类
 * @Author lihongliang
 * @Date 2025/12/19 17:36
 * @Version 1.0
 */
public class RegexPatterns {

    /**
     * 手机号正则表达式
     */
    public static final String PHONE_REGEX = "^1[3-9]\\d{9}$";

    /**
     * 邮箱正则表达式
     */
    public static final String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\\\.[a-zA-Z]{2,}$";

    /**
     * 密码正则表达式
     */
    public static final String PASSWORD_REGEX = "^^[a-zA-Z0-9]{6}$";

    /**
     * 验证码正则表达式
     */
    public static final String VERIFY_CODE_REGEX = "^\\d{6}$";
}
