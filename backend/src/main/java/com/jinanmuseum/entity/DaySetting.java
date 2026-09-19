package com.jinanmuseum.entity;

import com.baomidou.mybatisplus.annotation.*;

@TableName("day_setting")
public class DaySetting {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String setting_date;
    private Boolean is_open;
    private String reason;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getSettingDate() { return setting_date; }
    public void setSettingDate(String setting_date) { this.setting_date = setting_date; }
    public Boolean getIsOpen() { return is_open; }
    public void setIsOpen(Boolean is_open) { this.is_open = is_open; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}