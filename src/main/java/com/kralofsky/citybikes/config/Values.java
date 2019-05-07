package com.kralofsky.citybikes.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Values {

    @Value("${bot.token}")
    private String botToken;

    @Value("${bot.username}")
    private String botUsername;

    @Value("${bot.creatorId}")
    private int creatorId;

    @Value("${cacheDuration}")
    private int cacheDuration;

    public String getBotToken() {
        return botToken;
    }

    public String getBotUsername() {
        return botUsername;
    }

    public int getCreatorId() {
        return creatorId;
    }

    public int getCacheDuration() {
        return cacheDuration;
    }
}
