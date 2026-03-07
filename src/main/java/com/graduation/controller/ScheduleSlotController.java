package com.graduation.controller;
import com.graduation.common.ApiResponse;
import com.graduation.entity.ScheduleSlot;
import com.graduation.service.ScheduleSlotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/schedule-slot")
public class ScheduleSlotController {
    @Autowired
    private ScheduleSlotService scheduleSlotService;
    @GetMapping
    public ApiResponse<List<ScheduleSlot>> list() { return ApiResponse.success(scheduleSlotService.list()); }
    @GetMapping("/{id}")
    public ApiResponse<ScheduleSlot> getById(@PathVariable Long id) { return ApiResponse.success(scheduleSlotService.getById(id)); }
    @PostMapping
    public ApiResponse<Boolean> save(@RequestBody ScheduleSlot slot) { return ApiResponse.success(scheduleSlotService.save(slot)); }
    @PutMapping
    public ApiResponse<Boolean> update(@RequestBody ScheduleSlot slot) { return ApiResponse.success(scheduleSlotService.updateById(slot)); }
    @DeleteMapping("/{id}")
    public ApiResponse<Boolean> remove(@PathVariable Long id) { return ApiResponse.success(scheduleSlotService.removeById(id)); }
}

