package com.jinanmuseum.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jinanmuseum.common.BusinessException;
import com.jinanmuseum.domain.BookingPolicy;
import com.jinanmuseum.domain.IdCardValidator;
import com.jinanmuseum.domain.SlotTimes;
import com.jinanmuseum.entity.Booking;
import com.jinanmuseum.entity.BookingGuest;
import com.jinanmuseum.entity.SlotCapacity;
import com.jinanmuseum.mapper.BookingGuestMapper;
import com.jinanmuseum.mapper.BookingMapper;
import com.jinanmuseum.mapper.SlotCapacityMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class BookingService {

    private static final Pattern PHONE_PATTERN = Pattern.compile("^1[3-9]\\d{9}$");
    private static final DateTimeFormatter VIEW_TIME = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final String[] WEEKDAYS = {"周一", "周二", "周三", "周四", "周五", "周六", "周日"};
    private static final int DEFAULT_CAPACITY = 300;
    private static final int CODE_RETRY_LIMIT = 5;

    @Autowired
    private BookingMapper bookingMapper;
    @Autowired
    private BookingGuestMapper bookingGuestMapper;
    @Autowired
    private SlotCapacityMapper slotCapacityMapper;
    @Autowired
    private BookingPolicy bookingPolicy;
    @Autowired
    private Clock clock;

    /**
     * 创建预约单，强制执行 R1–R8、R11：
     * 窗口/截止/开放日 → 单据结构与实名 → 查重 → 容量（按入馆人数、事务内 FOR UPDATE 防超卖）→ 预约码。
     */
    @Transactional
    public Booking createBooking(String visitDate, String slot, String phone, List<BookingGuest> guests) {
        // R6 手机号
        if (phone == null || !PHONE_PATTERN.matcher(phone).matches()) {
            throw new BusinessException("请填写正确的 11 位手机号");
        }
        if (!SlotTimes.isValidSlot(slot)) {
            throw new BusinessException("时段不合法");
        }
        LocalDate date;
        try {
            date = LocalDate.parse(visitDate);
        } catch (Exception e) {
            throw new BusinessException("参观日期格式不正确");
        }
        // R1 预约窗口
        if (!bookingPolicy.isWithinBookingWindow(date)) {
            throw new BusinessException("仅可预约今天起 7 天内的日期");
        }
        // R3 开放日
        if (!bookingPolicy.isOpenDay(date)) {
            String reason = bookingPolicy.getOpenDayRule().getCloseReason(date);
            throw new BusinessException("该日期闭馆" + (reason != null ? "：" + reason : ""));
        }
        // R2 当天截止
        if (!bookingPolicy.canBookTodaySlot(date, slot)) {
            throw new BusinessException("已过该时段的当天预约截止时间");
        }
        // R4 单据结构：1 主预约人 + 0~2 同行人，共 1~3 人
        if (guests == null || guests.isEmpty()) {
            throw new BusinessException("至少需要 1 名入馆人");
        }
        if (guests.size() > 3) {
            throw new BusinessException("单笔预约最多 3 人（1 名主预约人 + 2 名同行人）");
        }
        for (BookingGuest guest : guests) {
            if (!"PRIMARY".equals(guest.getGuestType()) && !"COMPANION".equals(guest.getGuestType())) {
                throw new BusinessException("入馆人类型不合法");
            }
            if (guest.getName() == null || guest.getName().isBlank()) {
                throw new BusinessException("请填写入馆人姓名");
            }
            if (guest.getName().length() > 20) {
                throw new BusinessException("入馆人姓名不能超过 20 个字");
            }
            if (!IdCardValidator.isValid(guest.getIdCard())) {
                throw new BusinessException("证件号 " + maskIdCard(guest.getIdCard()) + " 格式不正确");
            }
        }
        if (guests.stream().filter(g -> "PRIMARY".equals(g.getGuestType())).count() != 1) {
            throw new BusinessException("每笔预约须恰好 1 名主预约人");
        }
        List<String> idCards = guests.stream().map(BookingGuest::getIdCard).collect(Collectors.toList());
        if (idCards.size() != idCards.stream().distinct().count()) {
            throw new BusinessException("同一预约单内证件号不得重复");
        }
        // R7 查重：任一证件号在参观日已存在于非取消单
        for (String idCard : idCards) {
            if (!bookingGuestMapper.selectByDateAndIdCard(visitDate, idCard).isEmpty()) {
                throw new BusinessException("证件号 " + maskIdCard(idCard) + " 在 " + visitDate + " 已有有效预约，无法重复预约");
            }
        }
        // R8 容量：按入馆人数计，事务内先锁行再计数，防超卖
        SlotCapacity sc = slotCapacityMapper.selectByDateAndSlotForUpdate(visitDate, slot);
        if (sc == null) {
            sc = new SlotCapacity();
            sc.setCapacityDate(visitDate);
            sc.setSlot(slot);
            sc.setCapacity(bookingPolicy.getSlotCapacity(date, slot));
            try {
                slotCapacityMapper.insert(sc);
                sc = slotCapacityMapper.selectByDateAndSlotForUpdate(visitDate, slot);
            } catch (DuplicateKeyException e) {
                // 并发首建撞 UNIQUE：回落到已存在行
                sc = slotCapacityMapper.selectByDateAndSlotForUpdate(visitDate, slot);
            }
        }
        int booked = bookingGuestMapper.countByDateAndSlot(visitDate, slot);
        if (booked + guests.size() > sc.getCapacity()) {
            throw new BusinessException("该时段剩余名额不足");
        }
        // R11 预约码
        String code = generateBookingCode();
        Booking booking = new Booking();
        booking.setCode(code);
        booking.setVisitDate(visitDate);
        booking.setSlot(slot);
        booking.setPhone(phone);
        booking.setStatus("ACTIVE");
        booking.setCreatedAt(LocalDateTime.now(clock));
        bookingMapper.insert(booking);
        for (BookingGuest guest : guests) {
            guest.setBookingId(booking.getId());
            bookingGuestMapper.insert(guest);
        }
        booking.setGuests(guests);
        return booking;
    }

    /** R9：仅 ACTIVE 且参观日前一天 24:00 前可整单取消，code + phone 双凭证 */
    @Transactional
    public Booking cancelBooking(String code, String phone) {
        Booking booking = requireBooking(code);
        if (phone == null || !phone.equals(booking.getPhone())) {
            throw new BusinessException("手机号与预约单不匹配");
        }
        if ("CANCELLED".equals(booking.getStatus())) {
            throw new BusinessException("该预约已取消");
        }
        if ("CHECKED_IN".equals(booking.getStatus())) {
            throw new BusinessException("该预约已核销，无法取消");
        }
        LocalDate visitDate = LocalDate.parse(booking.getVisitDate());
        if (!bookingPolicy.canCancel(visitDate)) {
            throw new BusinessException("已过取消截止时间（参观日前一天 24:00）");
        }
        booking.setStatus("CANCELLED");
        booking.setCancelledAt(LocalDateTime.now(clock));
        bookingMapper.updateById(booking);
        return booking;
    }

    /** R10：仅 ACTIVE 且参观当天可核销 */
    @Transactional
    public Booking checkIn(String code) {
        Booking booking = requireBooking(code);
        if ("CANCELLED".equals(booking.getStatus())) {
            throw new BusinessException("该预约已取消，无法核销");
        }
        if ("CHECKED_IN".equals(booking.getStatus())) {
            throw new BusinessException("该预约已核销");
        }
        LocalDate visitDate = LocalDate.parse(booking.getVisitDate());
        if (!bookingPolicy.canCheckIn(visitDate)) {
            throw new BusinessException("仅可在参观当天核销");
        }
        booking.setStatus("CHECKED_IN");
        booking.setCheckedInAt(LocalDateTime.now(clock));
        bookingMapper.updateById(booking);
        return loadGuests(booking);
    }

    /** R12 查询：预约码，或主预约人手机号 + 任一入馆人证件号 */
    public List<Booking> lookup(String code, String phone, String idCard) {
        List<Booking> result = new ArrayList<>();
        if (code != null && !code.isBlank()) {
            Booking booking = bookingMapper.selectByCode(code.trim());
            if (booking != null) {
                result.add(loadGuests(booking));
            }
            return result;
        }
        if (phone != null && !phone.isBlank() && idCard != null && !idCard.isBlank()) {
            return getBookingsByPhone(phone.trim()).stream()
                    .filter(b -> b.getGuests().stream().anyMatch(g -> idCard.trim().equals(g.getIdCard())))
                    .collect(Collectors.toList());
        }
        throw new BusinessException("请提供预约码，或手机号 + 证件号查询");
    }

    public Booking loadGuests(Booking booking) {
        if (booking != null) {
            booking.setGuests(bookingGuestMapper.selectByBookingId(booking.getId()));
        }
        return booking;
    }

    public List<Booking> getBookingsByPhone(String phone) {
        return bookingMapper.selectByPhone(phone).stream()
                .map(this::loadGuests)
                .collect(Collectors.toList());
    }

    /** 后台预约列表：数据库分页 + 多条件筛选 + 关键字（预约码/手机号/入馆人姓名/证件号） */
    public Map<String, Object> getBookings(String visitDate, String slot, String status, String keyword, int page, int size) {
        LambdaQueryWrapper<Booking> qw = new LambdaQueryWrapper<>();
        qw.eq(visitDate != null && !visitDate.isBlank(), Booking::getVisitDate, visitDate)
                .eq(slot != null && !slot.isBlank(), Booking::getSlot, slot)
                .eq(status != null && !status.isBlank(), Booking::getStatus, status);
        if (keyword != null && !keyword.isBlank()) {
            String kw = keyword.trim();
            List<Long> guestBookingIds = bookingGuestMapper.selectList(new LambdaQueryWrapper<BookingGuest>()
                            .like(BookingGuest::getName, kw).or().like(BookingGuest::getIdCard, kw))
                    .stream().map(BookingGuest::getBookingId).distinct().collect(Collectors.toList());
            qw.and(w -> {
                w.like(Booking::getCode, kw).or().like(Booking::getPhone, kw);
                if (!guestBookingIds.isEmpty()) {
                    w.or().in(Booking::getId, guestBookingIds);
                }
            });
        }
        qw.orderByDesc(Booking::getCreatedAt);
        Page<Booking> result = bookingMapper.selectPage(new Page<>(page, size), qw);
        List<Map<String, Object>> records = result.getRecords().stream()
                .map(this::loadGuests)
                .map(this::toView)
                .collect(Collectors.toList());
        Map<String, Object> data = new HashMap<>();
        data.put("records", records);
        data.put("total", result.getTotal());
        return data;
    }

    /** SPEC §5.1 预约单视图：掩码证件号、状态中文、可取消判定与截止时间 */
    public Map<String, Object> toView(Booking booking) {
        LocalDate visitDate = LocalDate.parse(booking.getVisitDate());
        Map<String, Object> view = new HashMap<>();
        view.put("code", booking.getCode());
        view.put("visitDate", booking.getVisitDate());
        view.put("weekday", WEEKDAYS[visitDate.getDayOfWeek().getValue() - 1]);
        view.put("slot", booking.getSlot());
        view.put("slotLabel", SlotTimes.getLabel(booking.getSlot()));
        view.put("status", booking.getStatus());
        view.put("statusLabel", statusLabel(booking.getStatus()));
        view.put("createdAt", booking.getCreatedAt() == null ? null : booking.getCreatedAt().format(VIEW_TIME));
        boolean cancellable = "ACTIVE".equals(booking.getStatus()) && bookingPolicy.canCancel(visitDate);
        view.put("cancellable", cancellable);
        view.put("cancelDeadline", visitDate.minusDays(1).toString() + " 24:00");
        List<Map<String, Object>> guests = booking.getGuests() == null ? List.of() : booking.getGuests().stream()
                .map(g -> {
                    Map<String, Object> gv = new HashMap<>();
                    gv.put("name", g.getName());
                    gv.put("idCardMasked", maskIdCard(g.getIdCard()));
                    gv.put("type", g.getGuestType());
                    return gv;
                })
                .collect(Collectors.toList());
        view.put("guests", guests);
        return view;
    }

    /** 看板：指定日期时段已约人数（ACTIVE 单的入馆人合计） */
    public int getBookedGuestCount(String date, String slot) {
        return bookingGuestMapper.countByDateAndSlot(date, slot);
    }

    /** 看板：指定日期全天已约人数（近 7 天趋势） */
    public int getBookedGuestCountByDate(String date) {
        return bookingGuestMapper.countByDate(date);
    }

    /** 看板：有效预约单数 */
    public int getActiveBookingCount() {
        return bookingMapper.selectList(new LambdaQueryWrapper<Booking>().eq(Booking::getStatus, "ACTIVE")).size();
    }

    /** 看板：今日已核销单数 */
    public int getTodayCheckInCount() {
        return bookingMapper.selectList(new LambdaQueryWrapper<Booking>()
                .eq(Booking::getStatus, "CHECKED_IN")
                .eq(Booking::getVisitDate, LocalDate.now(clock).toString())).size();
    }

    private Booking requireBooking(String code) {
        if (code == null || code.isBlank()) {
            throw new BusinessException("请提供预约码");
        }
        Booking booking = bookingMapper.selectByCode(code.trim());
        if (booking == null) {
            throw new BusinessException("预约码不存在");
        }
        return booking;
    }

    private String statusLabel(String status) {
        if ("CANCELLED".equals(status)) {
            return "已取消";
        }
        if ("CHECKED_IN".equals(status)) {
            return "已入馆";
        }
        return "有效";
    }

    private String maskIdCard(String idCard) {
        if (idCard == null || idCard.length() < 9) {
            return "****";
        }
        return idCard.substring(0, 4) + "**********" + idCard.substring(idCard.length() - 4);
    }

    /** R11：8 位数字、首位非 0，冲突重试 ≤5 次 */
    private String generateBookingCode() {
        SecureRandom random = new SecureRandom();
        for (int i = 0; i < CODE_RETRY_LIMIT; i++) {
            String code = (1 + random.nextInt(9)) + String.format("%07d", random.nextInt(10_000_000));
            if (bookingMapper.selectByCode(code) == null) {
                return code;
            }
        }
        throw new BusinessException("预约码生成失败，请稍后重试");
    }
}
