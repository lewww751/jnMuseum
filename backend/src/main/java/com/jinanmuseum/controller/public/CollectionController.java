package com.jinanmuseum.controller.public_;

import com.jinanmuseum.common.Result;
import com.jinanmuseum.entity.CollectionItem;
import com.jinanmuseum.service.CollectionItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/collections")
public class CollectionController {

    /** 镇馆之宝：种子设定为蛋壳黑陶高柄杯（schema 无独立标记列，按名称推导） */
    private static final String TREASURE_NAME = "蛋壳黑陶高柄杯";

    @Autowired
    private CollectionItemService collectionItemService;

    /** 已发布精选，按 sort_order；isHighlight 供前端打「镇馆之宝」角标 */
    @GetMapping
    public Result<List<Map<String, Object>>> getCollections() {
        List<Map<String, Object>> views = collectionItemService.getCollectionItems().stream()
                .map(this::toView)
                .collect(Collectors.toList());
        return Result.success(views);
    }

    private Map<String, Object> toView(CollectionItem item) {
        Map<String, Object> view = new HashMap<>();
        view.put("id", item.getId());
        view.put("name", item.getName());
        view.put("era", item.getEra());
        view.put("category", item.getCategory());
        view.put("description", item.getDescription());
        view.put("image", item.getImage());
        view.put("isHighlight", item.getName() != null && item.getName().contains(TREASURE_NAME));
        view.put("sortOrder", item.getSortOrder());
        return view;
    }
}
