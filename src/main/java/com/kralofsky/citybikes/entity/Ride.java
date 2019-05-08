package com.kralofsky.citybikes.entity;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Value
@Builder
public class Ride {
    @NonNull LocalDate date;
    @NonNull String startStation;
    @NonNull String endStation;
    @NonNull LocalDateTime startTime;
    @NonNull LocalDateTime endTime;
    @NonNull Double price;
    @NonNull Integer elevation;
}
