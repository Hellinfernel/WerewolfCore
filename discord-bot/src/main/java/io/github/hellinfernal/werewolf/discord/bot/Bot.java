package io.github.hellinfernal.werewolf.discord.bot;

import discord4j.common.util.Snowflake;
import discord4j.core.DiscordClient;
import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.spec.MessageCreateSpec;
import discord4j.core.spec.MessageEditSpec;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

public class Bot {


    public static void main(String[] args) {
        final Bot bot = new Bot();
        bot.start();
    }

    private DiscordClient _discordClient;
    private Map<Snowflake, GameBootstrap> _gamesToBootstrapByChannel = new HashMap<>();

    private void start() {
        _discordClient = DiscordClient.create(System.getenv("DISCORD_BOT_API_TOKEN"));
        _discordClient
                .withGateway(gateway -> Mono.when( //
                        gateway.on(MessageCreateEvent.class, this::startGame) //
                ))
                .block();
    }

    private Publisher<Void> startGame(final MessageCreateEvent event) {
        if (!event.getMessage().getContent().equalsIgnoreCase("!startGame")) {
            return Mono.empty();
        }

        return event.getMessage().getChannel().flatMap(c -> {
                    if (_gamesToBootstrapByChannel.containsKey(c.getId())) {
                        return c.createMessage("game already started, wont start another one");
                    }

                    final GameBootstrap bootstrap = _gamesToBootstrapByChannel.computeIfAbsent(c.getId(), GameBootstrap::new);
                    return c.createMessage(MessageCreateSpec.builder()
                                    .content("Please click Join to join the game, game will start within the next 3 minutes.")
                                    .addComponent(bootstrap.configureButtons())
                                    .build())
                            .then(joinGame(event))
                            .timeout(Duration.ofMinutes(3))
                            .onErrorResume(TimeoutException.class, ignore -> {
                                //TODO: later
                                //channelGame.initiate();
                                return Mono.empty();
                            });
                }
        ).then();
    }

    private Mono<Void> joinGame(final MessageCreateEvent event) {
        return event.getClient().on(ButtonInteractionEvent.class, buttonEvent -> {
            final GameBootstrap bootstrap = _gamesToBootstrapByChannel
                    .values()
                    .stream()
                    .filter(v -> v.hasButton(buttonEvent.getCustomId()))
                    .findFirst()
                    .orElse(null);

            if (bootstrap == null) {
                return buttonEvent
                        .deferReply()
                        .withEphemeral(true)
                        .then(buttonEvent.createFollowup("Sorry, the game you tried to interact with, already started.")
                                .withEphemeral(true))
                        .then();
            }

            if (bootstrap.hasClickedRegister(buttonEvent.getCustomId())) {
                if (bootstrap.join(event.getMember().get())) {
                    return buttonEvent
                            .deferReply()
                            .then(buttonEvent
                                    .getMessage()
                                    .map(message -> message
                                            .edit(MessageEditSpec
                                                    .builder()
                                                    .addComponent(bootstrap.configureButtons())
                                                    .build()))
                                    .orElse(Mono.empty()))
                            .then(buttonEvent.createFollowup(event
                                    .getMember()
                                    .get()
                                    .getTag() + " was added"))
                            .then();
                } else {
                    return buttonEvent
                            .deferReply()
                            .withEphemeral(true)
                            .then(buttonEvent.createFollowup("You are already in the game :D")
                                    .withEphemeral(true))
                            .then();

                }
            }

            if (bootstrap.hasClickedLeave(buttonEvent.getCustomId())) {
                if (bootstrap.leave(event.getMember().get())) {
                    return buttonEvent
                            .deferReply()
                            .then(buttonEvent
                                    .getMessage()
                                    .map(message -> message
                                            .edit(MessageEditSpec
                                                    .builder()
                                                    .addComponent(bootstrap.configureButtons())
                                                    .build()))
                                    .orElse(Mono.empty()))
                            .then(buttonEvent.createFollowup(event
                                    .getMember()
                                    .get()
                                    .getTag() + " was removed"))
                            .then();
                } else {
                    return buttonEvent
                            .deferReply()
                            .withEphemeral(true)
                            .then(buttonEvent.createFollowup("You aren't in the game :D")
                                    .withEphemeral(true))
                            .then();
                }
            } else {
                throw new RuntimeException("ID seems to be not correct.");
            }


        }).then();
    }

}
