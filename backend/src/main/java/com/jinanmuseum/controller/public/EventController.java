package com.jinanmuseum.controller.public_;

import com.jinanmuseum.common.Result;
import com.jinanmuseum.service.MuseumEventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/events")
public class EventController {

    @Autowired
    private MuseumEventService museumEventService;

    /** 已发布活动列表，category 可选过滤，含 eventStatus（SPEC §5.1） */
    @GetMapping
    public Result<List<Map<String, Object>>> getEvents(@RequestParam(required = false) String category) {
        return Result.success(museumEventService.getPublishedEvents(category));
    }
}
