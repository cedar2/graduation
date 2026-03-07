package com.graduation.controller;
import com.graduation.common.ApiResponse;
import com.graduation.entity.NotifyLog;
import com.graduation.service.NotifyLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/notify-log")
public class NotifyLogController {
    @Autowired
    private NotifyLogService notifyLogService;
    @GetMapping
    public ApiResponse<List<NotifyLog>> list() { return ApiResponse.success(notifyLogService.list()); }
    @GetMapping("/{id}")
    public ApiResponse<NotifyLog> getById(@PathVariable Long id) { return ApiResponse.success(notifyLogService.getById(id)); }
    @PostMapping
    public ApiResponse<Boolean> save(@RequestBody NotifyLog log) { return ApiResponse.success(notifyLogService.save(log)); }
    @PutMapping
    public ApiResponse<Boolean> update(@RequestBody NotifyLog log) { return ApiResponse.success(notifyLogService.updateById(log)); }
    @DeleteMapping("/{id}")
    public ApiResponse<Boolean> remove(@PathVariable Long id) { return ApiResponse.success(notifyLogService.removeById(id)); }
}

