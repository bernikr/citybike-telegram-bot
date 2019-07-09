package com.kralofsky.citybikes.service;

import com.kralofsky.citybikes.citybikeAPI.ApiException;
import com.kralofsky.citybikes.citybikeAPI.StationAPI;
import com.kralofsky.citybikes.entity.Location;
import com.kralofsky.citybikes.entity.Station;
import com.kralofsky.citybikes.persistance.Persistance;
import com.kralofsky.citybikes.util.LocationTools;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.abilitybots.api.util.Pair;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DefaultStationService implements StationService {
    private StationAPI stationAPI;
    private Persistance persistance;

    @Autowired
    public DefaultStationService(StationAPI stationAPI, Persistance persistance) {
        this.stationAPI = stationAPI;
        this.persistance = persistance;
    }

    @Override
    public List<Pair<Station, Double>> getNearbyStationInfos(Location loc, int count) {
        log.debug("Get StationInfos around Location " + loc);
        try {
            return stationAPI.getAllStations().stream()
                    .map(s -> Pair.of(s, LocationTools.calculateDistance(loc, s.getLocation())))
                    .sorted(Comparator.comparingDouble(Pair::b))
                    .limit(count)
                    .collect(Collectors.toList());
        } catch (ApiException e) {
            // TODO: rethrow error
            log.error(e.toString());
        }
        return null;
    }

    @Override
    public Optional<Station> getHomeStation(Long chatId) {
        try {
            Integer id = persistance.<Long, Integer>getMap("home_stations").get(chatId);
            if(id != null){
                return Optional.of(stationAPI.getById(id));
            } else {
                return Optional.empty();
            }
        } catch (ApiException e) {
            // TODO: rethrow error
            log.error(e.toString());
        }
        return Optional.empty();
    }

    @Override
    public Optional<Pair<Station, Double>> getHomeStation(Long chatId, Location loc) {
        return getHomeStation(chatId).map(s -> Pair.of(s, LocationTools.calculateDistance(loc, s.getLocation())));
    }

    @Override
    public Pair<Station, Double> setHomeStation(Long chatId, Location loc) {
        Pair<Station, Double> home = getNearbyStationInfos(loc, 1).get(0);
        persistance.<Long, Integer>getMap("home_stations").put(chatId, home.a().getId());
        return home;
    }
}
