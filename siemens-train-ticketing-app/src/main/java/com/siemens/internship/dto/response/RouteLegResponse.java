package com.siemens.internship.dto.response;

import java.time.LocalDateTime;

public record RouteLegResponse(
        String trainNumber,
        String trainName,
        String fromStation,
        String toStation,
        LocalDateTime departureTime,
        LocalDateTime arrivalTime
) {
}