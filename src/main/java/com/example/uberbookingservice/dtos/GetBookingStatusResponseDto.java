package com.example.uberbookingservice.dtos;

import com.example.uberentityservice.models.BookingStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GetBookingStatusResponseDto {
    private Long bookingId;
    private BookingStatus bookingStatus;
    private Long driverId;
    private String driverName;
    private String driverPhone;
    private Long passengerId;
    private Double startLatitude;
    private Double startLongitude;
    private Double endLatitude;
    private Double endLongitude;
}
