package com.siemens.internship.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "bookings")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "customer_name", nullable = false)
    private String customerName;

    @Column(name = "customer_email", nullable = false)
    private String customerEmail;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_id", nullable = false)
    private TrainSchedule schedule;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "from_station_id", nullable = false)
    private Station fromStation;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "to_station_id", nullable = false)
    private Station toStation;

    @Column(name = "number_of_tickets", nullable = false)
    private int numberOfTickets;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookingStatus status;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    protected Booking() {
    }

    public Booking(
            String customerName,
            String customerEmail,
            TrainSchedule schedule,
            Station fromStation,
            Station toStation,
            int numberOfTickets
    ) {
        if (schedule == null) {
            throw new IllegalArgumentException("Schedule must not be null.");
        }

        if (fromStation == null || toStation == null) {
            throw new IllegalArgumentException("Stations must not be null.");
        }

        if (fromStation.equals(toStation)) {
            throw new IllegalArgumentException("Departure and arrival stations must be different.");
        }

        if (numberOfTickets < 1) {
            throw new IllegalArgumentException("At least one ticket must be booked.");
        }

        this.customerName = validateText(customerName, "Customer name");
        this.customerEmail = validateText(customerEmail, "Customer email");
        this.schedule = schedule;
        this.fromStation = fromStation;
        this.toStation = toStation;
        this.numberOfTickets = numberOfTickets;
        this.status = BookingStatus.CONFIRMED;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public TrainSchedule getSchedule() {
        return schedule;
    }

    public Station getFromStation() {
        return fromStation;
    }

    public Station getToStation() {
        return toStation;
    }

    public int getNumberOfTickets() {
        return numberOfTickets;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void cancel() {
        this.status = BookingStatus.CANCELLED;
    }

    private String validateText(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " must not be blank.");
        }
        return value.trim();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Booking booking)) return false;
        return id != null && Objects.equals(id, booking.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}