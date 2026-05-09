package com.siemens.internship.optimizer.service;

import com.siemens.internship.optimizer.dto.OptimizedRouteRequest;
import com.siemens.internship.optimizer.dto.OptimizedRouteResponse;
import com.siemens.internship.optimizer.model.OptimizationCriterion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RouteOptimizerServiceTest {

    private RouteOptimizerService routeOptimizerService;

    @BeforeEach
    void setUp() {
        routeOptimizerService = new RouteOptimizerService();
    }

    @Test
    void findOptimalRoute_shouldReturnRouteOptimizedByDuration() {
        OptimizedRouteRequest request = new OptimizedRouteRequest(
                "Cluj-Napoca",
                "Iasi",
                OptimizationCriterion.DURATION
        );

        OptimizedRouteResponse response = routeOptimizerService.findOptimalRoute(request);

        assertEquals("Cluj-Napoca", response.from());
        assertEquals("Iasi", response.to());
        assertEquals(OptimizationCriterion.DURATION, response.criterion());
        assertEquals(List.of("Cluj-Napoca", "Brasov", "Iasi"), response.path());
        assertEquals(570, response.totalDurationMinutes());
        assertEquals(BigDecimal.valueOf(230), response.totalCost());
        assertEquals(590, response.totalDistanceKm());
    }

    @Test
    void findOptimalRoute_shouldReturnRouteOptimizedByCost() {
        OptimizedRouteRequest request = new OptimizedRouteRequest(
                "Cluj-Napoca",
                "Iasi",
                OptimizationCriterion.COST
        );

        OptimizedRouteResponse response = routeOptimizerService.findOptimalRoute(request);

        assertEquals("Cluj-Napoca", response.from());
        assertEquals("Iasi", response.to());
        assertEquals(OptimizationCriterion.COST, response.criterion());
        assertEquals(List.of("Cluj-Napoca", "Alba Iulia", "Brasov", "Iasi"), response.path());
        assertEquals(625, response.totalDurationMinutes());
        assertEquals(BigDecimal.valueOf(228), response.totalCost());
        assertEquals(630, response.totalDistanceKm());
    }

    @Test
    void findOptimalRoute_shouldReturnRouteOptimizedByDistance() {
        OptimizedRouteRequest request = new OptimizedRouteRequest(
                "Cluj-Napoca",
                "Iasi",
                OptimizationCriterion.DISTANCE
        );

        OptimizedRouteResponse response = routeOptimizerService.findOptimalRoute(request);

        assertEquals("Cluj-Napoca", response.from());
        assertEquals("Iasi", response.to());
        assertEquals(OptimizationCriterion.DISTANCE, response.criterion());
        assertEquals(List.of("Cluj-Napoca", "Brasov", "Iasi"), response.path());
        assertEquals(570, response.totalDurationMinutes());
        assertEquals(BigDecimal.valueOf(230), response.totalCost());
        assertEquals(590, response.totalDistanceKm());
    }

    @Test
    void findOptimalRoute_shouldBeCaseInsensitive() {
        OptimizedRouteRequest request = new OptimizedRouteRequest(
                "cluj-napoca",
                "iasi",
                OptimizationCriterion.DURATION
        );

        OptimizedRouteResponse response = routeOptimizerService.findOptimalRoute(request);

        assertEquals("Cluj-Napoca", response.from());
        assertEquals("Iasi", response.to());
        assertEquals(List.of("Cluj-Napoca", "Brasov", "Iasi"), response.path());
    }

    @Test
    void findOptimalRoute_shouldThrow_whenCityDoesNotExist() {
        OptimizedRouteRequest request = new OptimizedRouteRequest(
                "Cluj-Napoca",
                "Unknown City",
                OptimizationCriterion.DURATION
        );

        assertThrows(OptimizationException.class, () -> routeOptimizerService.findOptimalRoute(request));
    }

    @Test
    void findOptimalRoute_shouldThrow_whenOriginAndDestinationAreTheSame() {
        OptimizedRouteRequest request = new OptimizedRouteRequest(
                "Cluj-Napoca",
                "Cluj-Napoca",
                OptimizationCriterion.DURATION
        );

        assertThrows(OptimizationException.class, () -> routeOptimizerService.findOptimalRoute(request));
    }
}