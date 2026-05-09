package com.siemens.internship.dto.response;

public record TrainResponse(
        Long id,
        String trainNumber,
        String name,
        int capacity
) {
}