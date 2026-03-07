package com.graduation.controller;
import com.graduation.common.ApiResponse;
import com.graduation.entity.PatientProfile;
import com.graduation.service.PatientProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/patient-profile")
public class PatientProfileController {
    @Autowired
    private PatientProfileService patientProfileService;
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
}

