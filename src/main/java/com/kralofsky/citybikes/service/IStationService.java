package com.kralofsky.citybikes.service;

import com.kralofsky.citybikes.entity.Location;
import com.kralofsky.citybikes.entity.StationInfo;

import java.util.List;

public interface IStationService {
    List<StationInfo> getNearbyStationInfos(Location loc);
}
