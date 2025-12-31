package com.lee.redis.train.demo.controller;

import com.lee.redis.train.demo.entity.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

/**
 * @ClassName UploadController
 * @Description 博客文件上传控制器
 * @Author lihongliang
 * @Date 2025/12/30 10:40
 * @Version 1.0
 */
@Slf4j
@RestController
@RequestMapping("/upload")
public class UploadController {

    @Value("${app.upload.base-dir}")
    private String image_upload_path;

    @PostMapping("/blog")
    public Result uploadBlogFile(@RequestParam("file") MultipartFile file) {
        try {
            // 获取文件原始名
            String original = file.getOriginalFilename();
            // 生成新文件名
            String fileName = createNewFileName(original);
            // 保存文件
            file.transferTo(new File(image_upload_path, fileName));
            log.info("文件上传成功, 文件名: {}", fileName);
            return Result.success("上传成功", fileName);
        } catch (IOException e) {
            throw new RuntimeException("文件上传失败: ", e);
        }
    }

    private String createNewFileName(String originalFilename) {
        return System.currentTimeMillis() + originalFilename.substring(originalFilename.lastIndexOf("."));
    }

}
