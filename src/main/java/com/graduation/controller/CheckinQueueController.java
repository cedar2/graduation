package com.graduation.controller;
import com.graduation.common.ApiResponse;
import com.graduation.entity.CheckinQueue;
import com.graduation.service.CheckinQueueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/checkin-queue")
public class CheckinQueueController {
    @Autowired
    private CheckinQueueService checkinQueueService;
    @GetMapping
    public ApiResponse<List<CheckinQueue>> list() { return ApiResponse.success(checkinQueueService.list()); }
    @GetMapping("/{id}")
    public ApiResponse<CheckinQueue> getById(@PathVariable Long id) { return ApiResponse.success(checkinQueueService.getById(id)); }
    @PostMapping
    public ApiResponse<Boolean> save(@RequestBody CheckinQueue queue) { return ApiResponse.success(checkinQueueService.save(queue)); }
    @PutMapping
    public ApiResponse<Boolean> update(@RequestBody CheckinQueue queue) { return ApiResponse.success(checkinQueueService.updateById(queue)); }
    @DeleteMapping("/{id}")
    public ApiResponse<Boolean> remove(@PathVariable Long id) { return ApiResponse.success(checkinQueueService.removeById(id)); }
}

