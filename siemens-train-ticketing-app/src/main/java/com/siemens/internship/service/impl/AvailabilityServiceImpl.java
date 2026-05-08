package com.siemens.internship.service.impl;

import com.siemens.internship.exception.InvalidBookingException;
import com.siemens.internship.exception.NotEnoughSeatsException;
import com.siemens.internship.model.Booking;
import com.siemens.internship.model.BookingStatus;
import com.siemens.internship.model.TrainSchedule;
import com.siemens.internship.repository.BookingRepository;
import com.siemens.internship.service.AvailabilityService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AvailabilityServiceImpl implements AvailabilityService {

    private final BookingRepository bookingRepository;

    public AvailabilityServiceImpl(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    @Override
    public void validateAvailability(
            TrainSchedule schedule,
            Long fromStationId,
            Long toStationId,
            int requestedTickets
    ) {
        if (requestedTickets < 1) {
            throw new InvalidBookingException("At least one ticket must be booked.");
        }

        int requestedStartOrder = schedule.getRoute().getStopOrderForStation(fromStationId);
        int requestedEndOrder = schedule.getRoute().getStopOrderForStation(toStationId);

        if (requestedStartOrder >= requestedEndOrder) {
            throw new InvalidBookingException(
                    "Departure station must appear before arrival station on the selected route."
            );
        }

        List<Booking> confirmedBookings = bookingRepository.findByScheduleIdAndStatus(
                schedule.getId(),
                BookingStatus.CONFIRMED
        );

        int occupiedSeatsOnRequestedSegment = confirmedBookings.stream()
                .filter(existingBooking -> overlapsRequestedSegment(
                        existingBooking,
                        requestedStartOrder,
                        requestedEndOrder
                ))
                .mapToInt(Booking::getNumberOfTickets)
                .sum();

        int availableSeats = schedule.getTrain().getCapacity() - occupiedSeatsOnRequestedSegment;

        if (requestedTickets > availableSeats) {
            throw new NotEnoughSeatsException(
                    "Not enough seats available. Requested: "
                            + requestedTickets
                            + ", available: "
                            + availableSeats
                            + "."
            );
        }
    }

    private boolean overlapsRequestedSegment(
            Booking existingBooking,
            int requestedStartOrder,
            int requestedEndOrder
    ) {
        int existingStartOrder = existingBooking.getSchedule()
                .getRoute()
                .getStopOrderForStation(existingBooking.getFromStation().getId());

        int existingEndOrder = existingBooking.getSchedule()
                .getRoute()
                .getStopOrderForStation(existingBooking.getToStation().getId());

        return requestedStartOrder < existingEndOrder && existingStartOrder < requestedEndOrder;
    }
}