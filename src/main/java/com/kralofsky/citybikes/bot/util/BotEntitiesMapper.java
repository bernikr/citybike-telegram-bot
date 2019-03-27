package com.kralofsky.citybikes.bot.util;

import org.telegram.telegrambots.meta.api.objects.Location;

public class BotEntitiesMapper {
    public static com.kralofsky.citybikes.entity.Location botLocationtoLocationEntity(Location loc) {
        return new com.kralofsky.citybikes.entity.Location(loc.getLatitude().doubleValue(), loc.getLongitude().doubleValue());
    }
}
