package com.kralofsky.citybikes.entity;

import java.util.Objects;
import java.util.Optional;

public class StationInfo {
    private String name;
    private Integer free_boxes;
    private Integer free_bikes;
    private Integer distance;

    public StationInfo(String name, Integer free_boxes, Integer free_bikes, Integer distance) {
        this.name = name;
        this.free_boxes = free_boxes;
        this.free_bikes = free_bikes;
        this.distance = distance;
    }

    public StationInfo(String name, Integer free_boxes, Integer free_bikes) {
        this.name = name;
        this.free_boxes = free_boxes;
        this.free_bikes = free_bikes;
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

    public Optional<Integer> getDistance() {
        if(distance==null) return Optional.empty();
        else return Optional.of(distance);
    }

    public void setDistance(Integer distance) {
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
                Objects.equals(distance, that.distance);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, free_boxes, free_bikes, distance);
    }

    @Override
    public String toString() {
        return "StationInfo{" +
                "name='" + name + '\'' +
                ", free_boxes=" + free_boxes +
                ", free_bikes=" + free_bikes +
                ", distance=" + distance +
                '}';
    }
}
