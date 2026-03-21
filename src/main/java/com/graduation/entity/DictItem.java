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
@TableName("dict_item")
public class DictItem {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String typeCode;
    private String itemCode;
    private String itemName;
    private Integer sortNo;
    private Integer status;
    private Integer isDefault;
    private String remark;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

