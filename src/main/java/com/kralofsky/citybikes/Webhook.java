package com.kralofsky.citybikes;

import com.kralofsky.citybikes.bot.CitybikeTelegramBot;
import com.kralofsky.citybikes.config.Values;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.telegram.telegrambots.meta.api.objects.Update;

@RestController
public class Webhook {
    private CitybikeTelegramBot bot;

    @Autowired
    public Webhook(CitybikeTelegramBot bot) {
        this.bot = bot;
    }

    @PostMapping("/hooks/bot/{botToken}")
    public String webhook(Update update, @PathVariable String botToken){
        if (botToken.equals(bot.getBotToken())){
            bot.onUpdateReceived(update);
            return "ok";
        }
        throw new HttpClientErrorException(HttpStatus.NOT_FOUND);
    }
}
