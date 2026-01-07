package com.lee.redis.train.demo.dto;

import lombok.Data;

import java.util.List;

/**
 * @ClassName ScrollResultDTO
 * @Description 滚动分页数据
 * @Author lihongliang
 * @Date 2026/1/7 11:12
 * @Version 1.0
 */
@Data
public class ScrollResultDTO <T> {

    private Long minTime;

    private Integer offset;

    /**
     * 数据列表(滚动获取)
     */
    private List<T> list;


}
