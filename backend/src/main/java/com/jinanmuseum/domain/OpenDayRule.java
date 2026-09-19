package com.jinanmuseum.domain;

import java.time.*;
import java.util.*;

public class OpenDayRule {
    private final Clock clock;
    private final Map<LocalDate, DaySetting> overrides;

    public OpenDayRule(Clock clock, Map<LocalDate, DaySetting> overrides) {
        this.clock = clock;
        this.overrides = overrides != null ? overrides : Collections.emptyMap();
    }

    public boolean isOpen(LocalDate date) {
        DaySetting setting = overrides.get(date);
        if (setting != null) {
            return setting.isOpen;
        }

        // Default rule: Monday is closed, others are open
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        return dayOfWeek != DayOfWeek.MONDAY;
    }

    public String getCloseReason(LocalDate date) {
        if (isOpen(date)) {
            return null;
        }

        DaySetting setting = overrides.get(date);
        if (setting != null && !setting.isOpen) {
            return setting.reason;
        }

        if (date.getDayOfWeek() == DayOfWeek.MONDAY) {
            return "周一例行闭馆";
        }

        return null;
    }

    public String getOpenReason(LocalDate date) {
        if (!isOpen(date)) {
            return null;
        }

        DaySetting setting = overrides.get(date);
        if (setting != null && setting.isOpen) {
            return setting.reason;
        }

        return null;
    }

    public static class DaySetting {
        private final LocalDate date;
        private final boolean isOpen;
        private final String reason;

        public DaySetting(LocalDate date, boolean isOpen, String reason) {
            this.date = date;
            this.isOpen = isOpen;
            this.reason = reason;
        }

        public LocalDate getDate() {
            return date;
        }

        public boolean isOpen() {
            return isOpen;
        }

        public String getReason() {
            return reason;
        }
    }
}