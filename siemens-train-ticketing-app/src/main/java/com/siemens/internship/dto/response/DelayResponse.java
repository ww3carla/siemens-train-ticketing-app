package com.siemens.internship.dto.response;

public record DelayResponse(
        Long scheduleId,
        String trainNumber,
        int delayMinutes,
        String reason,
        int notifiedCustomers,
        String message
) {
}