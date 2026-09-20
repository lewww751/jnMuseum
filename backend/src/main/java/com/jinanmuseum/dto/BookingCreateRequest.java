package com.jinanmuseum.dto;

import java.util.List;

/**
 * 创建预约请求体（SPEC §5.1）。
 */
public record BookingCreateRequest(String visitDate, String slot, String phone, List<GuestInput> guests) {

    /**
     * 入馆人：type ∈ PRIMARY | COMPANION。
     */
    public record GuestInput(String type, String name, String idCard) {
    }
}
