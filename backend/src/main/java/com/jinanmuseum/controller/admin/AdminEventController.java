package com.jinanmuseum.controller.admin;

import com.jinanmuseum.common.Result;
import com.jinanmuseum.entity.MuseumEvent;
import com.jinanmuseum.service.MuseumEventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/admin/events")
public class AdminEventController {

    @Autowired
    private MuseumEventService museumEventService;

    @GetMapping
    public Result<List<MuseumEvent>> list() {
        return Result.success(museumEventService.getEvents(null));
    }

    @PostMapping
    public Result<MuseumEvent> save(@RequestBody MuseumEvent event) {
        museumEventService.saveEvent(event);
        return Result.success(event);
    }

    @PutMapping
    public Result<MuseumEvent> update(@RequestBody MuseumEvent event) {
        museumEventService.saveEvent(event);
        return Result.success(event);
    }

    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        museumEventService.deleteEvent(id);
        return Result.success("删除成功");
    }
}