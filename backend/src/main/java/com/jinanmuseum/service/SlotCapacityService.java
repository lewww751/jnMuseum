package com.jinanmuseum.service;

import com.jinanmuseum.entity.SlotCapacity;
import com.jinanmuseum.mapper.SlotCapacityMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SlotCapacityService {

    @Autowired
    private SlotCapacityMapper slotCapacityMapper;

    public SlotCapacity getSlotCapacity(String date, String slot) {
        return slotCapacityMapper.selectByDateAndSlot(date, slot);
    }

    public void saveSlotCapacity(SlotCapacity capacity) {
        slotCapacityMapper.updateById(capacity);
    }
}