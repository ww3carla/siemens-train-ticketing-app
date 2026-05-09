package com.siemens.internship.service.impl;

import com.siemens.internship.dto.request.CreateBookingRequest;
import com.siemens.internship.dto.response.BookingCreatedResponse;
import com.siemens.internship.dto.response.BookingResponse;
import com.siemens.internship.exception.InvalidBookingException;
import com.siemens.internship.exception.ResourceNotFoundException;
import com.siemens.internship.model.Booking;
import com.siemens.internship.model.BookingStatus;
import com.siemens.internship.model.Station;
import com.siemens.internship.model.TrainSchedule;
import com.siemens.internship.repository.BookingRepository;
import com.siemens.internship.repository.StationRepository;
import com.siemens.internship.repository.TrainScheduleRepository;
import com.siemens.internship.service.AvailabilityService;
import com.siemens.internship.service.BookingService;
import com.siemens.internship.service.EmailService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final TrainScheduleRepository trainScheduleRepository;
    private final StationRepository stationRepository;
    private final AvailabilityService availabilityService;
    private final EmailService emailService;

    public BookingServiceImpl(
            BookingRepository bookingRepository,
            TrainScheduleRepository trainScheduleRepository,
            StationRepository stationRepository,
            AvailabilityService availabilityService,
            EmailService emailService
    ) {
        this.bookingRepository = bookingRepository;
        this.trainScheduleRepository = trainScheduleRepository;
        this.stationRepository = stationRepository;
        this.availabilityService = availabilityService;
        this.emailService = emailService;
    }

    @Override
    @Transactional
    public BookingCreatedResponse createBooking(CreateBookingRequest request) {
        TrainSchedule schedule = trainScheduleRepository.findById(request.scheduleId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Schedule with id " + request.scheduleId() + " was not found."
                ));

        Station fromStation = stationRepository.findById(request.fromStationId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Departure station with id " + request.fromStationId() + " was not found."
                ));

        Station toStation = stationRepository.findById(request.toStationId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Arrival station with id " + request.toStationId() + " was not found."
                ));

        if (fromStation.equals(toStation)) {
            throw new InvalidBookingException("Departure and arrival stations must be different.");
        }

        availabilityService.validateAvailability(
                schedule,
                fromStation.getId(),
                toStation.getId(),
                request.numberOfTickets()
        );

        Booking booking = new Booking(
                request.customerName(),
                request.customerEmail(),
                schedule,
                fromStation,
                toStation,
                request.numberOfTickets()
        );

        Booking savedBooking = bookingRepository.save(booking);
        emailService.sendBookingConfirmation(savedBooking);

        return new BookingCreatedResponse(
                savedBooking.getId(),
                savedBooking.getStatus().name(),
                "Booking confirmed successfully. A confirmation email has been sent."
        );
    }

    @Override
    @Transactional(readOnly = true)
    public BookingResponse getBookingById(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Booking with id " + bookingId + " was not found."
                ));

        return toResponse(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingResponse> getBookingsForTrain(Long trainId) {
        return bookingRepository.findByScheduleTrainIdAndStatus(trainId, BookingStatus.CONFIRMED)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private BookingResponse toResponse(Booking booking) {
        return new BookingResponse(
                booking.getId(),
                booking.getCustomerName(),
                booking.getCustomerEmail(),
                booking.getSchedule().getTrain().getTrainNumber(),
                booking.getSchedule().getTrain().getName(),
                booking.getFromStation().getName(),
                booking.getToStation().getName(),
                booking.getNumberOfTickets(),
                booking.getStatus(),
                booking.getSchedule().getEffectiveDepartureTime(),
                booking.getSchedule().getEffectiveArrivalTime(),
                booking.getCreatedAt()
        );
    }
}