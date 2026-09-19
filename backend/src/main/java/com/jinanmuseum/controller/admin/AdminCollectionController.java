package com.jinanmuseum.controller.admin;

import com.jinanmuseum.common.Result;
import com.jinanmuseum.entity.CollectionItem;
import com.jinanmuseum.service.CollectionItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/admin/collections")
public class AdminCollectionController {

    @Autowired
    private CollectionItemService collectionItemService;

    @GetMapping
    public Result<List<CollectionItem>> list() {
        return Result.success(collectionItemService.getCollectionItems());
    }

    @PostMapping
    public Result<CollectionItem> save(@RequestBody CollectionItem item) {
        collectionItemService.saveItem(item);
        return Result.success(item);
    }

    @PutMapping
    public Result<CollectionItem> update(@RequestBody CollectionItem item) {
        collectionItemService.saveItem(item);
        return Result.success(item);
    }

    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        collectionItemService.deleteItem(id);
        return Result.success("删除成功");
    }
}