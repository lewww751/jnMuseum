package com.jinanmuseum.controller.admin;

import com.jinanmuseum.common.Result;
import com.jinanmuseum.service.AdminUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private AdminUserService adminUserService;

    /** SPEC §5.2：POST /api/admin/login → data 为 {token} */
    @PostMapping("/login")
    public Result<Map<String, String>> login(@RequestBody LoginBody body) {
        if (body == null || body.username() == null || body.password() == null) {
            return Result.error("请输入用户名和密码");
        }
        String token = adminUserService.login(body.username(), body.password());
        if (token == null) {
            return Result.error("用户名或密码错误");
        }
        return Result.success(Map.of("token", token));
    }

    record LoginBody(String username, String password) {
    }
}
