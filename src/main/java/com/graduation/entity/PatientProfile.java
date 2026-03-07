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
@TableName("patient_profile")
public class PatientProfile {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String realName;
    private String gender;
    private LocalDate birthDate;
    private String idCardNo;
    private String province;
    private String city;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

