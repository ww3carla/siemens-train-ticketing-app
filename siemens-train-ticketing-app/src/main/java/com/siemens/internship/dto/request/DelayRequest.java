package com.siemens.internship.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record DelayRequest(
        @Min(value = 0, message = "Delay minutes cannot be negative.")
        int delayMinutes,

        @NotBlank(message = "Delay reason is required.")
        String reason
) {
}