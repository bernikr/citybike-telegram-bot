package com.kralofsky.citybikes.citybikeAPI;

import com.kralofsky.citybikes.config.Values;
import com.kralofsky.citybikes.entity.Station;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
@Primary
public class CachedStationAPI implements StationAPI {
    private StationAPI impl;
    private long lastCallMillis = 0;
    private final int CACHE_TIME;
    private Collection<Station> cache = null;

    @Autowired
    public CachedStationAPI(StationAPI stationApiImplementation, Values values) {
        this(stationApiImplementation, values.getCacheDuration());
    }

    public CachedStationAPI(StationAPI stationApiImplementation, int cacheDuration) {
        impl = stationApiImplementation;
        CACHE_TIME = cacheDuration;
    }

    @Override
    public Collection<Station> getAllStations() throws ApiException {
        if(cache == null || lastCallMillis + CACHE_TIME < System.currentTimeMillis()) {
            cache = impl.getAllStations();
            lastCallMillis = System.currentTimeMillis();
        }
        return cache;
    }
}
