package com.graduation.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.graduation.dto.BatchOperationResponse;
import com.graduation.dto.PagedResult;
import com.graduation.dto.ScheduleSlotBatchUpdateRequest;
import com.graduation.dto.ScheduleSlotGenerateRequest;
import com.graduation.dto.ScheduleSlotImportRequest;
import com.graduation.dto.ScheduleSlotQueryRequest;
import com.graduation.dto.ScheduleSlotUpdateRequest;
import com.graduation.dto.ScheduleSlotView;
import com.graduation.entity.ScheduleSlot;

import java.util.List;

public interface ScheduleSlotService extends IService<ScheduleSlot> {

    PagedResult<ScheduleSlotView> query(ScheduleSlotQueryRequest request);

    List<ScheduleSlotView> listAvailable(ScheduleSlotQueryRequest request);

    BatchOperationResponse generateSlots(ScheduleSlotGenerateRequest request);

    BatchOperationResponse batchUpdate(ScheduleSlotBatchUpdateRequest request);

    ScheduleSlotView updateOne(Long id, ScheduleSlotUpdateRequest request);

    BatchOperationResponse importRows(ScheduleSlotImportRequest request);
}
