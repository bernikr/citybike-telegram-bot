package com.kralofsky.citybikes.citybikeAPI;

import com.kralofsky.citybikes.entity.Station;

import java.util.Collection;

public interface StationAPI {
    Collection<Station> getAllStations() throws ApiException;

    default Station getById(Integer id) throws ApiException {
        return getAllStations().stream()
                .filter(s -> s.getId().equals(id))
                .findAny()
                .orElseThrow(() -> new ApiException("No Station with id " + id + " found"));
    }
}
