package com.graduation.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("stat_daily_doctor")
public class StatDailyDoctor {
    @TableId(type = IdType.AUTO)
    private Long id;
    private LocalDate statDate;
    private Long deptId;
    private Long doctorId;
    private String timeSlot;
    private Integer totalAppointments;
    private Integer paidCount;
    private Integer checkinCount;
    private Integer completedCount;
    private Integer noShowCount;
    private Integer cancelCount;
    private BigDecimal avgRating;
    private Integer ratingCount;
    private LocalDateTime createdAt;
}

