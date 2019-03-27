package com.kralofsky.citybikes.service;

import com.kralofsky.citybikes.citybikeAPI.ApiException;
import com.kralofsky.citybikes.citybikeAPI.Station;
import com.kralofsky.citybikes.citybikeAPI.StationAPI;
import com.kralofsky.citybikes.entity.Location;
import com.kralofsky.citybikes.entity.StationInfo;
import com.kralofsky.citybikes.util.LocationTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
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
            List<StationInfo> result = stationAPI.getAllStations().stream()
                    .map(station -> stationToStationInfo(station, loc))
                    .sorted(Comparator.comparingDouble(s->s.getDistance()))
                    .limit(3)
                    .collect(Collectors.toList());
            LOGGER.debug(result.toString());
            return result;
        } catch (ApiException e) {
            // TODO: rethrow error
            LOGGER.error(e.toString());
        }
        return null;
    }

    private StationInfo stationToStationInfo(Station station, Location loc) {
        Location stationLoc = new Location(station.getLatitude(), station.getLongitude());
        Double distance = LocationTools.calculateDistance(loc, stationLoc);
        return new StationInfo(station.getName(), station.getFreeBoxes(), station.getFreeBikes(), stationLoc, distance);
    }
}
