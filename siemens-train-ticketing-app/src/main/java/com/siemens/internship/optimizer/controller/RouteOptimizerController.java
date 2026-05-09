package com.siemens.internship.optimizer.controller;

import com.siemens.internship.optimizer.dto.OptimizedRouteRequest;
import com.siemens.internship.optimizer.dto.OptimizedRouteResponse;
import com.siemens.internship.optimizer.service.RouteOptimizerService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/optimizer/routes")
public class RouteOptimizerController {

    private final RouteOptimizerService routeOptimizerService;

    public RouteOptimizerController(RouteOptimizerService routeOptimizerService) {
        this.routeOptimizerService = routeOptimizerService;
    }

    @PostMapping
    public OptimizedRouteResponse findOptimalRoute(@Valid @RequestBody OptimizedRouteRequest request) {
        return routeOptimizerService.findOptimalRoute(request);
    }
}