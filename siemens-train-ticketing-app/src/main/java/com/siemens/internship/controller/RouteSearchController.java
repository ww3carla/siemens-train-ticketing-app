package com.siemens.internship.controller;

import com.siemens.internship.dto.response.RouteSearchResponse;
import com.siemens.internship.service.RouteSearchService;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
@RequestMapping("/api/routes")
public class RouteSearchController {

    private final RouteSearchService routeSearchService;

    public RouteSearchController(RouteSearchService routeSearchService) {
        this.routeSearchService = routeSearchService;
    }

    @GetMapping("/search")
    public List<RouteSearchResponse> findConnections(
            @RequestParam @NotNull Long fromStationId,
            @RequestParam @NotNull Long toStationId
    ) {
        return routeSearchService.findConnections(fromStationId, toStationId);
    }
}