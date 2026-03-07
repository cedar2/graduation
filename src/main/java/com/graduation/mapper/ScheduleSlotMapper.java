package com.graduation.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.graduation.entity.ScheduleSlot;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

public interface ScheduleSlotMapper extends BaseMapper<ScheduleSlot> {

    @Update("""
            UPDATE schedule_slot
            SET remaining = remaining - 1
            WHERE id = #{id}
              AND remaining > 0
              AND status = 'OPEN'
            """)
    int decrementRemainingIfAvailable(@Param("id") Long id);
}
