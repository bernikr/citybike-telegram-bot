package com.kralofsky.citybikes.bot;

import com.kralofsky.citybikes.bot.util.ExternalAbility;
import com.kralofsky.citybikes.citybikeAPI.ApiException;
import com.kralofsky.citybikes.citybikeAPI.RideAPI;
import com.kralofsky.citybikes.entity.ApiUser;
import com.kralofsky.citybikes.persistance.Persistance;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.abilitybots.api.objects.Locality;
import org.telegram.abilitybots.api.objects.MessageContext;
import org.telegram.abilitybots.api.objects.Privacy;

@Component
@Slf4j
public class LoginAbility extends ExternalAbility {
    private Persistance persistance;

    public LoginAbility(Persistance persistance) {
        this.persistance = persistance;
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

        ApiUser user = new ApiUser(args[0], args[1]);

        RideAPI rideAPI = RideAPI.forUser(user);

        try {
            rideAPI.getRideCount();
            persistance.<Long, ApiUser>getMap("api_users").put(ctx.chatId(), user);
            silent.send(String.format("Logged in as %s (%s)", user.getUsername(), user.getFullName()), ctx.chatId());
        } catch (ApiException e) {
            silent.send(String.format("Could not log in as %s (Check username and password)", user.getUsername()), ctx.chatId());
        }
    }
}
