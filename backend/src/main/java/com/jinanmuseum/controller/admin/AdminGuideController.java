package com.jinanmuseum.controller.admin;

import com.jinanmuseum.common.Result;
import com.jinanmuseum.entity.GuideContent;
import com.jinanmuseum.service.GuideContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/guide")
public class AdminGuideController {

    @Autowired
    private GuideContentService guideContentService;

    @GetMapping("/{key}")
    public Result<GuideContent> get(@PathVariable String key) {
        return Result.success(guideContentService.getGuideContent(key));
    }

    @PutMapping("/{key}")
    public Result<GuideContent> save(@PathVariable String key, @RequestBody GuideContent content) {
        content.setPageKey(key);
        guideContentService.saveGuideContent(content);
        return Result.success(content);
    }
}