package com.lee.redis.train.demo.exception;

import com.lee.redis.train.demo.entity.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @ClassName WebExceptionAdvice
 * @Description 通用异常处理
 * @Author lihongliang
 * @Date 2025/12/19 10:27
 * @Version 1.0
 */
@Slf4j
@RestControllerAdvice
public class WebExceptionAdvice {

    @ExceptionHandler(RuntimeException.class)
    public String handleException(RuntimeException e) {
        log.error("运行时异常: {}", e.getMessage(), e);
        return "运行时异常: " + e.getMessage();
//        return Result.error(e.getMessage());
    }
}
