package com.kralofsky.citybikes.util;

import com.kralofsky.citybikes.entity.Location;

public class LocationTools {
    private final static double AVERAGE_RADIUS_OF_EARTH = 6371000;

    public static double calculateDistance(Location loc1, Location loc2) {

        double latDistance = Math.toRadians(loc1.getLatitude() - loc2.getLatitude());
        double lngDistance = Math.toRadians(loc1.getLongitude() - loc2.getLongitude());

        double a = (Math.sin(latDistance / 2) * Math.sin(latDistance / 2)) +
                        (Math.cos(Math.toRadians(loc1.getLatitude()))) *
                        (Math.cos(Math.toRadians(loc2.getLatitude()))) *
                        (Math.sin(lngDistance / 2)) *
                        (Math.sin(lngDistance / 2));

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return c * AVERAGE_RADIUS_OF_EARTH;
    }
}
