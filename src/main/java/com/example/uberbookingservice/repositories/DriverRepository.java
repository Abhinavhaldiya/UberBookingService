package com.example.uberbookingservice.repositories;

import com.example.uberentityservice.models.Driver;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DriverRepository extends JpaRepository<Driver,Long> {
}
