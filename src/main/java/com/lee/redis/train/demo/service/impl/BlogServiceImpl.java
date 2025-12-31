package com.lee.redis.train.demo.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lee.redis.train.demo.constants.UserHold;
import com.lee.redis.train.demo.dto.UserDTO;
import com.lee.redis.train.demo.entity.Blog;
import com.lee.redis.train.demo.entity.Result;
import com.lee.redis.train.demo.entity.User;
import com.lee.redis.train.demo.mapper.BlogMapper;
import com.lee.redis.train.demo.service.IBlogService;
import com.lee.redis.train.demo.service.IUserService;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.lee.redis.train.demo.constants.SystemConstants.DEFAULT_PAGE_SIZE;

/**
 * @ClassName BlogServiceImpl
 * @Description 博客服务类
 * @Author lihongliang
 * @Date 2025/12/30 10:39
 * @Version 1.0
 */
@Service
public class BlogServiceImpl extends ServiceImpl<BlogMapper, Blog> implements IBlogService {

    @Resource
    private IUserService userService;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 查询最热笔记
     * @param current 当前页
     * @return 热门笔记
     */
    @Override
    public Result queryHotBlog(Integer current) {
        // 查询当前页数据
        Page<Blog> page = query()
                .orderByDesc("liked")
                .page(new Page<>(current, DEFAULT_PAGE_SIZE));
        List<Blog> records = page.getRecords();

        records.forEach(blob -> {
            queryBlobUserInfo(blob);  // 聚合用户信息
            isBlogLiked(blob);  // 判断用户是否点赞
        });
        // 返回数据
        return Result.success(records);
    }

    /**
     * 根据 ID 查询博客
     * @param id 博客 ID
     * @return 博客信息
     */
    @Override
    public Result queryBlogById(Long id) {
        Blog blog = getById(id);
        if (blog == null) {
            return Result.notFount("笔记不存在");
        }
        // 聚合用户信息并返回
        queryBlobUserInfo(blog);
        // 判断用户是否点赞
        isBlogLiked(blog);
        return Result.success(blog);
    }

    @Override
    public Result likeBlog(Long id) {
        // 判断用户是否已经点赞, 没有点赞则Db liked +1, 并添加到 redis
        UserDTO user = UserHold.getUser();
        String key = "blog:liked:" + id;
        Boolean isMember = stringRedisTemplate.opsForSet().isMember(key, user.getId().toString());
        if (!Boolean.TRUE.equals(isMember)) {
            // 已点赞
            boolean update = update().setSql("liked = liked - 1").eq("id", id).update();
            if (update) {
                stringRedisTemplate.opsForSet().remove(key, user.getId().toString());
            }
        } else {
            boolean update = update().setSql("liked = liked + 1").eq("id", id).update();
            if (update) {
                stringRedisTemplate.opsForSet().add(key, user.getId().toString());
            }
        }
        // 如果点过赞则Db liked -1, 把用户从 redis set 中移除
        return Result.success();
    }

    /**
     * 查询笔记用户信息
     * @param blog 博客信息
     */
    private void queryBlobUserInfo(Blog blog) {
        Long userId = blog.getUserId();
        User user = userService.getById(userId);
        blog.setName(user.getNickName());
        blog.setIcon(user.getIcon());
    }

    /**
     * 判断用户是否点赞
     * @param blog 笔记信息
     */
    private void isBlogLiked(Blog blog) {
        // 判断用户是否已经点赞, 没有点赞则Db liked +1, 并添加到 redis
        UserDTO user = UserHold.getUser();
        if (user == null) {
            return;
        }
        String key = "blog:liked:" + blog.getId();
        Boolean isMember = stringRedisTemplate.opsForSet().isMember(key, user.getId().toString());
        blog.setIsLike(Boolean.TRUE.equals(isMember));
    }
}
