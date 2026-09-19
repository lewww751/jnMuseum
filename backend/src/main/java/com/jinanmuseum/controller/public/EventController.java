package com.jinanmuseum.controller.public_;

import com.jinanmuseum.entity.MuseumEvent;
import com.jinanmuseum.service.MuseumEventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/events")
public class EventController {

    @Autowired
    private MuseumEventService museumEventService;

    @GetMapping
    public List<MuseumEvent> getEvents(String category) {
        return museumEventService.getEvents(category);
    }
}