package com.jinanmuseum.dto;

/**
 * 取消预约请求体（SPEC §5.1：code + phone 双凭证）。
 */
public record CancelRequest(String code, String phone) {
}
