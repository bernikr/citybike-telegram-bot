package com.kralofsky.citybikes.service;

import com.kralofsky.citybikes.citybikeAPI.ApiException;
import com.kralofsky.citybikes.entity.Ride;

import java.util.Optional;

public interface RideService {
    String login(String username, String password, Long chatId) throws ApiException;

    Optional<Ride> getLastRide(Long chatId) throws ApiException;

    Ride getRide(Integer index, Long chatId) throws ApiException;

    int rideCount(Long chatId) throws ApiException;
}
