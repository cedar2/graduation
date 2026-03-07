package com.graduation.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("checkin_queue")
public class CheckinQueue {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long appointmentId;
    private Long doctorId;
    private Long deptId;
    private LocalDate visitDate;
    private String timeSlot;
    private Integer queueNo;
    private String status;
    private LocalDateTime checkedInAt;
    private LocalDateTime calledAt;
    private LocalDateTime doneAt;
}

