package com.jinanmuseum.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jinanmuseum.entity.MuseumEvent;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface MuseumEventMapper extends BaseMapper<MuseumEvent> {
    List<MuseumEvent> selectByCategory(@Param("category") String category);
}