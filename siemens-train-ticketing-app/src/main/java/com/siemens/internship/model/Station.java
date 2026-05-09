package com.siemens.internship.model;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(
        name = "stations",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_station_name", columnNames = "name")
        }
)
public class Station {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    protected Station() {
    }

    public Station(String name) {
        this.name = validateName(name);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void rename(String name) {
        this.name = validateName(name);
    }

    private String validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Station name must not be blank.");
        }
        return name.trim();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Station station)) return false;
        return id != null && Objects.equals(id, station.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}