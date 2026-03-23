package com.graduation.controller;

import com.graduation.common.ApiResponse;
import com.graduation.dto.BatchOperationResponse;
import com.graduation.dto.PagedResult;
import com.graduation.dto.ScheduleSlotBatchUpdateRequest;
import com.graduation.dto.ScheduleSlotGenerateRequest;
import com.graduation.dto.ScheduleSlotImportRequest;
import com.graduation.dto.ScheduleSlotQueryRequest;
import com.graduation.dto.ScheduleSlotUpdateRequest;
import com.graduation.dto.ScheduleSlotView;
import com.graduation.entity.ScheduleSlot;
import com.graduation.service.ScheduleSlotService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/schedule-slot")
public class ScheduleSlotController {

    private final ScheduleSlotService scheduleSlotService;

    public ScheduleSlotController(ScheduleSlotService scheduleSlotService) {
        this.scheduleSlotService = scheduleSlotService;
    }

    @GetMapping
    public ApiResponse<PagedResult<ScheduleSlotView>> query(ScheduleSlotQueryRequest request) {
        return ApiResponse.success(scheduleSlotService.query(request));
    }

    @GetMapping("/available")
    public ApiResponse<List<ScheduleSlotView>> available(ScheduleSlotQueryRequest request) {
        return ApiResponse.success(scheduleSlotService.listAvailable(request));
    }

    @GetMapping("/{id}")
    public ApiResponse<ScheduleSlot> getById(@PathVariable Long id) {
        return ApiResponse.success(scheduleSlotService.getById(id));
    }

    @PostMapping("/generate")
    public ApiResponse<BatchOperationResponse> generate(@Valid @RequestBody ScheduleSlotGenerateRequest request) {
        return ApiResponse.success(scheduleSlotService.generateSlots(request));
    }

    @PutMapping("/{id}")
    public ApiResponse<ScheduleSlotView> updateOne(@PathVariable Long id,
                                                   @Valid @RequestBody ScheduleSlotUpdateRequest request) {
        return ApiResponse.success(scheduleSlotService.updateOne(id, request));
    }

    @PutMapping("/batch")
    public ApiResponse<BatchOperationResponse> batchUpdate(@Valid @RequestBody ScheduleSlotBatchUpdateRequest request) {
        return ApiResponse.success(scheduleSlotService.batchUpdate(request));
    }

    @PostMapping("/import")
    public ApiResponse<BatchOperationResponse> importRows(@Valid @RequestBody ScheduleSlotImportRequest request) {
        return ApiResponse.success(scheduleSlotService.importRows(request));
    }
}
