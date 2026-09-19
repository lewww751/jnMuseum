package com.jinanmuseum.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jinanmuseum.entity.AdminUser;
import org.apache.ibatis.annotations.Select;

public interface AdminUserMapper extends BaseMapper<AdminUser> {

    @Select("SELECT COUNT(*) FROM admin_user")
    int selectCount();
}