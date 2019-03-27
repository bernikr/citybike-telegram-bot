package com.kralofsky.citybikes.entity;

import java.util.Objects;

public class StationInfo {
    private Integer id;
    private String name;
    private Integer freeBoxes;
    private Integer freeBikes;
    private Location location;
    private Double distance;

    public StationInfo(Integer id, String name, Integer freeBoxes, Integer freeBikes, Location location, Double distance) {
        this.id = id;
        this.name = name;
        this.freeBoxes = freeBoxes;
        this.freeBikes = freeBikes;
        this.location = location;
        this.distance = distance;
    }

    public StationInfo(Integer id, String name, Integer freeBoxes, Integer freeBikes, Location location) {
        this.id = id;
        this.name = name;
        this.freeBoxes = freeBoxes;
        this.freeBikes = freeBikes;
        this.location = location;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getFreeBoxes() {
        return freeBoxes;
    }

    public void setFreeBoxes(Integer freeBoxes) {
        this.freeBoxes = freeBoxes;
    }

    public Integer getFreeBikes() {
        return freeBikes;
    }

    public void setFreeBikes(Integer freeBikes) {
        this.freeBikes = freeBikes;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StationInfo that = (StationInfo) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(name, that.name) &&
                Objects.equals(freeBoxes, that.freeBoxes) &&
                Objects.equals(freeBikes, that.freeBikes) &&
                Objects.equals(location, that.location) &&
                Objects.equals(distance, that.distance);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, freeBoxes, freeBikes, location, distance);
    }

    @Override
    public String toString() {
        return "StationInfo{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", freeBoxes=" + freeBoxes +
                ", freeBikes=" + freeBikes +
                ", location=" + location +
                ", distance=" + distance +
                '}';
    }
}
