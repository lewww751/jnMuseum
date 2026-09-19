package com.jinanmuseum.controller.admin;

import com.jinanmuseum.common.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/upload")
public class UploadController {

    @Value("${app.upload-dir}")
    private String uploadDir;

    @PostMapping
    public Result<String> upload(@RequestParam MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            return Result.error("文件不能为空");
        }

        // 生成唯一文件名
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String newFilename = UUID.randomUUID().toString() + extension;

        // 保存文件
        Path filePath = Paths.get(uploadDir, newFilename);
        Files.createDirectories(filePath.getParent());
        file.transferTo(filePath);

        return Result.success("/uploads/" + newFilename);
    }
}