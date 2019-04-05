package com.kralofsky.citybikes.entity;

import java.util.Objects;

public class Station {
    private Integer id;
    private Integer internalId;
    private String name;
    private Integer boxes;
    private Integer freeBoxes;
    private Integer freeBikes;
    private Status status;
    private String description;
    private Location location;

    public Station(Integer id, Integer internalId, String name, Integer boxes, Integer freeBoxes, Integer freeBikes, Status status, String description, Location location) {
        this.id = id;
        this.internalId = internalId;
        this.name = name;
        this.boxes = boxes;
        this.freeBoxes = freeBoxes;
        this.freeBikes = freeBikes;
        this.status = status;
        this.description = description;
        this.location = location;
    }

    public Integer getId() {
        return id;
    }

    public Integer getInternalId() {
        return internalId;
    }

    public String getName() {
        return name;
    }

    public Integer getBoxes() {
        return boxes;
    }

    public Integer getFreeBoxes() {
        return freeBoxes;
    }

    public Integer getFreeBikes() {
        return freeBikes;
    }

    public Status getStatus() {
        return status;
    }

    public String getDescription() {
        return description;
    }

    public Location getLocation() {
        return location;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Station station = (Station) o;
        return Objects.equals(id, station.id) &&
                Objects.equals(internalId, station.internalId) &&
                Objects.equals(name, station.name) &&
                Objects.equals(boxes, station.boxes) &&
                Objects.equals(freeBoxes, station.freeBoxes) &&
                Objects.equals(freeBikes, station.freeBikes) &&
                status == station.status &&
                Objects.equals(description, station.description) &&
                Objects.equals(location, station.location);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, internalId, name, boxes, freeBoxes, freeBikes, status, description, location);
    }

    @Override
    public String toString() {
        return "Station{" +
                "id=" + id +
                ", internalId=" + internalId +
                ", name='" + name + '\'' +
                ", boxes=" + boxes +
                ", freeBoxes=" + freeBoxes +
                ", freeBikes=" + freeBikes +
                ", status=" + status +
                ", description='" + description + '\'' +
                ", location=" + location +
                '}';
    }

    public enum Status {
        ACTIVE, INOPERATIVE, UNDER_CONSTRUCTION;
    }
}
