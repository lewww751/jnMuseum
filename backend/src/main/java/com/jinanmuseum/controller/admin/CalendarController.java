package com.jinanmuseum.controller.admin;

import com.jinanmuseum.common.BusinessException;
import com.jinanmuseum.common.Result;
import com.jinanmuseum.entity.DaySetting;
import com.jinanmuseum.entity.SlotCapacity;
import com.jinanmuseum.domain.SlotTimes;
import com.jinanmuseum.service.BookingService;
import com.jinanmuseum.service.DaySettingService;
import com.jinanmuseum.mapper.SlotCapacityMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 容量与闭馆日管理（SPEC §5.2）：
 * GET  /api/admin/calendar?month=yyyy-MM
 * PUT/DELETE /api/admin/day-setting
 * PUT  /api/admin/slot-capacity
 */
@RestController
@RequestMapping("/api/admin")
public class CalendarController {

    private static final String[] WEEKDAYS = {"周一", "周二", "周三", "周四", "周五", "周六", "周日"};
    private static final int DEFAULT_CAPACITY = 300;

    @Autowired
    private DaySettingService daySettingService;
    @Autowired
    private com.jinanmuseum.service.SlotCapacityService slotCapacityService;
    @Autowired
    private SlotCapacityMapper slotCapacityMapper;
    @Autowired
    private BookingService bookingService;

    @GetMapping("/calendar")
    public Result<Map<String, Object>> calendar(@RequestParam String month) {
        YearMonth ym;
        try {
            ym = YearMonth.parse(month);
        } catch (DateTimeParseException e) {
            throw new BusinessException("月份格式不正确，应为 yyyy-MM");
        }
        List<Map<String, Object>> days = new ArrayList<>();
        for (int d = 1; d <= ym.lengthOfMonth(); d++) {
            LocalDate date = ym.atDay(d);
            String ds = date.toString();
            DaySetting override = daySettingService.getDaySetting(ds);
            boolean defaultOpen = date.getDayOfWeek().getValue() != 1; // 周一例行闭馆
            boolean isOpen = override != null ? Boolean.TRUE.equals(override.getIsOpen()) : defaultOpen;

            Map<String, Object> amCap = slotSummary(ds, SlotTimes.AM);
            Map<String, Object> pmCap = slotSummary(ds, SlotTimes.PM);
            Map<String, Object> slots = new HashMap<>();
            slots.put("AM", amCap);
            slots.put("PM", pmCap);

            Map<String, Object> day = new HashMap<>();
            day.put("date", ds);
            day.put("weekday", WEEKDAYS[date.getDayOfWeek().getValue() - 1]);
            day.put("defaultOpen", defaultOpen);
            day.put("isOpen", isOpen);
            day.put("override", override == null ? null : Map.of(
                    "isOpen", Boolean.TRUE.equals(override.getIsOpen()),
                    "reason", override.getReason() == null ? "" : override.getReason()));
            day.put("slots", slots);
            days.add(day);
        }
        return Result.success(Map.of("month", month, "days", days));
    }

    /** 覆盖某天开放状态（upsert） */
    @PutMapping("/day-setting")
    public Result<String> saveDaySetting(@RequestBody DaySetting setting) {
        if (setting.getSettingDate() == null || setting.getSettingDate().isBlank()) {
            throw new BusinessException("请提供日期");
        }
        try {
            LocalDate.parse(setting.getSettingDate());
        } catch (DateTimeParseException e) {
            throw new BusinessException("日期格式不正确");
        }
        daySettingService.saveDaySetting(setting);
        return Result.success("保存成功");
    }

    /** 移除某天覆盖，恢复默认规则 */
    @DeleteMapping("/day-setting")
    public Result<String> deleteDaySetting(@RequestParam String date) {
        daySettingService.deleteDaySetting(date);
        return Result.success("已恢复默认");
    }

    /** 调整某天某时段容量（1–5000，upsert） */
    @PutMapping("/slot-capacity")
    public Result<String> saveSlotCapacity(@RequestBody SlotCapacity capacity) {
        if (capacity.getCapacityDate() == null || capacity.getCapacityDate().isBlank()
                || !SlotTimes.isValidSlot(capacity.getSlot())) {
            throw new BusinessException("请提供日期与合法时段");
        }
        if (capacity.getCapacity() == null || capacity.getCapacity() < 1 || capacity.getCapacity() > 5000) {
            throw new BusinessException("容量须在 1–5000 之间");
        }
        slotCapacityService.saveSlotCapacity(capacity);
        return Result.success("保存成功");
    }

    private Map<String, Object> slotSummary(String date, String slot) {
        SlotCapacity sc = slotCapacityMapper.selectByDateAndSlot(date, slot);
        Map<String, Object> row = new HashMap<>();
        row.put("capacity", sc != null ? sc.getCapacity() : DEFAULT_CAPACITY);
        row.put("booked", bookingService.getBookedGuestCount(date, slot));
        return row;
    }
}
