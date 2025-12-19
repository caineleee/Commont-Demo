package com.lee.redis.train.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lee.redis.train.demo.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * @ClassName UserMapper
 * @Description 用户 Mapper
 * @Author lihongliang
 * @Date 2025/12/19 17:10
 * @Version 1.0
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
}
