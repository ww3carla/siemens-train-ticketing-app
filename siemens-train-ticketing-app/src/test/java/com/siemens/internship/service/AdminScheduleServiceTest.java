package com.siemens.internship.service;

import com.siemens.internship.dto.request.DelayRequest;
import com.siemens.internship.dto.response.DelayResponse;
import com.siemens.internship.model.*;
import com.siemens.internship.repository.BookingRepository;
import com.siemens.internship.repository.RouteRepository;
import com.siemens.internship.repository.TrainRepository;
import com.siemens.internship.repository.TrainScheduleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class AdminScheduleServiceTest {

    private TrainScheduleRepository trainScheduleRepository;
    private TrainRepository trainRepository;
    private RouteRepository routeRepository;
    private BookingRepository bookingRepository;
    private EmailService emailService;
    private AdminScheduleService adminScheduleService;

    private TrainSchedule schedule;
    private Booking booking;

    @BeforeEach
    void setUp() {
        trainScheduleRepository = mock(TrainScheduleRepository.class);
        trainRepository = mock(TrainRepository.class);
        routeRepository = mock(RouteRepository.class);
        bookingRepository = mock(BookingRepository.class);
        emailService = mock(EmailService.class);

        adminScheduleService = new AdminScheduleService(
                trainScheduleRepository,
                trainRepository,
                routeRepository,
                bookingRepository,
                emailService
        );

        Station cluj = stationWithId(1L, "Cluj-Napoca");
        Station bucharest = stationWithId(2L, "Bucharest");

        Train train = trainWithId(1L, new Train("IR-101", "Transylvania Express", 120));

        Route route = routeWithId(1L, new Route("Cluj-Napoca to Bucharest"));
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

        booking = new Booking(
                "Carla Bozintan",
                "carla@example.com",
                schedule,
                cluj,
                bucharest,
                2
        );
    }

    @Test
    void reportDelay_shouldUpdateDelayAndNotifyAffectedCustomers() {
        DelayRequest request = new DelayRequest(
                35,
                "Technical issue"
        );

        when(trainScheduleRepository.findById(1L)).thenReturn(Optional.of(schedule));
        when(bookingRepository.findByScheduleIdAndStatus(1L, BookingStatus.CONFIRMED))
                .thenReturn(List.of(booking));

        DelayResponse response = adminScheduleService.reportDelay(1L, request);

        assertEquals(1L, response.scheduleId());
        assertEquals("IR-101", response.trainNumber());
        assertEquals(35, response.delayMinutes());
        assertEquals("Technical issue", response.reason());
        assertEquals(1, response.notifiedCustomers());
        assertEquals(35, schedule.getDelayMinutes());

        verify(emailService).sendDelayNotification(schedule, List.of(booking));
    }

    @Test
    void reportDelay_shouldWork_whenNoCustomersAreAffected() {
        DelayRequest request = new DelayRequest(
                20,
                "Weather conditions"
        );

        when(trainScheduleRepository.findById(1L)).thenReturn(Optional.of(schedule));
        when(bookingRepository.findByScheduleIdAndStatus(1L, BookingStatus.CONFIRMED))
                .thenReturn(List.of());

        DelayResponse response = adminScheduleService.reportDelay(1L, request);

        assertEquals(20, response.delayMinutes());
        assertEquals(0, response.notifiedCustomers());
        assertEquals(20, schedule.getDelayMinutes());

        verify(emailService).sendDelayNotification(schedule, List.of());
    }

    private Station stationWithId(Long id, String name) {
        Station station = new Station(name);
        setId(station, id);
        return station;
    }

    private Train trainWithId(Long id, Train train) {
        setId(train, id);
        return train;
    }

    private Route routeWithId(Long id, Route route) {
        setId(route, id);
        return route;
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