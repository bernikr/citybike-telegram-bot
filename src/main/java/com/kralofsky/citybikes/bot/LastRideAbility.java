package com.kralofsky.citybikes.bot;

import com.kralofsky.citybikes.bot.util.ExternalAbility;
import com.kralofsky.citybikes.citybikeAPI.ApiException;
import com.kralofsky.citybikes.citybikeAPI.RideAPI;
import com.kralofsky.citybikes.entity.ApiUser;
import com.kralofsky.citybikes.entity.Ride;
import com.kralofsky.citybikes.persistance.Persistance;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.abilitybots.api.objects.Locality;
import org.telegram.abilitybots.api.objects.MessageContext;
import org.telegram.abilitybots.api.objects.Privacy;

@Component
@Slf4j
public class LastRideAbility extends ExternalAbility {
    private Persistance persistance;

    public LastRideAbility(Persistance persistance) {
        this.persistance = persistance;
    }

    @Override
    protected AbilityOptions getOptions() {
        return AbilityOptions.builder()
                .name("lastride")
                .locality(Locality.USER)
                .privacy(Privacy.PUBLIC)
                .build();
    }

    @Override
    protected void action(MessageContext ctx) {
        log.info("/lastride by " + ctx.user());

        ApiUser user = persistance.<Long, ApiUser>getMap("api_users").get(ctx.chatId());

        if (user == null) {
            silent.send("You must log in to do that. Use '/login username password'", ctx.chatId());
            return;
        }

        RideAPI rideAPI = RideAPI.forUser(user);

        try {
            Ride r = rideAPI.getRides().findFirst().orElseThrow();
            persistance.getMap("api_users").put(ctx.chatId(), user);
            silent.send(r.toString(), ctx.chatId());
        } catch (ApiException e) {
            silent.send(String.format("Error while fetching rides for user %s. Please log in again.", user.getUsername()), ctx.chatId());
        }
    }
}
