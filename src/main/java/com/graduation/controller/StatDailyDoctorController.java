package com.graduation.controller;
import com.graduation.common.ApiResponse;
import com.graduation.entity.StatDailyDoctor;
import com.graduation.service.StatDailyDoctorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/stat-daily-doctor")
public class StatDailyDoctorController {
    @Autowired
    private StatDailyDoctorService statDailyDoctorService;
    @GetMapping
    public ApiResponse<List<StatDailyDoctor>> list() { return ApiResponse.success(statDailyDoctorService.list()); }
    @GetMapping("/{id}")
    public ApiResponse<StatDailyDoctor> getById(@PathVariable Long id) { return ApiResponse.success(statDailyDoctorService.getById(id)); }
    @PostMapping
    public ApiResponse<Boolean> save(@RequestBody StatDailyDoctor stat) { return ApiResponse.success(statDailyDoctorService.save(stat)); }
    @PutMapping
    public ApiResponse<Boolean> update(@RequestBody StatDailyDoctor stat) { return ApiResponse.success(statDailyDoctorService.updateById(stat)); }
    @DeleteMapping("/{id}")
    public ApiResponse<Boolean> remove(@PathVariable Long id) { return ApiResponse.success(statDailyDoctorService.removeById(id)); }
}

