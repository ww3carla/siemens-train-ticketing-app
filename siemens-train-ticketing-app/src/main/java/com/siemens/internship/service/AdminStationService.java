package com.siemens.internship.service;

import com.siemens.internship.dto.request.StationRequest;
import com.siemens.internship.dto.response.StationResponse;
import com.siemens.internship.exception.InvalidBookingException;
import com.siemens.internship.exception.ResourceNotFoundException;
import com.siemens.internship.model.Station;
import com.siemens.internship.repository.StationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AdminStationService {

    private final StationRepository stationRepository;

    public AdminStationService(StationRepository stationRepository) {
        this.stationRepository = stationRepository;
    }

    @Transactional(readOnly = true)
    public List<StationResponse> getAllStations() {
        return stationRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public StationResponse getStationById(Long stationId) {
        Station station = findStationById(stationId);
        return toResponse(station);
    }

    @Transactional
    public StationResponse createStation(StationRequest request) {
        stationRepository.findByNameIgnoreCase(request.name())
                .ifPresent(station -> {
                    throw new InvalidBookingException("Station with name " + request.name() + " already exists.");
                });

        Station station = new Station(request.name());
        Station savedStation = stationRepository.save(station);

        return toResponse(savedStation);
    }

    @Transactional
    public StationResponse updateStation(Long stationId, StationRequest request) {
        Station station = findStationById(stationId);

        stationRepository.findByNameIgnoreCase(request.name())
                .filter(existingStation -> !existingStation.getId().equals(stationId))
                .ifPresent(existingStation -> {
                    throw new InvalidBookingException("Station with name " + request.name() + " already exists.");
                });

        station.rename(request.name());

        return toResponse(station);
    }

    @Transactional
    public void deleteStation(Long stationId) {
        if (!stationRepository.existsById(stationId)) {
            throw new ResourceNotFoundException("Station with id " + stationId + " was not found.");
        }

        stationRepository.deleteById(stationId);
    }

    private Station findStationById(Long stationId) {
        return stationRepository.findById(stationId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Station with id " + stationId + " was not found."
                ));
    }

    private StationResponse toResponse(Station station) {
        return new StationResponse(
                station.getId(),
                station.getName()
        );
    }
}