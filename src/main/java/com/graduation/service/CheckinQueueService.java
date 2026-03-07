package com.graduation.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.graduation.dto.CheckinRequest;
import com.graduation.dto.CheckinResponse;
import com.graduation.entity.CheckinQueue;

public interface CheckinQueueService extends IService<CheckinQueue> {
    CheckinResponse checkin(CheckinRequest request);
}
