package com.kralofsky.citybikes;

import com.kralofsky.citybikes.citybikeAPI.ApiException;
import com.kralofsky.citybikes.citybikeAPI.StationAPI;

public class mian {
    public static void main(String[] args) {
        try {
            StationAPI.getAllStations().forEach(System.out::println);
        } catch (ApiException e) {
            e.printStackTrace();
        }
    }
}
