package com.graduation.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("doctor_profile")
public class DoctorProfile {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Long deptId;
    private String realName;
    private String title;
    private String doctorType;
    private String intro;
    private String status;
    private BigDecimal avgRating;
    private Integer ratingCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

