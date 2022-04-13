package io.github.hellinfernal.werewolf.discord.bot;

import discord4j.common.util.Snowflake;
import discord4j.core.DiscordClient;
import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.spec.MessageCreateSpec;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Supplier;

public class Bot {


    public static void main(String[] args) {
        final Bot bot = new Bot();
        bot.start();
    }

    private DiscordClient _discordClient;
    private Map<Snowflake, GameBootstrap> _gamesToBootstrapByChannel = new HashMap<>();
    //TODO: scheduler/timer
    private ScheduledExecutorService _scheduler = Executors.newScheduledThreadPool(1);

    private void start() {
        _discordClient = DiscordClient.create(System.getenv("DISCORD_BOT_API_TOKEN"));
        _discordClient
                .withGateway(gateway -> Mono.when( //
                        gateway.on(MessageCreateEvent.class, this::startGame) //
                ))
                .block();
        _scheduler.scheduleAtFixedRate(this::checkGames, 30, 30, TimeUnit.SECONDS);
    }

    private void checkGames() {

        _gamesToBootstrapByChannel
                .entrySet()
                .stream()
                .filter(entry -> entry.getValue().get_started().isAfter(Instant.now().minusSeconds(30)))
        .forEach(entry -> entry.getValue().initiate());
        //TODO: user über spielstart informieren
        //GameBootstrap bootstrap = null;
        //_discordClient.getChannelById(Snowflake.of(1)).
    }

    private Publisher<Void> startGame(final MessageCreateEvent event) {
        if (!event.getMessage().getContent().equalsIgnoreCase("!startGame")) {
            return Mono.empty();
        }

        return event.getMessage().getChannel().flatMap(c -> {
            if (_gamesToBootstrapByChannel.containsKey(c.getId())) {
                return c.createMessage("game already started, wont start another one");
                    }

                    final GameBootstrap bootstrap = _gamesToBootstrapByChannel.computeIfAbsent(c.getId(), channelId -> new GameBootstrap(_discordClient, channelId));
                    return c.createMessage(MessageCreateSpec.builder()
                                    .content("Please click Join to join the game, game will start within the next 3 minutes.")
                                    .addComponent(bootstrap.configureButtonsActionRow())
                                    .build())
                            .then(generateGameMenu(event))
                            .timeout(Duration.ofMinutes(3))
                            .onErrorResume(TimeoutException.class, ignore -> {
                                //TODO: later
                                //channelGame.initiate();
                                return Mono.empty();
                            });
                }
        ).then();
    }

    private Mono<Void> generateGameMenu(final MessageCreateEvent event) {
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
            if (bootstrap.hasClickedRegister(buttonEvent.getCustomId()))
                return getJoinReaction(event, buttonEvent, bootstrap);
            if (bootstrap.hasClickedLeave(buttonEvent.getCustomId()))
                return getLeaveReaction(event, buttonEvent, bootstrap);
            if (bootstrap.hasClickedConfig(buttonEvent.getCustomId())){
                return getConfigReaction(event,buttonEvent,bootstrap);
            }
            return Mono.empty();


        }).then();
    }

    private Mono<Void> getConfigReaction(MessageCreateEvent event, ButtonInteractionEvent buttonEvent, GameBootstrap bootstrap) {
        return bootstrap.configMenu(buttonEvent, event);
    }

    @org.jetbrains.annotations.NotNull
    private Mono<Void> getLeaveReaction(MessageCreateEvent event, ButtonInteractionEvent buttonEvent, GameBootstrap bootstrap) {
        if (bootstrap.leave(event.getMember().get())) {
            return buttonEvent
                    .deferReply()
                    .then(Mono.just(bootstrap.configureButtonsEditSpec(buttonEvent.getMessage().get())))
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
    }

    @org.jetbrains.annotations.NotNull
    private Mono<Void> getJoinReaction(MessageCreateEvent event, ButtonInteractionEvent buttonEvent, GameBootstrap bootstrap) {
        if (bootstrap.join(event.getMember().get())) {
            Supplier<Mono<Message>> gameStarted = Mono::empty;
            if (bootstrap.hasReachedMinimumMembers()) {
                bootstrap.initiate();
                gameStarted = () -> buttonEvent
                        .getMessage()
                        .map(bootstrap::configureButtonEdit)
                        .get()
                        .then(buttonEvent.createFollowup("Game has started, have fun!"));
            }
            return buttonEvent
                    .deferReply()
                    .then(buttonEvent
                            .getMessage()
                            .map(bootstrap::configureButtonEdit)
                    .get())
                    .then(buttonEvent.createFollowup(event
                            .getMember()
                            .get()
                            .getTag() + " was added"))
                    .then(gameStarted.get())
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

}
