package com.siemens.internship.controller;

import com.siemens.internship.dto.request.RouteRequest;
import com.siemens.internship.dto.response.RouteResponse;
import com.siemens.internship.service.AdminRouteService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/routes")
public class AdminRouteController {

    private final AdminRouteService adminRouteService;

    public AdminRouteController(AdminRouteService adminRouteService) {
        this.adminRouteService = adminRouteService;
    }

    @GetMapping
    public List<RouteResponse> getAllRoutes() {
        return adminRouteService.getAllRoutes();
    }

    @GetMapping("/{routeId}")
    public RouteResponse getRouteById(@PathVariable Long routeId) {
        return adminRouteService.getRouteById(routeId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RouteResponse createRoute(@Valid @RequestBody RouteRequest request) {
        return adminRouteService.createRoute(request);
    }

    @PutMapping("/{routeId}")
    public RouteResponse updateRoute(
            @PathVariable Long routeId,
            @Valid @RequestBody RouteRequest request
    ) {
        return adminRouteService.updateRoute(routeId, request);
    }

    @DeleteMapping("/{routeId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRoute(@PathVariable Long routeId) {
        adminRouteService.deleteRoute(routeId);
    }
}