package com.kralofsky.citybikes.bot;

import com.kralofsky.citybikes.bot.util.BotEntitiesMapper;
import com.kralofsky.citybikes.bot.util.ExternalAbility;
import com.kralofsky.citybikes.bot.util.MessageFormatter;
import com.kralofsky.citybikes.entity.Location;
import com.kralofsky.citybikes.service.IStationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.abilitybots.api.objects.Flag;
import org.telegram.abilitybots.api.objects.Locality;
import org.telegram.abilitybots.api.objects.MessageContext;
import org.telegram.abilitybots.api.objects.Privacy;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;


@Component
@Slf4j
public class LocationInfoAbility extends ExternalAbility {
    private IStationService stationService;

    @Autowired
    public LocationInfoAbility(IStationService stationService) {
        this.stationService = stationService;
    }

    @Override
    protected ExternalAbility.AbilityOptions getOptions() {
        return AbilityOptions.builder()
                .name(CitybikeTelegramBot.DEFAULT_COMMAND)
                .flag(Flag.LOCATION)
                .locality(Locality.USER)
                .privacy(Privacy.PUBLIC)
                .build();
    }

    @Override
    protected void action(MessageContext ctx) {
        log.info("getLocationInformation around " + ctx.update().getMessage().getLocation() + " by " + ctx.user());
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