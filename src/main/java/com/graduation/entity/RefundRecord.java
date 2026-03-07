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
@TableName("refund_record")
public class RefundRecord {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long appointmentId;
    private String refundNo;
    private String channel;
    private BigDecimal amount;
    private String status;
    private String reason;
    private String thirdRefundNo;
    private LocalDateTime refundedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

