package com.jinanmuseum.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jinanmuseum.entity.Booking;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface BookingMapper extends BaseMapper<Booking> {
    List<Booking> selectByPhoneAndDateRange(@Param("phone") String phone, @Param("startDate") String startDate, @Param("endDate") String endDate);
    List<Booking> selectByPhoneAndDate(@Param("phone") String phone, @Param("date") String date);
    int selectCountByDateAndSlot(@Param("date") String date, @Param("slot") String slot);
    Booking selectByCode(@Param("code") String code);
    List<Booking> selectByPhone(@Param("phone") String phone);
}