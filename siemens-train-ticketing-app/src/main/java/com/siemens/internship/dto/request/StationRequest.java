package com.siemens.internship.dto.request;

import jakarta.validation.constraints.NotBlank;

public record StationRequest(
        @NotBlank(message = "Station name is required.")
        String name
) {
}