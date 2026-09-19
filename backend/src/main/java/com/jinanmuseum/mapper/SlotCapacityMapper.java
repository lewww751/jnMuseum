package com.jinanmuseum.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jinanmuseum.entity.SlotCapacity;
import org.apache.ibatis.annotations.Param;

public interface SlotCapacityMapper extends BaseMapper<SlotCapacity> {
    SlotCapacity selectByDateAndSlot(@Param("date") String date, @Param("slot") String slot);
}