package com.kralofsky.citybikes;

import com.kralofsky.citybikes.config.Values;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.abilitybots.api.bot.AbilityBot;
import org.telegram.abilitybots.api.objects.Ability;
import org.telegram.telegrambots.meta.api.objects.Update;

import static org.telegram.abilitybots.api.objects.Locality.USER;
import static org.telegram.abilitybots.api.objects.Privacy.ADMIN;


@Component
public class CitybikeTelegramBot extends AbilityBot {
    private static final Logger LOGGER = LoggerFactory.getLogger(CitybikeTelegramBot.class);
    private Values values;

    @Autowired
    public CitybikeTelegramBot(Values values) {
        super(values.getBotToken(), values.getBotUsername());
        this.values = values;
        LOGGER.info("created bot " + values.getBotToken() + " " + values.getBotUsername());
    }

    @Override
    public int creatorId() {
        return values.getCreatorId();
    }

    public Ability sayHelloWorld() {
        return Ability
                .builder()
                .name("hello")
                .info("says hello world!")
                .input(0)
                .locality(USER)
                .privacy(ADMIN)
                .action(ctx -> silent.send("Hello world!", ctx.chatId()))
                .post(ctx -> silent.send("Bye world!", ctx.chatId()))
                .build();
    }
}
