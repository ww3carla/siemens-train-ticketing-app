package com.siemens.internship.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateBookingRequest(
        @NotBlank(message = "Customer name is required.")
        String customerName,

        @NotBlank(message = "Customer email is required.")
        @Email(message = "Customer email must be valid.")
        String customerEmail,

        @NotNull(message = "Schedule id is required.")
        Long scheduleId,

        @NotNull(message = "Departure station id is required.")
        Long fromStationId,

        @NotNull(message = "Arrival station id is required.")
        Long toStationId,

        @Min(value = 1, message = "At least one ticket must be booked.")
        int numberOfTickets
) {
}