package com.kralofsky.citybikes.entity;

import lombok.Value;

@Value
public class Station {
    Integer id;
    Integer internalId;
    String name;
    Integer boxes;
    Integer freeBoxes;
    Integer freeBikes;
    Status status;
    String description;
    Location location;

    public enum Status {
        ACTIVE, INOPERATIVE, UNDER_CONSTRUCTION;
    }
}
