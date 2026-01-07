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
     * 查询指定用户的笔记 (滚动分页) // TODO ??? 注释不对
     * @param max 最大时间戳
     * @param offset 笔记偏移量
     * @return 笔记列表
     */
    Result queryBlogOfFollow(Long max, Integer offset);

    /**
     * 点赞笔记
     * @param id 笔记 ID
     * @return 点赞结果
     */
    Result likeBlog(Long id);

    /**
     * 查询笔记点赞信息(获取点赞数 + top5 user)
     * @param id 笔记 ID
     * @return 点赞信息
     */
    Result queryBlogLikes(Long id);

    /**
     * 创建笔记
     * @param blog 笔记信息
     * @return 笔记 ID
     */
    Result saveBlog(Blog blog);


}
