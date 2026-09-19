package com.jinanmuseum.controller.public_;

import com.jinanmuseum.common.Result;
import com.jinanmuseum.domain.BookingPolicy;
import com.jinanmuseum.domain.SlotTimes;
import com.jinanmuseum.entity.Booking;
import com.jinanmuseum.entity.BookingGuest;
import com.jinanmuseum.entity.SlotCapacity;
import com.jinanmuseum.mapper.SlotCapacityMapper;
import com.jinanmuseum.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.Clock;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/booking")
public class BookingController {

    private static final int DEFAULT_CAPACITY = 300;
    private static final String[] WEEKDAYS = {"周一", "周二", "周三", "周四", "周五", "周六", "周日"};

    @Autowired
    private BookingService bookingService;
    @Autowired
    private BookingPolicy bookingPolicy;
    @Autowired
    private SlotCapacityMapper slotCapacityMapper;
    @Autowired
    private Clock clock;

    /** R1：今天起 7 天的可约状态（开放日、时段余量、bookable 与原因） */
    @GetMapping("/availability")
    public Result<Map<String, Object>> availability() {
        LocalDate today = LocalDate.now(clock);
        List<Map<String, Object>> days = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            LocalDate date = today.plusDays(i);
            boolean isOpen = bookingPolicy.isOpenDay(date);
            String closeReason = bookingPolicy.getOpenDayRule().getCloseReason(date);

            List<Map<String, Object>> slots = new ArrayList<>();
            for (String slot : new String[]{SlotTimes.AM, SlotTimes.PM}) {
                SlotCapacity sc = slotCapacityMapper.selectByDateAndSlot(date.toString(), slot);
                int capacity = sc != null ? sc.getCapacity() : DEFAULT_CAPACITY;
                int booked = bookingService.getBookedGuestCount(date.toString(), slot);
                int remaining = Math.max(0, capacity - booked);
                boolean withinDeadline = bookingPolicy.canBookTodaySlot(date, slot);
                boolean bookable = isOpen && remaining > 0 && withinDeadline;

                String reason = null;
                if (!isOpen) {
                    reason = closeReason != null ? closeReason : "闭馆";
                } else if (remaining <= 0) {
                    reason = "该时段已约满";
                } else if (!withinDeadline) {
                    reason = "已过当天预约截止时间";
                }

                Map<String, Object> s = new HashMap<>();
                s.put("slot", slot);
                s.put("label", SlotTimes.getLabel(slot));
                s.put("capacity", capacity);
                s.put("booked", booked);
                s.put("remaining", remaining);
                s.put("bookable", bookable);
                s.put("reason", reason);
                slots.add(s);
            }

            Map<String, Object> day = new HashMap<>();
            day.put("date", date.toString());
            day.put("weekday", WEEKDAYS[date.getDayOfWeek().getValue() - 1]);
            day.put("isOpen", isOpen);
            day.put("closeReason", closeReason);
            day.put("slots", slots);
            days.add(day);
        }
        return Result.success(Map.of("days", days));
    }

    @PostMapping
    public Result<Booking> createBooking(
            @RequestParam String phone,
            @RequestParam String visitDate,
            @RequestParam String slot,
            @RequestBody List<BookingGuest> guests) {
        return bookingService.createBooking(phone, visitDate, slot, guests);
    }

    @GetMapping("/lookup")
    public Result<List<Booking>> lookupBooking(
            @RequestParam(required = false) String code,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) String idCard) {
        if (code != null) {
            Result<Booking> result = bookingService.getBookingByCode(code);
            Booking booking = result.getData();
            return Result.success(booking != null ? List.of(booking) : List.of());
        } else if (phone != null && idCard != null) {
            List<Booking> bookings = bookingService.getBookingsByPhone(phone);
            // Filter by idCard
            bookings = bookings.stream()
                    .filter(b -> b.getGuests().stream()
                            .anyMatch(g -> g.getIdCard().equals(idCard)))
                    .collect(Collectors.toList());
            return Result.success(bookings);
        }
        return Result.error("请提供 code 或 phone+idCard");
    }

    @PostMapping("/cancel")
    public Result<Booking> cancelBooking(
            @RequestParam String code,
            @RequestParam String phone) {
        return bookingService.cancelBooking(code, phone);
    }
}