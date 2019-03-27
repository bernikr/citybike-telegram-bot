package com.kralofsky.citybikes.entity;

import java.util.Objects;
import java.util.Optional;

public class StationInfo {
    private String name;
    private Integer free_boxes;
    private Integer free_bikes;
    private Location location;
    private Double distance;

    public StationInfo(String name, Integer free_boxes, Integer free_bikes, Location location, Double distance) {
        this.name = name;
        this.free_boxes = free_boxes;
        this.free_bikes = free_bikes;
        this.location = location;
        this.distance = distance;
    }

    public StationInfo(String name, Integer free_boxes, Integer free_bikes, Location location) {
        this.name = name;
        this.free_boxes = free_boxes;
        this.free_bikes = free_bikes;
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getFree_boxes() {
        return free_boxes;
    }

    public void setFree_boxes(Integer free_boxes) {
        this.free_boxes = free_boxes;
    }

    public Integer getFree_bikes() {
        return free_bikes;
    }

    public void setFree_bikes(Integer free_bikes) {
        this.free_bikes = free_bikes;
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
        return Objects.equals(name, that.name) &&
                Objects.equals(free_boxes, that.free_boxes) &&
                Objects.equals(free_bikes, that.free_bikes) &&
                Objects.equals(location, that.location) &&
                Objects.equals(distance, that.distance);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, free_boxes, free_bikes, location, distance);
    }

    @Override
    public String toString() {
        return "StationInfo{" +
                "name='" + name + '\'' +
                ", free_boxes=" + free_boxes +
                ", free_bikes=" + free_bikes +
                ", location=" + location +
                ", distance=" + distance +
                '}';
    }
}
