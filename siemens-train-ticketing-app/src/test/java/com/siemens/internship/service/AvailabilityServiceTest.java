package com.siemens.internship.service;

import com.siemens.internship.exception.InvalidBookingException;
import com.siemens.internship.exception.NotEnoughSeatsException;
import com.siemens.internship.model.*;
import com.siemens.internship.repository.BookingRepository;
import com.siemens.internship.service.impl.AvailabilityServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AvailabilityServiceTest {

    private BookingRepository bookingRepository;
    private AvailabilityService availabilityService;

    private Station stationA;
    private Station stationB;
    private Station stationC;
    private Station stationD;
    private TrainSchedule schedule;

    @BeforeEach
    void setUp() {
        bookingRepository = mock(BookingRepository.class);
        availabilityService = new AvailabilityServiceImpl(bookingRepository);

        stationA = stationWithId(1L, "A");
        stationB = stationWithId(2L, "B");
        stationC = stationWithId(3L, "C");
        stationD = stationWithId(4L, "D");

        Train train = new Train("IR-100", "Test Train", 10);

        Route route = new Route("A to D");
        route.addStop(stationA, 1);
        route.addStop(stationB, 2);
        route.addStop(stationC, 3);
        route.addStop(stationD, 4);

        schedule = scheduleWithId(
                1L,
                new TrainSchedule(
                        train,
                        route,
                        LocalDateTime.of(2026, 5, 1, 8, 0),
                        LocalDateTime.of(2026, 5, 1, 12, 0)
                )
        );
    }

    @Test
    void validateAvailability_shouldPass_whenEnoughSeatsAreAvailable() {
        when(bookingRepository.findByScheduleIdAndStatus(1L, BookingStatus.CONFIRMED))
                .thenReturn(List.of());

        assertDoesNotThrow(() -> availabilityService.validateAvailability(
                schedule,
                stationA.getId(),
                stationD.getId(),
                5
        ));
    }

    @Test
    void validateAvailability_shouldThrow_whenRequestedTicketsExceedAvailableSeats() {
        Booking existingBooking = new Booking(
                "Existing Customer",
                "existing@example.com",
                schedule,
                stationA,
                stationD,
                8
        );

        when(bookingRepository.findByScheduleIdAndStatus(1L, BookingStatus.CONFIRMED))
                .thenReturn(List.of(existingBooking));

        assertThrows(NotEnoughSeatsException.class, () -> availabilityService.validateAvailability(
                schedule,
                stationB.getId(),
                stationC.getId(),
                3
        ));
    }

    @Test
    void validateAvailability_shouldPass_whenExistingBookingDoesNotOverlapRequestedSegment() {
        Booking existingBooking = new Booking(
                "Existing Customer",
                "existing@example.com",
                schedule,
                stationA,
                stationB,
                10
        );

        when(bookingRepository.findByScheduleIdAndStatus(1L, BookingStatus.CONFIRMED))
                .thenReturn(List.of(existingBooking));

        assertDoesNotThrow(() -> availabilityService.validateAvailability(
                schedule,
                stationB.getId(),
                stationD.getId(),
                10
        ));
    }

    @Test
    void validateAvailability_shouldThrow_whenDepartureStationIsAfterArrivalStation() {
        when(bookingRepository.findByScheduleIdAndStatus(1L, BookingStatus.CONFIRMED))
                .thenReturn(List.of());

        assertThrows(InvalidBookingException.class, () -> availabilityService.validateAvailability(
                schedule,
                stationD.getId(),
                stationA.getId(),
                1
        ));
    }

    private Station stationWithId(Long id, String name) {
        Station station = new Station(name);
        setId(station, id);
        return station;
    }

    private TrainSchedule scheduleWithId(Long id, TrainSchedule schedule) {
        setId(schedule, id);
        return schedule;
    }

    private void setId(Object target, Long id) {
        try {
            Field field = target.getClass().getDeclaredField("id");
            field.setAccessible(true);
            field.set(target, id);
        } catch (NoSuchFieldException | IllegalAccessException exception) {
            throw new IllegalStateException("Could not set id for test object.", exception);
        }
    }
}