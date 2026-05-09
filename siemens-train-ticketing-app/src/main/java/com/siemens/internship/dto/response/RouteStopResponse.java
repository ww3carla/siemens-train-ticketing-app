package com.siemens.internship.dto.response;

public record RouteStopResponse(
        Long stationId,
        String stationName,
        int stopOrder
) {
}