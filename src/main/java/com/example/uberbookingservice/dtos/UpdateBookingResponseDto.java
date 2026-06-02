package com.example.uberbookingservice.dtos;

import com.example.uberentityservice.models.BookingStatus;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateBookingResponseDto {
    private long bookingId;
    private BookingStatus status;
    private Long driverId;
}
