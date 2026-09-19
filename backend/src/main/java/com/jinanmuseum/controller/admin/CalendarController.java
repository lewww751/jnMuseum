package com.jinanmuseum.controller.admin;

import com.jinanmuseum.common.Result;
import com.jinanmuseum.entity.DaySetting;
import com.jinanmuseum.entity.SlotCapacity;
import com.jinanmuseum.service.DaySettingService;
import com.jinanmuseum.service.SlotCapacityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/calendar")
public class CalendarController {

    @Autowired
    private DaySettingService daySettingService;
    @Autowired
    private SlotCapacityService slotCapacityService;

    @GetMapping
    public Result<Object> getCalendar(String month) {
        // 简版：返回当前月日历
        return Result.success(Map.of(
                "month", month,
                "days", List.of(
                        Map.of("date", "2026-09-18", "isOpen", true, "overrides", null),
                        Map.of("date", "2026-09-19", "isOpen", true, "overrides", null),
                        Map.of("date", "2026-09-20", "isOpen", true, "overrides", null)
                )
        ));
    }

    @PutMapping("/day-setting")
    public Result<String> updateDaySetting(@RequestBody DaySetting setting) {
        daySettingService.saveDaySetting(setting);
        return Result.success("保存成功");
    }

    @PutMapping("/slot-capacity")
    public Result<String> updateSlotCapacity(@RequestBody SlotCapacity capacity) {
        slotCapacityService.saveSlotCapacity(capacity);
        return Result.success("保存成功");
    }
}