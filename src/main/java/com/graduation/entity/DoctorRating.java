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
@TableName("doctor_rating")
public class DoctorRating {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long appointmentId;
    private Long doctorId;
    private Long patientId;
    private Integer score;
    private LocalDateTime createdAt;
}

