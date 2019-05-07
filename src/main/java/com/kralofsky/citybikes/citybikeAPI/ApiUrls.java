package com.kralofsky.citybikes.citybikeAPI;

import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import java.net.URL;

@Component

public class ApiUrls {
    public ApiUrls() {}

    @SneakyThrows
    public URL getStationApiUrl() {
        return new URL("http://dynamisch.citybikewien.at/citybike_xml.php");
    }
}
