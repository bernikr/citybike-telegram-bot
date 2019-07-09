package com.kralofsky.citybikes.service;

import com.kralofsky.citybikes.citybikeAPI.ApiException;
import com.kralofsky.citybikes.citybikeAPI.RideAPI;
import com.kralofsky.citybikes.entity.ApiUser;
import com.kralofsky.citybikes.entity.Ride;
import com.kralofsky.citybikes.persistance.Persistance;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class DefaultRideService implements RideService {
    private Persistance persistance;

    public DefaultRideService(Persistance persistance) {
        this.persistance = persistance;
    }

    @Override
    public String login(String username, String password, Long chatId) throws ApiException {
        ApiUser user = new ApiUser(username, password);
        RideAPI rideAPI = RideAPI.forUser(user);
        rideAPI.getRideCount();

        persistance.<Long, ApiUser>getMap("api_users").put(chatId, user);

        return user.getFullName();
    }

    @Override
    public Optional<Ride> getLastRide(Long chatId) throws ApiException {
        ApiUser user = persistance.<Long, ApiUser>getMap("api_users").get(chatId);

        if (user == null) {
            throw new ApiException("You must be logged in to do that.");
        }

        RideAPI rideAPI = RideAPI.forUser(user);

        return rideAPI.getRides().findFirst();
    }
}
