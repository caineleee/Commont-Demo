package com.lee.redis.train.demo.controller;

import com.lee.redis.train.demo.constants.UserHold;
import com.lee.redis.train.demo.dto.UserDTO;
import com.lee.redis.train.demo.entity.Blog;
import com.lee.redis.train.demo.entity.Result;
import com.lee.redis.train.demo.service.IBlogService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName BlogController
 * @Description
 * @Author lihongliang
 * @Date 2025/12/30 16:56
 * @Version 1.0
 */
@RestController
@RequestMapping("/blog")
public class BlogController {

    @Resource
    private IBlogService blogService;

    /**
     * 创建笔记
     * @param blog  笔记信息
     * @return  笔记 ID
     */
    @PostMapping
    public Result saveBlog(@RequestBody Blog blog) {
        // 获取登录用户
        UserDTO  user = UserHold.getUser();
        blog.setUserId(user.getId());
        // 保存笔记
        blogService.save(blog);
        return Result.success(blog.getId());
    }

    /**
     * 分页查询笔记
     * @param current  当前页
     * @return  笔记列表
     */
    @GetMapping ("/hot")
    public Result queryHotBlog(@RequestParam("current") Integer current) {
        return blogService.queryHotBlog(current);
    }

    /**
     *  查询笔记
     * @param id  笔记 ID
     * @return  笔记信息
     */
    @GetMapping("/{id}")
    public Result queryBlogById(@PathVariable("id") Long id) {
        return blogService.queryBlogById(id);
    }

     /**
     * 点赞笔记
     * @param id  笔记 ID
     * @return  操作结果
     */
    @PutMapping("/like/{id}")
    public Result likeBlog(@PathVariable("id") Long id) {
        return blogService.likeBlog(id);
    }

    @GetMapping("/likes/{id}")
    public Result queryBlogLikes(@PathVariable("id") Long id) {
        return blogService.queryBlogLikes(id);
    }



}
