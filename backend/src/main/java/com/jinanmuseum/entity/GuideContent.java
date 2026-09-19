package com.jinanmuseum.entity;

import com.baomidou.mybatisplus.annotation.*;
import java.time.LocalDateTime;

@TableName("guide_content")
public class GuideContent {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String pageKey;
    private String content;
    private LocalDateTime updated_at;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getPageKey() { return pageKey; }
    public void setPageKey(String pageKey) { this.pageKey = pageKey; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public LocalDateTime getUpdatedAt() { return updated_at; }
    public void setUpdatedAt(LocalDateTime updated_at) { this.updated_at = updated_at; }
}