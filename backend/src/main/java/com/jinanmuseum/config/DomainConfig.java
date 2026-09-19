package com.jinanmuseum.config;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jinanmuseum.domain.BookingPolicy;
import com.jinanmuseum.domain.OpenDayRule;
import com.jinanmuseum.entity.DaySetting;
import com.jinanmuseum.mapper.DaySettingMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;

/**
 * 领域类生产装配：Clock 固定 Asia/Shanghai（SPEC 约定），
 * OpenDayRule 覆盖项实时读 day_setting 表（后台月历修改即时生效），无覆盖回落默认规则。
 */
@Configuration
public class DomainConfig {

    @Bean
    public Clock clock() {
        return Clock.system(ZoneId.of("Asia/Shanghai"));
    }

    @Bean
    public OpenDayRule openDayRule(Clock clock, DaySettingMapper daySettingMapper) {
        return new OpenDayRule(clock, java.util.Collections.emptyMap()) {
            @Override
            public boolean isOpen(LocalDate date) {
                com.jinanmuseum.entity.DaySetting setting = find(date, daySettingMapper);
                if (setting != null) {
                    return Boolean.TRUE.equals(setting.getIsOpen());
                }
                return date.getDayOfWeek() != DayOfWeek.MONDAY;
            }

            @Override
            public String getCloseReason(LocalDate date) {
                if (isOpen(date)) {
                    return null;
                }
                com.jinanmuseum.entity.DaySetting setting = find(date, daySettingMapper);
                if (setting != null && !Boolean.TRUE.equals(setting.getIsOpen())) {
                    return setting.getReason();
                }
                if (date.getDayOfWeek() == DayOfWeek.MONDAY) {
                    return "周一例行闭馆";
                }
                return null;
            }

            @Override
            public String getOpenReason(LocalDate date) {
                if (!isOpen(date)) {
                    return null;
                }
                com.jinanmuseum.entity.DaySetting setting = find(date, daySettingMapper);
                if (setting != null && Boolean.TRUE.equals(setting.getIsOpen())) {
                    return setting.getReason();
                }
                return null;
            }
        };
    }

    private static DaySetting find(LocalDate date, DaySettingMapper mapper) {
        return mapper.selectOne(new QueryWrapper<DaySetting>().eq("setting_date", date.toString()));
    }

    @Bean
    public BookingPolicy bookingPolicy(Clock clock, OpenDayRule openDayRule) {
        return new BookingPolicy(clock, openDayRule);
    }
}
