package com.lee.redis.train.demo.dto;

import lombok.Data;

/**
 * @ClassName LoginFormDTO
 * @Description 登录DTO
 * @Author lihongliang
 * @Date 2025/12/19 18:09
 * @Version 1.0
 */
@Data
public class LoginFormDTO {
    private String phone;
    private String code;
    private String password;
}
