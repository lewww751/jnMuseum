package com.jinanmuseum.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jinanmuseum.entity.DaySetting;
import org.apache.ibatis.annotations.Param;

public interface DaySettingMapper extends BaseMapper<DaySetting> {
    DaySetting selectByDate(@Param("date") String date);
}