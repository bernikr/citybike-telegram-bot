package com.kralofsky.citybikes.bot

import com.kralofsky.citybikes.config.Values
import com.kralofsky.citybikes.config.logger
import org.springframework.stereotype.Component
import org.telegram.abilitybots.api.bot.AbilityBot
import org.telegram.abilitybots.api.bot.BaseAbilityBot
import org.telegram.abilitybots.api.db.DBContext
import org.telegram.abilitybots.api.objects.*
import org.telegram.abilitybots.api.sender.DefaultSender
import org.telegram.abilitybots.api.sender.MessageSender
import org.telegram.abilitybots.api.sender.SilentSender
import org.telegram.telegrambots.meta.api.objects.Update
import java.util.function.Predicate


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

abstract class ExternalAbility {
    protected var sender: MessageSender? = null
    protected var silent: SilentSender? = null
    protected var bot: BaseAbilityBot? = null

    protected abstract val options: AbilityOptions

    val ability: Ability
        get() {
            val o = options

            val ab = Ability.builder()
                    .name(o.name)
                    .info(o.info)
                    .locality(o.locality)
                    .privacy(o.privacy)
                    .input(o.input)
                    .action { this.action(it) }
                    .post { this.post(it) }
                    .flag(*o.flags.toTypedArray())

            replies().forEach { r -> ab.reply(r.action, *r.conditions.toTypedArray()) }

            return ab.build()
        }

    protected abstract fun action(ctx: MessageContext)

    protected fun post(ctx: MessageContext) {}

    protected open fun replies(): List<Reply> {
        return emptyList()
    }

    fun init(bot: BaseAbilityBot) {
        this.bot = bot
        this.sender = DefaultSender(bot)
        this.silent = SilentSender(sender)
    }


    data class AbilityOptions(
            var name: String = CitybikeTelegramBot.DEFAULT_COMMAND,
            var info: String = "",
            var privacy: Privacy = Privacy.PUBLIC,
            var locality: Locality = Locality.ALL,
            var input: Int = 0,
            var flags: MutableCollection<Predicate<Update>> = mutableListOf()
    ){
        fun name(name: String) = apply { this.name = name }
        fun info(info: String) = apply { this.info = info }
        fun privacy(privacy: Privacy) = apply { this.privacy = privacy }
        fun locality(locality: Locality) = apply { this.locality = locality }
        fun input(input: Int) = apply { this.input = input }
        fun flag(flag: Predicate<Update>) = apply { this.flags.add(flag) }
    }
}