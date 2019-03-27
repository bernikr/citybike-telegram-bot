package com.kralofsky.citybikes.bot.util;

import com.kralofsky.citybikes.entity.StationInfo;

import java.util.List;

public class MessageFormatter {
    public static String getStationInfoMessage(List<StationInfo> stationInfos){
        return stationInfos.stream()
                .map(MessageFormatter::getStationInfoMessage)
                .reduce("", (a, b)->a+"\n\n"+b);
    }

    public static String getStationInfoMessage(StationInfo stationInfo){
        return stationInfo.toString();
    }
}
