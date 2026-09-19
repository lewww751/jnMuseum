package com.jinanmuseum.controller.public_;

import com.jinanmuseum.entity.GuideContent;
import com.jinanmuseum.service.GuideContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/guide")
public class GuideController {

    @Autowired
    private GuideContentService guideContentService;

    @GetMapping("/{key}")
    public GuideContent getGuideContent(@PathVariable String key) {
        return guideContentService.getGuideContent(key);
    }
}