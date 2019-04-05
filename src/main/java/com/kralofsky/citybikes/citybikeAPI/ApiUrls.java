package com.kralofsky.citybikes.citybikeAPI;

import org.springframework.stereotype.Component;

import java.net.MalformedURLException;
import java.net.URL;

@Component
public class ApiUrls {
    public ApiUrls() {}

    public URL getStationApiUrl() {
        try {
            return new URL("http://dynamisch.citybikewien.at/citybike_xml.php");
        } catch (MalformedURLException e) {
            // TODO better error handling
            e.printStackTrace();
        }
        return null;
    }
}
