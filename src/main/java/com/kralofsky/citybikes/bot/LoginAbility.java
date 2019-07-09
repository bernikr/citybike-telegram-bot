package com.kralofsky.citybikes.bot;

import com.kralofsky.citybikes.bot.util.ExternalAbility;
import com.kralofsky.citybikes.citybikeAPI.ApiException;
import com.kralofsky.citybikes.citybikeAPI.RideAPI;
import org.springframework.stereotype.Component;
import org.telegram.abilitybots.api.objects.Locality;
import org.telegram.abilitybots.api.objects.MessageContext;
import org.telegram.abilitybots.api.objects.Privacy;

@Component
public class LoginAbility extends ExternalAbility {
    @Override
    protected AbilityOptions getOptions() {
        return AbilityOptions.builder()
                .name("login")
                .input(2)
                .locality(Locality.USER)
                .privacy(Privacy.PUBLIC)
                .build();
    }

    @Override
    protected void action(MessageContext ctx) {
        String[] args = ctx.arguments();

        RideAPI rideAPI = new RideAPI(args[0], args[1]);

        try {
            silent.send(rideAPI.getRides().findFirst().get().toString(), ctx.chatId());
        } catch (ApiException e) {
            e.printStackTrace();
        }
    }
}
