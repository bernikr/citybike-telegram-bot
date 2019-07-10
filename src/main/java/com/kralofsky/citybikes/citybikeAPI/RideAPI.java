package com.kralofsky.citybikes.citybikeAPI;

import com.kralofsky.citybikes.entity.ApiUser;
import com.kralofsky.citybikes.entity.Ride;

import java.util.function.Consumer;
import java.util.stream.Stream;

public interface RideAPI {
    int getRideCount() throws ApiException;

    Stream<Ride> getRides() throws ApiException;

    ApiUser getUser();

    void afterLogin(Consumer<RideAPI> action);

    static RideAPI forUser(ApiUser user) {
        return new DefaultRideAPI(user);
    }
}
