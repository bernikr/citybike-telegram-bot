package com.kralofsky.citybikes.bot.util;

import com.kralofsky.citybikes.entity.Ride;
import com.kralofsky.citybikes.entity.Station;
import org.stringtemplate.v4.ST;
import org.telegram.abilitybots.api.util.Pair;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class MessageFormatter {
    public static String stationToText(List<Pair<Station, Double>> StationDistancePairs){
        return StationDistancePairs.stream()
                .map(MessageFormatter::stationToText)
                .reduce("", (a, b)->a+"\n\n"+b);
    }

    public static String stationToText(Pair<Station, Double> StationDistancePair) {
        return stationToText(StationDistancePair.a(), StationDistancePair.b());
    }

    public static String stationToText(Station station) {
        return stationToText(station, null);
    }

    public static String stationToText(Station stationInfo, Double distance){
        String d;
        if (distance == null) d=null;
        else if (distance < 1000) d=String.format("%.0fm", distance);
        else d=String.format("%.1fkm", distance/1000);

        ST msg = new ST(
                "[<name>](http://maps.google.com/maps?q=<lat>,<lon>)" +
                "<if(distance_exists)> (<distance>)<endif>\n" +
                "<free_bikes_emoji> Bikes (*<free_bikes>*)\n" +
                "<free_boxes_emoji> Slots (*<free_boxes>*)"
        );
        
        msg.add("name", stationInfo.getName());
        msg.add("lat", stationInfo.getLocation().getLatitude());
        msg.add("lon", stationInfo.getLocation().getLongitude());
        msg.add("distance_exists", distance!=null);
        msg.add("distance", d);
        msg.add("free_bikes", stationInfo.getFreeBikes());
        msg.add("free_boxes", stationInfo.getFreeBoxes());
        msg.add("free_bikes_emoji", countToEmoji(stationInfo.getFreeBikes()));
        msg.add("free_boxes_emoji", countToEmoji(stationInfo.getFreeBoxes()));

        return msg.render();
    }

    public static String rideToText(Ride r){
        ST msg = new ST(
                "<date> <time_from>-<time_to>\n" +
                        "From: <station_from>\n" +
                        "To: <station_to>\n" +
                        "<cost> <elevation>"
        );

        msg.add("date", r.getDate().format(DateTimeFormatter.ofPattern("dd. MM. YYYY")));
        msg.add("time_from", r.getStartTime().format(DateTimeFormatter.ofPattern("HH:mm")));
        msg.add("time_to", r.getEndTime().format(DateTimeFormatter.ofPattern("HH:mm")));
        msg.add("station_from", r.getStartStation());
        msg.add("station_to", r.getEndStation());
        msg.add("cost", String.format("€%.2f", r.getPrice()));
        msg.add("elevation", String.format("%dm", r.getElevation()));

        return msg.render();
    }

    private static String countToEmoji(Integer num) {
        if (num <= 0) return "❌";
        else if (num <= 5) return "⚠️";
        else return "✅";
    }
}
