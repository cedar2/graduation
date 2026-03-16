package com.graduation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class PatientProfileCompleteRequest {

    @NotBlank(message = "realName不能为空")
    private String realName;

    @NotBlank(message = "gender不能为空")
    private String gender;

    @NotNull(message = "birthDate不能为空")
    private LocalDate birthDate;

    @NotBlank(message = "idCardNo不能为空")
    private String idCardNo;

    private String province;

    private String city;
}

