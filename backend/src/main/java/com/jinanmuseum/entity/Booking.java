package com.jinanmuseum.entity;

import com.baomidou.mybatisplus.annotation.*;
import java.time.LocalDateTime;
import java.util.List;

@TableName("booking")
public class Booking {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String code;
    private String visitDate;
    private String slot;
    private String phone;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime cancelledAt;
    private LocalDateTime checkedInAt;

    @TableField(exist = false)
    private List<BookingGuest> guests;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getVisitDate() { return visitDate; }
    public void setVisitDate(String visitDate) { this.visitDate = visitDate; }
    public String getSlot() { return slot; }
    public void setSlot(String slot) { this.slot = slot; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getCancelledAt() { return cancelledAt; }
    public void setCancelledAt(LocalDateTime cancelledAt) { this.cancelledAt = cancelledAt; }
    public LocalDateTime getCheckedInAt() { return checkedInAt; }
    public void setCheckedInAt(LocalDateTime checkedInAt) { this.checkedInAt = checkedInAt; }
    public List<BookingGuest> getGuests() { return guests; }
    public void setGuests(List<BookingGuest> guests) { this.guests = guests; }
}