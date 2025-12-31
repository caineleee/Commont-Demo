package com.lee.redis.train.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lee.redis.train.demo.entity.Blog;
import org.apache.ibatis.annotations.Mapper;

/**
 * @ClassName BlogMapper
 * @Description 博客 Mapper
 * @Author lihongliang
 * @Date 2025/12/30 10:37
 * @Version 1.0
 */
@Mapper
public interface BlogMapper extends BaseMapper<Blog> {
}
