package com.graduation.controller;
import com.graduation.common.ApiResponse;
import com.graduation.entity.ShiftRequest;
import com.graduation.service.ShiftRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/shift-request")
public class ShiftRequestController {
    @Autowired
    private ShiftRequestService shiftRequestService;
    @GetMapping
    public ApiResponse<List<ShiftRequest>> list() { return ApiResponse.success(shiftRequestService.list()); }
    @GetMapping("/{id}")
    public ApiResponse<ShiftRequest> getById(@PathVariable Long id) { return ApiResponse.success(shiftRequestService.getById(id)); }
    @PostMapping
    public ApiResponse<Boolean> save(@RequestBody ShiftRequest request) { return ApiResponse.success(shiftRequestService.save(request)); }
    @PutMapping
    public ApiResponse<Boolean> update(@RequestBody ShiftRequest request) { return ApiResponse.success(shiftRequestService.updateById(request)); }
    @DeleteMapping("/{id}")
    public ApiResponse<Boolean> remove(@PathVariable Long id) { return ApiResponse.success(shiftRequestService.removeById(id)); }
}

