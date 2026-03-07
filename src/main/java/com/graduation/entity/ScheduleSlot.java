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
@TableName("schedule_slot")
public class ScheduleSlot {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long doctorId;
    private Long deptId;
    private LocalDate visitDate;
    private String timeSlot;
    private String status;
    private Integer capacity;
    private Integer remaining;
    private BigDecimal fee;
    private LocalDateTime openTime;
    private LocalDateTime stopTime;
    private Long createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

