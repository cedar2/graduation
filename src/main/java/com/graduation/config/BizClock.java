package com.graduation.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
public class BizClock {

    @Value("${app.test-date:}")
    private String testDate;

    public LocalDate today() {
        if (testDate != null && !testDate.isBlank()) {
            return LocalDate.parse(testDate);
        }
        return LocalDate.now();
    }

    public LocalDateTime now() {
        return LocalDateTime.now();
    }
}

