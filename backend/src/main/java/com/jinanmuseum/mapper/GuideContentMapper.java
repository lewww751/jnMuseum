package com.jinanmuseum.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jinanmuseum.entity.GuideContent;
import org.apache.ibatis.annotations.Param;

public interface GuideContentMapper extends BaseMapper<GuideContent> {
    GuideContent selectByPageKey(@Param("pageKey") String pageKey);
}