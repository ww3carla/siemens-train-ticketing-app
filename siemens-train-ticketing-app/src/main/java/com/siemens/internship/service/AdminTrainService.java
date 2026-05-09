package com.siemens.internship.service;

import com.siemens.internship.dto.request.TrainRequest;
import com.siemens.internship.dto.response.TrainResponse;
import com.siemens.internship.exception.InvalidBookingException;
import com.siemens.internship.exception.ResourceNotFoundException;
import com.siemens.internship.model.Train;
import com.siemens.internship.repository.TrainRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AdminTrainService {

    private final TrainRepository trainRepository;

    public AdminTrainService(TrainRepository trainRepository) {
        this.trainRepository = trainRepository;
    }

    @Transactional(readOnly = true)
    public List<TrainResponse> getAllTrains() {
        return trainRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public TrainResponse getTrainById(Long trainId) {
        Train train = findTrainById(trainId);
        return toResponse(train);
    }

    @Transactional
    public TrainResponse createTrain(TrainRequest request) {
        trainRepository.findByTrainNumberIgnoreCase(request.trainNumber())
                .ifPresent(train -> {
                    throw new InvalidBookingException(
                            "Train with number " + request.trainNumber() + " already exists."
                    );
                });

        Train train = new Train(
                request.trainNumber(),
                request.name(),
                request.capacity()
        );

        Train savedTrain = trainRepository.save(train);

        return toResponse(savedTrain);
    }

    @Transactional
    public TrainResponse updateTrain(Long trainId, TrainRequest request) {
        Train train = findTrainById(trainId);

        trainRepository.findByTrainNumberIgnoreCase(request.trainNumber())
                .filter(existingTrain -> !existingTrain.getId().equals(trainId))
                .ifPresent(existingTrain -> {
                    throw new InvalidBookingException(
                            "Train with number " + request.trainNumber() + " already exists."
                    );
                });

        train.updateDetails(
                request.trainNumber(),
                request.name(),
                request.capacity()
        );

        return toResponse(train);
    }

    @Transactional
    public void deleteTrain(Long trainId) {
        if (!trainRepository.existsById(trainId)) {
            throw new ResourceNotFoundException("Train with id " + trainId + " was not found.");
        }

        trainRepository.deleteById(trainId);
    }

    private Train findTrainById(Long trainId) {
        return trainRepository.findById(trainId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Train with id " + trainId + " was not found."
                ));
    }

    private TrainResponse toResponse(Train train) {
        return new TrainResponse(
                train.getId(),
                train.getTrainNumber(),
                train.getName(),
                train.getCapacity()
        );
    }
}