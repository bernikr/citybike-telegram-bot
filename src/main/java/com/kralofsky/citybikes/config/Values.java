package com.kralofsky.citybikes.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Values {

    @Value("${custom.bot.token}")
    private String botToken;

    @Value("${custom.bot.username}")
    private String botUsername;

    @Value("${custom.bot.creatorId}")
    private int creatorId;

    public String getBotToken() {
        return botToken;
    }

    public String getBotUsername() {
        return botUsername;
    }

    public int getCreatorId() {
        return creatorId;
    }
}
