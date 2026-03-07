package com.graduation.controller;
import com.graduation.common.ApiResponse;
import com.graduation.entity.PaymentRecord;
import com.graduation.service.PaymentRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/payment-record")
public class PaymentRecordController {
    @Autowired
    private PaymentRecordService paymentRecordService;
    @GetMapping
    public ApiResponse<List<PaymentRecord>> list() { return ApiResponse.success(paymentRecordService.list()); }
    @GetMapping("/{id}")
    public ApiResponse<PaymentRecord> getById(@PathVariable Long id) { return ApiResponse.success(paymentRecordService.getById(id)); }
    @PostMapping
    public ApiResponse<Boolean> save(@RequestBody PaymentRecord record) { return ApiResponse.success(paymentRecordService.save(record)); }
    @PutMapping
    public ApiResponse<Boolean> update(@RequestBody PaymentRecord record) { return ApiResponse.success(paymentRecordService.updateById(record)); }
    @DeleteMapping("/{id}")
    public ApiResponse<Boolean> remove(@PathVariable Long id) { return ApiResponse.success(paymentRecordService.removeById(id)); }
}

