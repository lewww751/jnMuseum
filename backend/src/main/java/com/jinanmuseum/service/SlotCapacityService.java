package com.jinanmuseum.service;

import com.jinanmuseum.common.BusinessException;
import com.jinanmuseum.entity.SlotCapacity;
import com.jinanmuseum.mapper.SlotCapacityMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

@Service
public class SlotCapacityService {

    @Autowired
    private SlotCapacityMapper slotCapacityMapper;

    public SlotCapacity getSlotCapacity(String date, String slot) {
        return slotCapacityMapper.selectByDateAndSlot(date, slot);
    }

    /** upsert：无记录插入，有记录更新容量（并发首建回落更新） */
    public void saveSlotCapacity(SlotCapacity capacity) {
        SlotCapacity existing = slotCapacityMapper.selectByDateAndSlot(capacity.getCapacityDate(), capacity.getSlot());
        if (existing == null) {
            try {
                slotCapacityMapper.insert(capacity);
                return;
            } catch (DuplicateKeyException e) {
                existing = slotCapacityMapper.selectByDateAndSlot(capacity.getCapacityDate(), capacity.getSlot());
            }
        }
        existing.setCapacity(capacity.getCapacity());
        slotCapacityMapper.updateById(existing);
    }
}
