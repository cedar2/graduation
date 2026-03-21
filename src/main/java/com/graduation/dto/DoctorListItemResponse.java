package com.graduation.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class DoctorListItemResponse {
    private Long id;
    private Long userId;
    private Long deptId;
    private String deptName;
    private String realName;
    private String title;
    private String titleName;
    private String doctorType;
    private String doctorTypeName;
    private String status;
    private String statusName;
    private String intro;
    private BigDecimal avgRating;
    private Integer ratingCount;
}

