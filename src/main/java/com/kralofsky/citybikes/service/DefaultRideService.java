package com.kralofsky.citybikes.service;

import com.kralofsky.citybikes.citybikeAPI.ApiException;
import com.kralofsky.citybikes.citybikeAPI.RideAPI;
import com.kralofsky.citybikes.entity.ApiUser;
import com.kralofsky.citybikes.entity.Ride;
import com.kralofsky.citybikes.persistance.Persistance;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DefaultRideService implements RideService {
    private Persistance persistance;

    private Map<Long, RideAPI> rideApis = new HashMap<>();
    private Map<Long, List<Ride>> cachedRides = new HashMap<>();

    public DefaultRideService(Persistance persistance) {
        this.persistance = persistance;
    }

    @Override
    public String login(String username, String password, Long chatId) throws ApiException {
        logout(chatId);
        persistance.<Long, ApiUser>getMap("api_users").put(chatId, new ApiUser(username, password));
        RideAPI rideAPI = getRideAPI(chatId);
        rideAPI.afterLogin((r) -> persistance.<Long, ApiUser>getMap("api_users").put(chatId, r.getUser()));
        rideAPI.getRideCount();

        new Thread(() -> {
            try {
                loadRides(chatId);
            } catch (ApiException e) {
                log.error("error while loading rides in background", e);
            }
        }).start();

        return rideAPI.getUser().getFullName();
    }

    public void logout(Long chatId){
        persistance.<Long, ApiUser>getMap("api_users").remove(chatId);
        rideApis.remove(chatId);
        cachedRides.remove(chatId);
    }

    @Override
    public Optional<Ride> getLastRide(Long chatId) throws ApiException {
        loadRides(chatId);
        List<Ride> rides = cachedRides.get(chatId);
        if (rides.isEmpty()) return Optional.empty();
        return Optional.of(rides.get(rides.size()-1));
    }

    @Override
    public Ride getRide(Integer index, Long chatId) throws ApiException {
        loadRides(chatId);
        List<Ride> rides = cachedRides.get(chatId);
        return rides.get(index-1);
    }

    @Override
    public int rideCount(Long chatId) throws ApiException {
        loadRides(chatId);
        return cachedRides.get(chatId).size();
    }

    private RideAPI getRideAPI(Long chatId) throws ApiException {
        if (!rideApis.containsKey(chatId)) {
            ApiUser user = persistance.<Long, ApiUser>getMap("api_users").get(chatId);

            if (user == null) {
                throw new ApiException("You must be logged in to do that.");
            }

            rideApis.put(chatId, RideAPI.forUser(user));
        }
        return rideApis.get(chatId);
    }

    private void loadRides(Long chatId) throws ApiException {
        if (!cachedRides.containsKey(chatId)) {
            List<Ride> rides = getRideAPI(chatId).getRides().collect(Collectors.toList());
            Collections.reverse(rides);
            cachedRides.put(chatId, rides);
        } else {
            List<Ride> rides = cachedRides.get(chatId);
            if (rides.size() == getRideAPI(chatId).getRideCount()) return;
            List<Ride> newRides = getRideAPI(chatId).getRides().takeWhile(r -> !rides.contains(r)).collect(Collectors.toList());
            Collections.reverse(newRides);
            rides.addAll(newRides);
        }
    }
}
