package com.siemens.internship.service;

import com.siemens.internship.model.TrainSchedule;

public interface AvailabilityService {

    void validateAvailability(
            TrainSchedule schedule,
            Long fromStationId,
            Long toStationId,
            int requestedTickets
    );
}