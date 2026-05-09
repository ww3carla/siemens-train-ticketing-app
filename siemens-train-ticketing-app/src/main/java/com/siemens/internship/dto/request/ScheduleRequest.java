package com.siemens.internship.dto.request;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record ScheduleRequest(
        @NotNull(message = "Train id is required.")
        Long trainId,

        @NotNull(message = "Route id is required.")
        Long routeId,

        @NotNull(message = "Departure time is required.")
        LocalDateTime departureTime,

        @NotNull(message = "Arrival time is required.")
        LocalDateTime arrivalTime
) {
}