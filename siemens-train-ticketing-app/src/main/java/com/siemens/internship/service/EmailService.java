package com.siemens.internship.service;

import com.siemens.internship.model.Booking;
import com.siemens.internship.model.TrainSchedule;

import java.util.List;

public interface EmailService {

    void sendBookingConfirmation(Booking booking);

    void sendDelayNotification(TrainSchedule schedule, List<Booking> affectedBookings);
}