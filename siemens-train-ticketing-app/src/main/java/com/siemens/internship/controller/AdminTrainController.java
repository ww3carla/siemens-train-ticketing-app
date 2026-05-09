package com.siemens.internship.controller;

import com.siemens.internship.dto.request.TrainRequest;
import com.siemens.internship.dto.response.TrainResponse;
import com.siemens.internship.service.AdminTrainService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/trains")
public class AdminTrainController {

    private final AdminTrainService adminTrainService;

    public AdminTrainController(AdminTrainService adminTrainService) {
        this.adminTrainService = adminTrainService;
    }

    @GetMapping
    public List<TrainResponse> getAllTrains() {
        return adminTrainService.getAllTrains();
    }

    @GetMapping("/{trainId}")
    public TrainResponse getTrainById(@PathVariable Long trainId) {
        return adminTrainService.getTrainById(trainId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TrainResponse createTrain(@Valid @RequestBody TrainRequest request) {
        return adminTrainService.createTrain(request);
    }

    @PutMapping("/{trainId}")
    public TrainResponse updateTrain(
            @PathVariable Long trainId,
            @Valid @RequestBody TrainRequest request
    ) {
        return adminTrainService.updateTrain(trainId, request);
    }

    @DeleteMapping("/{trainId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTrain(@PathVariable Long trainId) {
        adminTrainService.deleteTrain(trainId);
    }
}