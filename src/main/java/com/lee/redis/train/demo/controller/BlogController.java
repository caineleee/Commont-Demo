package com.lee.redis.train.demo.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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

import java.util.List;

import static com.lee.redis.train.demo.constants.SystemConstants.MAX_PAGE_SIZE;

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
        return blogService.saveBlog(blog);
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
     * 查询指定用户的笔记 (分页)
     * @param current  当前页
     * @param id  用户 ID
     * @return  笔记列表
     */
    @GetMapping("/of/user")
    public Result queryBlogByUserId(@RequestParam(value = "current",  defaultValue = "1") Integer current,
                                    @RequestParam("id") Long id) {
        // 分页查询指定用户的笔记
        Page<Blog> blogs = blogService.query().eq("user_id", id).page(new Page<>(current, MAX_PAGE_SIZE));
        List<Blog> records = blogs.getRecords();
        return Result.success(records);
    }

     /**
     * 查询当前用户关注的所有用户的笔记 (滚动分页)
     * @param max  最大 ID
     * @param offset  偏移量
     * @return  笔记列表
     */
    @GetMapping ("/of/follow")
    public Result queryBlogOfFollow(@RequestParam("lastId")  Long max,
                                    @RequestParam(value = "offset", defaultValue = "0") Integer offset) {
        return blogService.queryBlogOfFollow(max, offset);
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

     /**
     * 查询笔记点赞数
     * @param id  笔记 ID
     * @return  点赞数
     */
    @GetMapping("/likes/{id}")
    public Result queryBlogLikes(@PathVariable("id") Long id) {
        return blogService.queryBlogLikes(id);
    }



}
