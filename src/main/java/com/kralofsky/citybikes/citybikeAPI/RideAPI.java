package com.kralofsky.citybikes.citybikeAPI;

import com.kralofsky.citybikes.entity.Ride;

import java.util.stream.Stream;

public interface RideAPI {
    int getRideCount() throws ApiException;

    Stream<Ride> getRides() throws ApiException;

    static RideAPI forUser(String username, String password) {
        return new DefaultRideAPI(username, password);
    }
}
