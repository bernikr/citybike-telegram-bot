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
public class LastRideAbility extends ExternalAbility {
    private RideService rideService;

    public LastRideAbility(RideService rideService) {
        this.rideService = rideService;
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

        try {
            rideService.getLastRide(ctx.chatId()).ifPresentOrElse(
                    r -> silent.send(r.toString(), ctx.chatId()),
                    () -> silent.send("Keine Fahrten gefunden.", ctx.chatId())
            );
        } catch (ApiException e) {
            silent.send(String.format("Error while fetching rides:\n%s", e.getMessage()), ctx.chatId());
        }
    }
}
