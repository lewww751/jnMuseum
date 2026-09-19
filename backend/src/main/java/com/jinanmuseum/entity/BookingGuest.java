package com.jinanmuseum.entity;

import com.baomidou.mybatisplus.annotation.*;

@TableName("booking_guest")
public class BookingGuest {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long bookingId;
    private String guestType;
    private String name;
    private String idCard;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getBookingId() { return bookingId; }
    public void setBookingId(Long bookingId) { this.bookingId = bookingId; }
    public String getGuestType() { return guestType; }
    public void setGuestType(String guestType) { this.guestType = guestType; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getIdCard() { return idCard; }
    public void setIdCard(String idCard) { this.idCard = idCard; }
}