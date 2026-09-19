package com.jinanmuseum.controller.public_;

import com.jinanmuseum.entity.CollectionItem;
import com.jinanmuseum.service.CollectionItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/collections")
public class CollectionController {

    @Autowired
    private CollectionItemService collectionItemService;

    @GetMapping
    public List<CollectionItem> getCollections() {
        return collectionItemService.getCollectionItems();
    }
}