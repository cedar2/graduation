package com.graduation.controller;
import com.graduation.common.ApiResponse;
import com.graduation.entity.DoctorRating;
import com.graduation.service.DoctorRatingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/doctor-rating")
public class DoctorRatingController {
    @Autowired
    private DoctorRatingService doctorRatingService;
    @GetMapping
    public ApiResponse<List<DoctorRating>> list() { return ApiResponse.success(doctorRatingService.list()); }
    @GetMapping("/{id}")
    public ApiResponse<DoctorRating> getById(@PathVariable Long id) { return ApiResponse.success(doctorRatingService.getById(id)); }
    @PostMapping
    public ApiResponse<Boolean> save(@RequestBody DoctorRating rating) { return ApiResponse.success(doctorRatingService.save(rating)); }
    @PutMapping
    public ApiResponse<Boolean> update(@RequestBody DoctorRating rating) { return ApiResponse.success(doctorRatingService.updateById(rating)); }
    @DeleteMapping("/{id}")
    public ApiResponse<Boolean> remove(@PathVariable Long id) { return ApiResponse.success(doctorRatingService.removeById(id)); }
}

