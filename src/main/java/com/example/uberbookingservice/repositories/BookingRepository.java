package com.example.uberbookingservice.repositories;

import com.example.uberentityservice.models.Booking;
import com.example.uberentityservice.models.BookingStatus;
import com.example.uberentityservice.models.Driver;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Transactional
    @Modifying
    @Query("UPDATE Booking b SET b.bookingStatus = :status, b.driver = :driver WHERE b.id = :bookingId AND b.bookingStatus = :currentStatus")
    int assignDriverIfRequested(@Param("bookingId") Long bookingId, @Param("status") BookingStatus status, @Param("driver") Driver driver, @Param("currentStatus") BookingStatus currentStatus);

    @Transactional
    @Modifying
    @Query("Update Booking b set b.bookingStatus = :status,b.driver=:driver where b.id = :bookingId")
    void updateBookingStatusAndDriverById(@Param("bookingId") Long bookingId, @Param("status") BookingStatus status, @Param("driver") Driver driver);

}