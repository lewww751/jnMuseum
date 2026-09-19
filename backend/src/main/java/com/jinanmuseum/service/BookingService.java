package com.jinanmuseum.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jinanmuseum.common.BusinessException;
import com.jinanmuseum.common.Result;
import com.jinanmuseum.domain.BookingPolicy;
import com.jinanmuseum.domain.IdCardValidator;
import com.jinanmuseum.entity.Booking;
import com.jinanmuseum.entity.BookingGuest;
import com.jinanmuseum.entity.SlotCapacity;
import com.jinanmuseum.mapper.BookingGuestMapper;
import com.jinanmuseum.mapper.BookingMapper;
import com.jinanmuseum.mapper.SlotCapacityMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class BookingService {

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

    @Transactional
    public Result<Booking> createBooking(String phone, String visitDate, String slot, List<BookingGuest> guests) {
        // R4: 单据结构验证 - 1-3 人，每人都实名，证件号不重复
        if (guests == null || guests.isEmpty()) {
            throw new BusinessException("至少需要 1 名入馆人");
        }
        if (guests.size() > 3) {
            throw new BusinessException("最多 3 名入馆人（1 主预约人 + 2 同行人）");
        }

        // 检查证件号唯一性
        List<String> idCards = guests.stream()
                .map(BookingGuest::getIdCard)
                .collect(Collectors.toList());
        if (idCards.size() != idCards.stream().distinct().count()) {
            throw new BusinessException("同一预约单内证件号不得重复");
        }

        // R5: 身份证校验
        for (BookingGuest guest : guests) {
            if (!IdCardValidator.isValid(guest.getIdCard())) {
                throw new BusinessException("证件号 " + guest.getIdCard() + " 格式不正确");
            }
        }

        // R7: 查重 - 同一证件号在当天已存在于其他预约单
        LocalDate date = LocalDate.parse(visitDate);
        for (String idCard : idCards) {
            List<BookingGuest> existing = bookingGuestMapper.selectByDateAndIdCard(visitDate, idCard);
            if (!existing.isEmpty()) {
                throw new BusinessException("证件号 " + idCard + " 在 " + visitDate + " 已有预约，无法重复预约");
            }
        }

        // R8: 容量检查与扣减
        SlotCapacity slotCapacity = slotCapacityMapper.selectByDateAndSlot(visitDate, slot);
        if (slotCapacity == null) {
            // 初始化默认容量
            slotCapacity = new SlotCapacity();
            slotCapacity.setCapacityDate(visitDate);
            slotCapacity.setSlot(slot);
            slotCapacity.setCapacity(bookingPolicy.getSlotCapacity(date, slot));
            slotCapacityMapper.insert(slotCapacity);
        }

        // 防超卖：SELECT ... FOR UPDATE
        int currentBooked = bookingMapper.selectCountByDateAndSlot(visitDate, slot);
        int totalPeople = guests.size();
        if (currentBooked + totalPeople > slotCapacity.getCapacity()) {
            throw new BusinessException("时段 " + slot + " 已约满，剩余名额不足");
        }

        // 生成预约码
        String code = generateBookingCode();

        // 创建预约单
        Booking booking = new Booking();
        booking.setCode(code);
        booking.setVisitDate(visitDate);
        booking.setSlot(slot);
        booking.setPhone(phone);
        booking.setStatus("ACTIVE");
        booking.setCreatedAt(LocalDateTime.now());
        bookingMapper.insert(booking);

        // 创建入馆人记录
        for (BookingGuest guest : guests) {
            guest.setBookingId(booking.getId());
            bookingGuestMapper.insert(guest);
        }

        booking.setGuests(guests);
        return Result.success(booking);
    }

    @Transactional
    public Result<Booking> cancelBooking(String code, String phone) {
        // 查询预约单
        Booking booking = bookingMapper.selectByCode(code);
        if (booking == null) {
            throw new BusinessException("预约码不存在");
        }
        if (!booking.getPhone().equals(phone)) {
            throw new BusinessException("手机号不匹配");
        }
        if (!"ACTIVE".equals(booking.getStatus())) {
            throw new BusinessException("该预约已" + booking.getStatus());
        }

        // R9: 取消期限检查
        LocalDate visitDate = LocalDate.parse(booking.getVisitDate());
        if (!bookingPolicy.isWithinCancellationWindow(visitDate)) {
            throw new BusinessException("预约已过取消期限（参观日前一天 24:00 前）");
        }

        // 执行取消
        booking.setStatus("CANCELLED");
        booking.setCancelledAt(LocalDateTime.now());
        bookingMapper.updateById(booking);

        return Result.success(booking);
    }

    @Transactional
    public Result<Booking> checkIn(String code) {
        // R10: 核销检查
        Booking booking = bookingMapper.selectByCode(code);
        if (booking == null) {
            throw new BusinessException("预约码不存在");
        }
        if (!"ACTIVE".equals(booking.getStatus())) {
            throw new BusinessException("该预约已" + booking.getStatus());
        }

        LocalDate visitDate = LocalDate.parse(booking.getVisitDate());
        if (!bookingPolicy.isCheckInEligible(visitDate)) {
            throw new BusinessException("仅可在参观当天核销");
        }

        // 执行核销
        booking.setStatus("CHECKED_IN");
        booking.setCheckedInAt(LocalDateTime.now());
        bookingMapper.updateById(booking);

        // 加载入馆人信息
        List<BookingGuest> guests = bookingGuestMapper.selectByBookingId(booking.getId());
        booking.setGuests(guests);

        return Result.success(booking);
    }

    public Result<Booking> getBookingByCode(String code) {
        Booking booking = bookingMapper.selectByCode(code);
        if (booking != null) {
            List<BookingGuest> guests = bookingGuestMapper.selectByBookingId(booking.getId());
            booking.setGuests(guests);
        }
        return Result.success(booking);
    }

    public List<Booking> getBookingsByPhone(String phone) {
        List<Booking> bookings = bookingMapper.selectByPhone(phone);
        for (Booking booking : bookings) {
            List<BookingGuest> guests = bookingGuestMapper.selectByBookingId(booking.getId());
            booking.setGuests(guests);
        }
        return bookings;
    }

    public List<Booking> getBookings(String phone, String startDate, String endDate, String status, int page, int size) {
        // 实现分页查询
        int offset = (page - 1) * size;
        List<Booking> bookings = bookingMapper.selectByPhoneAndDateRange(phone, startDate, endDate);
        if (status != null && !status.isEmpty()) {
            bookings = bookings.stream()
                    .filter(b -> status.equals(b.getStatus()))
                    .collect(Collectors.toList());
        }
        return bookings.subList(Math.min(offset, bookings.size()), 
                             Math.min(offset + size, bookings.size()));
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
        return bookingMapper.selectList(new QueryWrapper<Booking>().eq("status", "ACTIVE")).size();
    }

    /** 看板：今日已核销单数 */
    public int getTodayCheckInCount() {
        return bookingMapper.selectList(new QueryWrapper<Booking>()
                .eq("status", "CHECKED_IN")
                .eq("visit_date", LocalDate.now(clock).toString())).size();
    }

    private String generateBookingCode() {
        Random random = new Random();
        String code;
        do {
            code = String.format("%08d", random.nextInt(100000000));
        } while (bookingMapper.selectByCode(code) != null); // 确保唯一
        return code;
    }
}