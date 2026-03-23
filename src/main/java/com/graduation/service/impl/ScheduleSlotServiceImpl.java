package com.graduation.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.graduation.common.AuthUser;
import com.graduation.config.AuthContextHolder;
import com.graduation.dto.BatchOperationResponse;
import com.graduation.dto.PagedResult;
import com.graduation.dto.ScheduleSlotBatchUpdateRequest;
import com.graduation.dto.ScheduleSlotGenerateRequest;
import com.graduation.dto.ScheduleSlotImportRequest;
import com.graduation.dto.ScheduleSlotImportRowRequest;
import com.graduation.dto.ScheduleSlotQueryRequest;
import com.graduation.dto.ScheduleSlotUpdateRequest;
import com.graduation.dto.ScheduleSlotView;
import com.graduation.entity.DictItem;
import com.graduation.entity.DoctorProfile;
import com.graduation.entity.ScheduleSlot;
import com.graduation.entity.SysUser;
import com.graduation.mapper.DictItemMapper;
import com.graduation.mapper.DoctorProfileMapper;
import com.graduation.mapper.ScheduleSlotMapper;
import com.graduation.mapper.SysUserMapper;
import com.graduation.service.ScheduleSlotService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ScheduleSlotServiceImpl extends ServiceImpl<ScheduleSlotMapper, ScheduleSlot> implements ScheduleSlotService {

    private static final String ROLE_ADMIN = "ADMIN";
    private static final String ROLE_DOCTOR = "DOCTOR";

    private static final String SCOPE_DEPT = "DEPT";

    private static final String SLOT_AM = "AM";
    private static final String SLOT_PM = "PM";
    private static final Set<String> ALLOWED_TIME_SLOTS = Set.of(SLOT_AM, SLOT_PM);

    private static final String STATUS_OPEN = "OPEN";
    private static final String STATUS_CLOSED = "CLOSED";
    private static final String STATUS_FULL = "FULL";
    private static final Set<String> ALLOWED_STATUS = Set.of(STATUS_OPEN, STATUS_CLOSED, STATUS_FULL);

    private final DoctorProfileMapper doctorProfileMapper;
    private final DictItemMapper dictItemMapper;
    private final SysUserMapper sysUserMapper;

    public ScheduleSlotServiceImpl(DoctorProfileMapper doctorProfileMapper,
                                   DictItemMapper dictItemMapper,
                                   SysUserMapper sysUserMapper) {
        this.doctorProfileMapper = doctorProfileMapper;
        this.dictItemMapper = dictItemMapper;
        this.sysUserMapper = sysUserMapper;
    }

    @Override
    public PagedResult<ScheduleSlotView> query(ScheduleSlotQueryRequest request) {
        AccessScope scope = resolveScope(false);
        LambdaQueryWrapper<ScheduleSlot> wrapper = buildQueryWrapper(request, scope);

        Long pageNo = normalizePageNo(request.pageNo());
        Long pageSize = normalizePageSize(request.pageSize());
        boolean paged = request.paged() == null || request.paged();

        long total = baseMapper.selectCount(wrapper);
        List<ScheduleSlot> records;
        if (!paged) {
            records = baseMapper.selectList(wrapper);
            pageNo = 1L;
            pageSize = (long) records.size();
        } else {
            long offset = (pageNo - 1L) * pageSize;
            wrapper.last("LIMIT " + offset + "," + pageSize);
            records = baseMapper.selectList(wrapper);
        }

        return PagedResult.<ScheduleSlotView>builder()
                .total(total)
                .pageNo(pageNo)
                .pageSize(pageSize)
                .records(toViews(records))
                .build();
    }

    @Override
    public List<ScheduleSlotView> listAvailable(ScheduleSlotQueryRequest request) {
        LocalDate startDate = request.startDate() == null ? LocalDate.now() : request.startDate();
        LocalDate endDate = request.endDate() == null ? startDate.plusDays(6) : request.endDate();

        if (endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("结束日期不能早于开始日期");
        }

        ScheduleSlotQueryRequest normalized = new ScheduleSlotQueryRequest(
                request.deptId(),
                request.doctorId(),
                startDate,
                endDate,
                request.timeSlot(),
                STATUS_OPEN,
                request.minRemaining() == null ? 1 : request.minRemaining(),
                1L,
                5000L,
                false
        );

        return query(normalized).getRecords();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BatchOperationResponse generateSlots(ScheduleSlotGenerateRequest request) {
        AccessScope scope = resolveScope(true);
        validateDateRange(request.getStartDate(), request.getEndDate());

        Set<String> timeSlots = normalizeTimeSlots(request.getTimeSlots());
        String status = normalizeStatusInput(request.getStatus(), STATUS_OPEN);

        List<Long> doctorIds = resolveTargetDoctorIds(request.getDoctorIds(), scope, true);
        if (doctorIds.isEmpty()) {
            throw new IllegalArgumentException("未找到可生成排班的医生");
        }

        Map<Long, DoctorProfile> doctorMap = doctorProfileMapper.selectBatchIds(doctorIds).stream()
                .collect(Collectors.toMap(DoctorProfile::getId, d -> d, (a, b) -> a));
        if (doctorMap.isEmpty()) {
            throw new IllegalArgumentException("医生不存在");
        }

        List<ScheduleSlot> existing = baseMapper.selectList(new LambdaQueryWrapper<ScheduleSlot>()
                .in(ScheduleSlot::getDoctorId, doctorMap.keySet())
                .between(ScheduleSlot::getVisitDate, request.getStartDate(), request.getEndDate()));
        Set<String> existingKeys = existing.stream().map(this::buildUniqueKey).collect(Collectors.toSet());

        int success = 0;
        int skipped = 0;
        List<String> skippedReasons = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        for (DoctorProfile doctor : doctorMap.values()) {
            assertWriteAccessToDoctor(scope, doctor);
            LocalDate cursor = request.getStartDate();
            while (!cursor.isAfter(request.getEndDate())) {
                for (String timeSlot : timeSlots) {
                    String key = buildUniqueKey(doctor.getId(), cursor, timeSlot);
                    if (existingKeys.contains(key)) {
                        skipped++;
                        collectReason(skippedReasons, "已存在: doctorId=" + doctor.getId() + ", date=" + cursor + ", slot=" + timeSlot);
                        continue;
                    }

                    Integer capacity = resolveCapacity(request, timeSlot);
                    BigDecimal fee = resolveFee(request, timeSlot);

                    ScheduleSlot slot = ScheduleSlot.builder()
                            .doctorId(doctor.getId())
                            .deptId(doctor.getDeptId())
                            .visitDate(cursor)
                            .timeSlot(timeSlot)
                            .status(normalizeStatusByRemaining(status, capacity))
                            .capacity(capacity)
                            .remaining(capacity)
                            .fee(fee)
                            .createdBy(scope.userId)
                            .createdAt(now)
                            .updatedAt(now)
                            .build();
                    baseMapper.insert(slot);
                    success++;
                    existingKeys.add(key);
                }
                cursor = cursor.plusDays(1);
            }
        }

        return BatchOperationResponse.builder()
                .successCount(success)
                .skippedCount(skipped)
                .skippedReasons(skippedReasons)
                .build();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BatchOperationResponse batchUpdate(ScheduleSlotBatchUpdateRequest request) {
        AccessScope scope = resolveScope(true);
        ensureUpdatePayloadNotEmpty(request.getCapacity(), request.getFee(), request.getStatus());

        List<ScheduleSlot> targets = selectTargetSlots(request, scope);
        if (targets.isEmpty()) {
            throw new IllegalArgumentException("未找到需要更新的排班号源");
        }

        String targetStatus = request.getStatus() == null ? null : normalizeStatusInput(request.getStatus(), null);

        int success = 0;
        int skipped = 0;
        List<String> skippedReasons = new ArrayList<>();

        for (ScheduleSlot slot : targets) {
            try {
                applyUpdate(slot, request.getCapacity(), request.getFee(), targetStatus);
                success++;
            } catch (IllegalArgumentException ex) {
                skipped++;
                collectReason(skippedReasons, "id=" + slot.getId() + ": " + ex.getMessage());
            }
        }

        return BatchOperationResponse.builder()
                .successCount(success)
                .skippedCount(skipped)
                .skippedReasons(skippedReasons)
                .build();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ScheduleSlotView updateOne(Long id, ScheduleSlotUpdateRequest request) {
        AccessScope scope = resolveScope(true);
        ensureUpdatePayloadNotEmpty(request.getCapacity(), request.getFee(), request.getStatus());

        ScheduleSlot slot = baseMapper.selectById(id);
        if (slot == null) {
            throw new IllegalArgumentException("排班号源不存在");
        }
        assertWriteAccessToSlot(scope, slot);

        String status = request.getStatus() == null ? null : normalizeStatusInput(request.getStatus(), null);
        applyUpdate(slot, request.getCapacity(), request.getFee(), status);
        return toView(baseMapper.selectById(id), loadDictNameMap());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BatchOperationResponse importRows(ScheduleSlotImportRequest request) {
        AccessScope scope = resolveScope(true);

        List<ScheduleSlotImportRowRequest> rows = request.getRows();
        if (rows == null || rows.isEmpty()) {
            throw new IllegalArgumentException("导入数据为空");
        }

        Set<Long> doctorIds = rows.stream().map(ScheduleSlotImportRowRequest::getDoctorId).collect(Collectors.toSet());
        Map<Long, DoctorProfile> doctorMap = doctorProfileMapper.selectBatchIds(doctorIds).stream()
                .collect(Collectors.toMap(DoctorProfile::getId, d -> d, (a, b) -> a));

        int success = 0;
        int skipped = 0;
        List<String> skippedReasons = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        for (int i = 0; i < rows.size(); i++) {
            ScheduleSlotImportRowRequest row = rows.get(i);
            try {
                String timeSlot = normalizeTimeSlot(row.getTimeSlot());
                String status = normalizeStatusInput(row.getStatus(), STATUS_OPEN);
                DoctorProfile doctor = doctorMap.get(row.getDoctorId());
                if (doctor == null) {
                    throw new IllegalArgumentException("doctorId不存在");
                }
                assertWriteAccessToDoctor(scope, doctor);

                ScheduleSlot exists = baseMapper.selectOne(new LambdaQueryWrapper<ScheduleSlot>()
                        .eq(ScheduleSlot::getDoctorId, row.getDoctorId())
                        .eq(ScheduleSlot::getVisitDate, row.getVisitDate())
                        .eq(ScheduleSlot::getTimeSlot, timeSlot)
                        .last("LIMIT 1"));
                if (exists != null) {
                    skipped++;
                    collectReason(skippedReasons, "第" + (i + 1) + "行重复: doctorId=" + row.getDoctorId() + ", date=" + row.getVisitDate() + ", slot=" + timeSlot);
                    continue;
                }

                ScheduleSlot slot = ScheduleSlot.builder()
                        .doctorId(doctor.getId())
                        .deptId(doctor.getDeptId())
                        .visitDate(row.getVisitDate())
                        .timeSlot(timeSlot)
                        .capacity(row.getCapacity())
                        .remaining(row.getCapacity())
                        .fee(row.getFee())
                        .status(normalizeStatusByRemaining(status, row.getCapacity()))
                        .createdBy(scope.userId)
                        .createdAt(now)
                        .updatedAt(now)
                        .build();
                baseMapper.insert(slot);
                success++;
            } catch (IllegalArgumentException ex) {
                skipped++;
                collectReason(skippedReasons, "第" + (i + 1) + "行失败: " + ex.getMessage());
            }
        }

        return BatchOperationResponse.builder()
                .successCount(success)
                .skippedCount(skipped)
                .skippedReasons(skippedReasons)
                .build();
    }

    private List<ScheduleSlot> selectTargetSlots(ScheduleSlotBatchUpdateRequest request, AccessScope scope) {
        LambdaQueryWrapper<ScheduleSlot> wrapper = new LambdaQueryWrapper<ScheduleSlot>()
                .orderByAsc(ScheduleSlot::getVisitDate, ScheduleSlot::getTimeSlot, ScheduleSlot::getId);

        if (request.getIds() != null && !request.getIds().isEmpty()) {
            wrapper.in(ScheduleSlot::getId, request.getIds());
        }
        if (request.getDoctorId() != null) {
            wrapper.eq(ScheduleSlot::getDoctorId, request.getDoctorId());
        }
        if (request.getDeptId() != null) {
            wrapper.eq(ScheduleSlot::getDeptId, request.getDeptId());
        }
        if (request.getStartDate() != null) {
            wrapper.ge(ScheduleSlot::getVisitDate, request.getStartDate());
        }
        if (request.getEndDate() != null) {
            wrapper.le(ScheduleSlot::getVisitDate, request.getEndDate());
        }
        if (request.getTimeSlot() != null && !request.getTimeSlot().isBlank()) {
            wrapper.eq(ScheduleSlot::getTimeSlot, normalizeTimeSlot(request.getTimeSlot()));
        }
        if (request.getCurrentStatus() != null && !request.getCurrentStatus().isBlank()) {
            wrapper.eq(ScheduleSlot::getStatus, normalizeStatusInput(request.getCurrentStatus(), null));
        }

        applyScopeCondition(wrapper, scope);
        return baseMapper.selectList(wrapper);
    }

    private void applyUpdate(ScheduleSlot slot, Integer capacity, BigDecimal fee, String status) {
        int occupied = slot.getCapacity() - slot.getRemaining();
        int targetCapacity = capacity == null ? slot.getCapacity() : capacity;
        if (targetCapacity < occupied) {
            throw new IllegalArgumentException("容量不能小于已占用号源(" + occupied + ")");
        }

        int targetRemaining = targetCapacity - occupied;
        String targetStatus = status == null ? slot.getStatus() : status;
        targetStatus = normalizeStatusByRemaining(targetStatus, targetRemaining);

        slot.setCapacity(targetCapacity);
        slot.setRemaining(targetRemaining);
        if (fee != null) {
            slot.setFee(fee);
        }
        slot.setStatus(targetStatus);
        slot.setUpdatedAt(LocalDateTime.now());
        baseMapper.updateById(slot);
    }

    private LambdaQueryWrapper<ScheduleSlot> buildQueryWrapper(ScheduleSlotQueryRequest request, AccessScope scope) {
        LambdaQueryWrapper<ScheduleSlot> wrapper = new LambdaQueryWrapper<ScheduleSlot>()
                .orderByAsc(ScheduleSlot::getVisitDate, ScheduleSlot::getTimeSlot, ScheduleSlot::getId);

        if (request.deptId() != null) {
            wrapper.eq(ScheduleSlot::getDeptId, request.deptId());
        }
        if (request.doctorId() != null) {
            wrapper.eq(ScheduleSlot::getDoctorId, request.doctorId());
        }
        if (request.startDate() != null) {
            wrapper.ge(ScheduleSlot::getVisitDate, request.startDate());
        }
        if (request.endDate() != null) {
            wrapper.le(ScheduleSlot::getVisitDate, request.endDate());
        }
        if (request.timeSlot() != null && !request.timeSlot().isBlank()) {
            wrapper.eq(ScheduleSlot::getTimeSlot, normalizeTimeSlot(request.timeSlot()));
        }
        if (request.status() != null && !request.status().isBlank()) {
            wrapper.eq(ScheduleSlot::getStatus, normalizeStatusInput(request.status(), null));
        }
        if (request.minRemaining() != null) {
            wrapper.ge(ScheduleSlot::getRemaining, request.minRemaining());
        }

        applyScopeCondition(wrapper, scope);
        return wrapper;
    }

    private void applyScopeCondition(LambdaQueryWrapper<ScheduleSlot> wrapper, AccessScope scope) {
        if (scope.doctorId != null) {
            wrapper.eq(ScheduleSlot::getDoctorId, scope.doctorId);
        }
        if (scope.deptId != null) {
            wrapper.eq(ScheduleSlot::getDeptId, scope.deptId);
        }
    }

    private AccessScope resolveScope(boolean writeRequired) {
        AuthUser authUser = AuthContextHolder.get();
        if (authUser == null) {
            throw new IllegalStateException("未登录");
        }

        String role = authUser.getRole();
        if (ROLE_ADMIN.equals(role)) {
            SysUser sysUser = sysUserMapper.selectById(authUser.getUserId());
            if (sysUser == null) {
                throw new IllegalStateException("当前用户不存在");
            }
            Long deptId = null;
            if (SCOPE_DEPT.equalsIgnoreCase(sysUser.getAdminScopeType())) {
                deptId = sysUser.getAdminDeptId();
                if (deptId == null) {
                    throw new IllegalStateException("科室级管理员未配置adminDeptId");
                }
            }
            return new AccessScope(authUser.getUserId(), role, null, deptId);
        }

        if (ROLE_DOCTOR.equals(role)) {
            DoctorProfile profile = doctorProfileMapper.selectOne(new LambdaQueryWrapper<DoctorProfile>()
                    .eq(DoctorProfile::getUserId, authUser.getUserId())
                    .last("LIMIT 1"));
            if (profile == null) {
                throw new IllegalStateException("医生档案不存在");
            }
            return new AccessScope(authUser.getUserId(), role, profile.getId(), profile.getDeptId());
        }

        if (writeRequired) {
            throw new IllegalStateException("无排班写权限");
        }
        return new AccessScope(authUser.getUserId(), role, null, null);
    }

    private List<Long> resolveTargetDoctorIds(List<Long> requestDoctorIds, AccessScope scope, boolean writeRequired) {
        if (ROLE_DOCTOR.equals(scope.role)) {
            return Collections.singletonList(scope.doctorId);
        }

        if (ROLE_ADMIN.equals(scope.role)) {
            if (requestDoctorIds == null || requestDoctorIds.isEmpty()) {
                throw new IllegalArgumentException("doctorIds不能为空");
            }
            return requestDoctorIds.stream().filter(Objects::nonNull).distinct().collect(Collectors.toList());
        }

        if (writeRequired) {
            throw new IllegalStateException("无排班写权限");
        }
        return Collections.emptyList();
    }

    private void assertWriteAccessToSlot(AccessScope scope, ScheduleSlot slot) {
        if (ROLE_ADMIN.equals(scope.role)) {
            if (scope.deptId != null && !scope.deptId.equals(slot.getDeptId())) {
                throw new IllegalStateException("仅可操作本部门排班");
            }
            return;
        }

        if (ROLE_DOCTOR.equals(scope.role)) {
            if (!scope.doctorId.equals(slot.getDoctorId())) {
                throw new IllegalStateException("仅可操作本人排班");
            }
            return;
        }

        throw new IllegalStateException("无排班写权限");
    }

    private void assertWriteAccessToDoctor(AccessScope scope, DoctorProfile doctor) {
        if (ROLE_ADMIN.equals(scope.role)) {
            if (scope.deptId != null && !scope.deptId.equals(doctor.getDeptId())) {
                throw new IllegalStateException("仅可操作本部门医生排班");
            }
            return;
        }

        if (ROLE_DOCTOR.equals(scope.role)) {
            if (!scope.doctorId.equals(doctor.getId())) {
                throw new IllegalStateException("仅可操作本人排班");
            }
            return;
        }

        throw new IllegalStateException("无排班写权限");
    }

    private List<ScheduleSlotView> toViews(List<ScheduleSlot> slots) {
        if (slots == null || slots.isEmpty()) {
            return Collections.emptyList();
        }

        Map<String, String> dictNameMap = loadDictNameMap();
        return slots.stream().map(slot -> toView(slot, dictNameMap)).collect(Collectors.toList());
    }

    private ScheduleSlotView toView(ScheduleSlot slot, Map<String, String> dictNameMap) {
        return ScheduleSlotView.builder()
                .id(slot.getId())
                .doctorId(slot.getDoctorId())
                .deptId(slot.getDeptId())
                .visitDate(slot.getVisitDate())
                .timeSlot(slot.getTimeSlot())
                .timeSlotName(dictNameMap.get(slot.getTimeSlot()))
                .capacity(slot.getCapacity())
                .remaining(slot.getRemaining())
                .fee(slot.getFee())
                .status(slot.getStatus())
                .statusName(dictNameMap.get(slot.getStatus()))
                .openTime(slot.getOpenTime())
                .stopTime(slot.getStopTime())
                .build();
    }

    private Map<String, String> loadDictNameMap() {
        List<DictItem> items = dictItemMapper.selectList(new LambdaQueryWrapper<DictItem>()
                .in(DictItem::getTypeCode, List.of("TIME_SLOT", "SCHEDULE_STATUS"))
                .eq(DictItem::getStatus, 1));

        Map<String, String> result = new HashMap<>(items.size());
        for (DictItem item : items) {
            result.put(item.getItemCode(), item.getItemName());
        }
        return result;
    }

    private void validateDateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("起止日期不能为空");
        }
        if (endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("结束日期不能早于开始日期");
        }
        if (endDate.isAfter(startDate.plusDays(31))) {
            throw new IllegalArgumentException("批量生成日期区间最多31天");
        }
    }

    private Set<String> normalizeTimeSlots(List<String> timeSlots) {
        List<String> source = (timeSlots == null || timeSlots.isEmpty()) ? List.of(SLOT_AM, SLOT_PM) : timeSlots;
        Set<String> normalized = new LinkedHashSet<>();
        for (String timeSlot : source) {
            normalized.add(normalizeTimeSlot(timeSlot));
        }
        return normalized;
    }

    private String normalizeTimeSlot(String timeSlot) {
        if (timeSlot == null || timeSlot.isBlank()) {
            throw new IllegalArgumentException("timeSlot不能为空");
        }
        String normalized = timeSlot.trim().toUpperCase();
        if (!ALLOWED_TIME_SLOTS.contains(normalized)) {
            throw new IllegalArgumentException("timeSlot仅支持AM/PM");
        }
        return normalized;
    }

    private String normalizeStatusInput(String status, String defaultStatus) {
        String normalized = status;
        if (normalized == null || normalized.isBlank()) {
            normalized = defaultStatus;
        }
        if (normalized == null) {
            return null;
        }

        normalized = normalized.trim().toUpperCase();
        if (!ALLOWED_STATUS.contains(normalized)) {
            throw new IllegalArgumentException("status仅支持OPEN/CLOSED/FULL");
        }
        return normalized;
    }

    private String normalizeStatusByRemaining(String status, Integer remaining) {
        String normalizedStatus = normalizeStatusInput(status, STATUS_OPEN);
        if (STATUS_CLOSED.equals(normalizedStatus)) {
            return STATUS_CLOSED;
        }
        return remaining != null && remaining <= 0 ? STATUS_FULL : STATUS_OPEN;
    }

    private Integer resolveCapacity(ScheduleSlotGenerateRequest request, String timeSlot) {
        Integer capacity;
        if (SLOT_AM.equals(timeSlot)) {
            capacity = request.getAmCapacity() != null ? request.getAmCapacity() : request.getDefaultCapacity();
        } else {
            capacity = request.getPmCapacity() != null ? request.getPmCapacity() : request.getDefaultCapacity();
        }

        if (capacity == null || capacity <= 0) {
            throw new IllegalArgumentException("容量不能为空且必须>=1");
        }
        return capacity;
    }

    private BigDecimal resolveFee(ScheduleSlotGenerateRequest request, String timeSlot) {
        BigDecimal fee;
        if (SLOT_AM.equals(timeSlot)) {
            fee = request.getAmFee() != null ? request.getAmFee() : request.getDefaultFee();
        } else {
            fee = request.getPmFee() != null ? request.getPmFee() : request.getDefaultFee();
        }

        if (fee == null || fee.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("费用不能为空且必须>0");
        }
        return fee;
    }

    private void ensureUpdatePayloadNotEmpty(Integer capacity, BigDecimal fee, String status) {
        if (capacity == null && fee == null && (status == null || status.isBlank())) {
            throw new IllegalArgumentException("至少提供一个待更新字段: capacity/fee/status");
        }
    }

    private String buildUniqueKey(ScheduleSlot slot) {
        return buildUniqueKey(slot.getDoctorId(), slot.getVisitDate(), slot.getTimeSlot());
    }

    private String buildUniqueKey(Long doctorId, LocalDate visitDate, String timeSlot) {
        return doctorId + "|" + visitDate + "|" + timeSlot;
    }

    private Long normalizePageNo(Long pageNo) {
        if (pageNo == null || pageNo < 1) {
            return 1L;
        }
        return pageNo;
    }

    private Long normalizePageSize(Long pageSize) {
        if (pageSize == null || pageSize < 1) {
            return 10L;
        }
        return Math.min(pageSize, 200L);
    }

    private void collectReason(List<String> reasons, String value) {
        if (reasons.size() < 20) {
            reasons.add(value);
        }
    }

    private record AccessScope(Long userId, String role, Long doctorId, Long deptId) {
    }
}
