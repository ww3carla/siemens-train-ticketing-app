package com.siemens.internship.service;

import com.siemens.internship.dto.response.RouteLegResponse;
import com.siemens.internship.dto.response.RouteSearchResponse;
import com.siemens.internship.exception.InvalidBookingException;
import com.siemens.internship.exception.RouteNotFoundException;
import com.siemens.internship.model.Route;
import com.siemens.internship.model.TrainSchedule;
import com.siemens.internship.repository.StationRepository;
import com.siemens.internship.repository.TrainScheduleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
public class RouteSearchService {

    private final StationRepository stationRepository;
    private final TrainScheduleRepository trainScheduleRepository;

    public RouteSearchService(
            StationRepository stationRepository,
            TrainScheduleRepository trainScheduleRepository
    ) {
        this.stationRepository = stationRepository;
        this.trainScheduleRepository = trainScheduleRepository;
    }

    @Transactional(readOnly = true)
    public List<RouteSearchResponse> findConnections(Long fromStationId, Long toStationId) {
        validateStations(fromStationId, toStationId);

        List<TrainSchedule> schedules = trainScheduleRepository.findAll();

        List<RouteSearchResponse> directConnections = schedules.stream()
                .filter(schedule -> canTravelDirectly(schedule, fromStationId, toStationId))
                .map(schedule -> buildDirectConnection(schedule, fromStationId, toStationId))
                .toList();

        List<RouteSearchResponse> changeoverConnections = findChangeoverConnections(
                schedules,
                fromStationId,
                toStationId
        );

        List<RouteSearchResponse> allConnections = new java.util.ArrayList<>();
        allConnections.addAll(directConnections);
        allConnections.addAll(changeoverConnections);

        if (allConnections.isEmpty()) {
            throw new RouteNotFoundException(
                    "No possible connection was found between the selected stations."
            );
        }

        return allConnections.stream()
                .sorted(Comparator.comparing(RouteSearchResponse::departureTime))
                .toList();
    }

    private void validateStations(Long fromStationId, Long toStationId) {
        if (fromStationId == null || toStationId == null) {
            throw new InvalidBookingException("Departure and arrival station ids are required.");
        }

        if (fromStationId.equals(toStationId)) {
            throw new InvalidBookingException("Departure and arrival stations must be different.");
        }

        if (!stationRepository.existsById(fromStationId)) {
            throw new RouteNotFoundException("Departure station with id " + fromStationId + " was not found.");
        }

        if (!stationRepository.existsById(toStationId)) {
            throw new RouteNotFoundException("Arrival station with id " + toStationId + " was not found.");
        }
    }

    private boolean canTravelDirectly(
            TrainSchedule schedule,
            Long fromStationId,
            Long toStationId
    ) {
        Route route = schedule.getRoute();

        if (!route.containsStation(fromStationId) || !route.containsStation(toStationId)) {
            return false;
        }

        return route.getStopOrderForStation(fromStationId) < route.getStopOrderForStation(toStationId);
    }

    private RouteSearchResponse buildDirectConnection(
            TrainSchedule schedule,
            Long fromStationId,
            Long toStationId
    ) {
        String fromStationName = getStationName(schedule, fromStationId);
        String toStationName = getStationName(schedule, toStationId);

        RouteLegResponse leg = new RouteLegResponse(
                schedule.getTrain().getTrainNumber(),
                schedule.getTrain().getName(),
                fromStationName,
                toStationName,
                schedule.getEffectiveDepartureTime(),
                schedule.getEffectiveArrivalTime()
        );

        return new RouteSearchResponse(
                "DIRECT",
                List.of(leg),
                schedule.getEffectiveDepartureTime(),
                schedule.getEffectiveArrivalTime(),
                calculateMinutes(schedule.getEffectiveDepartureTime(), schedule.getEffectiveArrivalTime())
        );
    }

    private List<RouteSearchResponse> findChangeoverConnections(
            List<TrainSchedule> schedules,
            Long fromStationId,
            Long toStationId
    ) {
        return schedules.stream()
                .flatMap(firstSchedule -> schedules.stream()
                        .filter(secondSchedule -> !firstSchedule.equals(secondSchedule))
                        .filter(secondSchedule -> canBuildChangeover(
                                firstSchedule,
                                secondSchedule,
                                fromStationId,
                                toStationId
                        ))
                        .map(secondSchedule -> buildChangeoverConnection(
                                firstSchedule,
                                secondSchedule,
                                fromStationId,
                                toStationId
                        ))
                )
                .toList();
    }

