package com.kralofsky.citybikes.bot

import com.kralofsky.citybikes.bot.util.BotEntitiesMapper
import com.kralofsky.citybikes.bot.util.MessageFormatter
import com.kralofsky.citybikes.config.logger
import com.kralofsky.citybikes.entity.Location
import com.kralofsky.citybikes.service.IStationService
import lombok.extern.slf4j.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.telegram.abilitybots.api.objects.Flag
import org.telegram.abilitybots.api.objects.Locality
import org.telegram.abilitybots.api.objects.MessageContext
import org.telegram.abilitybots.api.objects.Privacy
import org.telegram.telegrambots.meta.api.methods.send.SendMessage


@Component
class LocationInfoAbility @Autowired
constructor(private val stationService: IStationService) : ExternalAbility() {
    private val log = logger()

    override val options = AbilityOptions()
                .name(CitybikeTelegramBot.DEFAULT_COMMAND)
                .flag(Flag.LOCATION)
                .locality(Locality.USER)
                .privacy(Privacy.PUBLIC)

    override fun action(ctx: MessageContext) {
        log.info("getLocationInformation around " + ctx.update().message.location + " by " + ctx.user())
        val l = BotEntitiesMapper.botLocationtoLocationEntity(ctx.update().message.location)
        silent!!.execute(SendMessage()
                .setChatId(ctx.chatId()!!)
                .setText(MessageFormatter.getStationInfoMessage(stationService.getNearbyStationInfos(l, 3)))
                .enableMarkdown(true)
                .disableWebPagePreview()
        )
        silent!!.execute(SendMessage()
                .setChatId(ctx.chatId()!!)
                .setText(stationService.getHomeStation(ctx.chatId(), l)
                        .map<String> { MessageFormatter.getStationInfoMessage(it) }
                        .orElse("No Home Station set. Use /sethome")
                )
                .enableMarkdown(true)
                .disableWebPagePreview()
        )
    }
}
