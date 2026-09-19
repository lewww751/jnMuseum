package com.jinanmuseum.config;

import com.jinanmuseum.common.Result;
import com.jinanmuseum.dto.LoginRequest;
import com.jinanmuseum.service.AdminUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private AdminUserService adminUserService;

    @PostMapping("/login")
    public Result<String> login(@RequestBody LoginRequest request) {
        String token = adminUserService.login(request.username(), request.password());
        if (token == null) {
            return Result.error("用户名或密码错误");
        }
        return Result.success(token);
    }
}
