package com.kralofsky.citybikes.bot

import com.google.common.collect.ImmutableMap
import com.kralofsky.citybikes.bot.util.ExternalAbility
import com.kralofsky.citybikes.config.Values
import com.kralofsky.citybikes.config.logger
import org.springframework.stereotype.Component
import org.telegram.abilitybots.api.bot.AbilityBot
import org.telegram.abilitybots.api.bot.BaseAbilityBot
import org.telegram.abilitybots.api.db.DBContext
import org.telegram.abilitybots.api.objects.Ability
import org.telegram.abilitybots.api.objects.Reply
import java.util.stream.Collectors
import java.util.stream.Stream


@Component
class CitybikeTelegramBot (
        private val values: Values,
        dbContext: DBContext,
        externalAbilities: Collection<ExternalAbility>
) : AbilityBot(values.botToken, values.botUsername, dbContext) {
    private final val log = logger()

    init {
        log.info("Initialize Bot " + values.botUsername)

        externalAbilities.forEach { a -> a.init(this) }

        registerAbilities(externalAbilities.map { it.ability })
    }

    override fun creatorId(): Int {
        return values.creatorId
    }

    // TODO This is a hack that uses reflection to write to private fields of the superclass...
    private fun registerAbilities(newAbilities: Collection<Ability>) {
        val bab = this.javaClass.superclass.superclass
        val abilitiesField = bab.getDeclaredField("abilities")
        abilitiesField.isAccessible = true
        val existingAbilities = abilitiesField.get(this) as Map<String, Ability>

        val abilities = (existingAbilities.values + newAbilities).map { it.name() to it }.toMap()

        abilitiesField.set(this, abilities)

        val newReplies = abilities.values.flatMap { it.replies() }

        val repliesField = bab.getDeclaredField("replies")
        repliesField.isAccessible = true
        val existingReplies = repliesField.get(this) as List<Reply>

        val replies = (existingReplies + newReplies)

        repliesField.set(this, replies)
    }

    companion object {
        val DEFAULT_COMMAND = BaseAbilityBot.DEFAULT
    }
}
