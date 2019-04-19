package com.kralofsky.citybikes.bot.util;

import org.telegram.abilitybots.api.bot.BaseAbilityBot;
import org.telegram.abilitybots.api.objects.*;
import org.telegram.abilitybots.api.sender.DefaultSender;
import org.telegram.abilitybots.api.sender.MessageSender;
import org.telegram.abilitybots.api.sender.SilentSender;
import org.telegram.telegrambots.meta.api.objects.Update;

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
                .input(o.argNum)
                .action(this::action)
                .post(this::post)
                .flag(o.flags);

        replies().forEach(r -> ab.reply(r.action, r.conditions.toArray(new Predicate[0])));

        return ab.build();
    }

    public static class AbilityOptions{
        private String name;
        private String info;
        private Privacy privacy;
        private Locality locality;
        private int argNum;
        private Predicate<Update>[] flags;

        public AbilityOptions() {}

        public AbilityOptions name(String name) {
            this.name = name;
            return this;
        }

        public AbilityOptions info(String info) {
            this.info = info;
            return this;
        }

        @SafeVarargs
        public final AbilityOptions flag(Predicate<Update>... flags) {
            this.flags = flags;
            return this;
        }

        public AbilityOptions locality(Locality type) {
            this.locality = type;
            return this;
        }

        public AbilityOptions input(int argNum) {
            this.argNum = argNum;
            return this;
        }

        public AbilityOptions privacy(Privacy privacy) {
            this.privacy = privacy;
            return this;
        }
    }
}
