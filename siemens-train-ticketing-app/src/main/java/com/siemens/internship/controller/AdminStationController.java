package com.siemens.internship.controller;

import com.siemens.internship.dto.request.StationRequest;
import com.siemens.internship.dto.response.StationResponse;
import com.siemens.internship.service.AdminStationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/stations")
public class AdminStationController {

    private final AdminStationService adminStationService;

    public AdminStationController(AdminStationService adminStationService) {
        this.adminStationService = adminStationService;
    }

    @GetMapping
    public List<StationResponse> getAllStations() {
        return adminStationService.getAllStations();
    }

    @GetMapping("/{stationId}")
    public StationResponse getStationById(@PathVariable Long stationId) {
        return adminStationService.getStationById(stationId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public StationResponse createStation(@Valid @RequestBody StationRequest request) {
        return adminStationService.createStation(request);
    }

    @PutMapping("/{stationId}")
    public StationResponse updateStation(
            @PathVariable Long stationId,
            @Valid @RequestBody StationRequest request
    ) {
        return adminStationService.updateStation(stationId, request);
    }

    @DeleteMapping("/{stationId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteStation(@PathVariable Long stationId) {
        adminStationService.deleteStation(stationId);
    }
}