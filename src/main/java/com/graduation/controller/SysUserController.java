package com.graduation.controller;
import com.graduation.common.ApiResponse;
import com.graduation.entity.SysUser;
import com.graduation.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/sys-user")
public class SysUserController {
    @Autowired
    private SysUserService sysUserService;
    @GetMapping
    public ApiResponse<List<SysUser>> list() { return ApiResponse.success(sysUserService.list()); }
    @GetMapping("/{id}")
    public ApiResponse<SysUser> getById(@PathVariable Long id) { return ApiResponse.success(sysUserService.getById(id)); }
    @PostMapping
    public ApiResponse<Boolean> save(@RequestBody SysUser user) { return ApiResponse.success(sysUserService.save(user)); }
    @PutMapping
    public ApiResponse<Boolean> update(@RequestBody SysUser user) { return ApiResponse.success(sysUserService.updateById(user)); }
    @DeleteMapping("/{id}")
    public ApiResponse<Boolean> remove(@PathVariable Long id) { return ApiResponse.success(sysUserService.removeById(id)); }
}

