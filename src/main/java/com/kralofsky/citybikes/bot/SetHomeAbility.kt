package com.kralofsky.citybikes.bot

import com.kralofsky.citybikes.bot.util.BotEntitiesMapper
import com.kralofsky.citybikes.bot.util.MessageFormatter
import com.kralofsky.citybikes.config.logger
import com.kralofsky.citybikes.service.IStationService
import org.springframework.stereotype.Component
import org.telegram.abilitybots.api.objects.*
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update
import java.util.function.Consumer
import java.util.function.Predicate


@Component
class SetHomeAbility(private val stationService: IStationService) : ExternalAbility() {
    private val log = logger()

    override val options = AbilityOptions(
            name = "sethome",
            info = "Set or Change the Home Station",
            locality = Locality.USER
    )

    private val isReplyToBot: Predicate<Update> =
            Predicate { it.message.replyToMessage.from.userName.equals(bot!!.botUsername, ignoreCase = true) }

    override fun action(ctx: MessageContext) {
        log.info("/setHome by " + ctx.user())
        silent!!.forceReply(request_for_location_msg, ctx.chatId()!!)
    }

    override val replies = listOf(
            Reply.of(Consumer { this.setHome(it) }, Flag.LOCATION, Flag.REPLY, isReplyToBot, isReplyToMessage(request_for_location_msg))
    )

    private fun setHome(upd: Update) {
        log.info("New Home location recieved: " + upd.message.location + " for user " + upd.message.from)
        val l = BotEntitiesMapper.botLocationtoLocationEntity(upd.message.location)
        val home = stationService.setHomeStation(upd.message.chatId, l)
        silent!!.execute(SendMessage()
                .setChatId(upd.message.chatId!!)
                .setText("*Home Station set to:*\n\n" + MessageFormatter.getStationInfoMessage(home))
                .enableMarkdown(true)
                .disableWebPagePreview()
        )
    }

    private fun isReplyToMessage(message: String): Predicate<Update> {
        return Predicate { it.message.replyToMessage?.text.equals(message, ignoreCase = true)
        }
    }

    companion object {
        private const val request_for_location_msg = "Please reply with a location, the nearest CityBike station will be set as home and displayed every time."
    }
}
