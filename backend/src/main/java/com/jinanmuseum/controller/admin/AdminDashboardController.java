package com.jinanmuseum.controller.admin;

import com.jinanmuseum.common.Result;
import com.jinanmuseum.domain.SlotTimes;
import com.jinanmuseum.entity.SlotCapacity;
import com.jinanmuseum.mapper.CollectionItemMapper;
import com.jinanmuseum.mapper.ExhibitionMapper;
import com.jinanmuseum.mapper.MuseumEventMapper;
import com.jinanmuseum.mapper.SlotCapacityMapper;
import com.jinanmuseum.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Clock;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据看板：今日/明日时段容量、近 7 天趋势、统计数字。
 */
@RestController
@RequestMapping("/api/admin")
public class AdminDashboardController {

    private static final int DEFAULT_CAPACITY = 300;

    @Autowired
    private BookingService bookingService;
    @Autowired
    private SlotCapacityMapper slotCapacityMapper;
    @Autowired
    private ExhibitionMapper exhibitionMapper;
    @Autowired
    private MuseumEventMapper museumEventMapper;
    @Autowired
    private CollectionItemMapper collectionItemMapper;
    @Autowired
    private Clock clock;

    @GetMapping("/dashboard")
    public Result<Map<String, Object>> dashboard() {
        LocalDate today = LocalDate.now(clock);

        List<Map<String, Object>> last7Days = new ArrayList<>();
        for (int i = 6; i >= 0; i--) {
            LocalDate day = today.minusDays(i);
            Map<String, Object> row = new HashMap<>();
            row.put("date", day.toString());
            row.put("booked", bookingService.getBookedGuestCountByDate(day.toString()));
            last7Days.add(row);
        }

        Map<String, Object> stats = new HashMap<>();
        stats.put("activeBookings", bookingService.getActiveBookingCount());
        stats.put("todayCheckIns", bookingService.getTodayCheckInCount());
        stats.put("exhibitionCount", exhibitionMapper.selectList(null).size());
        stats.put("eventCount", museumEventMapper.selectList(null).size());
        stats.put("collectionCount", collectionItemMapper.selectList(null).size());

        Map<String, Object> data = new HashMap<>();
        data.put("today", slotSummary(today));
        data.put("tomorrow", slotSummary(today.plusDays(1)));
        data.put("last7Days", last7Days);
        data.put("stats", stats);
        return Result.success(data);
    }

    private List<Map<String, Object>> slotSummary(LocalDate date) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (String slot : new String[]{SlotTimes.AM, SlotTimes.PM}) {
            SlotCapacity sc = slotCapacityMapper.selectByDateAndSlot(date.toString(), slot);
            int capacity = sc != null ? sc.getCapacity() : DEFAULT_CAPACITY;
            Map<String, Object> row = new HashMap<>();
            row.put("slot", slot);
            row.put("label", SlotTimes.getLabel(slot));
            row.put("capacity", capacity);
            row.put("booked", bookingService.getBookedGuestCount(date.toString(), slot));
            result.add(row);
        }
        return result;
    }
}
