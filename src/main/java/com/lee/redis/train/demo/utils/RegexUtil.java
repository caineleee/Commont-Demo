package com.lee.redis.train.demo.utils;

import lombok.NonNull;

/**
 * @ClassName RegexUtil
 * @Description 正则校验工具类
 * @Author lihongliang
 * @Date 2025/12/19 17:43
 * @Version 1.0
 */
public class RegexUtil {

    public static boolean isPhone(@NonNull String phone) {
        return phone.matches(RegexPatterns.PHONE_REGEX);
    }

    public static boolean isCode(@NonNull String code) {
        return code.matches(RegexPatterns.VERIFY_CODE_REGEX);
    }

    public static boolean isEmail(@NonNull String email) {
        return email.matches(RegexPatterns.EMAIL_REGEX);
    }

    public static boolean isPassword(@NonNull String password) {
        return password.matches(RegexPatterns.PASSWORD_REGEX);
    }
}
