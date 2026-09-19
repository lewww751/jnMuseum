package com.jinanmuseum.entity;

import com.baomidou.mybatisplus.annotation.*;

@TableName("slot_capacity")
public class SlotCapacity {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String capacity_date;
    private String slot;
    private Integer capacity;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCapacityDate() { return capacity_date; }
    public void setCapacityDate(String capacity_date) { this.capacity_date = capacity_date; }
    public String getSlot() { return slot; }
    public void setSlot(String slot) { this.slot = slot; }
    public Integer getCapacity() { return capacity; }
    public void setCapacity(Integer capacity) { this.capacity = capacity; }
}