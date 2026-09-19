package com.jinanmuseum.service;

import com.jinanmuseum.entity.CollectionItem;
import com.jinanmuseum.mapper.CollectionItemMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CollectionItemService {

    @Autowired
    private CollectionItemMapper collectionItemMapper;

    public List<CollectionItem> getCollectionItems() {
        return collectionItemMapper.selectByPublished();
    }

    public CollectionItem getItemById(Long id) {
        return collectionItemMapper.selectById(id);
    }

    public void saveItem(CollectionItem item) {
        if (item.getId() == null) {
            collectionItemMapper.insert(item);
        } else {
            collectionItemMapper.updateById(item);
        }
    }

    public void deleteItem(Long id) {
        collectionItemMapper.deleteById(id);
    }
}