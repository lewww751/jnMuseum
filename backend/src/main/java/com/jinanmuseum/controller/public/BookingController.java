package com.jinanmuseum.controller.public_;

import com.jinanmuseum.common.Result;
import com.jinanmuseum.domain.BookingPolicy;
import com.jinanmuseum.domain.SlotTimes;
import com.jinanmuseum.dto.BookingCreateRequest;
import com.jinanmuseum.dto.CancelRequest;
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

    /** 创建预约（SPEC §5.1：JSON body，R1–R8、R11 服务端强制） */
    @PostMapping
    public Result<Map<String, Object>> createBooking(@RequestBody BookingCreateRequest request) {
        List<BookingGuest> guests = new ArrayList<>();
        if (request.guests() != null) {
            for (BookingCreateRequest.GuestInput input : request.guests()) {
                BookingGuest guest = new BookingGuest();
                guest.setGuestType(input.type());
                guest.setName(input.name());
                guest.setIdCard(input.idCard());
                guests.add(guest);
            }
        }
        Booking booking = bookingService.createBooking(
                request.visitDate(), request.slot(), request.phone(), guests);
        return Result.success(createSuccessView(booking));
    }

    /** R12 查询：预约码，或手机号 + 证件号；返回预约单视图（证件号掩码） */
    @GetMapping("/lookup")
    public Result<List<Map<String, Object>>> lookup(
            @RequestParam(required = false) String code,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) String idCard) {
        List<Map<String, Object>> views = bookingService.lookup(code, phone, idCard).stream()
                .map(bookingService::toView)
                .collect(java.util.stream.Collectors.toList());
        return Result.success(views);
    }

    /** R9 取消：code + phone 双凭证，返回取消后的预约单视图 */
    @PostMapping("/cancel")
    public Result<Map<String, Object>> cancelBooking(@RequestBody CancelRequest request) {
        Booking booking = bookingService.cancelBooking(request.code(), request.phone());
        return Result.success(bookingService.toView(booking));
    }

    private Map<String, Object> createSuccessView(Booking booking) {
        Map<String, Object> view = new HashMap<>();
        view.put("code", booking.getCode());
        view.put("visitDate", booking.getVisitDate());
        view.put("slotLabel", SlotTimes.getLabel(booking.getSlot()));
        view.put("createdAt", booking.getCreatedAt() == null
                ? null
                : booking.getCreatedAt().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        List<Map<String, Object>> guests = new ArrayList<>();
        for (BookingGuest g : booking.getGuests()) {
            Map<String, Object> gv = new HashMap<>();
            gv.put("name", g.getName());
            gv.put("idCardMasked", maskIdCard(g.getIdCard()));
            gv.put("type", g.getGuestType());
            guests.add(gv);
        }
        view.put("guests", guests);
        return view;
    }

    private String maskIdCard(String idCard) {
        if (idCard == null || idCard.length() < 9) {
            return "****";
        }
        return idCard.substring(0, 4) + "**********" + idCard.substring(idCard.length() - 4);
    }
}
