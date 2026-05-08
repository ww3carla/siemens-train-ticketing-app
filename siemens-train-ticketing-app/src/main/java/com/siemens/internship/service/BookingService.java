package com.siemens.internship.service;

import com.siemens.internship.dto.request.CreateBookingRequest;
import com.siemens.internship.dto.response.BookingCreatedResponse;
import com.siemens.internship.dto.response.BookingResponse;

import java.util.List;

public interface BookingService {

    BookingCreatedResponse createBooking(CreateBookingRequest request);

    BookingResponse getBookingById(Long bookingId);

    List<BookingResponse> getBookingsForTrain(Long trainId);
}