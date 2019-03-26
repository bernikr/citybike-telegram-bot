package com.kralofsky.citybikes.citybikeAPI;

import java.util.Objects;

public class Station {
    Integer id;
    Integer internal_id;
    String name;
    Integer boxes;
    Integer free_boxes;
    Integer free_bikes;
    String status;
    String description;
    Double latitude;
    Double longitude;

    public Station(Integer id, Integer internal_id, String name, Integer boxes, Integer free_boxes, Integer free_bikes, String status, String description, Double latitude, Double longitude) {
        this.id = id;
        this.internal_id = internal_id;
        this.name = name;
        this.boxes = boxes;
        this.free_boxes = free_boxes;
        this.free_bikes = free_bikes;
        this.status = status;
        this.description = description;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Integer getId() {
        return id;
    }

    public Integer getInternal_id() {
        return internal_id;
    }

    public String getName() {
        return name;
    }

    public Integer getBoxes() {
        return boxes;
    }

    public Integer getFree_boxes() {
        return free_boxes;
    }

    public Integer getFree_bikes() {
        return free_bikes;
    }

    public String getStatus() {
        return status;
    }

    public String getDescription() {
        return description;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Station station = (Station) o;
        return Objects.equals(id, station.id) &&
                Objects.equals(internal_id, station.internal_id) &&
                Objects.equals(name, station.name) &&
                Objects.equals(boxes, station.boxes) &&
                Objects.equals(free_boxes, station.free_boxes) &&
                Objects.equals(free_bikes, station.free_bikes) &&
                Objects.equals(status, station.status) &&
                Objects.equals(description, station.description) &&
                Objects.equals(latitude, station.latitude) &&
                Objects.equals(longitude, station.longitude);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, internal_id, name, boxes, free_boxes, free_bikes, status, description, latitude, longitude);
    }

    @Override
    public String toString() {
        return "Station{" +
                "id=" + id +
                ", internal_id=" + internal_id +
                ", name='" + name + '\'' +
                ", boxes=" + boxes +
                ", free_boxes=" + free_boxes +
                ", free_bikes=" + free_bikes +
                ", status='" + status + '\'' +
                ", description='" + description + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }
}
