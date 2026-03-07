package com.graduation.controller;
import com.graduation.common.ApiResponse;
import com.graduation.entity.Appointment;
import com.graduation.service.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/appointment")
public class AppointmentController {
    @Autowired
    private AppointmentService appointmentService;
    @GetMapping
    public ApiResponse<List<Appointment>> list() { return ApiResponse.success(appointmentService.list()); }
    @GetMapping("/{id}")
    public ApiResponse<Appointment> getById(@PathVariable Long id) { return ApiResponse.success(appointmentService.getById(id)); }
    @PostMapping
    public ApiResponse<Boolean> save(@RequestBody Appointment appointment) { return ApiResponse.success(appointmentService.save(appointment)); }
    @PutMapping
    public ApiResponse<Boolean> update(@RequestBody Appointment appointment) { return ApiResponse.success(appointmentService.updateById(appointment)); }
    @DeleteMapping("/{id}")
    public ApiResponse<Boolean> remove(@PathVariable Long id) { return ApiResponse.success(appointmentService.removeById(id)); }
}

