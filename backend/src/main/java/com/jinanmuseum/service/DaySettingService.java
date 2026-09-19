package com.jinanmuseum.service;

import com.jinanmuseum.entity.DaySetting;
import com.jinanmuseum.mapper.DaySettingMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DaySettingService {

    @Autowired
    private DaySettingMapper daySettingMapper;

    public DaySetting getDaySetting(String date) {
        return daySettingMapper.selectByDate(date);
    }

    public void saveDaySetting(DaySetting setting) {
        daySettingMapper.updateById(setting);
    }
}