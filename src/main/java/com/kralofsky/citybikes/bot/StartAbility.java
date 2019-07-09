package com.kralofsky.citybikes.bot;

import com.kralofsky.citybikes.bot.util.ExternalAbility;
import org.springframework.stereotype.Component;
import org.telegram.abilitybots.api.objects.Locality;
import org.telegram.abilitybots.api.objects.MessageContext;
import org.telegram.abilitybots.api.objects.Privacy;

@Component
public class StartAbility extends ExternalAbility {
    private final String message = "Just send a location to get information on the Citybike Stations around you. Use /sethome to set your favourite station that gets displayed every time regardless of your location.\n" +
            "\n" +
            "THIS BOT IS UNOFFICIAL:\n" +
            "Citybike and the GEWISTA Werbegesellschaft m.b.H. are NOT associated in any way with the operator of this bot, are NOT responsible for the content of this bot and take NO responsibility or liability concerning accuracy or timeliness of the data provided.";

    @Override
    protected AbilityOptions getOptions() {
        return AbilityOptions.builder()
                .name("start")
                .input(0)
                .locality(Locality.USER)
                .privacy(Privacy.PUBLIC)
                .build();
    }

    @Override
    protected void action(MessageContext ctx) {
        silent.send(message, ctx.chatId());
    }
}
