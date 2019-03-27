package com.kralofsky.citybikes.unit;

import com.kralofsky.citybikes.citybikeAPI.ApiException;
import com.kralofsky.citybikes.citybikeAPI.Station;
import com.kralofsky.citybikes.citybikeAPI.StationAPI;
import org.junit.Assert;
import org.junit.Test;

import java.util.Collection;

import static org.junit.Assert.*;

public class StationApiTest {

    private StationAPI getStationAPIwithData(String filename) {
        return new StationAPI(getClass().getClassLoader().getResource("StationAPI/" + filename + ".xml"));
    }

    @Test
    public void givenValidData_whenGetAllStations_allStationsAreNotNull() throws ApiException {
        StationAPI api = getStationAPIwithData("valid1");
        Collection<Station> stations = api.getAllStations();
        assertNotNull(stations);
        stations.forEach(Assert::assertNotNull);
    }

    @Test
    public void givenValidDataWith3Stations_whenGetAllStations_ListContains3Stations() throws ApiException {
        StationAPI api = getStationAPIwithData("valid1");
        Collection<Station> stations = api.getAllStations();
        assertEquals(3, stations.size());
    }

    @Test
    public void givenEmptyData_whenGetAllStations_returnEmptyList() throws ApiException {
        StationAPI api = getStationAPIwithData("empty");
        Collection<Station> stations = api.getAllStations();
        assertTrue(stations.isEmpty());
    }

    @Test
    public void givenSingleStation_whenGetAllStations_returnThatStation() throws ApiException {
        StationAPI api = getStationAPIwithData("validSingle");
        Collection<Station> stations = api.getAllStations();
        assertEquals(1, stations.size());
        Station s = stations.stream().findAny().get();
        assertEquals(108, s.getId().intValue());
        assertEquals(1026, s.getInternalId().intValue());
        assertEquals("Friedrich Schmidtplatz", s.getName());
        assertEquals(35, s.getBoxes().intValue());
        assertEquals(30, s.getFreeBoxes().intValue());
        assertEquals(5, s.getFreeBikes().intValue());
        assertEquals(Station.Status.ACTIVE, s.getStatus());
        assertEquals("Ecke Lichtenfelsgasse U2 Station Rathaus", s.getDescription());
        assertEquals(48.210425, s.getLatitude(), 0.00000000001);
        assertEquals(16.356100, s.getLongitude(), 0.00000000001);
    }

    @Test
    public void givenSingleStationWithMoreLinebreaks_whenGetAllStations_returnThatStation() throws ApiException {
        StationAPI api = getStationAPIwithData("validLinebreaks");
        Collection<Station> stations = api.getAllStations();
        assertEquals(1, stations.size());
        Station s = stations.stream().findAny().get();
        assertEquals(108, s.getId().intValue());
        assertEquals(1026, s.getInternalId().intValue());
        assertEquals("Friedrich Schmidtplatz", s.getName());
        assertEquals(35, s.getBoxes().intValue());
        assertEquals(30, s.getFreeBoxes().intValue());
        assertEquals(5, s.getFreeBikes().intValue());
        assertEquals(Station.Status.ACTIVE, s.getStatus());
        assertEquals("Ecke Lichtenfelsgasse\nU2 Station Rathaus", s.getDescription());
        assertEquals(48.210425, s.getLatitude(), 0.00000000001);
        assertEquals(16.356100, s.getLongitude(), 0.00000000001);
    }

    @Test(expected = ApiException.class)
    public void givenMissingField_whenGetAllStations_throwApiException() throws ApiException {
        StationAPI api = getStationAPIwithData("missingField");
        Collection<Station> stations = api.getAllStations();
    }

    @Test(expected = ApiException.class)
    public void givenDuplicateField_whenGetAllStations_throwApiException() throws ApiException {
        StationAPI api = getStationAPIwithData("duplicateField");
        Collection<Station> stations = api.getAllStations();
    }

    @Test(expected = ApiException.class)
    public void givenInvalidXml_whenGetAllStations_throwApiException() throws ApiException {
        StationAPI api = getStationAPIwithData("invalidXml");
        Collection<Station> stations = api.getAllStations();
    }

    @Test(expected = ApiException.class)
    public void givenInvalidStatus_whenGetAllStations_throwApiException() throws ApiException {
        StationAPI api = getStationAPIwithData("invalidStatus");
        Collection<Station> stations = api.getAllStations();
    }
}
