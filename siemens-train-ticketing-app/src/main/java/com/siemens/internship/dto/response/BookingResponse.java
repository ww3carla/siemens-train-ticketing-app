package com.siemens.internship.dto.response;

import com.siemens.internship.model.BookingStatus;

import java.time.LocalDateTime;

public record BookingResponse(
        Long bookingId,
        String customerName,
        String customerEmail,
        String trainNumber,
        String trainName,
        String fromStation,
        String toStation,
        int numberOfTickets,
        BookingStatus status,
        LocalDateTime departureTime,
        LocalDateTime arrivalTime,
        LocalDateTime createdAt
) {
}