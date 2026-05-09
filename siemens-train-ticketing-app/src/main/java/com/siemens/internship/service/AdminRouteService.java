package com.siemens.internship.service;

import com.siemens.internship.dto.request.RouteRequest;
import com.siemens.internship.dto.response.RouteResponse;
import com.siemens.internship.dto.response.RouteStopResponse;
import com.siemens.internship.exception.BusinessValidationException;
import com.siemens.internship.exception.ResourceNotFoundException;
import com.siemens.internship.model.Route;
import com.siemens.internship.model.Station;
import com.siemens.internship.repository.RouteRepository;
import com.siemens.internship.repository.StationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;

@Service
public class AdminRouteService {

    private final RouteRepository routeRepository;
    private final StationRepository stationRepository;

    public AdminRouteService(
            RouteRepository routeRepository,
            StationRepository stationRepository
    ) {
        this.routeRepository = routeRepository;
        this.stationRepository = stationRepository;
    }

    @Transactional(readOnly = true)
    public List<RouteResponse> getAllRoutes() {
        return routeRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public RouteResponse getRouteById(Long routeId) {
        Route route = findRouteById(routeId);
        return toResponse(route);
    }

    @Transactional
    public RouteResponse createRoute(RouteRequest request) {
        validateStationIds(request.stationIds());

        Route route = new Route(request.name());
        List<Station> stations = findStationsByIdsInOrder(request.stationIds());

        for (int index = 0; index < stations.size(); index++) {
            route.addStop(stations.get(index), index + 1);
        }

        Route savedRoute = routeRepository.save(route);
        return toResponse(savedRoute);
    }

    @Transactional
    public RouteResponse updateRoute(Long routeId, RouteRequest request) {
        validateStationIds(request.stationIds());

        Route route = findRouteById(routeId);
        List<Station> stations = findStationsByIdsInOrder(request.stationIds());

        route.rename(request.name());

        route.clearStops();
        routeRepository.flush();

        for (int index = 0; index < stations.size(); index++) {
            route.addStop(stations.get(index), index + 1);
        }

        return toResponse(route);
    }

    @Transactional
    public void deleteRoute(Long routeId) {
        if (!routeRepository.existsById(routeId)) {
            throw new ResourceNotFoundException("Route with id " + routeId + " was not found.");
        }

        routeRepository.deleteById(routeId);
    }

    private Route findRouteById(Long routeId) {
        return routeRepository.findById(routeId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Route with id " + routeId + " was not found."
                ));
    }

    private List<Station> findStationsByIdsInOrder(List<Long> stationIds) {
        return stationIds.stream()
                .map(this::findStationById)
                .toList();
    }

    private Station findStationById(Long stationId) {
        return stationRepository.findById(stationId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Station with id " + stationId + " was not found."
                ));
    }

    private void validateStationIds(List<Long> stationIds) {
        if (stationIds == null || stationIds.size() < 2) {
            throw new BusinessValidationException("A route must contain at least two stations.");
        }

        if (new HashSet<>(stationIds).size() != stationIds.size()) {
            throw new BusinessValidationException("A route cannot contain duplicate stations.");
        }
    }

    private RouteResponse toResponse(Route route) {
        List<RouteStopResponse> stops = route.getStops()
                .stream()
                .map(routeStop -> new RouteStopResponse(
                        routeStop.getStation().getId(),
                        routeStop.getStation().getName(),
                        routeStop.getStopOrder()
                ))
                .toList();

        return new RouteResponse(
                route.getId(),
                route.getName(),
                stops
        );
    }
}