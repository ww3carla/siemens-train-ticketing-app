package com.siemens.internship.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public record RouteSearchResponse(
        String connectionType,
        List<RouteLegResponse> legs,
        LocalDateTime departureTime,
        LocalDateTime arrivalTime,
        int totalTravelMinutes
) {
}