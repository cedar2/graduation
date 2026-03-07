package com.graduation.controller;
import com.graduation.common.ApiResponse;
import com.graduation.entity.QueueState;
import com.graduation.service.QueueStateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/queue-state")
public class QueueStateController {
    @Autowired
    private QueueStateService queueStateService;
    @GetMapping
    public ApiResponse<List<QueueState>> list() { return ApiResponse.success(queueStateService.list()); }
    @GetMapping("/{id}")
    public ApiResponse<QueueState> getById(@PathVariable Long id) { return ApiResponse.success(queueStateService.getById(id)); }
    @PostMapping
    public ApiResponse<Boolean> save(@RequestBody QueueState state) { return ApiResponse.success(queueStateService.save(state)); }
    @PutMapping
    public ApiResponse<Boolean> update(@RequestBody QueueState state) { return ApiResponse.success(queueStateService.updateById(state)); }
    @DeleteMapping("/{id}")
    public ApiResponse<Boolean> remove(@PathVariable Long id) { return ApiResponse.success(queueStateService.removeById(id)); }
}

