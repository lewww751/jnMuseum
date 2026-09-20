package com.jinanmuseum.controller.public_;

import com.jinanmuseum.common.Result;
import com.jinanmuseum.service.ExhibitionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/exhibitions")
public class ExhibitionController {

    @Autowired
    private ExhibitionService exhibitionService;

    /** 已发布展览列表，kind 可选过滤，含 displayStatus（SPEC §5.1） */
    @GetMapping
    public Result<List<Map<String, Object>>> getExhibitions(@RequestParam(required = false) String kind) {
        return Result.success(exhibitionService.getPublishedExhibitions(kind));
    }

    @GetMapping("/{id}")
    public Result<Map<String, Object>> getExhibitionById(@PathVariable Long id) {
        return Result.success(exhibitionService.getPublishedExhibition(id));
    }
}
