package com.graduation.controller;
import com.graduation.common.ApiResponse;
import com.graduation.entity.DoctorProfile;
import com.graduation.service.DoctorProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/doctor-profile")
public class DoctorProfileController {
    @Autowired
    private DoctorProfileService doctorProfileService;
    @GetMapping
    public ApiResponse<List<DoctorProfile>> list() { return ApiResponse.success(doctorProfileService.list()); }
    @GetMapping("/{id}")
    public ApiResponse<DoctorProfile> getById(@PathVariable Long id) { return ApiResponse.success(doctorProfileService.getById(id)); }
    @PostMapping
    public ApiResponse<Boolean> save(@RequestBody DoctorProfile profile) { return ApiResponse.success(doctorProfileService.save(profile)); }
    @PutMapping
    public ApiResponse<Boolean> update(@RequestBody DoctorProfile profile) { return ApiResponse.success(doctorProfileService.updateById(profile)); }
    @DeleteMapping("/{id}")
    public ApiResponse<Boolean> remove(@PathVariable Long id) { return ApiResponse.success(doctorProfileService.removeById(id)); }
}

