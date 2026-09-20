package com.jinanmuseum.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jinanmuseum.common.BusinessException;
import com.jinanmuseum.entity.Exhibition;
import com.jinanmuseum.mapper.ExhibitionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ExhibitionService {

    @Autowired
    private ExhibitionMapper exhibitionMapper;
    @Autowired
    private Clock clock;

    // ---- 后台：全量 CRUD（含未发布） ----

    public List<Exhibition> getExhibitions(String kind) {
        if (kind == null || kind.isEmpty()) {
            return exhibitionMapper.selectList(null);
        }
        return exhibitionMapper.selectByKind(kind);
    }

    public Exhibition getExhibitionById(Long id) {
        return exhibitionMapper.selectById(id);
    }

    public void saveExhibition(Exhibition exhibition) {
        if (exhibition.getId() == null) {
            exhibitionMapper.insert(exhibition);
        } else {
            exhibitionMapper.updateById(exhibition);
        }
    }

    public void deleteExhibition(Long id) {
        exhibitionMapper.deleteById(id);
    }

    // ---- 观众端：已发布 + 展出状态（SPEC §5.1） ----

    /** 排序：常设（sort_order）在前，临展按结束日期倒序 */
    public List<Map<String, Object>> getPublishedExhibitions(String kind) {
        List<Exhibition> list;
        if (kind == null || kind.isBlank()) {
            List<Exhibition> all = exhibitionMapper.selectList(new LambdaQueryWrapper<Exhibition>()
                    .eq(Exhibition::getPublished, true));
            List<Exhibition> permanent = all.stream()
                    .filter(e -> "PERMANENT".equals(e.getKind()))
                    .sorted(Comparator.comparing(e -> e.getSortOrder() == null ? Integer.MAX_VALUE : e.getSortOrder()))
                    .collect(Collectors.toList());
            List<Exhibition> temporary = all.stream()
                    .filter(e -> "TEMPORARY".equals(e.getKind()))
                    .sorted(Comparator.comparing(Exhibition::getEndDate,
                            Comparator.nullsLast(Comparator.reverseOrder())))
                    .collect(Collectors.toList());
            list = new ArrayList<>(permanent);
            list.addAll(temporary);
        } else {
            list = exhibitionMapper.selectByKind(kind);
        }
        LocalDate today = LocalDate.now(clock);
        return list.stream().map(e -> toPublicView(e, today, false)).collect(Collectors.toList());
    }

    public Map<String, Object> getPublishedExhibition(Long id) {
        Exhibition exhibition = exhibitionMapper.selectById(id);
        if (exhibition == null || !Boolean.TRUE.equals(exhibition.getPublished())) {
            throw new BusinessException("展览不存在");
        }
        return toPublicView(exhibition, LocalDate.now(clock), true);
    }

    private Map<String, Object> toPublicView(Exhibition e, LocalDate today, boolean withContent) {
        Map<String, Object> view = new HashMap<>();
        view.put("id", e.getId());
        view.put("title", e.getTitle());
        view.put("kind", e.getKind());
        view.put("hall", e.getHall());
        view.put("summary", e.getSummary());
        view.put("coverImage", e.getCoverImage());
        view.put("startDate", e.getStartDate() == null ? null : e.getStartDate().toString());
        view.put("endDate", e.getEndDate() == null ? null : e.getEndDate().toString());
        view.put("displayStatus", displayStatus(e, today));
        if (withContent) {
            view.put("content", e.getContent());
        }
        return view;
    }

    /** 常设恒 ONGOING；临展按日期推导 UPCOMING / ONGOING / ENDED */
    private String displayStatus(Exhibition e, LocalDate today) {
        if ("PERMANENT".equals(e.getKind())) {
            return "ONGOING";
        }
        if (e.getStartDate() != null && today.isBefore(e.getStartDate())) {
            return "UPCOMING";
        }
        if (e.getEndDate() != null && today.isAfter(e.getEndDate())) {
            return "ENDED";
        }
        return "ONGOING";
    }
}
