package com.siemens.internship.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

public record RouteRequest(
        @NotBlank(message = "Route name is required.")
        String name,

        @NotEmpty(message = "Route must contain station ids.")
        @Size(min = 2, message = "A route must contain at least two stations.")
        List<Long> stationIds
) {
}