package com.jinanmuseum.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jinanmuseum.entity.Booking;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface BookingMapper extends BaseMapper<Booking> {
    int selectCountByDateAndSlot(@Param("date") String date, @Param("slot") String slot);
    Booking selectByCode(@Param("code") String code);
    List<Booking> selectByPhone(@Param("phone") String phone);
}
