package io.github.hellinfernal.werewolf.discord.bot;

import discord4j.core.DiscordClient;
import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.component.ActionRow;
import discord4j.core.object.entity.Member;
import discord4j.core.spec.MessageCreateSpec;
import discord4j.core.spec.MessageEditSpec;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Bot {


    public static void main(String[] args) {
        final Bot bot = new Bot();
        bot.start();
    }

    private DiscordClient _discordClient;
    private Map<ChannelGame, List<Member>> _games = new HashMap<>();

    private void start() {
        _discordClient = DiscordClient.create("OTI4NjE4Njk5NTYzMzUyMDc0.YdbZjg.-Y31xPE71yxDBZc3qRauyLdXLQY");
        _discordClient
                .withGateway(gateway -> {
                    return Mono.when( //
                            gateway.on(MessageCreateEvent.class, this::startGame) //
                    );
                })
                .block();
    }

    private Publisher<Void> startGame(final MessageCreateEvent event) {
        if (!event.getMessage().getContent().equalsIgnoreCase("!startGame")) {
            return Mono.empty();
        }

        return event.getMessage().getChannel().flatMap(c -> {
                    final ChannelGame channelGame = new ChannelGame(c.getId());
                    if (!_games.containsKey(channelGame)) {
                        _games.computeIfAbsent(channelGame, id -> new ArrayList<>());
                        return c.createMessage(MessageCreateSpec.builder()
                                        .content("Please click Join to join the game, game will start within the next 3 minutes.")
                                        .addComponent(ActionRow.of(channelGame.registerButton))
                                        .build())
                                .then(joinGame(event));
                    }
                    return c.createMessage("game already started, wont start another one");
                }
        ).then();
    }

    private Mono<Void> joinGame(final MessageCreateEvent event) {
        return event.getClient().on(ButtonInteractionEvent.class, buttonEvent -> {
            final ChannelGame runningGame = _games.keySet().stream().filter(c -> c.registerButton.getCustomId().get().equalsIgnoreCase(buttonEvent.getCustomId())).findFirst().orElse(null);

            if (!runningGame.join(event.getMember().get())) {
                return buttonEvent.getMessage()
                        .map(message -> message.edit(MessageEditSpec.builder()
                                        .addComponent(ActionRow.of(runningGame.registerButton.disabled()))
                                        .build())
                                .then())
                        .orElse(Mono.empty());
            } else {
                return buttonEvent.getMessage()
                        .map(message -> message.edit(MessageEditSpec.builder()
                                        .addComponent(ActionRow.of(runningGame.registerButton))
                                        .build())
                                .then())
                        .orElse(Mono.empty());
            }
        }).then();
    }

}
