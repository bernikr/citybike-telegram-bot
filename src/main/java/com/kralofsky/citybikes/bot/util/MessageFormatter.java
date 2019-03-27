package com.kralofsky.citybikes.bot.util;

import com.kralofsky.citybikes.entity.StationInfo;
import org.stringtemplate.v4.ST;

import java.util.List;

public class MessageFormatter {
    public static String getStationInfoMessage(List<StationInfo> stationInfos){
        return stationInfos.stream()
                .map(MessageFormatter::getStationInfoMessage)
                .reduce("", (a, b)->a+"\n\n"+b);
    }

    public static String getStationInfoMessage(StationInfo stationInfo){
        String d;
        if (stationInfo.getDistance() == null) d=null;
        else if (stationInfo.getDistance() < 1000) d=String.format("%.0fm", stationInfo.getDistance());
        else d=String.format("%.1fkm", stationInfo.getDistance()/1000);

        ST msg = new ST(
                "[<name>](http://maps.google.com/maps?q=<lat>,<lon>)" +
                "<if(distance_exists)> (<distance>)<endif>\n" +
                "<free_bikes_emoji> Bikes (*<free_bikes>*)\n" +
                "<free_boxes_emoji> Slots (*<free_boxes>*)"
        );
        
        msg.add("name", stationInfo.getName());
        msg.add("lat", stationInfo.getLocation().getLatitude());
        msg.add("lon", stationInfo.getLocation().getLongitude());
        msg.add("distance_exists", stationInfo.getDistance()!=null);
        msg.add("distance", d);
        msg.add("free_bikes", stationInfo.getFreeBikes());
        msg.add("free_boxes", stationInfo.getFreeBoxes());
        msg.add("free_bikes_emoji", countToEmoji(stationInfo.getFreeBikes()));
        msg.add("free_boxes_emoji", countToEmoji(stationInfo.getFreeBoxes()));

        return msg.render();
    }

    private static String countToEmoji(Integer num) {
        if (num <= 0) return "❌";
        else if (num <= 5) return "⚠️";
        else return "✅";
    }
}
