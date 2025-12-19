package com.lee.redis.train.demo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @ClassName Result
 * @Description 响应实体
 * @Author lihongliang
 * @Date 2025/12/19 10:59
 * @Version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class Result {
    private int code;
    private String message;
    private String detail;
    private Object data;
    private long timestamp;
    private int total;

    public static Result success(String message) {
        return new Result()
                .setCode(200)
                .setMessage(message)
                .setTimestamp(System.currentTimeMillis());
    }

    public static Result success(Object data) {
        return new Result()
                .setCode(200)
                .setMessage("success")
                .setData(data)
                .setTimestamp(System.currentTimeMillis());
    }

    public static <T> Result success(List<T> data) {
        return new Result()
                .setCode(200)
                .setMessage("success")
                .setData(data)
                .setTotal(data.size())
                .setTimestamp(System.currentTimeMillis());
    }

    public static <T> Result success(List<T> data, int total) {
        return new Result()
                .setCode(200)
                .setMessage("success")
                .setData(data)
                .setTotal(total)
                .setTimestamp(System.currentTimeMillis());
    }

    public static Result error(String message) {
        return new Result()
                .setCode(500)
                .setMessage("failure")
                .setDetail(message)
                .setTimestamp(System.currentTimeMillis());
    }
}
