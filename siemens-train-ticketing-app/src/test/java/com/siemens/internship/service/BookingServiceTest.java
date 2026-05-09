package com.siemens.internship.service;

import com.siemens.internship.dto.request.CreateBookingRequest;
import com.siemens.internship.dto.response.BookingCreatedResponse;
import com.siemens.internship.exception.ResourceNotFoundException;
import com.siemens.internship.model.*;
import com.siemens.internship.repository.BookingRepository;
import com.siemens.internship.repository.StationRepository;
import com.siemens.internship.repository.TrainScheduleRepository;
import com.siemens.internship.service.impl.BookingServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class BookingServiceTest {

    private BookingRepository bookingRepository;
    private TrainScheduleRepository trainScheduleRepository;
    private StationRepository stationRepository;
    private AvailabilityService availabilityService;
    private EmailService emailService;
    private BookingService bookingService;

    private Station cluj;
    private Station bucharest;
    private TrainSchedule schedule;

    @BeforeEach
    void setUp() {
        bookingRepository = mock(BookingRepository.class);
        trainScheduleRepository = mock(TrainScheduleRepository.class);
        stationRepository = mock(StationRepository.class);
        availabilityService = mock(AvailabilityService.class);
        emailService = mock(EmailService.class);

        bookingService = new BookingServiceImpl(
                bookingRepository,
                trainScheduleRepository,
                stationRepository,
                availabilityService,
                emailService
        );

        cluj = stationWithId(1L, "Cluj-Napoca");
        bucharest = stationWithId(5L, "Bucharest");

        Train train = new Train("IR-101", "Transylvania Express", 120);

        Route route = new Route("Cluj-Napoca to Bucharest");
        route.addStop(cluj, 1);
        route.addStop(bucharest, 2);

        schedule = scheduleWithId(
                1L,
                new TrainSchedule(
                        train,
                        route,
                        LocalDateTime.of(2026, 5, 1, 8, 0),
                        LocalDateTime.of(2026, 5, 1, 16, 30)
                )
        );
    }

    @Test
    void createBooking_shouldSaveBookingAndSendConfirmationEmail() {
        CreateBookingRequest request = new CreateBookingRequest(
                "Carla Bozintan",
                "carla@example.com",
                1L,
                1L,
                5L,
                2
        );

        when(trainScheduleRepository.findById(1L)).thenReturn(Optional.of(schedule));
        when(stationRepository.findById(1L)).thenReturn(Optional.of(cluj));
        when(stationRepository.findById(5L)).thenReturn(Optional.of(bucharest));
        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> {
            Booking booking = invocation.getArgument(0);
            setId(booking, 10L);
            return booking;
        });

        BookingCreatedResponse response = bookingService.createBooking(request);

        assertEquals(10L, response.bookingId());
        assertEquals("CONFIRMED", response.status());
        assertTrue(response.message().contains("Booking confirmed"));

        verify(availabilityService).validateAvailability(schedule, 1L, 5L, 2);
        verify(bookingRepository).save(any(Booking.class));
        verify(emailService).sendBookingConfirmation(any(Booking.class));
    }

    @Test
    void createBooking_shouldThrow_whenScheduleDoesNotExist() {
        CreateBookingRequest request = new CreateBookingRequest(
                "Carla Bozintan",
                "carla@example.com",
                999L,
                1L,
                5L,
                2
        );

        when(trainScheduleRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> bookingService.createBooking(request));

        verify(bookingRepository, never()).save(any());
        verify(emailService, never()).sendBookingConfirmation(any());
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