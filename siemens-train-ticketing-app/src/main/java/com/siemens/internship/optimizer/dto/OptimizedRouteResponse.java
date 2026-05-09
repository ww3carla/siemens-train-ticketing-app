package com.siemens.internship.optimizer.dto;

import com.siemens.internship.optimizer.model.OptimizationCriterion;

import java.math.BigDecimal;
import java.util.List;

public record OptimizedRouteResponse(
        String from,
        String to,
        OptimizationCriterion criterion,
        List<String> path,
        int totalDurationMinutes,
        BigDecimal totalCost,
        int totalDistanceKm
) {
}