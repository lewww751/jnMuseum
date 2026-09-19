package com.jinanmuseum.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jinanmuseum.entity.Exhibition;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ExhibitionMapper extends BaseMapper<Exhibition> {
    List<Exhibition> selectByKind(@Param("kind") String kind);
}