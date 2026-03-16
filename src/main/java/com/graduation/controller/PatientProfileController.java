package com.graduation.controller;

import com.graduation.common.ApiResponse;
import com.graduation.common.AuthUser;
import com.graduation.config.AuthContextHolder;
import com.graduation.dto.PatientProfileCompleteRequest;
import com.graduation.entity.PatientProfile;
import com.graduation.service.PatientProfileService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/patient-profile")
public class PatientProfileController {
    @Autowired
    private PatientProfileService patientProfileService;

    @GetMapping("/me")
    public ApiResponse<PatientProfile> me() {
        AuthUser authUser = requirePatient();
        return ApiResponse.success(patientProfileService.getByUserId(authUser.getUserId()));
    }

    @PutMapping("/me")
    public ApiResponse<PatientProfile> completeProfile(@Valid @RequestBody PatientProfileCompleteRequest request) {
        AuthUser authUser = requirePatient();
        return ApiResponse.success(patientProfileService.completeProfile(authUser.getUserId(), request));
    }

    @GetMapping
    public ApiResponse<List<PatientProfile>> list() { return ApiResponse.success(patientProfileService.list()); }

    @GetMapping("/{id}")
    public ApiResponse<PatientProfile> getById(@PathVariable Long id) { return ApiResponse.success(patientProfileService.getById(id)); }

    @PostMapping
    public ApiResponse<Boolean> save(@RequestBody PatientProfile profile) { return ApiResponse.success(patientProfileService.save(profile)); }

    @PutMapping
    public ApiResponse<Boolean> update(@RequestBody PatientProfile profile) { return ApiResponse.success(patientProfileService.updateById(profile)); }

    @DeleteMapping("/{id}")
    public ApiResponse<Boolean> remove(@PathVariable Long id) { return ApiResponse.success(patientProfileService.removeById(id)); }

    private AuthUser requirePatient() {
        AuthUser authUser = AuthContextHolder.get();
        if (authUser == null) {
            throw new IllegalStateException("未登录");
        }
        if (!"PATIENT".equals(authUser.getRole())) {
            throw new IllegalStateException("仅患者可操作个人档案");
        }
        return authUser;
    }
}
