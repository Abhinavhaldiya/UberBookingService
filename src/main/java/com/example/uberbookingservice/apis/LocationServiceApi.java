package com.example.uberbookingservice.apis;

import com.example.uberbookingservice.dtos.DriverLocationDto;
import com.example.uberbookingservice.dtos.NearbyDriversRequestDto;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface LocationServiceApi {
    @GET("/api/location/nearby/drivers")
    Call<DriverLocationDto[]> getNearbyDrivers(@Query("latitude") Double latitude, @Query("longitude") Double longitude);
}
