package com.jinanmuseum.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("museum_event")
public class MuseumEvent {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String title;
    private String category; // 讲座 | 工作坊 | 亲子活动 | 其他
    private String location;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String summary;
    private String coverImage;
    private Boolean published;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}