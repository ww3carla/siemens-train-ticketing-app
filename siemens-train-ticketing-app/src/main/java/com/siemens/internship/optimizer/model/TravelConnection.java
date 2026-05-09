package com.siemens.internship.optimizer.model;

import java.math.BigDecimal;

public record TravelConnection(
        String from,
        String to,
        int durationMinutes,
        BigDecimal cost,
        int distanceKm
) {
    public TravelConnection {
        if (from == null || from.isBlank()) {
            throw new IllegalArgumentException("Origin city must not be blank.");
        }

        if (to == null || to.isBlank()) {
            throw new IllegalArgumentException("Destination city must not be blank.");
        }

        if (durationMinutes <= 0) {
            throw new IllegalArgumentException("Duration must be greater than zero.");
        }

        if (cost == null || cost.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Cost must be greater than zero.");
        }

        if (distanceKm <= 0) {
            throw new IllegalArgumentException("Distance must be greater than zero.");
        }

        from = from.trim();
        to = to.trim();
    }

    public int weightFor(OptimizationCriterion criterion) {
        return switch (criterion) {
            case DURATION -> durationMinutes;
            case COST -> cost.multiply(BigDecimal.valueOf(100)).intValue();
            case DISTANCE -> distanceKm;
        };
    }
}