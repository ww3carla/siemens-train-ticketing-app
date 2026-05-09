package com.siemens.internship.service;

import com.siemens.internship.dto.request.ScheduleRequest;
import com.siemens.internship.dto.response.ScheduleResponse;
import com.siemens.internship.exception.ResourceNotFoundException;
import com.siemens.internship.model.Route;
import com.siemens.internship.model.Train;
import com.siemens.internship.model.TrainSchedule;
import com.siemens.internship.repository.RouteRepository;
import com.siemens.internship.repository.TrainRepository;
import com.siemens.internship.repository.TrainScheduleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AdminScheduleService {

    private final TrainScheduleRepository trainScheduleRepository;
    private final TrainRepository trainRepository;
    private final RouteRepository routeRepository;

    public AdminScheduleService(
            TrainScheduleRepository trainScheduleRepository,
            TrainRepository trainRepository,
            RouteRepository routeRepository
    ) {
        this.trainScheduleRepository = trainScheduleRepository;
        this.trainRepository = trainRepository;
        this.routeRepository = routeRepository;
    }

    @Transactional(readOnly = true)
    public List<ScheduleResponse> getAllSchedules() {
        return trainScheduleRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ScheduleResponse getScheduleById(Long scheduleId) {
        TrainSchedule schedule = findScheduleById(scheduleId);
        return toResponse(schedule);
    }

    @Transactional
    public ScheduleResponse createSchedule(ScheduleRequest request) {
        Train train = findTrainById(request.trainId());
        Route route = findRouteById(request.routeId());

        TrainSchedule schedule = new TrainSchedule(
                train,
                route,
                request.departureTime(),
                request.arrivalTime()
        );

        TrainSchedule savedSchedule = trainScheduleRepository.save(schedule);

        return toResponse(savedSchedule);
    }

    @Transactional
    public ScheduleResponse updateSchedule(Long scheduleId, ScheduleRequest request) {
        TrainSchedule schedule = findScheduleById(scheduleId);
        Train train = findTrainById(request.trainId());
        Route route = findRouteById(request.routeId());

        schedule.updateTrainAndRoute(train, route);
        schedule.updateTimes(request.departureTime(), request.arrivalTime());

        return toResponse(schedule);
    }

    @Transactional
    public void deleteSchedule(Long scheduleId) {
        if (!trainScheduleRepository.existsById(scheduleId)) {
            throw new ResourceNotFoundException("Schedule with id " + scheduleId + " was not found.");
        }

        trainScheduleRepository.deleteById(scheduleId);
    }

    private TrainSchedule findScheduleById(Long scheduleId) {
        return trainScheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Schedule with id " + scheduleId + " was not found."
                ));
    }

    private Train findTrainById(Long trainId) {
        return trainRepository.findById(trainId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Train with id " + trainId + " was not found."
                ));
    }

    private Route findRouteById(Long routeId) {
        return routeRepository.findById(routeId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Route with id " + routeId + " was not found."
                ));
    }

    private ScheduleResponse toResponse(TrainSchedule schedule) {
        return new ScheduleResponse(
                schedule.getId(),
                schedule.getTrain().getId(),
                schedule.getTrain().getTrainNumber(),
                schedule.getTrain().getName(),
                schedule.getRoute().getId(),
                schedule.getRoute().getName(),
                schedule.getDepartureTime(),
                schedule.getArrivalTime(),
                schedule.getDelayMinutes(),
                schedule.getEffectiveDepartureTime(),
                schedule.getEffectiveArrivalTime()
        );
    }
}