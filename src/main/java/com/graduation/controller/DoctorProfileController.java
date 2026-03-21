package com.graduation.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.graduation.common.ApiResponse;
import com.graduation.dto.DoctorListItemResponse;
import com.graduation.entity.DoctorProfile;
import com.graduation.service.DoctorProfileService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/doctor-profile")
public class DoctorProfileController {

    private final DoctorProfileService doctorProfileService;

    public DoctorProfileController(DoctorProfileService doctorProfileService) {
        this.doctorProfileService = doctorProfileService;
    }

    @GetMapping
    public ApiResponse<List<DoctorProfile>> list(@RequestParam(required = false) Long deptId,
                                                 @RequestParam(required = false) String status,
                                                 @RequestParam(required = false) String doctorType,
                                                 @RequestParam(required = false) String keyword) {
        LambdaQueryWrapper<DoctorProfile> wrapper = new LambdaQueryWrapper<DoctorProfile>()
                .orderByAsc(DoctorProfile::getId);
        if (deptId != null) {
            wrapper.eq(DoctorProfile::getDeptId, deptId);
        }
        if (status != null && !status.isBlank()) {
            wrapper.eq(DoctorProfile::getStatus, status);
        }
        if (doctorType != null && !doctorType.isBlank()) {
            wrapper.eq(DoctorProfile::getDoctorType, doctorType);
        }
        if (keyword != null && !keyword.isBlank()) {
            wrapper.like(DoctorProfile::getRealName, keyword);
        }
        return ApiResponse.success(doctorProfileService.list(wrapper));
    }

    @GetMapping("/options")
    public ApiResponse<List<DoctorListItemResponse>> listForDisplay(@RequestParam(required = false) Long deptId,
                                                                    @RequestParam(required = false) String status,
                                                                    @RequestParam(required = false) String doctorType,
                                                                    @RequestParam(required = false) String keyword) {
        return ApiResponse.success(doctorProfileService.listForDisplay(deptId, keyword, status, doctorType));
    }

    @GetMapping("/{id}")
    public ApiResponse<DoctorProfile> getById(@PathVariable Long id) {
        return ApiResponse.success(doctorProfileService.getById(id));
    }

    @PostMapping
    public ApiResponse<Boolean> save(@RequestBody DoctorProfile profile) {
        return ApiResponse.success(doctorProfileService.save(profile));
    }

    @PutMapping
    public ApiResponse<Boolean> update(@RequestBody DoctorProfile profile) {
        return ApiResponse.success(doctorProfileService.updateById(profile));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Boolean> remove(@PathVariable Long id) {
        return ApiResponse.success(doctorProfileService.removeById(id));
    }
}
