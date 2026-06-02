package com.example.uberbookingservice.services;

import com.example.uberbookingservice.dtos.CreateBookingDto;
import com.example.uberbookingservice.dtos.CreateBookingResponseDto;
import com.example.uberbookingservice.dtos.GetBookingStatusResponseDto;
import com.example.uberbookingservice.dtos.UpdateBookingRequestDto;
import com.example.uberbookingservice.dtos.UpdateBookingResponseDto;

public interface BookingService {
    CreateBookingResponseDto createBooking(CreateBookingDto bookingDetails);
    UpdateBookingResponseDto updateBooking(UpdateBookingRequestDto bookingRequestDto, Long bookingId);
    GetBookingStatusResponseDto getBookingStatus(Long bookingId);
}
