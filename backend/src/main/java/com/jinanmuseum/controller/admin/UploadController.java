package com.jinanmuseum.controller.admin;

import com.jinanmuseum.common.BusinessException;
import com.jinanmuseum.common.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/upload")
public class UploadController {

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("jpg", "jpeg", "png", "webp");
    private static final long MAX_SIZE = 5 * 1024 * 1024;

    @Value("${app.upload-dir}")
    private String uploadDir;

    @PostMapping
    public Result<String> upload(@RequestParam("file") MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("请选择要上传的图片");
        }
        if (file.getSize() > MAX_SIZE) {
            throw new BusinessException("图片不能超过 5MB");
        }
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new BusinessException("仅支持图片文件");
        }
        String originalFilename = file.getOriginalFilename();
        int dot = originalFilename == null ? -1 : originalFilename.lastIndexOf('.');
        if (dot < 0 || dot == originalFilename.length() - 1) {
            throw new BusinessException("仅支持 jpg/png/webp 图片");
        }
        String extension = originalFilename.substring(dot + 1).toLowerCase();
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new BusinessException("仅支持 jpg/png/webp 图片");
        }

        String newFilename = UUID.randomUUID().toString().replace("-", "") + "." + extension;
        Path dir = Paths.get(uploadDir).toAbsolutePath();
        Files.createDirectories(dir);
        file.transferTo(dir.resolve(newFilename).toFile());
        return Result.success("/uploads/" + newFilename);
    }
}
