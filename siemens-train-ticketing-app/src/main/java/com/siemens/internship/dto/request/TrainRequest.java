package com.siemens.internship.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record TrainRequest(
        @NotBlank(message = "Train number is required.")
        String trainNumber,

        @NotBlank(message = "Train name is required.")
        String name,

        @Min(value = 1, message = "Train capacity must be greater than zero.")
        int capacity
) {
}