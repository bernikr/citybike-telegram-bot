package com.kralofsky.citybikes.service;

import com.kralofsky.citybikes.citybikeAPI.ApiException;
import com.kralofsky.citybikes.citybikeAPI.Station;
import com.kralofsky.citybikes.citybikeAPI.StationAPI;
import com.kralofsky.citybikes.entity.Location;
import com.kralofsky.citybikes.entity.StationInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StationService implements IStationService {
    private static final Logger LOGGER = LoggerFactory.getLogger(StationService.class);

    private StationAPI stationAPI;

    @Autowired
    public StationService(StationAPI stationAPI) {
        this.stationAPI = stationAPI;
    }

    @Override
    public List<StationInfo> getNearbyStationInfos(Location loc) {
        LOGGER.debug("Get StationInfos around Location " + loc);
        try {
            // TODO: sort stations by distance
            List<StationInfo> result = stationAPI.getAllStations().stream().limit(3).map(station -> stationToStationInfo(station, loc)).collect(Collectors.toList());
            LOGGER.debug(result.toString());
            return result;
        } catch (ApiException e) {
            // TODO: rethrow error
            LOGGER.error(e.toString());
        }
        return null;
    }

    private StationInfo stationToStationInfo(Station station, Location loc) {
        Integer distance = null; // TODO: add distance calculation
        return new StationInfo(station.getName(), station.getFree_boxes(), station.getFree_bikes(), distance);
    }
}
