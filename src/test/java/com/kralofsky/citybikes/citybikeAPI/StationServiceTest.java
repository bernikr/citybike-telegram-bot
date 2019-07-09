package com.kralofsky.citybikes.citybikeAPI;

import com.kralofsky.citybikes.entity.Location;
import com.kralofsky.citybikes.entity.Station;
import com.kralofsky.citybikes.persistance.Persistance;
import com.kralofsky.citybikes.service.StationService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.telegram.abilitybots.api.util.Pair;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class StationServiceTest {
    @Mock
    private StationAPI stationAPI;

    @Mock
    private Persistance persistance;

    @InjectMocks
    private StationService stationService;

    private Station STATION_1 = new Station(1, 2, "name", 3,4,5,
            Station.Status.ACTIVE, "desc", new Location(48.19987078817638,16.36822705522536));

    @Test
    public void givenOneStation_whenGetNearbyStationInfo_returnThatStationWithCorrectDistance() throws ApiException {
        when(stationAPI.getAllStations()).thenReturn(List.of(STATION_1));
        List<Pair<Station, Double>> result = stationService.getNearbyStationInfos(
                new Location(48.19979476928711, 16.368078231811523), 3);
        assertEquals(1, result.size());
        Pair<Station, Double> sp = result.get(0);
        assertEquals(STATION_1, sp.a());
        assertEquals(13.896, sp.b(), 0.001);
    }
}
