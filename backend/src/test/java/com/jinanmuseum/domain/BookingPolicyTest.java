package com.jinanmuseum.domain;

import org.junit.jupiter.api.Test;
import java.time.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class BookingPolicyTest {

    private static final ZoneId ZONE = ZoneId.of("Asia/Shanghai");

    @Test
    void shouldAcceptTodayAndSixDaysAhead() {
        Clock clock = Clock.fixed(LocalDate.of(2026, 9, 18).atStartOfDay(ZONE).toInstant(), ZONE);
        BookingPolicy policy = new BookingPolicy(clock, new OpenDayRule(clock, Collections.emptyMap()));

        LocalDate today = LocalDate.of(2026, 9, 18);
        LocalDate day1 = LocalDate.of(2026, 9, 19);
        LocalDate day2 = LocalDate.of(2026, 9, 20);
        LocalDate day3 = LocalDate.of(2026, 9, 21);
        LocalDate day4 = LocalDate.of(2026, 9, 22);
        LocalDate day5 = LocalDate.of(2026, 9, 23);
        LocalDate day6 = LocalDate.of(2026, 9, 24);

        // Day7 (Sept 25) should also be valid as it's "today + 6"
        LocalDate day7 = LocalDate.of(2026, 9, 25);

        assertTrue(policy.isInBookingWindow(today), "Today should be in booking window");
        assertTrue(policy.isInBookingWindow(day1), "Day +1 should be in booking window");
        assertTrue(policy.isInBookingWindow(day2), "Day +2 should be in booking window");
        assertTrue(policy.isInBookingWindow(day3), "Day +3 should be in booking window");
        assertTrue(policy.isInBookingWindow(day4), "Day +4 should be in booking window");
        assertTrue(policy.isInBookingWindow(day5), "Day +5 should be in booking window");
        assertTrue(policy.isInBookingWindow(day6), "Day +6 should be in booking window");
    }

    @Test
    void shouldRejectSevenDaysAhead() {
        Clock clock = Clock.fixed(LocalDate.of(2026, 9, 18).atStartOfDay(ZONE).toInstant(), ZONE);
        BookingPolicy policy = new BookingPolicy(clock, new OpenDayRule(clock, Collections.emptyMap()));

        LocalDate day7 = LocalDate.of(2026, 9, 25); // This is actually +7 from Sept 18
        assertFalse(policy.isInBookingWindow(day7), "Day +7 should be outside booking window");
    }

    @Test
    void shouldRejectPastDates() {
        Clock clock = Clock.fixed(LocalDate.of(2026, 9, 18).atStartOfDay(ZONE).toInstant(), ZONE);
        BookingPolicy policy = new BookingPolicy(clock, new OpenDayRule(clock, Collections.emptyMap()));

        LocalDate yesterday = LocalDate.of(2026, 9, 17);
        LocalDate lastWeek = LocalDate.of(2026, 9, 11);

        assertFalse(policy.isInBookingWindow(yesterday), "Yesterday should be outside booking window");
        assertFalse(policy.isInBookingWindow(lastWeek), "Last week should be outside booking window");
    }

    @Test
    void shouldRejectSameDayAMAfterCutoff() {
        // Set clock to 9:00 AM on Sept 18
        Clock clock = Clock.fixed(
                LocalDate.of(2026, 9, 18).atTime(9, 0).atZone(ZONE).toInstant(),
                ZONE
        );
        BookingPolicy policy = new BookingPolicy(clock, new OpenDayRule(clock, Collections.emptyMap()));

        LocalDate today = LocalDate.of(2026, 9, 18);
        assertFalse(policy.canBookTodaySlot(today, SlotTimes.AM),
                "AM slot should be rejected after 9:00");
    }

    @Test
    void shouldAcceptSameDayAMBeforeCutoff() {
        // Set clock to 8:59 AM on Sept 18
        Clock clock = Clock.fixed(
                LocalDate.of(2026, 9, 18).atTime(8, 59).atZone(ZONE).toInstant(),
                ZONE
        );
        BookingPolicy policy = new BookingPolicy(clock, new OpenDayRule(clock, Collections.emptyMap()));

        LocalDate today = LocalDate.of(2026, 9, 18);
        assertTrue(policy.canBookTodaySlot(today, SlotTimes.AM),
                "AM slot should be accepted before 9:00");
    }

    @Test
    void shouldAcceptSameDayPMBeforeCutoff() {
        // Set clock to 12:59 PM on Sept 18
        Clock clock = Clock.fixed(
                LocalDate.of(2026, 9, 18).atTime(12, 59).atZone(ZONE).toInstant(),
                ZONE
        );
        BookingPolicy policy = new BookingPolicy(clock, new OpenDayRule(clock, Collections.emptyMap()));

        LocalDate today = LocalDate.of(2026, 9, 18);
        assertTrue(policy.canBookTodaySlot(today, SlotTimes.PM),
                "PM slot should be accepted before 13:00");
    }

    @Test
    void shouldRejectSameDayPMAfterCutoff() {
        // Set clock to 13:00 PM on Sept 18
        Clock clock = Clock.fixed(
                LocalDate.of(2026, 9, 18).atTime(13, 0).atZone(ZONE).toInstant(),
                ZONE
        );
        BookingPolicy policy = new BookingPolicy(clock, new OpenDayRule(clock, Collections.emptyMap()));

        LocalDate today = LocalDate.of(2026, 9, 18);
        assertFalse(policy.canBookTodaySlot(today, SlotTimes.PM),
                "PM slot should be rejected after 13:00");
    }

    @Test
    void shouldAcceptFutureDateRegardlessOfTime() {
        // Set clock to 14:00 PM on Sept 18 (past both cutoffs)
        Clock clock = Clock.fixed(
                LocalDate.of(2026, 9, 18).atTime(14, 0).atZone(ZONE).toInstant(),
                ZONE
        );
        BookingPolicy policy = new BookingPolicy(clock, new OpenDayRule(clock, Collections.emptyMap()));

        LocalDate tomorrow = LocalDate.of(2026, 9, 19);
        // Future dates should not be affected by today's cutoffs
        assertTrue(policy.canBookTodaySlot(tomorrow, SlotTimes.AM),
                "Future AM slot should be accepted regardless of current time");
        assertTrue(policy.canBookTodaySlot(tomorrow, SlotTimes.PM),
                "Future PM slot should be accepted regardless of current time");
    }

    @Test
    void shouldAcceptCancellationBeforeVisitDay() {
        Clock clock = Clock.fixed(
                LocalDate.of(2026, 9, 18).atTime(23, 59).atZone(ZONE).toInstant(),
                ZONE
        );
        BookingPolicy policy = new BookingPolicy(clock, new OpenDayRule(clock, Collections.emptyMap()));

        LocalDate visitDate = LocalDate.of(2026, 9, 19);
        assertTrue(policy.canCancel(visitDate),
                "Should accept cancellation before visit day");
    }

    @Test
    void shouldRejectCancellationOnVisitDay() {
        Clock clock = Clock.fixed(
                LocalDate.of(2026, 9, 19).atStartOfDay(ZONE).toInstant(),
                ZONE
        );
        BookingPolicy policy = new BookingPolicy(clock, new OpenDayRule(clock, Collections.emptyMap()));

        LocalDate visitDate = LocalDate.of(2026, 9, 19);
        assertFalse(policy.canCancel(visitDate),
                "Should reject cancellation on visit day");
    }

    @Test
    void shouldRejectCancellationAfterVisitDay() {
        Clock clock = Clock.fixed(
                LocalDate.of(2026, 9, 20).atStartOfDay(ZONE).toInstant(),
                ZONE
        );
        BookingPolicy policy = new BookingPolicy(clock, new OpenDayRule(clock, Collections.emptyMap()));

        LocalDate visitDate = LocalDate.of(2026, 9, 19);
        assertFalse(policy.canCancel(visitDate),
                "Should reject cancellation after visit day");
    }

    @Test
    void shouldAcceptCheckInOnVisitDay() {
        Clock clock = Clock.fixed(
                LocalDate.of(2026, 9, 18).atTime(10, 0).atZone(ZONE).toInstant(),
                ZONE
        );
        BookingPolicy policy = new BookingPolicy(clock, new OpenDayRule(clock, Collections.emptyMap()));

        LocalDate today = LocalDate.of(2026, 9, 18);
        assertTrue(policy.canCheckIn(today),
                "Should accept check-in on visit day");
    }

    @Test
    void shouldRejectCheckInNotOnVisitDay() {
        Clock clock = Clock.fixed(
                LocalDate.of(2026, 9, 18).atTime(10, 0).atZone(ZONE).toInstant(),
                ZONE
        );
        BookingPolicy policy = new BookingPolicy(clock, new OpenDayRule(clock, Collections.emptyMap()));

        LocalDate yesterday = LocalDate.of(2026, 9, 17);
        LocalDate tomorrow = LocalDate.of(2026, 9, 19);

        assertFalse(policy.canCheckIn(yesterday),
                "Should reject check-in on yesterday");
        assertFalse(policy.canCheckIn(tomorrow),
                "Should reject check-in on tomorrow");
    }

    @Test
    void shouldValidateBookingWindowWithOpenDay() {
        Clock clock = Clock.fixed(LocalDate.of(2026, 9, 18).atStartOfDay(ZONE).toInstant(), ZONE);
        BookingPolicy policy = new BookingPolicy(clock, new OpenDayRule(clock, Collections.emptyMap()));

        // Monday (Sept 21) is closed by default
        LocalDate monday = LocalDate.of(2026, 9, 21);
        assertTrue(policy.isInBookingWindow(monday), "Monday should be in booking window");
        assertFalse(policy.isOpenDay(monday), "Monday should not be open");
    }

    @Test
    void shouldCalculateCancellationDeadline() {
        Clock clock = Clock.fixed(
                LocalDate.of(2026, 9, 18).atTime(14, 0).atZone(ZONE).toInstant(),
                ZONE
        );
        BookingPolicy policy = new BookingPolicy(clock, new OpenDayRule(clock, Collections.emptyMap()));

        LocalDate visitDate = LocalDate.of(2026, 9, 20);
        LocalDateTime deadline = policy.getCancellationDeadline(visitDate);

        assertEquals(LocalDateTime.of(2026, 9, 19, 23, 59), deadline,
                "Cancellation deadline should be day before visit at 23:59");
    }
}