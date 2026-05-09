package com.siemens.internship.dto.response;

import java.util.List;

public record RouteResponse(
        Long id,
        String name,
        List<RouteStopResponse> stops
) {
}