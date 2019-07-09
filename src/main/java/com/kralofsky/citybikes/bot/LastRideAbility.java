package com.kralofsky.citybikes.bot;

import com.kralofsky.citybikes.bot.util.ExternalAbility;
import com.kralofsky.citybikes.bot.util.MessageFormatter;
import com.kralofsky.citybikes.citybikeAPI.ApiException;
import com.kralofsky.citybikes.service.RideService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.abilitybots.api.objects.*;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

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
                    r -> silent.execute(new SendMessage()
                            .setChatId(ctx.chatId())
                            .setText(MessageFormatter.rideToText(r))
                            .setReplyMarkup(getNavigation())
                    ),
                    () -> silent.send("Keine Fahrten gefunden.", ctx.chatId())
            );
        } catch (ApiException e) {
            silent.send(String.format("Error while fetching rides:\n%s", e.getMessage()), ctx.chatId());
        }
    }

    @Override
    protected List<Reply> replies() {
        return List.of(Reply.of(this::navigationClicked, Flag.CALLBACK_QUERY));
    }

    private void navigationClicked(Update update) {
        log.info("navigation button clicked by " + update.getCallbackQuery().getFrom());
    }

    private static InlineKeyboardMarkup getNavigation(){
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();

        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();

        List<InlineKeyboardButton> rowInline = new ArrayList<>();

        rowInline.add(new InlineKeyboardButton().setText("back").setCallbackData("gallery:back"));
        rowInline.add(new InlineKeyboardButton().setText("next").setCallbackData("gallery:next"));

        rowsInline.add(rowInline);

        markupInline.setKeyboard(rowsInline);

        return markupInline;
    }
}
