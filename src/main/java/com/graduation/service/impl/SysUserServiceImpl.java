package com.graduation.service.impl;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.graduation.entity.SysUser;
import com.graduation.mapper.SysUserMapper;
import com.graduation.service.SysUserService;
import org.springframework.stereotype.Service;
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {}

