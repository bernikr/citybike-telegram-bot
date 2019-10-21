package com.kralofsky.citybikes;

import com.kralofsky.citybikes.bot.CitybikeTelegramBot;
import com.kralofsky.citybikes.config.Values;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import org.telegram.telegrambots.util.WebhookUtils;

import javax.annotation.PostConstruct;

@Service
@Slf4j
public class Startup {
    private CitybikeTelegramBot bot;
    private Values values;

    @Autowired
    public Startup(CitybikeTelegramBot bot, Values values) {
        this.bot = bot;
        this.values = values;
    }

    @PostConstruct
    public void startup() throws TelegramApiRequestException {
        if(values.isWebhook()){
            log.info("startup with webhook (URL: " + values.getBaseURL() + ")");
            WebhookUtils.setWebhook(bot, values.getBaseURL() + "hooks/bot/" + values.getBotToken(), null);
        } else {
            log.info("No Webhook Configured, using Long Polling");
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
}
