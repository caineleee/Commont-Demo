package com.lee.redis.train.demo.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lee.redis.train.demo.constants.UserHold;
import com.lee.redis.train.demo.dto.ScrollResultDTO;
import com.lee.redis.train.demo.dto.UserDTO;
import com.lee.redis.train.demo.entity.Blog;
import com.lee.redis.train.demo.entity.Follow;
import com.lee.redis.train.demo.entity.Result;
import com.lee.redis.train.demo.entity.User;
import com.lee.redis.train.demo.mapper.BlogMapper;
import com.lee.redis.train.demo.service.IBlogService;
import com.lee.redis.train.demo.service.IFollowService;
import com.lee.redis.train.demo.service.IUserService;
import jakarta.annotation.Resource;
import jodd.util.StringUtil;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static com.lee.redis.train.demo.constants.RedisConstants.BLOB_LIKE_KEY;
import static com.lee.redis.train.demo.constants.RedisConstants.FEED_KEY;
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

    @Resource
    private IFollowService followService;

    /**
     * 创建笔记
     * @param blog 笔记信息
     * @return 笔记 ID
     */
    @Override
    public Result saveBlog(Blog blog) {
        // 获取当前用户
        UserDTO user = UserHold.getUser();
        blog.setUserId(user.getId());

        // 保存笔记
        boolean isSaved = save(blog);
        if (!isSaved) {
            return Result.error("笔记保存失败");
        }
        // 查询笔记作者的所有关注者
        List<Follow> follows = followService.query().eq("follow_id", user.getId()).list();
        // 推送笔记 ID 给所有粉丝
        for(Follow follow : follows) {
            // 获取粉丝 ID
            Long followId = follow.getUserId();
            // 将笔记 ID 推入关注者的收件箱(Redis feed zset 集合)
            stringRedisTemplate.opsForZSet().add(FEED_KEY + followId, blog.getId().toString(), System.currentTimeMillis());
        }

        return Result.success(blog.getId());
    }

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
     * 查询指定用户的笔记 (滚动分页)
     * @param max  最大时间戳
     * @param offset  时间戳偏移量
     * @return 笔记列表
     */
    @Override
    public Result queryBlogOfFollow(Long max, Integer offset) {
        // 1.获取当前用户, 查询 feed 收件箱
        UserDTO user = UserHold.getUser();
        Set<ZSetOperations.TypedTuple<String>> typedTuples = stringRedisTemplate.opsForZSet()
                .reverseRangeByScoreWithScores(FEED_KEY + user.getId(), 0, max, offset, DEFAULT_PAGE_SIZE);
        // 2. 解析数据 blob id, minTime 时间戳, offset
        if (typedTuples == null || typedTuples.isEmpty()) {
            return Result.notFount("没有更多笔记了");
        }
        List<Long> ids = new ArrayList<>(typedTuples.size());
        long minTime = 0;
        int offsetCount = 1;
        for (ZSetOperations.TypedTuple<String> tuple : typedTuples) {
            ids.add(Long.valueOf(tuple.getValue()));
            long time = tuple.getScore().longValue();
            if (time == minTime) {
                offsetCount++;
            } else {
                minTime = time;
                offsetCount = 1;
            }
        }
        // 获取 blog 列表
        List<Blog> blogs = query().in("id", ids)
                .last("order by field(id, " + StrUtil.join(",", ids) + ")").list();

        for (Blog blog : blogs) {
            // 给分页数据的每一个笔记查询用户信息
            queryBlobUserInfo(blog);
            // 判断每一个笔记是否被当前用户点赞
            isBlogLiked(blog);
        }
        // 封装并返回滚动分页数据
        ScrollResultDTO<Blog> scrollResultDTO = new ScrollResultDTO<>();
        scrollResultDTO.setList(blogs);
        scrollResultDTO.setMinTime(minTime);
        scrollResultDTO.setOffset(offsetCount);
        return Result.success(scrollResultDTO);
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

    /**
     * 点赞笔记
     * @param id 笔记 ID
     * @return 操作结果
     */
    @Override
    public Result likeBlog(Long id) {
        // 判断用户是否已经点赞, 没有点赞则Db liked +1, 并添加到 redis
        UserDTO user = UserHold.getUser();
        String key = BLOB_LIKE_KEY + id;
        // Zset 无法没有 isMember 的方法来实现成员判断, 只能通过score 来判断
        Double isMember = stringRedisTemplate.opsForZSet().score(key, user.getId().toString());
        if (isMember != null) {
            // 已点赞
            boolean update = update().setSql("liked = liked - 1").eq("id", id).update();
            if (update) {
                stringRedisTemplate.opsForZSet().remove(key, user.getId().toString());
            }
        } else {
            boolean update = update().setSql("liked = liked + 1").eq("id", id).update();
            if (update) {
                // 使用 redis zset 存储点赞数据, 并以时间戳为score
                stringRedisTemplate.opsForZSet().add(key, user.getId().toString(), System.currentTimeMillis());
            }
        }
        // 如果点过赞则Db liked -1, 把用户从 redis set 中移除
        return Result.success();
    }

    /**
     * 查询笔记点赞信息 (获取点赞数 + top5 user)
     * @param id 笔记 ID
     * @return 点赞信息
     */
    @Override
    public Result queryBlogLikes(Long id) {
        // 从 redis 中获取点赞用户列表 top5
        Set<String> top5 = stringRedisTemplate.opsForZSet().range(BLOB_LIKE_KEY + id, 0, 4);
        if (top5 == null || top5.isEmpty()) {
            return Result.success(Collections.emptyList());
        }
        // 查询 top5 用户信息(DB)
        List<Long> ids = top5.stream().map(Long::valueOf).toList();
        String field = StringUtil.join(ids, ",");
        // listByIds 返回数据顺序不对, 需要指定先点赞的id排前面, 替换为指定顺序查询方式 FILED
        List<UserDTO> users = userService.query()
                .in("id", ids)
                .last("order by field(id, " + field + ")").list()
                .stream()
                .map(user -> BeanUtil.copyProperties(user, UserDTO.class)).toList();

        return Result.success(users);
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
            return; // 未登录,无需查询是否点赞
        }
        String key = BLOB_LIKE_KEY + blog.getId();
        Double isMember = stringRedisTemplate.opsForZSet().score(key, user.getId().toString());
        blog.setIsLike(isMember != null);
    }
}
