package com.kralofsky.citybikes.service;

import com.kralofsky.citybikes.citybikeAPI.ApiException;
import com.kralofsky.citybikes.citybikeAPI.Station;
import com.kralofsky.citybikes.citybikeAPI.StationAPI;
import com.kralofsky.citybikes.entity.Location;
import com.kralofsky.citybikes.entity.StationInfo;
import com.kralofsky.citybikes.persistance.Persistance;
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
    private Persistance persistance;

    @Autowired
    public StationService(StationAPI stationAPI, Persistance persistance) {
        this.stationAPI = stationAPI;
        this.persistance = persistance;
    }

    @Override
    public List<StationInfo> getNearbyStationInfos(Location loc, int count) {
        LOGGER.debug("Get StationInfos around Location " + loc);
        try {
            return stationAPI.getAllStations().stream()
                    .map(station -> stationToStationInfo(station, loc))
                    .sorted(Comparator.comparingDouble(StationInfo::getDistance))
                    .limit(count)
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
            Integer id = persistance.<Long, Integer>getMap("home_stations").get(chatId);
            if(id != null){
                return stationToStationInfo(stationAPI.getById(id), loc);
            } else {
                return null;
            }
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

    @Override
    public StationInfo setHomeStation(Long chatId, Location loc) {
        StationInfo home = getNearbyStationInfos(loc, 1).get(0);
        persistance.<Long, Integer>getMap("home_stations").put(chatId, home.getId());
        return home;
    }

    private static StationInfo stationToStationInfo(Station station, Location loc) {
        Location stationLoc = null;
        Double distance = null;
        if (loc != null) {
            stationLoc = new Location(station.getLatitude(), station.getLongitude());
            distance = LocationTools.calculateDistance(loc, stationLoc);
        }
        return new StationInfo(station.getId(), station.getName(), station.getFreeBoxes(), station.getFreeBikes(), stationLoc, distance);
    }
}
