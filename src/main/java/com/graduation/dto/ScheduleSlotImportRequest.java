package com.graduation.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class ScheduleSlotImportRequest {
    @Valid
    @NotEmpty(message = "rows不能为空")
    private List<ScheduleSlotImportRowRequest> rows;
}

