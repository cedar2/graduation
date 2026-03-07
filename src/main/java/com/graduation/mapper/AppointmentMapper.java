package com.graduation.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.graduation.entity.Appointment;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDate;
import java.time.LocalDateTime;

public interface AppointmentMapper extends BaseMapper<Appointment> {

    @Select("""
            SELECT COUNT(1)
            FROM appointment
            WHERE patient_id = #{patientId}
              AND visit_date = #{visitDate}
              AND time_slot = #{timeSlot}
              AND status IN ('UNPAID', 'PAID', 'CHECKED_IN', 'COMPLETED')
            """)
    int countActiveConflict(@Param("patientId") Long patientId,
                            @Param("visitDate") LocalDate visitDate,
                            @Param("timeSlot") String timeSlot);

    @Select("""
            SELECT *
            FROM appointment
            WHERE checkin_code = #{checkinCode}
              AND visit_date = #{visitDate}
            LIMIT 1
            """)
    Appointment findByCheckinCodeAndVisitDate(@Param("checkinCode") String checkinCode,
                                              @Param("visitDate") LocalDate visitDate);

    @Update("""
            UPDATE appointment
            SET status = 'CHECKED_IN',
                checked_in_at = #{checkedInAt},
                updated_at = #{checkedInAt}
            WHERE id = #{appointmentId}
              AND status = 'PAID'
            """)
    int markCheckedInIfPaid(@Param("appointmentId") Long appointmentId,
                            @Param("checkedInAt") LocalDateTime checkedInAt);
}
