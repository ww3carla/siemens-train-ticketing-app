package com.siemens.internship.dto.response;

public record BookingCreatedResponse(
        Long bookingId,
        String status,
        String message
) {
}