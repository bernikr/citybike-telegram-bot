package com.kralofsky.citybikes.bot.util;

import lombok.Builder;
import lombok.Singular;
import org.telegram.abilitybots.api.bot.BaseAbilityBot;
import org.telegram.abilitybots.api.objects.*;
import org.telegram.abilitybots.api.sender.DefaultSender;
import org.telegram.abilitybots.api.sender.MessageSender;
import org.telegram.abilitybots.api.sender.SilentSender;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

public abstract class ExternalAbility {
    protected MessageSender sender;
    protected SilentSender silent;
    protected BaseAbilityBot bot;

    protected String DEFAULT;

    abstract protected AbilityOptions getOptions();

    abstract protected void action(MessageContext ctx);

    protected void post(MessageContext ctx){}

    protected List<Reply> replies(){
        return Collections.emptyList();
    }

    public void init(BaseAbilityBot bot, String defaultCommand) {
        this.bot = bot;
        this.sender = new DefaultSender(bot);
        this.silent = new SilentSender(sender);
        this.DEFAULT = defaultCommand;
    }

    public Ability getAbility(){
        AbilityOptions o = getOptions();

        final Ability.AbilityBuilder ab = Ability.builder()
                .name(o.name)
                .info(o.info)
                .locality(o.locality)
                .privacy(o.privacy)
                .input(o.input)
                .action(this::action)
                .post(this::post)
                .flag(o.flags.toArray(new Predicate[0]));

        replies().forEach(r -> ab.reply(r.action, r.conditions.toArray(new Predicate[0])));

        return ab.build();
    }

    @Builder
    public static class AbilityOptions{
        private String name;
        private String info;
        private Privacy privacy;
        private Locality locality;
        private int input;

        @Singular private Collection<Predicate<Update>> flags;
    }
}
