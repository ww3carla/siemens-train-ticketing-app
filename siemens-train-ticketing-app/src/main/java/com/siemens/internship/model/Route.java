package com.siemens.internship.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "routes")
public class Route {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @OneToMany(
            mappedBy = "route",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private final List<RouteStop> stops = new ArrayList<>();

    protected Route() {
    }

    public Route(String name) {
        this.name = validateText(name, "Route name");
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<RouteStop> getStops() {
        return stops.stream()
                .sorted(Comparator.comparingInt(RouteStop::getStopOrder))
                .toList();
    }

    public void rename(String name) {
        this.name = validateText(name, "Route name");
    }

    public void addStop(Station station, int stopOrder) {
        if (station == null) {
            throw new IllegalArgumentException("Station must not be null.");
        }

        if (stopOrder < 1) {
            throw new IllegalArgumentException("Stop order must be greater than zero.");
        }

        boolean duplicateOrder = stops.stream()
                .anyMatch(stop -> stop.getStopOrder() == stopOrder);

        if (duplicateOrder) {
            throw new IllegalArgumentException("Stop order already exists on this route.");
        }

        boolean duplicateStation = stops.stream()
                .anyMatch(stop -> stop.getStation().equals(station));

        if (duplicateStation) {
            throw new IllegalArgumentException("Station already exists on this route.");
        }

        stops.add(new RouteStop(this, station, stopOrder));
    }

    public int getStopOrderForStation(Long stationId) {
        if (stationId == null) {
            throw new IllegalArgumentException("Station id must not be null.");
        }

        return stops.stream()
                .filter(stop -> stop.getStation().getId().equals(stationId))
                .map(RouteStop::getStopOrder)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Station does not belong to this route."));
    }

    public boolean containsStation(Long stationId) {
        return stops.stream()
                .anyMatch(stop -> stop.getStation().getId().equals(stationId));
    }

    private String validateText(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " must not be blank.");
        }
        return value.trim();
    }

    public void clearStops() {
        stops.clear();
    }

    public void replaceStops(List<Station> stations) {
        if (stations == null || stations.size() < 2) {
            throw new IllegalArgumentException("A route must contain at least two stations.");
        }

        stops.clear();

        for (int index = 0; index < stations.size(); index++) {
            addStop(stations.get(index), index + 1);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Route route)) return false;
        return id != null && Objects.equals(id, route.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}