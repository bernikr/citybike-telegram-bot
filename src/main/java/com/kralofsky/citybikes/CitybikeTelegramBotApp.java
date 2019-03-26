package com.kralofsky.citybikes;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.telegram.telegrambots.ApiContextInitializer;

@SpringBootApplication
public class CitybikeTelegramBotApp {
    public static void main(String[] args) {
        ApiContextInitializer.init();
        SpringApplication.run(CitybikeTelegramBotApp.class, args);
    }
}
