package com.kralofsky.citybikes.bot;

import com.kralofsky.citybikes.bot.util.BotEntitiesMapper;
import com.kralofsky.citybikes.bot.util.MessageFormatter;
import com.kralofsky.citybikes.config.Values;
import com.kralofsky.citybikes.entity.Location;
import com.kralofsky.citybikes.entity.StationInfo;
import com.kralofsky.citybikes.service.IStationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.abilitybots.api.bot.AbilityBot;
import org.telegram.abilitybots.api.db.DBContext;
import org.telegram.abilitybots.api.objects.Ability;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.function.Predicate;

import static org.telegram.abilitybots.api.objects.Flag.LOCATION;
import static org.telegram.abilitybots.api.objects.Flag.REPLY;
import static org.telegram.abilitybots.api.objects.Locality.USER;
import static org.telegram.abilitybots.api.objects.Privacy.PUBLIC;


@Component
public class CitybikeTelegramBot extends AbilityBot {
    private static final Logger LOGGER = LoggerFactory.getLogger(CitybikeTelegramBot.class);

    private Values values;
    private IStationService stationService;

    @Autowired
    public CitybikeTelegramBot(Values values, IStationService stationService, DBContext dbContext) {
        super(values.getBotToken(), values.getBotUsername(), dbContext);
        this.values = values;
        this.stationService = stationService;
        LOGGER.info("Initialize Bot " + values.getBotUsername());
    }

    @Override
    public int creatorId() {
        return values.getCreatorId();
    }

    public Ability getLocationInformation() {
        return Ability.builder()
                .name(DEFAULT)
                .flag(LOCATION)
                .locality(USER)
                .privacy(PUBLIC)
                .action(ctx -> {
                    LOGGER.info("getLocationInformation around " + ctx.update().getMessage().getLocation() + " by " + ctx.user());
                    Location l = BotEntitiesMapper.botLocationtoLocationEntity(ctx.update().getMessage().getLocation());
                    silent.execute(new SendMessage()
                            .setChatId(ctx.chatId())
                            .setText(MessageFormatter.getStationInfoMessage(stationService.getNearbyStationInfos(l, 3)))
                            .enableMarkdown(true)
                            .disableWebPagePreview()
                    );
                    silent.execute(new SendMessage()
                            .setChatId(ctx.chatId())
                            .setText(stationService.getHomeStation(ctx.chatId(), l)
                                    .map(MessageFormatter::getStationInfoMessage)
                                    .orElse("No Home Station set. Use /sethome")
                            )
                            .enableMarkdown(true)
                            .disableWebPagePreview()
                    );
                }).build();
    }

    public Ability setHomeStation() {
        String request_for_location_msg = "Please reply with a location, the nearest CityBike station will be set as home and displayed every time.";

        return Ability.builder()
                .name("sethome")
                .input(0)
                .info("Set or Change the Home Station")
                .locality(USER)
                .privacy(PUBLIC)
                .action(ctx -> {
                    LOGGER.info("/setHome by " + ctx.user());
                    silent.forceReply(request_for_location_msg, ctx.chatId());
                })
                .reply(upd -> {
                            LOGGER.info("New Home location recieved: " + upd.getMessage().getLocation() + " for user " + upd.getMessage().getFrom());
                            Location l = BotEntitiesMapper.botLocationtoLocationEntity(upd.getMessage().getLocation());
                            StationInfo home = stationService.setHomeStation(upd.getMessage().getChatId(), l);
                            silent.execute(new SendMessage()
                                    .setChatId(upd.getMessage().getChatId())
                                    .setText("*Home Station set to:*\n\n" + MessageFormatter.getStationInfoMessage(home))
                                    .enableMarkdown(true)
                                    .disableWebPagePreview()
                            );
                        },
                        LOCATION, REPLY, isReplyToBot(), isReplyToMessage(request_for_location_msg)
                ).build();
    }

    private Predicate<Update> isReplyToMessage(String message) {
        return upd -> {
            Message reply = upd.getMessage().getReplyToMessage();
            return reply.hasText() && reply.getText().equalsIgnoreCase(message);
        };
    }

    private Predicate<Update> isReplyToBot() {
        return upd -> upd.getMessage().getReplyToMessage().getFrom().getUserName().equalsIgnoreCase(getBotUsername());
    }
}
