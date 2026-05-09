package com.siemens.internship.service.impl;

import com.siemens.internship.model.Booking;
import com.siemens.internship.model.TrainSchedule;
import com.siemens.internship.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LoggingEmailService implements EmailService {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoggingEmailService.class);

    @Override
    public void sendBookingConfirmation(Booking booking) {
        LOGGER.info(
                """
                Sending booking confirmation email:
                To: {}
                Subject: Booking confirmation #{}
                Message: Your booking for train {} from {} to {} has been confirmed for {} ticket(s).
                """,
                booking.getCustomerEmail(),
                booking.getId(),
                booking.getSchedule().getTrain().getTrainNumber(),
                booking.getFromStation().getName(),
                booking.getToStation().getName(),
                booking.getNumberOfTickets()
        );
    }

    @Override
    public void sendDelayNotification(TrainSchedule schedule, List<Booking> affectedBookings) {
        for (Booking booking : affectedBookings) {
            LOGGER.info(
                    """
                    Sending delay notification email:
                    To: {}
                    Subject: Train delay notification
                    Message: Train {} is delayed by {} minutes. Your booking #{} is affected.
                    """,
                    booking.getCustomerEmail(),
                    schedule.getTrain().getTrainNumber(),
                    schedule.getDelayMinutes(),
                    booking.getId()
            );
        }
    }
}