package com.siemens.internship.repository;

import com.siemens.internship.model.Booking;
import com.siemens.internship.model.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByScheduleIdAndStatus(Long scheduleId, BookingStatus status);

    List<Booking> findByScheduleTrainIdAndStatus(Long trainId, BookingStatus status);
}