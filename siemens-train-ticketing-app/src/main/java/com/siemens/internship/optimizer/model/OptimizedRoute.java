package com.siemens.internship.optimizer.model;

import java.math.BigDecimal;
import java.util.List;

public record OptimizedRoute(
        String from,
        String to,
        OptimizationCriterion criterion,
        List<String> path,
        int totalDurationMinutes,
        BigDecimal totalCost,
        int totalDistanceKm
) {
}