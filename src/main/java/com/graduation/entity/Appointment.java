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
@TableName("appointment")
public class Appointment {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String appointmentNo;
    private Long patientId;
    private Long doctorId;
    private Long deptId;
    private Long scheduleId;
    private LocalDate visitDate;
    private String timeSlot;
    private BigDecimal fee;
    private String status;
    private LocalDateTime paidAt;
    private LocalDateTime canceledAt;
    private LocalDateTime refundedAt;
    private LocalDateTime checkedInAt;
    private LocalDateTime completedAt;
    private LocalDateTime noShowMarkedAt;
    private String cancelReason;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

