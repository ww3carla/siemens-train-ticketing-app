package com.siemens.internship.dto.response;

import java.time.LocalDateTime;

public record ScheduleResponse(
        Long id,
        Long trainId,
        String trainNumber,
        String trainName,
        Long routeId,
        String routeName,
        LocalDateTime departureTime,
        LocalDateTime arrivalTime,
        int delayMinutes,
        LocalDateTime effectiveDepartureTime,
        LocalDateTime effectiveArrivalTime
) {
}