package com.kralofsky.citybikes.bot;

import com.kralofsky.citybikes.bot.util.BotEntitiesMapper;
import com.kralofsky.citybikes.bot.util.MessageFormatter;
import com.kralofsky.citybikes.entity.Location;
import com.kralofsky.citybikes.entity.Station;
import com.kralofsky.citybikes.service.IStationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.abilitybots.api.objects.*;
import org.telegram.abilitybots.api.util.Pair;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;
import java.util.function.Predicate;


@Component
@Slf4j
public class SetHomeAbility extends ExternalAbility {
    private IStationService stationService;

    private final static String request_for_location_msg = "Please reply with a location, the nearest CityBike station will be set as home and displayed every time.";

    @Autowired
    public SetHomeAbility(IStationService stationService) {
        this.stationService = stationService;
    }

    @Override
    protected AbilityOptions getOptions() {
        return new AbilityOptions()
                .name("sethome")
                .input(0)
                .info("Set or Change the Home Station")
                .locality(Locality.USER)
                .privacy(Privacy.PUBLIC);
    }

    @Override
    protected void action(MessageContext ctx) {
        log.info("/setHome by " + ctx.user());
        getSilent().forceReply(request_for_location_msg, ctx.chatId());
    }

    @Override
    protected List<Reply> replies() {
        return List.of(Reply.of(this::setHome, Flag.LOCATION, Flag.REPLY, isReplyToBot(), isReplyToMessage(request_for_location_msg)));
    }

    private void setHome(Update upd){
        log.info("New Home location recieved: " + upd.getMessage().getLocation() + " for user " + upd.getMessage().getFrom());
        Location l = BotEntitiesMapper.botLocationtoLocationEntity(upd.getMessage().getLocation());
        Pair<Station, Double> home = stationService.setHomeStation(upd.getMessage().getChatId(), l);
        getSilent().execute(new SendMessage()
                .setChatId(upd.getMessage().getChatId())
                .setText("*Home Station set to:*\n\n" + MessageFormatter.getStationInfoMessage(home))
                .enableMarkdown(true)
                .disableWebPagePreview()
        );
    }

    private Predicate<Update> isReplyToMessage(String message) {
        return upd -> {
            Message reply = upd.getMessage().getReplyToMessage();
            return reply.hasText() && reply.getText().equalsIgnoreCase(message);
        };
    }

    private Predicate<Update> isReplyToBot() {
        return upd -> upd.getMessage().getReplyToMessage().getFrom().getUserName().equalsIgnoreCase(getBot().getBotUsername());
    }
}
