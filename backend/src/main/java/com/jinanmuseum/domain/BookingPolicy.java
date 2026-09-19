package com.jinanmuseum.domain;

import java.time.*;
import java.util.Map;

public class BookingPolicy {

    private final Clock clock;
    private final OpenDayRule openDayRule;
    private final Map<LocalDate, Integer> slotCapacities;
    private final int defaultCapacity = 300;

    public BookingPolicy(Clock clock, OpenDayRule openDayRule, Map<LocalDate, Integer> slotCapacities) {
        this.clock = clock;
        this.openDayRule = openDayRule;
        this.slotCapacities = slotCapacities != null ? slotCapacities : Map.of();
    }

    public BookingPolicy(Clock clock, OpenDayRule openDayRule) {
        this(clock, openDayRule, Map.of());
    }

    // R1: 预约窗口 - 今天起 7 天内（含当天）
    public boolean isWithinBookingWindow(LocalDate visitDate) {
        LocalDate today = LocalDate.now(clock);
        return !visitDate.isBefore(today) && !visitDate.isAfter(today.plusDays(6));
    }

    public boolean isInBookingWindow(LocalDate visitDate) {
        return isWithinBookingWindow(visitDate);
    }

    // R2: 该 (日期, 时段) 当前是否仍可提交预约（当天受截止时间约束，未来日期不受限）
    public boolean canBookTodaySlot(LocalDate visitDate, String slot) {
        return isWithinSameDayDeadline(visitDate, slot);
    }

    // R2: 当天截止 - 当天 AM 须在 09:00 前、PM 须在 13:00 前提交
    public boolean isWithinSameDayDeadline(LocalDate visitDate, String slot) {
        LocalDate today = LocalDate.now(clock);
        if (!visitDate.equals(today)) return true; // 未来日期不受限

        LocalTime now = LocalTime.now(clock);
        if ("AM".equals(slot)) {
            return now.isBefore(LocalTime.of(9, 0));
        } else if ("PM".equals(slot)) {
            return now.isBefore(LocalTime.of(13, 0));
        }
        return false;
    }

    // R9: 可取消期限 - 参观日前一天 24:00 前可取消
    public boolean isWithinCancellationWindow(LocalDate visitDate) {
        LocalDate today = LocalDate.now(clock);
        LocalDate deadline = visitDate.minusDays(1);
        return !today.isAfter(deadline);
    }

    // R10: 可核销日期 - 仅当天可核销
    public boolean isCheckInEligible(LocalDate visitDate) {
        return visitDate.equals(LocalDate.now(clock));
    }

    public int getSlotCapacity(LocalDate date, String slot) {
        return slotCapacities.getOrDefault(date, defaultCapacity);
    }

    public LocalDateTime getCancellationDeadline(LocalDate visitDate) {
        return visitDate.minusDays(1).atTime(23, 59);
    }

    public boolean isOpenDay(LocalDate date) {
        return openDayRule.isOpen(date);
    }

    public boolean canCancel(LocalDate visitDate) {
        return isWithinCancellationWindow(visitDate);
    }

    public boolean canCheckIn(LocalDate visitDate) {
        return isCheckInEligible(visitDate);
    }

    public OpenDayRule getOpenDayRule() {
        return openDayRule;
    }
}