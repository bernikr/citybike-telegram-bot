package com.kralofsky.citybikes;

import com.kralofsky.citybikes.citybikeAPI.ApiException;
import com.kralofsky.citybikes.citybikeAPI.StationAPI;

public class main {
    public static void main(String[] args) {
        StationAPI stationAPI = new StationAPI();
        try {
            stationAPI.getAllStations().forEach(System.out::println);
        } catch (ApiException e) {
            e.printStackTrace();
        }
    }
}
