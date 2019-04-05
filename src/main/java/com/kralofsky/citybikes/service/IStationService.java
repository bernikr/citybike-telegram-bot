package com.kralofsky.citybikes.service;

import com.kralofsky.citybikes.entity.Station;
import com.kralofsky.citybikes.entity.Location;
import org.telegram.abilitybots.api.util.Pair;

import java.util.List;
import java.util.Optional;

public interface IStationService {
    List<Pair<Station, Double>> getNearbyStationInfos(Location loc, int count);

    Optional<Pair<Station, Double>> getHomeStation(Long chatId, Location loc);

    Optional<Station> getHomeStation(Long chatId);

    Pair<Station, Double> setHomeStation(Long chatId, Location loc);
}
