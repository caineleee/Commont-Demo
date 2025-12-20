package com.lee.redis.train.demo.constants;

import com.lee.redis.train.demo.dto.UserDTO;
import com.lee.redis.train.demo.entity.User;

/**
 * @ClassName UserHold
 * @Description 用户常量
 * @Author lihongliang
 * @Date 2025/12/19 21:55
 * @Version 1.0
 */
public class UserHold {

    public static final ThreadLocal<UserDTO> USER_HOLD = new ThreadLocal<>();

    public static UserDTO getUser() {
        return USER_HOLD.get();
    }

    public static void setUser(UserDTO user) {
        USER_HOLD.set(user);
    }

    public static void removeUser() {
        USER_HOLD.remove();
    }
}
