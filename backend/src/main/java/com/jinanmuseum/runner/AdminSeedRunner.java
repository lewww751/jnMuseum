package com.jinanmuseum.runner;

import com.jinanmuseum.entity.AdminUser;
import com.jinanmuseum.mapper.AdminUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminSeedRunner implements CommandLineRunner {

    @Autowired
    private AdminUserMapper adminUserMapper;

    @Override
    public void run(String... args) throws Exception {
        // 检查是否已存在管理员（selectCount 在部分驱动下有 Integer/Long 装箱问题，用列表判空）
        if (adminUserMapper.selectList(null).isEmpty()) {
            AdminUser admin = new AdminUser();
            admin.setUsername("admin");
            // 密码：admin123，BCrypt 加密
            String passwordHash = new BCryptPasswordEncoder().encode("admin123");
            admin.setPasswordHash(passwordHash);
            adminUserMapper.insert(admin);
        }
    }
}