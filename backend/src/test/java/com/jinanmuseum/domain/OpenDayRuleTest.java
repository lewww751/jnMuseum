package com.jinanmuseum.domain;

import org.junit.jupiter.api.Test;
import java.time.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class OpenDayRuleTest {

    // Clock for Asia/Shanghai timezone
    private static final ZoneId ZONE = ZoneId.of("Asia/Shanghai");

    @Test
    void shouldRejectMondayDefaultRule() {
        Clock clock = Clock.fixed(LocalDate.of(2026, 9, 21).atStartOfDay(ZONE).toInstant(), ZONE); // Monday
        OpenDayRule rule = new OpenDayRule(clock, Collections.emptyMap());

        LocalDate monday = LocalDate.of(2026, 9, 21);
        assertFalse(rule.isOpen(monday), "Monday should be closed by default");
    }

    @Test
    void shouldAcceptMondayWithOverrideOpen() {
        Clock clock = Clock.fixed(LocalDate.of(2026, 9, 21).atStartOfDay(ZONE).toInstant(), ZONE); // Monday
        LocalDate monday = LocalDate.of(2026, 9, 21);

        Map<LocalDate, OpenDayRule.DaySetting> overrides = new HashMap<>();
        overrides.put(monday, new OpenDayRule.DaySetting(monday, true, "Special Open Day"));

        OpenDayRule rule = new OpenDayRule(clock, overrides);
        assertTrue(rule.isOpen(monday), "Monday with override open should be open");
        assertEquals("Special Open Day", rule.getOpenReason(monday));
    }

    @Test
    void shouldAcceptSundayDefaultRule() {
        Clock clock = Clock.fixed(LocalDate.of(2026, 9, 20).atStartOfDay(ZONE).toInstant(), ZONE); // Sunday
        OpenDayRule rule = new OpenDayRule(clock, Collections.emptyMap());

        LocalDate sunday = LocalDate.of(2026, 9, 20);
        assertTrue(rule.isOpen(sunday), "Sunday should be open by default");
    }

    @Test
    void shouldRejectSundayWithOverrideClosed() {
        Clock clock = Clock.fixed(LocalDate.of(2026, 9, 20).atStartOfDay(ZONE).toInstant(), ZONE); // Sunday
        LocalDate sunday = LocalDate.of(2026, 9, 20);

        Map<LocalDate, OpenDayRule.DaySetting> overrides = new HashMap<>();
        overrides.put(sunday, new OpenDayRule.DaySetting(sunday, false, "Holiday Closure"));

        OpenDayRule rule = new OpenDayRule(clock, overrides);
        assertFalse(rule.isOpen(sunday), "Sunday with override closed should be closed");
        assertEquals("Holiday Closure", rule.getCloseReason(sunday));
    }

    @Test
    void shouldAcceptTuesdayToSaturdayByDefault() {
        Clock clock = Clock.fixed(LocalDate.of(2026, 9, 18).atStartOfDay(ZONE).toInstant(), ZONE);
        OpenDayRule rule = new OpenDayRule(clock, Collections.emptyMap());

        LocalDate tuesday = LocalDate.of(2026, 9, 22);
        LocalDate wednesday = LocalDate.of(2026, 9, 23);
        LocalDate thursday = LocalDate.of(2026, 9, 24);
        LocalDate friday = LocalDate.of(2026, 9, 25);
        LocalDate saturday = LocalDate.of(2026, 9, 26);

        assertTrue(rule.isOpen(tuesday), "Tuesday should be open by default");
        assertTrue(rule.isOpen(wednesday), "Wednesday should be open by default");
        assertTrue(rule.isOpen(thursday), "Thursday should be open by default");
        assertTrue(rule.isOpen(friday), "Friday should be open by default");
        assertTrue(rule.isOpen(saturday), "Saturday should be open by default");
    }

    @Test
    void shouldOverrideTakesPrecedenceOverDefaultRule() {
        Clock clock = Clock.fixed(LocalDate.of(2026, 9, 22).atStartOfDay(ZONE).toInstant(), ZONE);
        LocalDate tuesday = LocalDate.of(2026, 9, 22);

        Map<LocalDate, OpenDayRule.DaySetting> overrides = new HashMap<>();
        overrides.put(tuesday, new OpenDayRule.DaySetting(tuesday, false, "Maintenance"));

        OpenDayRule rule = new OpenDayRule(clock, overrides);
        assertFalse(rule.isOpen(tuesday), "Override closed should take precedence over default open");
    }

    @Test
    void shouldReturnDefaultCloseReasonForMonday() {
        Clock clock = Clock.fixed(LocalDate.of(2026, 9, 21).atStartOfDay(ZONE).toInstant(), ZONE);
        OpenDayRule rule = new OpenDayRule(clock, Collections.emptyMap());

        LocalDate monday = LocalDate.of(2026, 9, 21);
        assertEquals("周一例行闭馆", rule.getCloseReason(monday));
    }

    @Test
    void shouldReturnNullReasonWhenOpen() {
        Clock clock = Clock.fixed(LocalDate.of(2026, 9, 22).atStartOfDay(ZONE).toInstant(), ZONE);
        OpenDayRule rule = new OpenDayRule(clock, Collections.emptyMap());

        LocalDate tuesday = LocalDate.of(2026, 9, 22);
        assertNull(rule.getCloseReason(tuesday));
        assertNull(rule.getOpenReason(tuesday));
    }

    @Test
    void shouldHandleMultipleOverrides() {
        Clock clock = Clock.fixed(LocalDate.of(2026, 9, 20).atStartOfDay(ZONE).toInstant(), ZONE);

        Map<LocalDate, OpenDayRule.DaySetting> overrides = new HashMap<>();
        overrides.put(LocalDate.of(2026, 9, 20), new OpenDayRule.DaySetting(LocalDate.of(2026, 9, 20), false, "Maintenance"));
        overrides.put(LocalDate.of(2026, 9, 21), new OpenDayRule.DaySetting(LocalDate.of(2026, 9, 21), true, "Special Event"));
        overrides.put(LocalDate.of(2026, 9, 22), new OpenDayRule.DaySetting(LocalDate.of(2026, 9, 22), false, "Renovation"));

        OpenDayRule rule = new OpenDayRule(clock, overrides);

        assertFalse(rule.isOpen(LocalDate.of(2026, 9, 20))); // Sunday closed
        assertTrue(rule.isOpen(LocalDate.of(2026, 9, 21)));  // Monday open
        assertFalse(rule.isOpen(LocalDate.of(2026, 9, 22))); // Tuesday closed
        assertTrue(rule.isOpen(LocalDate.of(2026, 9, 23)));  // Wednesday open (no override)
    }
}