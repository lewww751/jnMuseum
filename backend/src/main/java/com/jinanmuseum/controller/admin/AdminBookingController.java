package com.jinanmuseum.controller.admin;

import com.jinanmuseum.common.Result;
import com.jinanmuseum.entity.Booking;
import com.jinanmuseum.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/bookings")
public class AdminBookingController {

    @Autowired
    private BookingService bookingService;

    @GetMapping
    public Result<List<Booking>> list(
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return Result.success(bookingService.getBookings(phone, startDate, endDate, status, page, size));
    }

    @PostMapping("/check-in")
    public Result<Booking> checkIn(@RequestParam String code) {
        return bookingService.checkIn(code);
    }
}