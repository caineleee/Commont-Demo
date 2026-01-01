package com.lee.redis.train.demo.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lee.redis.train.demo.constants.UserHold;
import com.lee.redis.train.demo.dto.UserDTO;
import com.lee.redis.train.demo.entity.Follow;
import com.lee.redis.train.demo.entity.Result;
import com.lee.redis.train.demo.mapper.FollowMapper;
import com.lee.redis.train.demo.service.IFollowService;
import com.lee.redis.train.demo.service.IUserService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import static com.lee.redis.train.demo.constants.RedisConstants.FOLLOW_KEY;

/**
 * @ClassName FollowServiceImpl
 * @Description
 * @Author lihongliang
 * @Date 2025/12/31 21:21
 * @Version 1.0
 */
@Slf4j
@Service
public class FollowServiceImpl extends ServiceImpl<FollowMapper, Follow> implements IFollowService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private IUserService userService;

    /**
     * 关注 | 取关
     *
     * @param followUserId 关注用户ID
     * @param isFollow     是否关注
     * @return 操作结果
     */
    @Override
    public Result follow(Long followUserId, Boolean isFollow) {
        // 判断需要关注还是取关?  关注
        UserDTO user = UserHold.getUser();
        String redisKey = FOLLOW_KEY + user.getId();
        Follow follow = new Follow();
        follow.setUserId(user.getId());
        follow.setFollowId(followUserId);
        if  (isFollow) {
            // 保存数据
            boolean isSuccess = save(follow);
            if (!isSuccess) {
                return Result.error("关注失败");
            }
            // 将数据保存到 Redis
            stringRedisTemplate.opsForSet().add(redisKey, followUserId.toString());
            return Result.success("关注成功");
        }
        // 取关
        boolean isRemoved = remove(new QueryWrapper<Follow>()
                .eq( "user_id", user.getId()).eq(FOLLOW_KEY, followUserId));
         if (!isRemoved) {
            return Result.error("取关失败");
        }
        // 移除 Redis 数据
        stringRedisTemplate.opsForSet().remove( redisKey, followUserId.toString());
        return Result.success("取关成功");
    }

    /**
     * 查询是否关注
     *
     * @param followUserId 关注用户ID
     * @return 关注结果
     */
    @Override
    public Result isFollow(Long followUserId) {
        // 判断需要关注还是取关?  关注
        UserDTO user = UserHold.getUser();
        Long count = query().eq("user_id", user.getId()).eq("follow_id", followUserId).count();
        return Result.success(count > 0);
    }

     /**
     * 共同关注
     *
     * @param id 目标用户 ID
     * @return 共同关注的用户列表
     */
    @Override
    public Result followCommons(Long id) {
        UserDTO user = UserHold.getUser();
        Set<String> commons = stringRedisTemplate.opsForSet()
                .intersect(FOLLOW_KEY + user.getId(), FOLLOW_KEY + id);
        if  (commons == null || commons.isEmpty()) {
            return Result.success(Collections.emptyList());
        }
        List<Long> list = commons.stream().map(Long::valueOf).toList();
        List<UserDTO> users = userService.listByIds(list)
                .stream().map(item -> BeanUtil.copyProperties(item, UserDTO.class)).toList();
        return Result.success(users);
    }
}
