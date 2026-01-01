package com.lee.redis.train.demo.controller;

import com.lee.redis.train.demo.entity.Result;
import com.lee.redis.train.demo.service.IFollowService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName FollowController
 * @Description
 * @Author lihongliang
 * @Date 2025/12/31 21:23
 * @Version 1.0
 */
@RestController
@RequestMapping("/follow")
public class FollowController {

    @Resource
    private IFollowService followService;

    /**
     * 关注 或 取消关注
     * @param followUserId 关注用户 id
     * @param isFollow 是否关注
     * @return 响应结果
     */
    @PutMapping("/{id}/{isFollow}")
    public Result follow(@PathVariable("id") Long followUserId, @PathVariable("isFollow") Boolean isFollow) {
        return followService.follow(followUserId, isFollow);
    }

    /**
     * 判断当前用户是否关注了指定用户
     * @param followUserId 关注用户 id
     * @return 响应结果
     */
    @GetMapping("/or/not/{id}")
    public Result isFollow(@PathVariable("id") Long followUserId) {
        return followService.isFollow(followUserId);
    }

    /**
     * 共同关注
     * @param id 目标用户 id
     * @return 响应结果
     */
    @GetMapping("/common/{id}")
    public Result common(@PathVariable("id") Long id) {
        return followService.followCommons(id);
    }
}
