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
@TableName("notify_log")
public class NotifyLog {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String bizType;
    private Long bizId;
    private Long receiverUserId;
    private String channel;
    private String templateCode;
    private String content;
    private String status;
    private LocalDateTime sentAt;
    private String errorMsg;
    private LocalDateTime createdAt;
}

