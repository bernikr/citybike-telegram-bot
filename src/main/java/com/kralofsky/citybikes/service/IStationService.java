package com.kralofsky.citybikes.service;

import com.kralofsky.citybikes.entity.Location;
import com.kralofsky.citybikes.entity.StationInfo;

import java.util.List;
import java.util.Optional;

public interface IStationService {
    List<StationInfo> getNearbyStationInfos(Location loc, int count);

    Optional<StationInfo> getHomeStation(Long chatId, Location loc);

    Optional<StationInfo> getHomeStation(Long chatId);

    StationInfo setHomeStation(Long chatId, Location loc);
}
