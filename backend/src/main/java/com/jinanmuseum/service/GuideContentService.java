package com.jinanmuseum.service;

import com.jinanmuseum.entity.GuideContent;
import com.jinanmuseum.mapper.GuideContentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GuideContentService {

    @Autowired
    private GuideContentMapper guideContentMapper;

    public GuideContent getGuideContent(String pageKey) {
        return guideContentMapper.selectByPageKey(pageKey);
    }

    public void saveGuideContent(GuideContent content) {
        guideContentMapper.updateById(content);
    }
}