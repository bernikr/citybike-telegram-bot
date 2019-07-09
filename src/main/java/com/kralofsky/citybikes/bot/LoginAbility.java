package com.kralofsky.citybikes.bot;

import com.kralofsky.citybikes.bot.util.ExternalAbility;
import com.kralofsky.citybikes.citybikeAPI.ApiException;
import com.kralofsky.citybikes.service.RideService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.abilitybots.api.objects.Locality;
import org.telegram.abilitybots.api.objects.MessageContext;
import org.telegram.abilitybots.api.objects.Privacy;

@Component
@Slf4j
public class LoginAbility extends ExternalAbility {
    private RideService rideService;

    public LoginAbility(RideService rideService) {
        this.rideService = rideService;
    }

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
        log.info("/login by " + ctx.user());
        String[] args = ctx.arguments();
        String username = args[0];
        String password = args[1];

        try {
            String userFullName = rideService.login(username, password, ctx.chatId());
            silent.send(String.format("Logged in as %s (%s)", username, userFullName), ctx.chatId());
        } catch (ApiException e) {
            silent.send(String.format("Could not log in as %s:\n%s", username, e.getMessage()), ctx.chatId());
        }
    }
}
