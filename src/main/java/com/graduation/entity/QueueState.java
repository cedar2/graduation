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
@TableName("queue_state")
public class QueueState {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long doctorId;
    private LocalDate visitDate;
    private String timeSlot;
    private Integer currentCallNo;
    private Long updatedBy;
    private LocalDateTime updatedAt;
}

