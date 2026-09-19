package com.jinanmuseum.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jinanmuseum.entity.CollectionItem;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CollectionItemMapper extends BaseMapper<CollectionItem> {
    List<CollectionItem> selectByPublished();
}