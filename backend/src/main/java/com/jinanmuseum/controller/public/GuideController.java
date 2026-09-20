package com.jinanmuseum.controller.public_;

import com.jinanmuseum.common.BusinessException;
import com.jinanmuseum.common.Result;
import com.jinanmuseum.entity.GuideContent;
import com.jinanmuseum.service.GuideContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/guide")
public class GuideController {

    @Autowired
    private GuideContentService guideContentService;

    /** SPEC §5.1：key ∈ visit|about → {"content": "..."} */
    @GetMapping("/{key}")
    public Result<Map<String, String>> getGuideContent(@PathVariable String key) {
        if (!"visit".equals(key) && !"about".equals(key)) {
            throw new BusinessException("页面不存在");
        }
        GuideContent content = guideContentService.getGuideContent(key);
        if (content == null) {
            throw new BusinessException("内容不存在");
        }
        return Result.success(Map.of("content", content.getContent() == null ? "" : content.getContent()));
    }
}
