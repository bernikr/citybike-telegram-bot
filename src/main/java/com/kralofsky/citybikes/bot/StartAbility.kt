package com.kralofsky.citybikes.bot

import org.springframework.stereotype.Component
import org.telegram.abilitybots.api.objects.Locality
import org.telegram.abilitybots.api.objects.MessageContext
import org.telegram.abilitybots.api.objects.Privacy

@Component
class StartAbility : ExternalAbility() {
    private val message = "Just send a location to get information on the Citybike Stations around you. Use /sethome to set your favourite station that gets displayed every time regardless of your location.\n" +
            "\n" +
            "THIS BOT IS UNOFFICIAL:\n" +
            "Citybike and the GEWISTA Werbegesellschaft m.b.H. are NOT associated in any way with the operator of this bot, are NOT responsible for the content of this bot and take NO responsibility or liability concerning accuracy or timeliness of the data provided."

    override val options = AbilityOptions(
            name="start",
            locality = Locality.USER,
            privacy = Privacy.PUBLIC
    )

    override fun action(ctx: MessageContext) {
        silent!!.send(message, ctx.chatId()!!)
    }
}
