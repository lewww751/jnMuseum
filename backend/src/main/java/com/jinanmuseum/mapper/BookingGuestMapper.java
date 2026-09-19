package com.jinanmuseum.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jinanmuseum.entity.BookingGuest;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface BookingGuestMapper extends BaseMapper<BookingGuest> {
    List<BookingGuest> selectByBookingId(@Param("bookingId") Long bookingId);
    List<BookingGuest> selectByDateAndIdCard(@Param("date") String date, @Param("idCard") String idCard);
    int countByDateAndSlot(@Param("date") String date, @Param("slot") String slot);
    int countByDate(@Param("date") String date);
}