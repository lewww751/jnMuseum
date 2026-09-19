package com.jinanmuseum.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jinanmuseum.config.JwtUtil;
import com.jinanmuseum.entity.AdminUser;
import com.jinanmuseum.mapper.AdminUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * 管理员登录：校验 BCrypt 密码，签发 JWT。
 */
@Service
public class AdminUserService {

    @Autowired
    private AdminUserMapper adminUserMapper;
    @Autowired
    private JwtUtil jwtUtil;

    /**
     * @return 登录成功返回 token，失败返回 null
     */
    public String login(String username, String password) {
        AdminUser user = adminUserMapper.selectOne(
                new QueryWrapper<AdminUser>().eq("username", username));
        if (user == null) {
            return null;
        }
        if (!new BCryptPasswordEncoder().matches(password, user.getPasswordHash())) {
            return null;
        }
        return jwtUtil.generateToken(user.getUsername());
    }
}
