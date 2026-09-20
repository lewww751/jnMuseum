package com.jinanmuseum.service;

import com.jinanmuseum.entity.DaySetting;
import com.jinanmuseum.mapper.DaySettingMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

@Service
public class DaySettingService {

    @Autowired
    private DaySettingMapper daySettingMapper;

    public DaySetting getDaySetting(String date) {
        return daySettingMapper.selectByDate(date);
    }

    /** upsert：无记录插入，有记录更新（保留原 id） */
    public void saveDaySetting(DaySetting setting) {
        DaySetting existing = daySettingMapper.selectByDate(setting.getSettingDate());
        if (existing == null) {
            try {
                daySettingMapper.insert(setting);
            } catch (DuplicateKeyException e) {
                existing = daySettingMapper.selectByDate(setting.getSettingDate());
                setting.setId(existing.getId());
                daySettingMapper.updateById(setting);
            }
        } else {
            setting.setId(existing.getId());
            daySettingMapper.updateById(setting);
        }
    }

    public void deleteDaySetting(String date) {
        DaySetting existing = daySettingMapper.selectByDate(date);
        if (existing != null) {
            daySettingMapper.deleteById(existing.getId());
        }
    }
}
