package com.siemens.internship.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "train_schedules")
public class TrainSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "train_id", nullable = false)
    private Train train;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "route_id", nullable = false)
    private Route route;

    @Column(name = "departure_time", nullable = false)
    private LocalDateTime departureTime;

    @Column(name = "arrival_time", nullable = false)
    private LocalDateTime arrivalTime;

    @Column(name = "delay_minutes", nullable = false)
    private int delayMinutes;

    protected TrainSchedule() {
    }

    public TrainSchedule(Train train, Route route, LocalDateTime departureTime, LocalDateTime arrivalTime) {
        if (train == null) {
            throw new IllegalArgumentException("Train must not be null.");
        }

        if (route == null) {
            throw new IllegalArgumentException("Route must not be null.");
        }

        validateTimes(departureTime, arrivalTime);

        this.train = train;
        this.route = route;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.delayMinutes = 0;
    }

    public Long getId() {
        return id;
    }

    public Train getTrain() {
        return train;
    }

    public Route getRoute() {
        return route;
    }

    public LocalDateTime getDepartureTime() {
        return departureTime;
    }

    public LocalDateTime getArrivalTime() {
        return arrivalTime;
    }

    public int getDelayMinutes() {
        return delayMinutes;
    }

    public LocalDateTime getEffectiveDepartureTime() {
        return departureTime.plusMinutes(delayMinutes);
    }

    public LocalDateTime getEffectiveArrivalTime() {
        return arrivalTime.plusMinutes(delayMinutes);
    }

    public void updateTimes(LocalDateTime departureTime, LocalDateTime arrivalTime) {
        validateTimes(departureTime, arrivalTime);
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
    }

    public void updateDelay(int delayMinutes) {
        if (delayMinutes < 0) {
            throw new IllegalArgumentException("Delay minutes cannot be negative.");
        }
        this.delayMinutes = delayMinutes;
    }

    private void validateTimes(LocalDateTime departureTime, LocalDateTime arrivalTime) {
        if (departureTime == null || arrivalTime == null) {
            throw new IllegalArgumentException("Departure and arrival times must not be null.");
        }

        if (!arrivalTime.isAfter(departureTime)) {
            throw new IllegalArgumentException("Arrival time must be after departure time.");
        }
    }

    public void updateTrainAndRoute(Train train, Route route) {
        if (train == null) {
            throw new IllegalArgumentException("Train must not be null.");
        }

        if (route == null) {
            throw new IllegalArgumentException("Route must not be null.");
        }

        this.train = train;
        this.route = route;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TrainSchedule schedule)) return false;
        return id != null && Objects.equals(id, schedule.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}