package com.graduation.controller;
import com.graduation.common.ApiResponse;
import com.graduation.entity.RefundRecord;
import com.graduation.service.RefundRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/refund-record")
public class RefundRecordController {
    @Autowired
    private RefundRecordService refundRecordService;
    @GetMapping
    public ApiResponse<List<RefundRecord>> list() { return ApiResponse.success(refundRecordService.list()); }
    @GetMapping("/{id}")
    public ApiResponse<RefundRecord> getById(@PathVariable Long id) { return ApiResponse.success(refundRecordService.getById(id)); }
    @PostMapping
    public ApiResponse<Boolean> save(@RequestBody RefundRecord record) { return ApiResponse.success(refundRecordService.save(record)); }
    @PutMapping
    public ApiResponse<Boolean> update(@RequestBody RefundRecord record) { return ApiResponse.success(refundRecordService.updateById(record)); }
    @DeleteMapping("/{id}")
    public ApiResponse<Boolean> remove(@PathVariable Long id) { return ApiResponse.success(refundRecordService.removeById(id)); }
}

