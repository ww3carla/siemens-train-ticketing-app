package com.siemens.internship.optimizer.dto;

import com.siemens.internship.optimizer.model.OptimizationCriterion;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record OptimizedRouteRequest(
        @NotBlank(message = "Origin city is required.")
        String from,

        @NotBlank(message = "Destination city is required.")
        String to,

        @NotNull(message = "Optimization criterion is required.")
        OptimizationCriterion criterion
) {
}