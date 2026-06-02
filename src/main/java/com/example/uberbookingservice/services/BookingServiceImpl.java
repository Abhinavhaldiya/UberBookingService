package com.example.uberbookingservice.services;

import com.example.uberbookingservice.apis.LocationServiceApi;
import com.example.uberbookingservice.apis.UberSocketApi;
import com.example.uberbookingservice.dtos.*;
import com.example.uberbookingservice.repositories.BookingRepository;
import com.example.uberbookingservice.repositories.DriverRepository;
import com.example.uberbookingservice.repositories.PassengerRepository;
import com.example.uberentityservice.models.Booking;
import com.example.uberentityservice.models.BookingStatus;
import com.example.uberentityservice.models.Driver;
import com.example.uberentityservice.models.ExactLocation;
import com.example.uberentityservice.models.Passenger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.Arrays;
import java.util.List;

@Service
public class BookingServiceImpl implements BookingService {
    private final PassengerRepository passengerRepository;
    private final BookingRepository bookingRepository;
    private final DriverRepository driverRepository;

    private final LocationServiceApi locationServiceApi;
    private final UberSocketApi uberSocketApi;

    public BookingServiceImpl(PassengerRepository passengerRepository, BookingRepository bookingRepository, DriverRepository driverRepository, LocationServiceApi locationServiceApi, UberSocketApi uberSocketApi) {
        this.passengerRepository = passengerRepository;
        this.bookingRepository = bookingRepository;
        this.locationServiceApi = locationServiceApi;
        this.driverRepository = driverRepository;
        this.uberSocketApi = uberSocketApi;
    }

    @Override
    public CreateBookingResponseDto createBooking(CreateBookingDto bookingDetails) {
        Passenger passenger = passengerRepository.findById(bookingDetails.getPassengerId())
                .orElseThrow(() -> new RuntimeException("Passenger not found: " + bookingDetails.getPassengerId()));
        Booking booking = Booking.builder()
                .bookingStatus(BookingStatus.REQUESTED)
                .startLocation(bookingDetails.getStartLocation())
                .endLocation(bookingDetails.getEndLocation())
                .passenger(passenger)
                .build();
        Booking newBooking = bookingRepository.save(booking);


        NearbyDriversRequestDto request = NearbyDriversRequestDto.builder()
                .latitude(bookingDetails.getStartLocation().getLatitude())
                .longitude(bookingDetails.getStartLocation().getLongitude())
                .build();
        // make an async api call to location service to fetch nearby drivers
        processNearByDriversAsync(request, bookingDetails.getPassengerId(), newBooking.getId());

        return CreateBookingResponseDto.builder()
                .bookingId(newBooking.getId())
                .bookingStatus(newBooking.getBookingStatus().toString())
                .build();
    }

    @Override
    @Transactional
    public UpdateBookingResponseDto updateBooking(UpdateBookingRequestDto updateBookingRequestDto, Long bookingId) {
        Driver driver = driverRepository.findById(updateBookingRequestDto.getDriverId())
                .orElseThrow(() -> new RuntimeException("Driver not found: " + updateBookingRequestDto.getDriverId()));

        int updated = bookingRepository.assignDriverIfRequested(
                bookingId,
                BookingStatus.valueOf(updateBookingRequestDto.getStatus()),
                driver,
                BookingStatus.REQUESTED
        );

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found with id: " + bookingId));

        if (updated == 0) {
            System.out.println("Booking " + bookingId + " already assigned — ignoring driver " + driver.getId());
        }

        return UpdateBookingResponseDto.builder()
                .bookingId(bookingId)
                .status(booking.getBookingStatus())
                .driverId(booking.getDriver() != null ? booking.getDriver().getId() : null)
                .build();
    }

    private void processNearByDriversAsync(NearbyDriversRequestDto nearbyDriversRequestDto, Long passengerId, Long bookingId) {
        Call<DriverLocationDto[]> call = locationServiceApi.getNearbyDrivers(nearbyDriversRequestDto.getLatitude(), nearbyDriversRequestDto.getLongitude());

        call.enqueue(new retrofit2.Callback<DriverLocationDto[]>() {
            @Override
            public void onResponse(Call<DriverLocationDto[]> call, retrofit2.Response<DriverLocationDto[]> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<DriverLocationDto> nearbyDrivers = Arrays.asList(response.body());
                    nearbyDrivers.forEach(nearbyDriver -> {
                        System.out.println(nearbyDriver.getDriverId() + " " + "lat: " + nearbyDriver.getLatitude() + "long: " + nearbyDriver.getLongitude());
                    });
                    List<Long> driverIds = nearbyDrivers.stream()
                            .map(d -> Long.parseLong(d.getDriverId()))
                            .toList();
                    try {
                        raiseRideRequestAsync(RideRequestDto.builder().passengerId(passengerId).bookingId(bookingId).driverIds(driverIds).build());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    try {
                        System.out.println("Request failed. Code: " + response.code() + " Body: " + (response.errorBody() != null ? response.errorBody().string() : "null"));
                    } catch (Exception e) {
                        System.out.println("Request failed. Code: " + response.code());
                    }
                }
            }

            @Override
            public void onFailure(Call<DriverLocationDto[]> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    @Override
    @Transactional
    public GetBookingStatusResponseDto getBookingStatus(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found: " + bookingId));

        Driver driver = booking.getDriver();
        ExactLocation start = booking.getStartLocation();
        ExactLocation end = booking.getEndLocation();

        return GetBookingStatusResponseDto.builder()
                .bookingId(booking.getId())
                .bookingStatus(booking.getBookingStatus())
                .passengerId(booking.getPassenger().getId())
                .driverId(driver != null ? driver.getId() : null)
                .driverName(driver != null ? driver.getName() : null)
                .driverPhone(driver != null ? driver.getPhoneNumber() : null)
                .startLatitude(start != null ? start.getLatitude() : null)
                .startLongitude(start != null ? start.getLongitude() : null)
                .endLatitude(end != null ? end.getLatitude() : null)
                .endLongitude(end != null ? end.getLongitude() : null)
                .build();
    }

    private void raiseRideRequestAsync(RideRequestDto requestDto) throws IOException {
        Call<Boolean> call = uberSocketApi.raiseRideRequest(requestDto);

        System.out.println(call.request().url() + " " + call.request().method() + " " + call.request().headers());
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Boolean result = response.body();
                    System.out.println("Ride request raised successfully: " + result);
                } else {
                    System.out.println("Request for ride failed" + response.message());
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }
}