    private boolean canBuildChangeover(
            TrainSchedule firstSchedule,
            TrainSchedule secondSchedule,
            Long fromStationId,
            Long toStationId
    ) {
        Route firstRoute = firstSchedule.getRoute();
        Route secondRoute = secondSchedule.getRoute();

        if (!firstRoute.containsStation(fromStationId) || !secondRoute.containsStation(toStationId)) {
            return false;
        }

        return firstRoute.getStops()
                .stream()
                .map(routeStop -> routeStop.getStation().getId())
                .filter(changeStationId -> !changeStationId.equals(fromStationId))
                .filter(secondRoute::containsStation)
                .anyMatch(changeStationId -> isValidChangeover(
                        firstSchedule,
                        secondSchedule,
                        fromStationId,
                        changeStationId,
                        toStationId
                ));
    }

    private boolean isValidChangeover(
            TrainSchedule firstSchedule,
            TrainSchedule secondSchedule,
            Long fromStationId,
            Long changeStationId,
            Long toStationId
    ) {
        Route firstRoute = firstSchedule.getRoute();
        Route secondRoute = secondSchedule.getRoute();

        boolean firstLegValid =
                firstRoute.getStopOrderForStation(fromStationId) < firstRoute.getStopOrderForStation(changeStationId);

        boolean secondLegValid =
                secondRoute.getStopOrderForStation(changeStationId) < secondRoute.getStopOrderForStation(toStationId);

        boolean timeValid =
                !secondSchedule.getEffectiveDepartureTime().isBefore(firstSchedule.getEffectiveArrivalTime());

        return firstLegValid && secondLegValid && timeValid;
    }

    private RouteSearchResponse buildChangeoverConnection(
            TrainSchedule firstSchedule,
            TrainSchedule secondSchedule,
            Long fromStationId,
            Long toStationId
    ) {
        Long changeStationId = findFirstValidChangeStation(
                firstSchedule,
                secondSchedule,
                fromStationId,
                toStationId
        );

        String fromStationName = getStationName(firstSchedule, fromStationId);
        String changeStationName = getStationName(firstSchedule, changeStationId);
        String toStationName = getStationName(secondSchedule, toStationId);

        RouteLegResponse firstLeg = new RouteLegResponse(
                firstSchedule.getTrain().getTrainNumber(),
                firstSchedule.getTrain().getName(),
                fromStationName,
                changeStationName,
                firstSchedule.getEffectiveDepartureTime(),
                firstSchedule.getEffectiveArrivalTime()
        );

        RouteLegResponse secondLeg = new RouteLegResponse(
                secondSchedule.getTrain().getTrainNumber(),
                secondSchedule.getTrain().getName(),
                changeStationName,
                toStationName,
                secondSchedule.getEffectiveDepartureTime(),
                secondSchedule.getEffectiveArrivalTime()
        );

        return new RouteSearchResponse(
                "CHANGEOVER",
                List.of(firstLeg, secondLeg),
                firstSchedule.getEffectiveDepartureTime(),
                secondSchedule.getEffectiveArrivalTime(),
                calculateMinutes(firstSchedule.getEffectiveDepartureTime(), secondSchedule.getEffectiveArrivalTime())
        );
    }

    private Long findFirstValidChangeStation(
            TrainSchedule firstSchedule,
            TrainSchedule secondSchedule,
            Long fromStationId,
            Long toStationId
    ) {
        return firstSchedule.getRoute()
                .getStops()
                .stream()
                .map(routeStop -> routeStop.getStation().getId())
                .filter(changeStationId -> secondSchedule.getRoute().containsStation(changeStationId))
                .filter(changeStationId -> isValidChangeover(
                        firstSchedule,
                        secondSchedule,
                        fromStationId,
                        changeStationId,
                        toStationId
                ))
                .findFirst()
                .orElseThrow(() -> new RouteNotFoundException("No valid changeover station was found."));
    }

    private String getStationName(TrainSchedule schedule, Long stationId) {
        return schedule.getRoute()
                .getStops()
                .stream()
                .filter(routeStop -> routeStop.getStation().getId().equals(stationId))
                .map(routeStop -> routeStop.getStation().getName())
                .findFirst()
                .orElseThrow(() -> new RouteNotFoundException("Station was not found on route."));
    }

    private int calculateMinutes(LocalDateTime departureTime, LocalDateTime arrivalTime) {
        return Math.toIntExact(Duration.between(departureTime, arrivalTime).toMinutes());
    }
}