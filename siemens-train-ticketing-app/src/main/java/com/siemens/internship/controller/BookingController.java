package com.siemens.internship.controller;

import com.siemens.internship.dto.request.CreateBookingRequest;
import com.siemens.internship.dto.response.BookingCreatedResponse;
import com.siemens.internship.dto.response.BookingResponse;
import com.siemens.internship.service.BookingService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingCreatedResponse createBooking(@Valid @RequestBody CreateBookingRequest request) {
        return bookingService.createBooking(request);
    }

    @GetMapping("/{bookingId}")
    public BookingResponse getBookingById(@PathVariable Long bookingId) {
        return bookingService.getBookingById(bookingId);
    }
}