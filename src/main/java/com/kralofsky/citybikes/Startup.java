package com.kralofsky.citybikes;

import com.kralofsky.citybikes.bot.CitybikeTelegramBot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;

import javax.annotation.PostConstruct;

@Service
public class Startup {
    private CitybikeTelegramBot bot;

    @Autowired
    public Startup(CitybikeTelegramBot bot) {
        this.bot = bot;
    }

    @PostConstruct
    public void startup() throws TelegramApiRequestException {
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
        telegramBotsApi.registerBot(new TelegramLongPollingBot() {
            @Override
            public String getBotToken() {
                return bot.getBotToken();
            }

            @Override
            public void onUpdateReceived(Update update) {
                bot.onUpdateReceived(update);
            }

            @Override
            public String getBotUsername() {
                return bot.getBotUsername();
            }
        });
    }
}
