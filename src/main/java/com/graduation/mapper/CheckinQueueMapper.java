package com.graduation.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.graduation.entity.CheckinQueue;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;

public interface CheckinQueueMapper extends BaseMapper<CheckinQueue> {

    @Select("""
            SELECT *
            FROM checkin_queue
            WHERE appointment_id = #{appointmentId}
            LIMIT 1
            """)
    CheckinQueue findByAppointmentId(@Param("appointmentId") Long appointmentId);

    @Select("""
            SELECT MAX(queue_no)
            FROM checkin_queue
            WHERE doctor_id = #{doctorId}
              AND visit_date = #{visitDate}
              AND time_slot = #{timeSlot}
            """)
    Integer selectMaxQueueNo(@Param("doctorId") Long doctorId,
                             @Param("visitDate") LocalDate visitDate,
                             @Param("timeSlot") String timeSlot);

    @Select("SELECT GET_LOCK(#{lockKey}, #{timeoutSeconds})")
    Integer acquireLock(@Param("lockKey") String lockKey,
                        @Param("timeoutSeconds") int timeoutSeconds);

    @Select("SELECT RELEASE_LOCK(#{lockKey})")
    Integer releaseLock(@Param("lockKey") String lockKey);
}
