package com.jinanmuseum.controller.admin;

import com.jinanmuseum.common.Result;
import com.jinanmuseum.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/bookings")
public class AdminBookingController {

    @Autowired
    private BookingService bookingService;

    /** SPEC §5.2：筛选（日期/时段/状态/关键字）+ 分页 → records + total */
    @GetMapping
    public Result<Map<String, Object>> list(
            @RequestParam(required = false) String visitDate,
            @RequestParam(required = false) String slot,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return Result.success(bookingService.getBookings(visitDate, slot, status, keyword, page, size));
    }

    /** SPEC §5.2：POST /api/admin/bookings/{code}/check-in */
    @PostMapping("/{code}/check-in")
    public Result<Map<String, Object>> checkIn(@PathVariable String code) {
        return Result.success(bookingService.toView(bookingService.checkIn(code)));
    }
}
