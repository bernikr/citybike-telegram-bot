package com.kralofsky.citybikes.citybikeAPI;

import com.kralofsky.citybikes.citybikeAPI.util.ApiUrls;
import com.kralofsky.citybikes.entity.Station;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.net.URL;
import java.util.Collection;
import java.util.Optional;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class BasicStationApiTest {
    @Mock
    private ApiUrls apiUrls;

    @InjectMocks
    private BasicStationAPI api;

    private URL getTestdataUrl(String filename) {
        return getClass().getClassLoader().getResource("StationAPI/" + filename + ".xml");
    }

    @Test
    public void givenValidData_whenGetAllStations_allStationsAreNotNull() throws ApiException {
        Mockito.when(apiUrls.getStationApiUrl()).thenReturn(getTestdataUrl("valid1"));
        Collection<Station> stations = api.getAllStations();
        assertNotNull(stations);
        stations.forEach(Assert::assertNotNull);
    }

    @Test
    public void givenValidDataWith3Stations_whenGetAllStations_ListContains3Stations() throws ApiException {
        Mockito.when(apiUrls.getStationApiUrl()).thenReturn(getTestdataUrl("valid1"));
        Collection<Station> stations = api.getAllStations();
        assertEquals(3, stations.size());
    }

    @Test
    public void givenEmptyData_whenGetAllStations_returnEmptyList() throws ApiException {
        Mockito.when(apiUrls.getStationApiUrl()).thenReturn(getTestdataUrl("empty"));
        Collection<Station> stations = api.getAllStations();
        assertTrue(stations.isEmpty());
    }

    @Test
    public void givenSingleStation_whenGetAllStations_returnThatStation() throws ApiException {
        Mockito.when(apiUrls.getStationApiUrl()).thenReturn(getTestdataUrl("validSingle"));
        Collection<Station> stations = api.getAllStations();
        assertEquals(1, stations.size());
        Optional<Station> os = stations.stream().findAny();
        assertTrue(os.isPresent());
        Station s = os.get();
        assertEquals(108, s.getId().intValue());
        assertEquals(1026, s.getInternalId().intValue());
        assertEquals("Friedrich Schmidtplatz", s.getName());
        assertEquals(35, s.getBoxes().intValue());
        assertEquals(30, s.getFreeBoxes().intValue());
        assertEquals(5, s.getFreeBikes().intValue());
        assertEquals(Station.Status.ACTIVE, s.getStatus());
        assertEquals("Ecke Lichtenfelsgasse U2 Station Rathaus", s.getDescription());
        assertEquals(48.210425, s.getLocation().getLatitude(), 0.00000000001);
        assertEquals(16.356100, s.getLocation().getLongitude(), 0.00000000001);
    }

    @Test
    public void givenSingleStationWithMoreLinebreaks_whenGetAllStations_returnThatStation() throws ApiException {
        Mockito.when(apiUrls.getStationApiUrl()).thenReturn(getTestdataUrl("validLinebreaks"));
        Collection<Station> stations = api.getAllStations();
        assertEquals(1, stations.size());
        Optional<Station> os = stations.stream().findAny();
        assertTrue(os.isPresent());
        Station s = os.get();
        assertEquals(108, s.getId().intValue());
        assertEquals(1026, s.getInternalId().intValue());
        assertEquals("Friedrich Schmidtplatz", s.getName());
        assertEquals(35, s.getBoxes().intValue());
        assertEquals(30, s.getFreeBoxes().intValue());
        assertEquals(5, s.getFreeBikes().intValue());
        assertEquals(Station.Status.ACTIVE, s.getStatus());
        assertEquals("Ecke Lichtenfelsgasse\nU2 Station Rathaus", s.getDescription());
        assertEquals(48.210425, s.getLocation().getLatitude(), 0.00000000001);
        assertEquals(16.356100, s.getLocation().getLongitude(), 0.00000000001);
    }

    @Test(expected = ApiException.class)
    public void givenMissingField_whenGetAllStations_throwApiException() throws ApiException {
        Mockito.when(apiUrls.getStationApiUrl()).thenReturn(getTestdataUrl("missingField"));
        api.getAllStations();
    }

    @Test(expected = ApiException.class)
    public void givenDuplicateField_whenGetAllStations_throwApiException() throws ApiException {
        Mockito.when(apiUrls.getStationApiUrl()).thenReturn(getTestdataUrl("duplicateField"));
        api.getAllStations();
    }

    @Test(expected = ApiException.class)
    public void givenInvalidXml_whenGetAllStations_throwApiException() throws ApiException {
        Mockito.when(apiUrls.getStationApiUrl()).thenReturn(getTestdataUrl("invalidXml"));
        api.getAllStations();
    }

    @Test(expected = ApiException.class)
    public void givenInvalidStatus_whenGetAllStations_throwApiException() throws ApiException {
        Mockito.when(apiUrls.getStationApiUrl()).thenReturn(getTestdataUrl("invalidStatus"));
        api.getAllStations();
    }
}
