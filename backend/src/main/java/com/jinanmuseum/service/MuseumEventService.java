package com.jinanmuseum.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jinanmuseum.entity.MuseumEvent;
import com.jinanmuseum.mapper.MuseumEventMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class MuseumEventService {

    private static final DateTimeFormatter VIEW_TIME = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Autowired
    private MuseumEventMapper museumEventMapper;
    @Autowired
    private Clock clock;

    // ---- 后台：全量 CRUD（含未发布） ----

    public List<MuseumEvent> getEvents(String category) {
        if (category == null || category.isEmpty()) {
            return museumEventMapper.selectList(null);
        }
        return museumEventMapper.selectByCategory(category);
    }

    public MuseumEvent getEventById(Long id) {
        return museumEventMapper.selectById(id);
    }

    public void saveEvent(MuseumEvent event) {
        if (event.getId() == null) {
            museumEventMapper.insert(event);
        } else {
            museumEventMapper.updateById(event);
        }
    }

    public void deleteEvent(Long id) {
        museumEventMapper.deleteById(id);
    }

    // ---- 观众端：已发布 + 活动状态（SPEC §5.1） ----

    /** 已发布活动按开始时间倒序；eventStatus 按当前时间推导 */
    public List<Map<String, Object>> getPublishedEvents(String category) {
        LambdaQueryWrapper<MuseumEvent> qw = new LambdaQueryWrapper<MuseumEvent>()
                .eq(MuseumEvent::getPublished, true)
                .eq(category != null && !category.isBlank(), MuseumEvent::getCategory, category)
                .orderByDesc(MuseumEvent::getStartTime);
        LocalDateTime now = LocalDateTime.now(clock);
        return museumEventMapper.selectList(qw).stream()
                .map(e -> toPublicView(e, now))
                .collect(Collectors.toList());
    }

    private Map<String, Object> toPublicView(MuseumEvent e, LocalDateTime now) {
        Map<String, Object> view = new HashMap<>();
        view.put("id", e.getId());
        view.put("title", e.getTitle());
        view.put("category", e.getCategory());
        view.put("location", e.getLocation());
        view.put("startTime", e.getStartTime() == null ? null : e.getStartTime().format(VIEW_TIME));
        view.put("endTime", e.getEndTime() == null ? null : e.getEndTime().format(VIEW_TIME));
        view.put("summary", e.getSummary());
        view.put("coverImage", e.getCoverImage());
        view.put("eventStatus", eventStatus(e, now));
        return view;
    }

    /** now<start → UPCOMING；now > COALESCE(end, start+2h) → ENDED；否则 ONGOING */
    private String eventStatus(MuseumEvent e, LocalDateTime now) {
        if (e.getStartTime() == null || now.isBefore(e.getStartTime())) {
            return "UPCOMING";
        }
        LocalDateTime end = e.getEndTime() != null ? e.getEndTime() : e.getStartTime().plusHours(2);
        if (now.isAfter(end)) {
            return "ENDED";
        }
        return "ONGOING";
    }
}
