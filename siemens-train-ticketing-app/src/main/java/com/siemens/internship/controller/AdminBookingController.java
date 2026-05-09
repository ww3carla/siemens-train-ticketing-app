package com.siemens.internship.controller;

import com.siemens.internship.dto.response.BookingResponse;
import com.siemens.internship.service.BookingService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminBookingController {

    private final BookingService bookingService;

    public AdminBookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping("/trains/{trainId}/bookings")
    public List<BookingResponse> getBookingsForTrain(@PathVariable Long trainId) {
        return bookingService.getBookingsForTrain(trainId);
    }
}