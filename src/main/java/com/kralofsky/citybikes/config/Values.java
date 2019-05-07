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
    @Value("${bot.token}")
    String botToken;

    @Value("${bot.username}")
    String botUsername;

    @Value("${bot.creatorId}")
    int creatorId;

    @Value("${cacheDuration}")
    int cacheDuration;
}
