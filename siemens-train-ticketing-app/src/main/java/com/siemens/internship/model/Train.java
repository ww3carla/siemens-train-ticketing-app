package com.siemens.internship.model;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(
        name = "trains",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_train_number", columnNames = "train_number")
        }
)
public class Train {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "train_number", nullable = false)
    private String trainNumber;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private int capacity;

    protected Train() {
    }

    public Train(String trainNumber, String name, int capacity) {
        validateCapacity(capacity);
        this.trainNumber = validateText(trainNumber, "Train number");
        this.name = validateText(name, "Train name");
        this.capacity = capacity;
    }

    public Long getId() {
        return id;
    }

    public String getTrainNumber() {
        return trainNumber;
    }

    public String getName() {
        return name;
    }

    public int getCapacity() {
        return capacity;
    }

    public void updateDetails(String trainNumber, String name, int capacity) {
        validateCapacity(capacity);
        this.trainNumber = validateText(trainNumber, "Train number");
        this.name = validateText(name, "Train name");
        this.capacity = capacity;
    }

    private void validateCapacity(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Train capacity must be greater than zero.");
        }
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
        if (!(o instanceof Train train)) return false;
        return id != null && Objects.equals(id, train.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}