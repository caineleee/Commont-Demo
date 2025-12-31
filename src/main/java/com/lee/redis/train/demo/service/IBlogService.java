package com.lee.redis.train.demo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lee.redis.train.demo.entity.Blog;
import com.lee.redis.train.demo.entity.Result;

/**
 * @ClassName IBlogService
 * @Description 博客服务接口
 * @Author lihongliang
 * @Date 2025/12/30 10:38
 * @Version 1.0
 */
public interface IBlogService extends IService<Blog> {

    /**
     * 查询笔记详情
     * @param id 博客 ID
     * @return 博客详情
     */
    Result queryBlogById(Long id);

    /**
     * 分页查询笔记
     * @param current 当前页
     * @return 分页数据
     */
    Result queryHotBlog(Integer current);

    /**
     * 点赞笔记
     * @param id 笔记 ID
     * @return 点赞结果
     */
    Result likeBlog(Long id);


}
