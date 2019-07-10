package com.kralofsky.citybikes.bot;

import com.kralofsky.citybikes.bot.util.ExternalAbility;
import com.kralofsky.citybikes.bot.util.MessageFormatter;
import com.kralofsky.citybikes.citybikeAPI.ApiException;
import com.kralofsky.citybikes.service.RideService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.abilitybots.api.objects.*;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
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
            int rideCount = rideService.rideCount(ctx.chatId());
            rideService.getLastRide(ctx.chatId()).ifPresentOrElse(
                    r -> silent.execute(new SendMessage()
                            .setChatId(ctx.chatId())
                            .setText(MessageFormatter.rideToText(r, rideCount, rideCount))
                            .setReplyMarkup(getNavigation(rideCount))
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
        String dataString = update.getCallbackQuery().getData();
        Message orgMsg = update.getCallbackQuery().getMessage();
        if (dataString == null || orgMsg == null) return;
        String[] data = dataString.split(":");
        if (!data[0].equals("rides")) return;
        int index = Integer.parseInt(data[1]);
        try {
            int rideCount = rideService.rideCount(orgMsg.getChatId());
            silent.execute(new EditMessageText()
                    .setChatId(orgMsg.getChatId())
                    .setMessageId(orgMsg.getMessageId())
                    .setText(MessageFormatter.rideToText(
                            rideService.getRide(index, orgMsg.getChatId()),
                            index, rideCount
                            ))
                    .setReplyMarkup(getNavigation(index))
            );
        } catch (ApiException e) {
            e.printStackTrace();
        }
    }

    private static InlineKeyboardMarkup getNavigation(int index){
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();

        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();

        List<InlineKeyboardButton> rowInline = new ArrayList<>();

        rowInline.add(new InlineKeyboardButton().setText("back").setCallbackData("rides:" + (index+1)));
        rowInline.add(new InlineKeyboardButton().setText("next").setCallbackData("rides:" + (index-1)));

        rowsInline.add(rowInline);

        markupInline.setKeyboard(rowsInline);

        return markupInline;
    }
}
