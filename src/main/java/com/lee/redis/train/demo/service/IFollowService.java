package com.lee.redis.train.demo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lee.redis.train.demo.entity.Follow;
import com.lee.redis.train.demo.entity.Result;

/**
 * @ClassName IFollowService
 * @Description
 * @Author lihongliang
 * @Date 2025/12/31 21:20
 * @Version 1.0
 */
public interface IFollowService extends IService<Follow> {

    /**
     * 关注 | 取关
     * @param followUserId 关注用户ID
     * @param isFollow 是否关注
     * @return 操作结果
     */
    Result follow(Long followUserId, Boolean isFollow);

    /**
     * 查询是否关注
     * @param followUserId 关注用户ID
     * @return 关注结果
     */
    Result isFollow(Long followUserId);

    /**
     * 查询共同关注
     * @param id 用户ID
     * @return 共同关注结果
     */
    Result followCommons(Long id);
}
