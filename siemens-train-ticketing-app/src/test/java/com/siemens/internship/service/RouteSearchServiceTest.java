package com.siemens.internship.service;

import com.siemens.internship.dto.response.RouteSearchResponse;
import com.siemens.internship.exception.InvalidBookingException;
import com.siemens.internship.exception.RouteNotFoundException;
import com.siemens.internship.model.Route;
import com.siemens.internship.model.Station;
import com.siemens.internship.model.Train;
import com.siemens.internship.model.TrainSchedule;
import com.siemens.internship.repository.StationRepository;
import com.siemens.internship.repository.TrainScheduleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class RouteSearchServiceTest {

    private StationRepository stationRepository;
    private TrainScheduleRepository trainScheduleRepository;
    private RouteSearchService routeSearchService;

    private Station cluj;
    private Station brasov;
    private Station bucharest;
    private Station iasi;

    @BeforeEach
    void setUp() {
        stationRepository = mock(StationRepository.class);
        trainScheduleRepository = mock(TrainScheduleRepository.class);

        routeSearchService = new RouteSearchService(
                stationRepository,
                trainScheduleRepository
        );

        cluj = stationWithId(1L, "Cluj-Napoca");
        brasov = stationWithId(2L, "Brasov");
        bucharest = stationWithId(3L, "Bucharest");
        iasi = stationWithId(4L, "Iasi");

        when(stationRepository.existsById(1L)).thenReturn(true);
        when(stationRepository.existsById(2L)).thenReturn(true);
        when(stationRepository.existsById(3L)).thenReturn(true);
        when(stationRepository.existsById(4L)).thenReturn(true);
    }

    @Test
    void findConnections_shouldReturnDirectConnection() {
        TrainSchedule directSchedule = directSchedule();

        when(trainScheduleRepository.findAll()).thenReturn(List.of(directSchedule));

        List<RouteSearchResponse> result = routeSearchService.findConnections(
                cluj.getId(),
                bucharest.getId()
        );

        assertEquals(1, result.size());
        assertEquals("DIRECT", result.getFirst().connectionType());
        assertEquals("Cluj-Napoca", result.getFirst().legs().getFirst().fromStation());
        assertEquals("Bucharest", result.getFirst().legs().getFirst().toStation());
    }

    @Test
    void findConnections_shouldReturnChangeoverConnection() {
        TrainSchedule firstSchedule = clujToBrasovSchedule();
        TrainSchedule secondSchedule = brasovToIasiSchedule();

        when(trainScheduleRepository.findAll()).thenReturn(List.of(firstSchedule, secondSchedule));

        List<RouteSearchResponse> result = routeSearchService.findConnections(
                cluj.getId(),
                iasi.getId()
        );

        assertEquals(1, result.size());
        assertEquals("CHANGEOVER", result.getFirst().connectionType());
        assertEquals(2, result.getFirst().legs().size());
        assertEquals("Brasov", result.getFirst().legs().getFirst().toStation());
        assertEquals("Brasov", result.getFirst().legs().get(1).fromStation());
    }

    @Test
    void findConnections_shouldThrow_whenNoConnectionExists() {
        TrainSchedule directSchedule = directSchedule();

        when(trainScheduleRepository.findAll()).thenReturn(List.of(directSchedule));

        assertThrows(RouteNotFoundException.class, () -> routeSearchService.findConnections(
                iasi.getId(),
                cluj.getId()
        ));
    }

    @Test
    void findConnections_shouldThrow_whenDepartureAndArrivalAreTheSame() {
        assertThrows(InvalidBookingException.class, () -> routeSearchService.findConnections(
                cluj.getId(),
                cluj.getId()
        ));
    }

    private TrainSchedule directSchedule() {
        Route route = new Route("Cluj-Napoca to Bucharest");
        route.addStop(cluj, 1);
        route.addStop(brasov, 2);
        route.addStop(bucharest, 3);

        return new TrainSchedule(
                new Train("IR-101", "Transylvania Express", 120),
                route,
                LocalDateTime.of(2026, 5, 1, 8, 0),
                LocalDateTime.of(2026, 5, 1, 16, 30)
        );
    }

    private TrainSchedule clujToBrasovSchedule() {
        Route route = new Route("Cluj-Napoca to Brasov");
        route.addStop(cluj, 1);
        route.addStop(brasov, 2);

        return new TrainSchedule(
                new Train("IR-201", "Cluj Brasov Express", 100),
                route,
                LocalDateTime.of(2026, 5, 1, 8, 0),
                LocalDateTime.of(2026, 5, 1, 12, 0)
        );
    }

    private TrainSchedule brasovToIasiSchedule() {
        Route route = new Route("Brasov to Iasi");
        route.addStop(brasov, 1);
        route.addStop(iasi, 2);

        return new TrainSchedule(
                new Train("IR-202", "Moldova Express", 100),
                route,
                LocalDateTime.of(2026, 5, 1, 13, 0),
                LocalDateTime.of(2026, 5, 1, 20, 0)
        );
    }

    private Station stationWithId(Long id, String name) {
        Station station = new Station(name);
        setId(station, id);
        return station;
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