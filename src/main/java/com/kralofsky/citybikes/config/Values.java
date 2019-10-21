package com.kralofsky.citybikes.config;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
public class Values {
    @Value("${BOT_TOKEN}")
    String botToken;

    @Value("${BOT_USERNAME}")
    String botUsername;

    @Value("${BOT_CREATORID}")
    int creatorId;

    @Value("${cacheDuration}")
    int cacheDuration;
}
