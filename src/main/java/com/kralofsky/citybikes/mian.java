package com.kralofsky.citybikes;

import com.kralofsky.citybikes.citybikeAPI.StationAPI;

public class mian {
    public static void main(String[] args) {
        StationAPI.getAllStations().forEach(System.out::println);
    }
}
