package com.jinanmuseum.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("exhibition")
public class Exhibition {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String title;
    private String kind; // PERMANENT | TEMPORARY
    private String hall;
    private String summary;
    private String content;
    private String coverImage;
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean published;
    private Integer sortOrder;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}