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
            return stationAPI.getAllStations().stream()
                    .map(station -> stationToStationInfo(station, loc))
                    .sorted(Comparator.comparingDouble(StationInfo::getDistance))
                    .limit(3)
                    .collect(Collectors.toList());
        } catch (ApiException e) {
            // TODO: rethrow error
            LOGGER.error(e.toString());
        }
        return null;
    }

    @Override
    public StationInfo getHomeStation(Long chatId, Location loc) {
        try {
            // TODO: return the correct station
            return stationAPI.getAllStations().stream()
                    .findAny()
                    .map(station -> stationToStationInfo(station, loc))
                    .get();
        } catch (ApiException e) {
            // TODO: rethrow error
            LOGGER.error(e.toString());
        }
        return null;
    }

    @Override
    public StationInfo getHomeStation(Long chatId) {
        return getHomeStation(chatId, null);
    }

    private StationInfo stationToStationInfo(Station station, Location loc) {
        Location stationLoc = null;
        Double distance = null;
        if (loc != null) {
            stationLoc = new Location(station.getLatitude(), station.getLongitude());
            distance = LocationTools.calculateDistance(loc, stationLoc);
        }
        return new StationInfo(station.getName(), station.getFreeBoxes(), station.getFreeBikes(), stationLoc, distance);
    }
}
