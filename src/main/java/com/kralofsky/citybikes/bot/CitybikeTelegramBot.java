package com.kralofsky.citybikes.bot;

import com.google.common.collect.ImmutableMap;
import com.kralofsky.citybikes.bot.util.ExternalAbility;
import com.kralofsky.citybikes.config.Values;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.abilitybots.api.bot.AbilityBot;
import org.telegram.abilitybots.api.bot.BaseAbilityBot;
import org.telegram.abilitybots.api.db.DBContext;
import org.telegram.abilitybots.api.objects.Ability;
import org.telegram.abilitybots.api.objects.Reply;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Component
@Slf4j
public class CitybikeTelegramBot extends AbilityBot {
    static final String DEFAULT_COMMAND = BaseAbilityBot.DEFAULT;

    private Values values;

    @Autowired
    public CitybikeTelegramBot(Values values, DBContext dbContext, Collection<? extends ExternalAbility> externalAbilities) {
        super(values.getBotToken(), values.getBotUsername(), dbContext);
        this.values = values;
        log.info("Initialize Bot " + values.getBotUsername());

        externalAbilities.forEach(a -> a.init(this));
        Collection<Ability> newAbilities = externalAbilities.stream().map(ExternalAbility::getAbility).collect(Collectors.toList());

        try {
            registerAbilities(newAbilities);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            log.error("Error while registering Abilities", e);
            throw new RuntimeException(e);
        }

    }

    @Override
    public int creatorId() {
        return values.getCreatorId();
    }

    // TODO This is a hack that uses reflection to write to private fields of the superclass...
    private void registerAbilities(Collection<Ability> newAbilities) throws NoSuchFieldException, IllegalAccessException {
        Class bab = this.getClass().getSuperclass().getSuperclass();
        Field abilitiesField = bab.getDeclaredField("abilities");
        abilitiesField.setAccessible(true);
        Map<String, Ability> existingAbilities = (Map<String, Ability>) abilitiesField.get(this);

        Map<String, Ability> abilities = Stream
                .concat(existingAbilities.values().stream(), newAbilities.stream())
                .collect(ImmutableMap::<String, Ability>builder,
                        (b, a) -> b.put(a.name(), a),
                        (b1, b2) -> b1.putAll(b2.build()))
                .build();

        abilitiesField.set(this, abilities);

        Stream<Reply> newReplies = abilities.values().stream().flatMap(ability -> ability.replies().stream());

        Field repliesField = bab.getDeclaredField("replies");
        repliesField.setAccessible(true);
        List<Reply> existingReplies = (List<Reply>) repliesField.get(this);

        List<Reply> replies = Stream.concat(existingReplies.stream(), newReplies).collect(Collectors.toList());

        repliesField.set(this, replies);
    }
}
