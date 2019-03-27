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
import org.telegram.abilitybots.api.objects.Ability;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import static org.telegram.abilitybots.api.objects.Flag.LOCATION;
import static org.telegram.abilitybots.api.objects.Locality.USER;
import static org.telegram.abilitybots.api.objects.Privacy.PUBLIC;


@Component
public class CitybikeTelegramBot extends AbilityBot {
    private static final Logger LOGGER = LoggerFactory.getLogger(CitybikeTelegramBot.class);

    private Values values;
    private IStationService stationService;

    @Autowired
    public CitybikeTelegramBot(Values values, IStationService stationService) {
        super(values.getBotToken(), values.getBotUsername());
        this.values = values;
        this.stationService = stationService;
        LOGGER.info("created bot " + values.getBotToken() + " " + values.getBotUsername());
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
                    Location l = BotEntitiesMapper.botLocationtoLocationEntity(ctx.update().getMessage().getLocation());

                    silent.execute(new SendMessage()
                            .setChatId(ctx.chatId())
                            .setText(MessageFormatter.getStationInfoMessage(stationService.getNearbyStationInfos(l)))
                            .enableMarkdown(true)
                            .disableWebPagePreview()
                    );
                }).build();
    }
}
