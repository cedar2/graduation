package com.graduation.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("department")
public class Department {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private String code;
    private String locationDesc;
    private String floor;
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

