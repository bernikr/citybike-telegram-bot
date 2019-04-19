package com.kralofsky.citybikes.bot;

import com.kralofsky.citybikes.bot.util.BotEntitiesMapper;
import com.kralofsky.citybikes.bot.util.ExternalAbility;
import com.kralofsky.citybikes.bot.util.MessageFormatter;
import com.kralofsky.citybikes.entity.Location;
import com.kralofsky.citybikes.service.IStationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.abilitybots.api.objects.MessageContext;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import static org.telegram.abilitybots.api.objects.Flag.LOCATION;
import static org.telegram.abilitybots.api.objects.Locality.USER;
import static org.telegram.abilitybots.api.objects.Privacy.PUBLIC;

@Component
public class LocationInfoAbility extends ExternalAbility {
    private static final Logger LOGGER = LoggerFactory.getLogger(CitybikeTelegramBot.class);

    private IStationService stationService;

    @Autowired
    public LocationInfoAbility(IStationService stationService) {
        this.stationService = stationService;
    }

    @Override
    protected ExternalAbility.AbilityOptions getOptions() {
        return new AbilityOptions()
                .name(DEFAULT)
                .flag(LOCATION)
                .locality(USER)
                .privacy(PUBLIC);
    }

    @Override
    protected void action(MessageContext ctx) {
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
    }
}
