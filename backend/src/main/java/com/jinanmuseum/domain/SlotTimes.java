package com.jinanmuseum.domain;

import java.time.LocalTime;

public final class SlotTimes {
    public static final String AM = "AM";
    public static final String PM = "PM";

    public static final LocalTime AM_START = LocalTime.of(9, 0);
    public static final LocalTime AM_END = LocalTime.of(12, 0);
    public static final LocalTime PM_START = LocalTime.of(13, 0);
    public static final LocalTime PM_END = LocalTime.of(16, 30);

    public static final LocalTime AM_CUTOFF = LocalTime.of(9, 0);
    public static final LocalTime PM_CUTOFF = LocalTime.of(13, 0);

    private SlotTimes() {
        // Utility class
    }

    public static String getLabel(String slot) {
        if (AM.equals(slot)) {
            return "上午 9:00–12:00";
        } else if (PM.equals(slot)) {
            return "下午 13:00–16:30";
        }
        throw new IllegalArgumentException("Invalid slot: " + slot);
    }

    public static boolean isValidSlot(String slot) {
        return AM.equals(slot) || PM.equals(slot);
    }
}