package com.jinanmuseum.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("collection_item")
public class CollectionItem {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private String era;
    private String category;
    private String description;
    private String image;
    private Boolean published;
    private Integer sortOrder;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}