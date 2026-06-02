package com.example.uberbookingservice.controllers;

import com.example.uberbookingservice.dtos.CreateBookingDto;
import com.example.uberbookingservice.dtos.CreateBookingResponseDto;
import com.example.uberbookingservice.dtos.GetBookingStatusResponseDto;
import com.example.uberbookingservice.dtos.UpdateBookingRequestDto;
import com.example.uberbookingservice.dtos.UpdateBookingResponseDto;
import com.example.uberbookingservice.services.BookingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/booking")
public class BookingController {
    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public ResponseEntity<CreateBookingResponseDto> createBooking(@RequestBody CreateBookingDto createBookingDto) {
        return new ResponseEntity<>(bookingService.createBooking(createBookingDto), HttpStatus.CREATED);
    }

    @PostMapping("/{bookingId}")
    public ResponseEntity<UpdateBookingResponseDto> updateBooking(@RequestBody UpdateBookingRequestDto updateBookingRequestDto, @PathVariable long bookingId) {
        return new ResponseEntity<>(bookingService.updateBooking(updateBookingRequestDto, bookingId), HttpStatus.OK);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<GetBookingStatusResponseDto> getBookingStatus(@PathVariable long bookingId) {
        return new ResponseEntity<>(bookingService.getBookingStatus(bookingId), HttpStatus.OK);
    }
}
