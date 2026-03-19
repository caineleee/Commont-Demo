package com.lee.redis.train.demo.entity;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

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

    public static Result success() {
        return new Result()
                .setCode(OK.value())
                .setMessage("success")
                .setTimestamp(System.currentTimeMillis());
    }

    public static Result success(String message) {
        return new Result()
                .setCode(OK.value())
                .setMessage("success")
                .setDetail(message)
                .setTimestamp(System.currentTimeMillis());
    }

    public static Result success(String message, Object data) {
        return new Result()
                .setCode(OK.value())
                .setMessage("success")
                .setDetail(message)
                .setData(data)
                .setTimestamp(System.currentTimeMillis());
    }

    public static Result success(Object data) {
        return new Result()
                .setCode(OK.value())
                .setMessage("success")
                .setData(data)
                .setTimestamp(System.currentTimeMillis());
    }

    public static <T> Result success(List<T> data) {
        return new Result()
                .setCode(OK.value())
                .setMessage("success")
                .setData(data)
                .setTotal(data.size())
                .setTimestamp(System.currentTimeMillis());
    }

    public static <T> Result success(List<T> data, int total) {
        return new Result()
                .setCode(OK.value())
                .setMessage("success")
                .setData(data)
                .setTotal(total)
                .setTimestamp(System.currentTimeMillis());
    }

    public static <T> Result success(Page<T> data) {
        return new Result()
                .setCode(OK.value())
                .setMessage("success")
                .setData(data.getRecords())
                .setTotal((int) data.getSize())
                .setTimestamp(System.currentTimeMillis());
    }

    public static Result error(String message) {
        return new Result()
                .setCode(INTERNAL_SERVER_ERROR.value())
                .setMessage("failure")
                .setDetail(message)
                .setTimestamp(System.currentTimeMillis());
    }

    public static Result notFount() {
        return notFount("");
    }

    public static Result notFount(String message) {
        return new Result()
                .setCode(NOT_FOUND.value())
                .setMessage("failure")
                .setDetail(message)
                .setTimestamp(System.currentTimeMillis());
    }

    public static Result unAuthorized(String message) {
        return new Result()
                .setCode(UNAUTHORIZED.value())
                .setMessage("failure")
                .setDetail(message)
                .setTimestamp(System.currentTimeMillis());
    }
}
